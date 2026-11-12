package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ReplenishmentOrderService {
    private static final Logger LOG = Logger.getLogger(ReplenishmentOrderService.class.getName());
    private final ReplenishmentOrderDAO dao;
    private final InventoryDAO inventoryDAO;

    public ReplenishmentOrderService() throws SQLException {
        this.dao = new ReplenishmentOrderDAO(DBUtil.getConnection());
        this.inventoryDAO = new InventoryDAO(DBUtil.getConnection());
    }

    public List<ReplenishmentOrder> getAll() throws SQLException {
        return dao.getAll();
    }

    public ReplenishmentOrder getById(Long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(ReplenishmentOrder order) throws SQLException {
        if (order == null) {
            throw new IllegalArgumentException("Замовлення на поповнення не може бути null");
        }
        if (order.getState() == null) {
            throw new IllegalArgumentException("Стан замовлення не може бути null");
        }

        Inventory inventory = inventoryDAO.getById(order.getInventoryId());
        if (inventory == null) {
            throw new IllegalArgumentException("Запис запасу з ID " + order.getInventoryId() + " не існує");
        }

        Supplier supplier = new SupplierDAO(DBUtil.getConnection()).getById(order.getSupplierId());
        if (supplier == null) {
            throw new IllegalArgumentException("Постачальник з ID " + order.getSupplierId() + " не існує");
        }

        double f1 = RiskCalculator.factor1(inventory.getCurrentStock(), inventory.getMinRequiredStock());
        double f2 = inventory.getConsumptionIntensity();
        double f3 = supplier.getDelayRiskScore();
        double integralIndex = RiskCalculator.calculate(f1, f2, f3);

        order.setF1(f1);
        order.setF2(f2);
        order.setF3(f3);
        order.setRiskIndex(integralIndex);
        order.setState(OrderState.CREATED);

        dao.create(order);
        LOG.info("Створено замовлення на поповнення");
    }

    public void update(ReplenishmentOrder order) throws SQLException {
        if (order == null) {
            throw new IllegalArgumentException("Замовлення на поповнення не може бути null");
        }
        if (order.getState() == null) {
            throw new IllegalArgumentException("Стан замовлення не може бути null");
        }

        Inventory inventory = inventoryDAO.getById(order.getInventoryId());
        if (inventory == null) {
            throw new IllegalArgumentException("Запис запасу з ID " + order.getInventoryId() + " не існує");
        }

        Supplier supplier = new SupplierDAO(DBUtil.getConnection()).getById(order.getSupplierId());
        if (supplier == null) {
            throw new IllegalArgumentException("Постачальник з ID " + order.getSupplierId() + " не існує");
        }

        double f1 = RiskCalculator.factor1(inventory.getCurrentStock(), inventory.getMinRequiredStock());
        double f2 = inventory.getConsumptionIntensity();
        double f3 = supplier.getDelayRiskScore();
        double integralIndex = RiskCalculator.calculate(f1, f2, f3);

        order.setF1(f1);
        order.setF2(f2);
        order.setF3(f3);
        order.setRiskIndex(integralIndex);

        dao.update(order);
        LOG.info("Оновлено замовлення на поповнення");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено замовлення на поповнення");
    }

    public static boolean isAllowedTransition(OrderState from, OrderState to) {
        return (from == OrderState.CREATED && to == OrderState.APPROVED)
                || (from == OrderState.APPROVED && to == OrderState.ORDERED)
                || (from == OrderState.ORDERED && to == OrderState.RECEIVED);
    }

    public void changeState(long id, OrderState nextState) throws SQLException {
        ReplenishmentOrder current = dao.getById(id);

        if (current == null) {
            throw new IllegalArgumentException("Замовлення на поповнення не знайдено");
        }

        if (!isAllowedTransition(current.getState(), nextState)) {
            LOG.warning("Недопустимий перехід стану: " + current.getState() + " -> " + nextState);
            throw new IllegalArgumentException("Недопустимий перехід стану");
        }
        dao.updateState(id, nextState);
        LOG.info("Змінено стан замовлення: " + current.getState() + " -> " + nextState);
    }
}
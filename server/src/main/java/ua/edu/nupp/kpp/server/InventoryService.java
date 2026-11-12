package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class InventoryService {
    private static final Logger LOG = Logger.getLogger(InventoryService.class.getName());
    private final InventoryDAO dao;
    private final ProductDAO productDAO;

    public InventoryService() throws SQLException {
        this.dao = new InventoryDAO(DBUtil.getConnection());
        this.productDAO = new ProductDAO(DBUtil.getConnection());
    }

    public List<Inventory> getAll() throws SQLException {
        return dao.getAll();
    }

    public Inventory getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Inventory inv) throws SQLException {
        if (inv == null) {
            throw new IllegalArgumentException("Запас не може бути null");
        }
        if (inv.getCurrentStock() < 0) {
            throw new IllegalArgumentException("Поточний запас не може бути від'ємним");
        }
        if (inv.getMinRequiredStock() < 0) {
            throw new IllegalArgumentException("Мінімальний необхідний запас не може бути від'ємним");
        }
        if (inv.getConsumptionIntensity() < 0) {
            throw new IllegalArgumentException("Інтенсивність споживання не може бути від'ємною");
        }

        Product product = productDAO.getById(inv.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Товар з ID " + inv.getProductId() + " не існує в базі");
        }

        dao.create(inv);
        LOG.info("Додано запас");
    }

    public void update(Inventory inv) throws SQLException {
        if (inv == null) {
            throw new IllegalArgumentException("Запас не може бути null");
        }
        if (inv.getCurrentStock() < 0) {
            throw new IllegalArgumentException("Поточний запас не може бути від'ємним");
        }
        if (inv.getMinRequiredStock() < 0) {
            throw new IllegalArgumentException("Мінімальний необхідний запас не може бути від'ємним");
        }
        if (inv.getConsumptionIntensity() < 0) {
            throw new IllegalArgumentException("Інтенсивність споживання не може бути від'ємною");
        }

        Product product = productDAO.getById(inv.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Товар з ID " + inv.getProductId() + " не існує в базі");
        }

        dao.update(inv);
        LOG.info("Оновлено запас");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено запас");
    }
}
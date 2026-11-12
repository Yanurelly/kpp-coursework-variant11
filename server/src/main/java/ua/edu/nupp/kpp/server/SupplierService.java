package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class SupplierService {
    private static final Logger LOG = Logger.getLogger(SupplierService.class.getName());
    private final SupplierDAO dao;

    public SupplierService() throws SQLException {
        this.dao = new SupplierDAO(DBUtil.getConnection());
    }

    public List<Supplier> getAll() throws SQLException {
        return dao.getAll();
    }

    public Supplier getById(Long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Supplier s) throws SQLException {
        if (s == null) {
            throw new IllegalArgumentException("Постачальник не може бути null");
        }
        if (s.getName() == null || s.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Назва постачальника не може бути порожньою");
        }
        if (s.getContactInfo() == null || s.getContactInfo().trim().isEmpty()) {
            throw new IllegalArgumentException("Контактна інформація не може бути порожньою");
        }
        if (s.getDelayRiskScore() < 0) {
            throw new IllegalArgumentException("Показник ризику затримки не може бути від'ємним");
        }
        dao.create(s);
        LOG.info("Додано постачальника");
    }

    public void update(Supplier s) throws SQLException {
        if (s == null) {
            throw new IllegalArgumentException("Постачальник не може бути null");
        }
        if (s.getName() == null || s.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Назва постачальника не може бути порожньою");
        }
        if (s.getContactInfo() == null || s.getContactInfo().trim().isEmpty()) {
            throw new IllegalArgumentException("Контактна інформація не може бути порожньою");
        }
        if (s.getDelayRiskScore() < 0) {
            throw new IllegalArgumentException("Показник ризику затримки не може бути від'ємним");
        }
        dao.update(s);
        LOG.info("Оновлено постачальника");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено постачальника");
    }
}
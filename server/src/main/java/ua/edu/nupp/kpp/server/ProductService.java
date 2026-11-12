package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ProductService {
    private static final Logger LOG = Logger.getLogger(ProductService.class.getName());
    private final ProductDAO dao;

    public ProductService() throws SQLException {
        this.dao = new ProductDAO(DBUtil.getConnection());
    }

    public List<Product> getAll() throws SQLException {
        return dao.getAll();
    }

    public Product getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Product p) throws SQLException {
        if (p == null) {
            throw new IllegalArgumentException("Товар не може бути null");
        }
        if (p.getSku() == null || p.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("Артикул (SKU) товару не може бути порожнім");
        }
        if (p.getName() == null || p.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Назва товару не може бути порожньою");
        }
        if (p.getCategory() == null || p.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Категорія товару не може бути порожньою");
        }
        dao.create(p);
        LOG.info("Додано товар");
    }

    public void update(Product p) throws SQLException {
        if (p == null) {
            throw new IllegalArgumentException("Товар не може бути null");
        }
        if (p.getSku() == null || p.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("Артикул (SKU) товару не може бути порожнім");
        }
        if (p.getName() == null || p.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Назва товару не може бути порожньою");
        }
        if (p.getCategory() == null || p.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Категорія товару не може бути порожньою");
        }
        dao.update(p);
        LOG.info("Оновлено товар");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено товар");
    }
}
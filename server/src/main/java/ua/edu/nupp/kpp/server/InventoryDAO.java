package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {
    private final Connection connection;

    public InventoryDAO(Connection connection) {
        this.connection = connection;
    }

    public void create(Inventory inventory) throws SQLException {
        String sql = "INSERT INTO inventory (product_id, current_stock, min_required_stock, consumption_intensity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, inventory.getProductId());
            stmt.setDouble(2, inventory.getCurrentStock());
            stmt.setDouble(3, inventory.getMinRequiredStock());
            stmt.setDouble(4, inventory.getConsumptionIntensity());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    inventory.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public Inventory getById(long id) throws SQLException {
        String sql = "SELECT * FROM inventory WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Inventory(
                            rs.getLong("id"),
                            rs.getLong("product_id"),
                            rs.getDouble("current_stock"),
                            rs.getDouble("min_required_stock"),
                            rs.getDouble("consumption_intensity")
                    );
                }
            }
        }
        return null;
    }

    public List<Inventory> getAll() throws SQLException {
        List<Inventory> inventories = new ArrayList<>();
        String sql = "SELECT * FROM inventory";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                inventories.add(new Inventory(
                        rs.getLong("id"),
                        rs.getLong("product_id"),
                        rs.getDouble("current_stock"),
                        rs.getDouble("min_required_stock"),
                        rs.getDouble("consumption_intensity")
                ));
            }
        }
        return inventories;
    }

    public void update(Inventory inventory) throws SQLException {
        String sql = "UPDATE inventory SET product_id = ?, current_stock = ?, min_required_stock = ?, consumption_intensity = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, inventory.getProductId());
            stmt.setDouble(2, inventory.getCurrentStock());
            stmt.setDouble(3, inventory.getMinRequiredStock());
            stmt.setDouble(4, inventory.getConsumptionIntensity());
            stmt.setLong(5, inventory.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM inventory WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
}
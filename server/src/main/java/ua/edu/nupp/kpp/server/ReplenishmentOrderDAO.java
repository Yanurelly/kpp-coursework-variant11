package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReplenishmentOrderDAO {
    private final Connection connection;

    public ReplenishmentOrderDAO(Connection connection) {
        this.connection = connection;
    }

    public void create(ReplenishmentOrder order) throws SQLException {
        String sql = "INSERT INTO replenishment_order (inventory_id, supplier_id, state, f1, f2, f3, risk_index) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, order.getInventoryId());
            stmt.setLong(2, order.getSupplierId());
            stmt.setString(3, order.getState().name());
            stmt.setDouble(4, order.getF1());
            stmt.setDouble(5, order.getF2());
            stmt.setDouble(6, order.getF3());
            stmt.setDouble(7, order.getRiskIndex());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public ReplenishmentOrder getById(long id) throws SQLException {
        String sql = "SELECT * FROM replenishment_order WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ReplenishmentOrder(
                            rs.getLong("id"),
                            rs.getLong("inventory_id"),
                            rs.getLong("supplier_id"),
                            OrderState.valueOf(rs.getString("state")),
                            rs.getDouble("f1"),
                            rs.getDouble("f2"),
                            rs.getDouble("f3"),
                            rs.getDouble("risk_index")
                    );
                }
            }
        }
        return null;
    }

    public List<ReplenishmentOrder> getAll() throws SQLException {
        List<ReplenishmentOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM replenishment_order";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(new ReplenishmentOrder(
                        rs.getLong("id"),
                        rs.getLong("inventory_id"),
                        rs.getLong("supplier_id"),
                        OrderState.valueOf(rs.getString("state")),
                        rs.getDouble("f1"),
                        rs.getDouble("f2"),
                        rs.getDouble("f3"),
                        rs.getDouble("risk_index")
                ));
            }
        }
        return orders;
    }

    public void update(ReplenishmentOrder order) throws SQLException {
        String sql = "UPDATE replenishment_order SET inventory_id = ?, supplier_id = ?, state = ?, f1 = ?, f2 = ?, f3 = ?, risk_index = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, order.getInventoryId());
            stmt.setLong(2, order.getSupplierId());
            stmt.setString(3, order.getState().name());
            stmt.setDouble(4, order.getF1());
            stmt.setDouble(5, order.getF2());
            stmt.setDouble(6, order.getF3());
            stmt.setDouble(7, order.getRiskIndex());
            stmt.setLong(8, order.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM replenishment_order WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    public void updateState(long id, OrderState state) throws SQLException {
        String sql = "UPDATE replenishment_order SET state = ? WHERE id = ?";

        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, state.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }
}
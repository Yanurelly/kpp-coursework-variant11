package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class ReplenishmentOrderServlet extends HttpServlet {
    private final ReplenishmentOrderService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public ReplenishmentOrderServlet() {
        try {
            this.service = new ReplenishmentOrderService();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка ініціалізації ReplenishmentOrderService", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("add".equals(action)) {
                ReplenishmentOrder order = new ReplenishmentOrder();
                order.setInventoryId(parseLong(req.getParameter("inventoryId")));
                order.setSupplierId(parseLong(req.getParameter("supplierId")));
                order.setState(OrderState.CREATED);
                service.add(order);
            } else if ("update".equals(action)) {
                long orderId = id(req);
                ReplenishmentOrder existing = service.getById(orderId);
                if (existing == null) {
                    throw new IllegalArgumentException("Замовлення на поповнення не знайдено");
                }
                ReplenishmentOrder order = new ReplenishmentOrder();
                order.setId(orderId);
                order.setInventoryId(parseLong(req.getParameter("inventoryId")));
                order.setSupplierId(parseLong(req.getParameter("supplierId")));
                order.setState(existing.getState());
                service.update(order);
            } else if ("transition".equals(action)) {
                String stateVal = req.getParameter("state");
                if (stateVal == null || stateVal.trim().isEmpty()) {
                    throw new IllegalArgumentException("Стан замовлення не може бути порожнім");
                }
                service.changeState(id(req), OrderState.valueOf(stateVal.trim()));
            } else if ("delete".equals(action)) {
                service.delete(id(req));
            } else {
                throw new IllegalArgumentException("Невідома операція");
            }
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private long id(HttpServletRequest req) {
        return parseLong(req.getParameter("id"));
    }

    private long parseLong(String val) {
        if (val == null || val.trim().isEmpty()) {
            throw new IllegalArgumentException("Некоректний ідентифікатор");
        }
        return Long.parseLong(val.trim());
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp, Exception e) throws IOException {
        if (e instanceof IllegalArgumentException) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        writeJson(resp, Map.of("error", e.getMessage() == null ? "Помилка" : e.getMessage()));
    }
}
package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class InventoryServlet extends HttpServlet {
    private final InventoryService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public InventoryServlet() {
        try {
            this.service = new InventoryService();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка ініціалізації InventoryService", e);
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
                Inventory inv = new Inventory();
                inv.setProductId(parseLong(req.getParameter("productId")));
                inv.setCurrentStock(parseDouble(req.getParameter("currentStock")));
                inv.setMinRequiredStock(parseDouble(req.getParameter("minRequiredStock")));
                inv.setConsumptionIntensity(parseDouble(req.getParameter("consumptionIntensity")));
                service.add(inv);
            } else if ("update".equals(action)) {
                Inventory inv = new Inventory();
                inv.setId(id(req));
                inv.setProductId(parseLong(req.getParameter("productId")));
                inv.setCurrentStock(parseDouble(req.getParameter("currentStock")));
                inv.setMinRequiredStock(parseDouble(req.getParameter("minRequiredStock")));
                inv.setConsumptionIntensity(parseDouble(req.getParameter("consumptionIntensity")));
                service.update(inv);
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

    private double parseDouble(String val) {
        if (val == null || val.trim().isEmpty()) {
            throw new IllegalArgumentException("Числове поле не може бути порожнім");
        }
        return Double.parseDouble(val.trim());
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
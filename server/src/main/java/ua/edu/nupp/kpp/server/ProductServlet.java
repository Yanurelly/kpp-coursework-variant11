package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class ProductServlet extends HttpServlet {
    private final ProductService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public ProductServlet() {
        try {
            this.service = new ProductService();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка ініціалізації ProductService", e);
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
                Product p = new Product();
                p.setSku(req.getParameter("sku"));
                p.setName(req.getParameter("name"));
                p.setCategory(req.getParameter("category"));
                service.add(p);
            } else if ("update".equals(action)) {
                Product p = new Product();
                p.setId(id(req));
                p.setSku(req.getParameter("sku"));
                p.setName(req.getParameter("name"));
                p.setCategory(req.getParameter("category"));
                service.update(p);
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
        String val = req.getParameter("id");
        if (val == null || val.trim().isEmpty()) {
            throw new IllegalArgumentException("Некоректний ID");
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
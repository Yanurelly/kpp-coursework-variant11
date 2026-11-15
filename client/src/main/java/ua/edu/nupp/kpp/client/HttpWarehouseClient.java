package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class HttpWarehouseClient {
    private static final String BASE = "http://localhost:8080/warehouse-server";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Product> getProducts() {
        return Arrays.asList(read(get("/products"), Product[].class));
    }

    public void addProduct(Product p) {
        post("/products", "action=add&sku=" + enc(p.getSku())
                + "&name=" + enc(p.getName())
                + "&category=" + enc(p.getCategory()));
    }

    public void updateProduct(Product p) {
        post("/products", "action=update&id=" + p.getId()
                + "&sku=" + enc(p.getSku())
                + "&name=" + enc(p.getName())
                + "&category=" + enc(p.getCategory()));
    }

    public void deleteProduct(long id) {
        post("/products", "action=delete&id=" + id);
    }


    public List<Supplier> getSuppliers() {
        return Arrays.asList(read(get("/suppliers"), Supplier[].class));
    }

    public void addSupplier(Supplier s) {
        post("/suppliers", supplierForm("add", s));
    }

    public void updateSupplier(Supplier s) {
        post("/suppliers", supplierForm("update", s) + "&id=" + s.getId());
    }

    public void deleteSupplier(long id) {
        post("/suppliers", "action=delete&id=" + id);
    }

    public List<Inventory> getInventories() {
        return Arrays.asList(read(get("/inventories"), Inventory[].class));
    }


    public void addInventory(Inventory inv) {
        post("/inventories", inventoryForm("add", inv));
    }

    public void updateInventory(Inventory inv) {
        post("/inventories", inventoryForm("update", inv) + "&id=" + inv.getId());
    }

    public void deleteInventory(long id) {
        post("/inventories", "action=delete&id=" + id);
    }

    // --- ReplenishmentOrder CRUD & Lifecycle ---
    public List<ReplenishmentOrder> getOrders() {
        return Arrays.asList(read(get("/replenishment-orders"), ReplenishmentOrder[].class));
    }

    public void addOrder(long inventoryId, long supplierId) {
        post("/replenishment-orders", "action=add&inventoryId=" + inventoryId + "&supplierId=" + supplierId);
    }

    public void updateOrder(long id, long inventoryId, long supplierId) {
        post("/replenishment-orders", "action=update&id=" + id + "&inventoryId=" + inventoryId + "&supplierId=" + supplierId);
    }

    public void changeOrderState(long id, OrderState state) {
        post("/replenishment-orders", "action=transition&id=" + id + "&state=" + (state == null ? "" : state.name()));
    }

    public void deleteOrder(long id) {
        post("/replenishment-orders", "action=delete&id=" + id);
    }

    private String supplierForm(String action, Supplier s) {
        return "action=" + action
                + "&name=" + enc(s.getName())
                + "&contactInfo=" + enc(s.getContactInfo())
                + "&delayRiskScore=" + s.getDelayRiskScore();
    }

    private String inventoryForm(String action, Inventory inv) {
        return "action=" + action
                + "&productId=" + inv.getProductId()
                + "&currentStock=" + inv.getCurrentStock()
                + "&minRequiredStock=" + inv.getMinRequiredStock()
                + "&consumptionIntensity=" + inv.getConsumptionIntensity();
    }

    private String get(String path) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path)).GET().build();
        return send(req);
    }

    private String post(String path, String form) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
                .build();
        return send(req);
    }

    private String send(HttpRequest req) {
        try {
            HttpResponse<String> r = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (r.statusCode() >= 400) {
                String errorMsg = extractErrorMessage(r.body());
                throw new IllegalArgumentException(errorMsg);
            }
            return r.body();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Помилка HTTP: " + e.getMessage(), e);
        }
    }

    private String extractErrorMessage(String body) {
        try {
            JsonNode node = mapper.readTree(body);
            if (node.has("error")) {
                return node.get("error").asText();
            }
        } catch (Exception ignored) {
        }
        return body;
    }

    private <T> T read(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Помилка JSON: " + e.getMessage(), e);
        }
    }

    private String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
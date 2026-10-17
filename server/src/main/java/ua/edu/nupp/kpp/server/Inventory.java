package ua.edu.nupp.kpp.server;

public class Inventory {
    private Long id;
    private long productId;
    private double currentStock;
    private double minRequiredStock;
    private double consumptionIntensity;

    public Inventory() {}

    public Inventory(Long id, long productId, double currentStock, double minRequiredStock, double consumptionIntensity) {
        this.id = id;
        this.productId = productId;
        this.currentStock = currentStock;
        this.minRequiredStock = minRequiredStock;
        this.consumptionIntensity = consumptionIntensity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public double getCurrentStock() { return currentStock; }
    public void setCurrentStock(double currentStock) { this.currentStock = currentStock; }
    public double getMinRequiredStock() { return minRequiredStock; }
    public void setMinRequiredStock(double minRequiredStock) { this.minRequiredStock = minRequiredStock; }
    public double getConsumptionIntensity() { return consumptionIntensity; }
    public void setConsumptionIntensity(double consumptionIntensity) { this.consumptionIntensity = consumptionIntensity; }
}
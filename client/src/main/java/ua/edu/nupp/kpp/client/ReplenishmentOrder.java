package ua.edu.nupp.kpp.client;

public class ReplenishmentOrder {
    private Long id;
    private long inventoryId;
    private long supplierId;
    private OrderState state;
    private double f1;
    private double f2;
    private double f3;
    private double riskIndex;

    public ReplenishmentOrder() {}

    public ReplenishmentOrder(Long id, long inventoryId, long supplierId, OrderState state, double f1, double f2, double f3, double riskIndex) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.supplierId = supplierId;
        this.state = state;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.riskIndex = riskIndex;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getInventoryId() { return inventoryId; }
    public void setInventoryId(long inventoryId) { this.inventoryId = inventoryId; }
    public long getSupplierId() { return supplierId; }
    public void setSupplierId(long supplierId) { this.supplierId = supplierId; }
    public OrderState getState() { return state; }
    public void setState(OrderState state) { this.state = state; }
    public double getF1() { return f1; }
    public void setF1(double f1) { this.f1 = f1; }
    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }
    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }
    public double getRiskIndex() { return riskIndex; }
    public void setRiskIndex(double riskIndex) { this.riskIndex = riskIndex; }
}
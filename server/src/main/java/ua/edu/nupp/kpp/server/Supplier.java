package ua.edu.nupp.kpp.server;

public class Supplier {
    private Long id;
    private String name;
    private String contactInfo;
    private double delayRiskScore;

    public Supplier() {}

    public Supplier(Long id, String name, String contactInfo, double delayRiskScore) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.delayRiskScore = delayRiskScore;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public double getDelayRiskScore() { return delayRiskScore; }
    public void setDelayRiskScore(double delayRiskScore) { this.delayRiskScore = delayRiskScore; }
}
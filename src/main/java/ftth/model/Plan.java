package ftth.model;

public class Plan {

    private long id;
    private String name;
    private String speed;
    private String dataLimit;
    private int otts;
    private double price;
    private String oltType;
    private boolean active;

<<<<<<< Updated upstream
    public Plan(long id, String name, String speed, String dataLimit,
                int otts, double price, String oltType, boolean active) {
        this.id = id;
        this.name = name;
        this.speed = speed;
        this.dataLimit = dataLimit;
        this.otts = otts;
        this.price = price;
        this.oltType = oltType;
        this.active = active;
    }

    public Plan(String name, String speed, String dataLimit,
                int otts, double price, String oltType, boolean active) {
        this(0L, name, speed, dataLimit, otts, price, oltType, active);
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getSpeed() { return speed; }
    public String getDataLimit() { return dataLimit; }
    public int getOtts() { return otts; }
    public double getPrice() { return price; }
    public String getOltType() { return oltType; }
    public boolean isActive() { return active; }

    public void setName(String name) { this.name = name; }
    public void setSpeed(String speed) { this.speed = speed; }
    public void setDataLimit(String dataLimit) { this.dataLimit = dataLimit; }
    public void setOtts(int otts) { this.otts = otts; }
    public void setPrice(double price) { this.price = price; }
    public void setOltType(String oltType) { this.oltType = oltType; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " | " + speed + " | " + dataLimit +
               " | " + otts + " OTTs | INR " + price + "/mo | " +
               oltType + " [" + (active ? "Active" : "Disabled") + "]";
=======
    private Long planId;
    private String planName;
    private String speedLabel;
    private String dataLimitLabel;
    private Integer ottCount;
    private BigDecimal monthlyPrice;
    private String oltType;
    private boolean active;
    private LocalDateTime createdAt;


    // ===============================
    // Constructors
    // ===============================

    public Plan() {}

    public Plan(String planName, String speedLabel, String dataLimitLabel,
                Integer ottCount, BigDecimal monthlyPrice, String oltType) {
        this.planName = planName;
        this.speedLabel = speedLabel;
        this.dataLimitLabel = dataLimitLabel;
        this.ottCount = ottCount;
        this.monthlyPrice = monthlyPrice;
        this.oltType = oltType;
        this.active = true;
    }

    public Plan(String planName, String speedLabel, String dataLimitLabel,
                Integer ottCount, BigDecimal monthlyPrice, String oltType, boolean active) {
        this.planName = planName;
        this.speedLabel = speedLabel;
        this.dataLimitLabel = dataLimitLabel;
        this.ottCount = ottCount;
        this.monthlyPrice = monthlyPrice;
        this.oltType = oltType;
        this.active = active;
    }

    public Plan(Long planId, String planName, String speedLabel, String dataLimitLabel,
                Integer ottCount, BigDecimal monthlyPrice, String oltType,
                boolean active, LocalDateTime createdAt) {
        this.planId = planId;
        this.planName = planName;
        this.speedLabel = speedLabel;
        this.dataLimitLabel = dataLimitLabel;
        this.ottCount = ottCount;
        this.monthlyPrice = monthlyPrice;
        this.oltType = oltType;
        this.active = active;
        this.createdAt = createdAt;
    }

    // ===============================
    // Getters and Setters
    // ===============================

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public String getSpeedLabel() { return speedLabel; }
    public void setSpeedLabel(String speedLabel) { this.speedLabel = speedLabel; }
    public String getDataLimitLabel() { return dataLimitLabel; }
    public void setDataLimitLabel(String dataLimitLabel) { this.dataLimitLabel = dataLimitLabel; }
    public Integer getOttCount() { return ottCount; }
    public void setOttCount(Integer ottCount) { this.ottCount = ottCount; }
    public BigDecimal getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }
    public String getOltType() { return oltType; }
    public void setOltType(String oltType) { this.oltType = oltType; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }


    // ===============================
    // toString (debugging)
    // ===============================

    @Override
    public String toString() {
        return "Plan{" +
                "planId=" + planId +
                ", planName='" + planName + '\'' +
                ", speedLabel='" + speedLabel + '\'' +
                ", dataLimitLabel='" + dataLimitLabel + '\'' +
                ", ottCount=" + ottCount +
                ", monthlyPrice=" + monthlyPrice +
                ", oltType='" + oltType + '\'' +
                ", active=" + active +
                '}';
>>>>>>> Stashed changes
    }
}

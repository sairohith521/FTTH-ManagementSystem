package ftth.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain model representing an FTTH plan.
 * Maps to the 'plans' table.
 */
public class Plan {

    // ===============================
    // Fields (DB columns)
    // ===============================

    private Long planId;                // plan_id (PK)
    private String planName;            // plan_name
    private String speedLabel;          // speed_label
    private String dataLimitLabel;      // data_limit_label
    private Integer ottCount;           // ott_count
    private BigDecimal monthlyPrice;    // monthly_price
    private String oltType;             // olt_type
    private boolean active;             // is_active
    private LocalDateTime createdAt;    // created_at

// ✅ derived / runtime-only
    private int customerCount;


    // ===============================
    // Constructors
    // ===============================

    // No-args constructor (required by frameworks)
    public Plan() {
    }

    // Constructor for NEW plan (before DB insert)
    public Plan(String planName,
                String speedLabel,
                String dataLimitLabel,
                Integer ottCount,
                BigDecimal monthlyPrice,
                String oltType) {
        this.planName = planName;
        this.speedLabel = speedLabel;
        this.dataLimitLabel = dataLimitLabel;
        this.ottCount = ottCount;
        this.monthlyPrice = monthlyPrice;
        this.oltType = oltType;
        this.active = true;
    }

    // Constructor for NEW plan with explicit active flag
    public Plan(String planName,
                String speedLabel,
                String dataLimitLabel,
                Integer ottCount,
                BigDecimal monthlyPrice,
                String oltType,
                boolean active) {
        this.planName = planName;
        this.speedLabel = speedLabel;
        this.dataLimitLabel = dataLimitLabel;
        this.ottCount = ottCount;
        this.monthlyPrice = monthlyPrice;
        this.oltType = oltType;
        this.active = active;
    }

    // Full constructor (used when reading from DB)
    public Plan(Long planId,
                String planName,
                String speedLabel,
                String dataLimitLabel,
                Integer ottCount,
                BigDecimal monthlyPrice,
                String oltType,
                boolean active,
                LocalDateTime createdAt) {
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

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getSpeedLabel() {
        return speedLabel;
    }

    public void setSpeedLabel(String speedLabel) {
        this.speedLabel = speedLabel;
    }

    public String getDataLimitLabel() {
        return dataLimitLabel;
    }

    public void setDataLimitLabel(String dataLimitLabel) {
        this.dataLimitLabel = dataLimitLabel;
    }

    public Integer getOttCount() {
        return ottCount;
    }

    public void setOttCount(Integer ottCount) {
        this.ottCount = ottCount;
    }

    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public String getOltType() {
        return oltType;
    }

    public void setOltType(String oltType) {
        this.oltType = oltType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public int getCustomerCount() { return customerCount; }
    public void setCustomerCount(int customerCount) { this.customerCount = customerCount; }


    // ===============================
    // Domain helper methods
    // ===============================

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }


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
                ", createdAt=" + createdAt +
                '}';
    }
}
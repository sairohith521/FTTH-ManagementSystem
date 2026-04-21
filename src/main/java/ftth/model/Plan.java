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
    private String planCode;            // plan_code
    private String planName;            // plan_name
    private String speedLabel;          // speed_label
    private String dataLimitLabel;      // data_limit_label
    private Integer ottCount;           // ott_count
    private BigDecimal monthlyPrice;    // monthly_price
    private String oltType;             // olt_type
    private boolean active;             // is_active
    private LocalDateTime createdAt;    // created_at


    // ===============================
    // Constructors
    // ===============================

    // No-args constructor (required by frameworks)
    public Plan() {
    }

    // Constructor for NEW plan (before DB insert)
    public Plan(String planCode,
                String planName,
                String speedLabel,
                String dataLimitLabel,
                Integer ottCount,
                BigDecimal monthlyPrice,
                String oltType) {
        this.planCode = planCode;
        this.planName = planName;
        this.speedLabel = speedLabel;
        this.dataLimitLabel = dataLimitLabel;
        this.ottCount = ottCount;
        this.monthlyPrice = monthlyPrice;
        this.oltType = oltType;
        this.active = true;
    }

    // Full constructor (used when reading from DB)
    public Plan(Long planId,
                String planCode,
                String planName,
                String speedLabel,
                String dataLimitLabel,
                Integer ottCount,
                BigDecimal monthlyPrice,
                String oltType,
                boolean active,
                LocalDateTime createdAt) {
        this.planId = planId;
        this.planCode = planCode;
        this.planName = planName;
        this.speedLabel = speedLabel;
        this.dataLimitLabel = dataLimitLabel;
        this.ottCount = ottCount;
        this.monthlyPrice = monthlyPrice;
        this.oltType = oltType;
        this.active = active;
        this.createdAt = createdAt;
    }
// ✅ Constructor for NEW plan creation
public Plan(String planCode,
            String planName,
            String speedLabel,
            String dataLimitLabel,
            Integer ottCount,
            BigDecimal monthlyPrice,
            String oltType,
            boolean active) {

    this.planCode = planCode;
    this.planName = planName;
    this.speedLabel = speedLabel;
    this.dataLimitLabel = dataLimitLabel;
    this.ottCount = ottCount;
    this.monthlyPrice = monthlyPrice;
    this.oltType = oltType;
    this.active = active;
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

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
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
                ", planCode='" + planCode + '\'' +
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
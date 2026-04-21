package ftth.model;

import java.time.LocalDateTime;

/**
 * Domain model representing an OLT (Optical Line Terminal).
 * Maps to the 'olts' table.
 */
public class Olt {

    // ===============================
    // Fields (DB columns)
    // ===============================

    private Long oltId;               // olt_id (PK)
    private String oltCode;            // olt_code
    private Long serviceAreaId;        // service_area_id (FK)
    private String oltType;            // olt_type
    private boolean active;            // is_active
    private LocalDateTime createdAt;   // created_at


    // ===============================
    // Constructors
    // ===============================

    // No-args constructor (required by frameworks)
    public Olt() {
    }

    // Constructor for NEW OLT (before DB insert)
    public Olt(String oltCode,
               Long serviceAreaId,
               String oltType) {
        this.oltCode = oltCode;
        this.serviceAreaId = serviceAreaId;
        this.oltType = oltType;
        this.active = true;
    }

    // Full constructor (used when reading from DB)
    public Olt(Long oltId,
               String oltCode,
               Long serviceAreaId,
               String oltType,
               boolean active,
               LocalDateTime createdAt) {
        this.oltId = oltId;
        this.oltCode = oltCode;
        this.serviceAreaId = serviceAreaId;
        this.oltType = oltType;
        this.active = active;
        this.createdAt = createdAt;
    }


    // ===============================
    // Getters and Setters
    // ===============================

    public Long getOltId() {
        return oltId;
    }

    public void setOltId(Long oltId) {
        this.oltId = oltId;
    }

    public String getOltCode() {
        return oltCode;
    }

    public void setOltCode(String oltCode) {
        this.oltCode = oltCode;
    }

    public Long getServiceAreaId() {
        return serviceAreaId;
    }

    public void setServiceAreaId(Long serviceAreaId) {
        this.serviceAreaId = serviceAreaId;
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
        return "Olt{" +
                "oltId=" + oltId +
                ", oltCode='" + oltCode + '\'' +
                ", serviceAreaId=" + serviceAreaId +
                ", oltType='" + oltType + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}
package ftth.model;

import java.time.LocalDateTime;

/**
 * Domain model representing a service area.
 * Maps to the 'service_areas' table.
 */
public class ServiceArea {

    // ===============================
    // Fields (DB columns)
    // ===============================

    private Long serviceAreaId;        // service_area_id (PK)
    private String pincode;            // pincode
    private boolean active;            // is_active
    private LocalDateTime createdAt;   // created_at


    // ===============================
    // Constructors
    // ===============================

    // No-args constructor (required by frameworks)
    public ServiceArea() {
    }

    // Constructor for NEW service area (before DB insert)
    public ServiceArea(String pincode) {
        this.pincode = pincode;
        this.active = true;
    }

    // Full constructor (used when reading from DB)
    public ServiceArea(Long serviceAreaId,
                       String pincode,
                       boolean active,
                       LocalDateTime createdAt) {
        this.serviceAreaId = serviceAreaId;
        this.pincode = pincode;
        this.active = active;
        this.createdAt = createdAt;
    }


    // ===============================
    // Getters and Setters
    // ===============================

    public Long getServiceAreaId() {
        return serviceAreaId;
    }

    public void setServiceAreaId(Long serviceAreaId) {
        this.serviceAreaId = serviceAreaId;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
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
        return "ServiceArea{" +
                "serviceAreaId=" + serviceAreaId +
                ", pincode='" + pincode + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}
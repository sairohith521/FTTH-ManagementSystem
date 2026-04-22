package ftth.model;

import java.time.LocalDateTime;

public class Olt {

    private Long oltId;
    private String oltCode;
    private Long serviceAreaId;
    private String oltType;
    private boolean active;
    private LocalDateTime createdAt;

    public Olt() {
    }

    public Olt(String oltCode, Long serviceAreaId, String oltType) {
        this.oltCode = oltCode;
        this.serviceAreaId = serviceAreaId;
        this.oltType = oltType;
        this.active = true;
    }

    public Olt(Long oltId, String oltCode, Long serviceAreaId, String oltType,
               boolean active, LocalDateTime createdAt) {
        this.oltId = oltId;
        this.oltCode = oltCode;
        this.serviceAreaId = serviceAreaId;
        this.oltType = oltType;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getOltId() { return oltId; }
    public void setOltId(Long oltId) { this.oltId = oltId; }
    public String getOltCode() { return oltCode; }
    public void setOltCode(String oltCode) { this.oltCode = oltCode; }
    public Long getServiceAreaId() { return serviceAreaId; }
    public void setServiceAreaId(Long serviceAreaId) { this.serviceAreaId = serviceAreaId; }
    public String getOltType() { return oltType; }
    public void setOltType(String oltType) { this.oltType = oltType; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Olt{oltId=" + oltId + ", oltCode='" + oltCode + "', oltType='" + oltType + "', active=" + active + "}";
    }
}

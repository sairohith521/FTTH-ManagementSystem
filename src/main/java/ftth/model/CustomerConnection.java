package ftth.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ftth.model.enums.ConnectionStatus;

/**
 * Domain model representing a customer connection.
 * Maps to 'customer_connections' table.
 */
public class CustomerConnection {

    // ===============================
    // Fields (DB columns)
    // ===============================

    private Long connectionId;              // connection_id (PK)
    private Long customerId;                // customer_id (FK)
    private Long planId;                    // plan_id (FK)
    private Long portId;                    // port_id (FK)
    private Long serviceAreaId;             // service_area_id (FK)

    private ConnectionStatus connectionStatus; // connection_status
    private LocalDate activatedOn;           // activated_on
    private LocalDate disconnectedOn;         // disconnected_on

    private Integer billingDay;              // billing_day
    private Long createdBy;                  // created_by (user_id)
    private Long updatedBy;                  // updated_by (user_id)

    private LocalDateTime createdAt;          // created_at
    private LocalDateTime updatedAt;          // updated_at


    // ===============================
    // Constructors
    // ===============================

    // No-args constructor (required by frameworks)
    public CustomerConnection() {
    }

    // Constructor for NEW connection (before DB insert)
    public CustomerConnection(Long customerId,
                              Long planId,
                              Long portId,
                              Long serviceAreaId,
                              LocalDate activatedOn,
                              Integer billingDay,
                              Long createdBy) {

        this.customerId = customerId;
        this.planId = planId;
        this.portId = portId;
        this.serviceAreaId = serviceAreaId;
        this.activatedOn = activatedOn;
        this.billingDay = billingDay;
        this.createdBy = createdBy;
        this.connectionStatus = ConnectionStatus.ACTIVE;
    }

    // Full constructor (used when reading from DB)
    public CustomerConnection(Long connectionId,
                              Long customerId,
                              Long planId,
                              Long portId,
                              Long serviceAreaId,
                              ConnectionStatus connectionStatus,
                              LocalDate activatedOn,
                              LocalDate disconnectedOn,
                              Integer billingDay,
                              Long createdBy,
                              Long updatedBy,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {

        this.connectionId = connectionId;
        this.customerId = customerId;
        this.planId = planId;
        this.portId = portId;
        this.serviceAreaId = serviceAreaId;
        this.connectionStatus = connectionStatus;
        this.activatedOn = activatedOn;
        this.disconnectedOn = disconnectedOn;
        this.billingDay = billingDay;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    // ===============================
    // Getters and Setters
    // ===============================

    public Long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Long connectionId) {
        this.connectionId = connectionId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getPortId() {
        return portId;
    }

    public void setPortId(Long portId) {
        this.portId = portId;
    }

    public Long getServiceAreaId() {
        return serviceAreaId;
    }

    public void setServiceAreaId(Long serviceAreaId) {
        this.serviceAreaId = serviceAreaId;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public LocalDate getActivatedOn() {
        return activatedOn;
    }

    public void setActivatedOn(LocalDate activatedOn) {
        this.activatedOn = activatedOn;
    }

    public LocalDate getDisconnectedOn() {
        return disconnectedOn;
    }

    public void setDisconnectedOn(LocalDate disconnectedOn) {
        this.disconnectedOn = disconnectedOn;
    }

    public Integer getBillingDay() {
        return billingDay;
    }

    public void setBillingDay(Integer billingDay) {
        this.billingDay = billingDay;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    // ===============================
    // Domain helper methods
    // ===============================

    public boolean isActive() {
        return ConnectionStatus.ACTIVE.equals(this.connectionStatus);
    }

    public void disconnect(LocalDate disconnectDate, Long updatedBy) {
        this.connectionStatus = ConnectionStatus.DISCONNECTED;
        this.disconnectedOn = disconnectDate;
        this.updatedBy = updatedBy;
    }


    // ===============================
    // toString (debugging)
    // ===============================

    @Override
    public String toString() {
        return "CustomerConnection{" +
                "connectionId=" + connectionId +
                ", customerId=" + customerId +
                ", planId=" + planId +
                ", portId=" + portId +
                ", serviceAreaId=" + serviceAreaId +
                ", connectionStatus=" + connectionStatus +
                ", activatedOn=" + activatedOn +
                ", disconnectedOn=" + disconnectedOn +
                ", billingDay=" + billingDay +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
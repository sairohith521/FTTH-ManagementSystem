package ftth.model;

import java.sql.Date;
import java.sql.Timestamp;

public class CustomerConnection {

    private long connectionId;
    private long customerId;
    private long planId;
    private long portId;
    private long serviceAreaId;

    private String connectionStatus;
    private Date activatedOn;
    private Date disconnectedOn;
    private int billingDay;

    private long createdBy;
    private long updatedBy;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    // 🔹 Default Constructor
    public CustomerConnection() {
    }

    // 🔹 Parameterized Constructor
    public CustomerConnection(long connectionId, long customerId, long planId,
                              long portId, long serviceAreaId, String connectionStatus,
                              Date activatedOn, Date disconnectedOn, int billingDay,
                              long createdBy, long updatedBy,
                              Timestamp createdAt, Timestamp updatedAt) {

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

    // 🔹 Getters and Setters

    public long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public long getPortId() {
        return portId;
    }

    public void setPortId(long portId) {
        this.portId = portId;
    }

    public long getServiceAreaId() {
        return serviceAreaId;
    }

    public void setServiceAreaId(long serviceAreaId) {
        this.serviceAreaId = serviceAreaId;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public Date getActivatedOn() {
        return activatedOn;
    }

    public void setActivatedOn(Date activatedOn) {
        this.activatedOn = activatedOn;
    }

    public Date getDisconnectedOn() {
        return disconnectedOn;
    }

    public void setDisconnectedOn(Date disconnectedOn) {
        this.disconnectedOn = disconnectedOn;
    }

    public int getBillingDay() {
        return billingDay;
    }

    public void setBillingDay(int billingDay) {
        this.billingDay = billingDay;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // 🔹 toString()

    @Override
    public String toString() {
        return "CustomerConnection{" +
                "connectionId=" + connectionId +
                ", customerId=" + customerId +
                ", planId=" + planId +
                ", portId=" + portId +
                ", serviceAreaId=" + serviceAreaId +
                ", connectionStatus='" + connectionStatus + '\'' +
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
package ftth.model;

import java.time.LocalDateTime;

import ftth.model.enums.PortStatus;

/**
 * Domain model representing a physical network port.
 * Maps to the 'ports' table.
 */
public class Port {

    // ===============================
    // Fields (DB columns)
    // ===============================

    private Long portId;               // port_id (PK)
    private Long splitterId;           // splitter_id (FK)
    private Integer portNumber;        // port_number
    private PortStatus portStatus;     // port_status (ENUM)
    private LocalDateTime createdAt;   // created_at


    // ===============================
    // Constructors
    // ===============================

    // No-args constructor (required by frameworks)
    public Port() {
    }

    // Constructor for NEW port (before DB insert)
    public Port(Long splitterId,
                Integer portNumber) {
        this.splitterId = splitterId;
        this.portNumber = portNumber;
        this.portStatus = PortStatus.AVAILABLE;
    }

    // Full constructor (used when reading from DB)
    public Port(Long portId,
                Long splitterId,
                Integer portNumber,
                PortStatus portStatus,
                LocalDateTime createdAt) {
        this.portId = portId;
        this.splitterId = splitterId;
        this.portNumber = portNumber;
        this.portStatus = portStatus;
        this.createdAt = createdAt;
    }


    // ===============================
    // Getters and Setters
    // ===============================

    public Long getPortId() {
        return portId;
    }

    public void setPortId(Long portId) {
        this.portId = portId;
    }

    public Long getSplitterId() {
        return splitterId;
    }

    public void setSplitterId(Long splitterId) {
        this.splitterId = splitterId;
    }

    public Integer getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }

    public PortStatus getPortStatus() {
        return portStatus;
    }

    public void setPortStatus(PortStatus portStatus) {
        this.portStatus = portStatus;
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

    public boolean isAvailable() {
        return PortStatus.AVAILABLE.equals(this.portStatus);
    }

    public void assign() {
        if (!isAvailable()) {
            throw new IllegalStateException("Port is not available for assignment");
        }
        this.portStatus = PortStatus.ASSIGNED;
    }

    public void markFaulty() {
        this.portStatus = PortStatus.FAULTY;
    }

    public void disable() {
        this.portStatus = PortStatus.DISABLED;
    }


    // ===============================
    // toString (debugging)
    // ===============================

    @Override
    public String toString() {
        return "Port{" +
                "portId=" + portId +
                ", splitterId=" + splitterId +
                ", portNumber=" + portNumber +
                ", portStatus=" + portStatus +
                ", createdAt=" + createdAt +
                '}';
    }
}
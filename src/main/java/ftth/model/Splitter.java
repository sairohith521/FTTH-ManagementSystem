package ftth.model;

import java.time.LocalDateTime;

/**
 * Domain model representing a splitter device.
 * Maps to the 'splitters' table.
 */
public class Splitter {

    // ===============================
    // Fields (DB columns)
    // ===============================

    private Long splitterId;           // splitter_id (PK)
    private Long oltId;                // olt_id (FK)
    private String splitterCode;       // splitter_code
    private Integer splitterNumber;    // splitter_number
    private boolean active;            // is_active
    private LocalDateTime createdAt;   // created_at

    // Transient fields (not in DB, used for display)
    private int totalPorts;
    private int availablePorts;


    // ===============================
    // Constructors
    // ===============================

    // No-args constructor (required by frameworks)
    public Splitter() {
    }

    // Constructor for NEW splitter (before DB insert)
    public Splitter(Long oltId,
                    Integer splitterNumber,
                    String splitterCode) {
        this.oltId = oltId;
        this.splitterNumber = splitterNumber;
        this.splitterCode = splitterCode;
        this.active = true;
    }

    // Full constructor (used when reading from DB)
    public Splitter(Long splitterId,
                    Long oltId,
                    String splitterCode,
                    Integer splitterNumber,
                    boolean active,
                    LocalDateTime createdAt) {
        this.splitterId = splitterId;
        this.oltId = oltId;
        this.splitterCode = splitterCode;
        this.splitterNumber = splitterNumber;
        this.active = active;
        this.createdAt = createdAt;
    }


    // ===============================
    // Getters and Setters
    // ===============================

    public Long getSplitterId() {
        return splitterId;
    }

    public void setSplitterId(Long splitterId) {
        this.splitterId = splitterId;
    }

    public Long getOltId() {
        return oltId;
    }

    public void setOltId(Long oltId) {
        this.oltId = oltId;
    }

    public String getSplitterCode() {
        return splitterCode;
    }

    public void setSplitterCode(String splitterCode) {
        this.splitterCode = splitterCode;
    }

    public Integer getSplitterNumber() {
        return splitterNumber;
    }

    public void setSplitterNumber(Integer splitterNumber) {
        this.splitterNumber = splitterNumber;
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

    public int getTotalPorts() {
        return totalPorts;
    }

    public void setTotalPorts(int totalPorts) {
        this.totalPorts = totalPorts;
    }

    public int getAvailablePorts() {
        return availablePorts;
    }

    public void setAvailablePorts(int availablePorts) {
        this.availablePorts = availablePorts;
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
        return "Splitter{" +
                "splitterId=" + splitterId +
                ", oltId=" + oltId +
                ", splitterCode='" + splitterCode + '\'' +
                ", splitterNumber=" + splitterNumber +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}
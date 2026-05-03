package ftth.model.enums;
/**
 * Represents status of a physical port.
 * Maps to ENUM('AVAILABLE','ASSIGNED','FAULTY','DISABLED') in DB.
 */
public enum PortStatus {
    AVAILABLE,
    ASSIGNED,
    FAULTY,
    DISABLED
}
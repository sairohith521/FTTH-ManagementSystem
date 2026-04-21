package ftth.model.enums;
/**
 * Represents type of email sent.
 * Maps to ENUM in email_logs table.
 */
public enum EmailType {
    ORDER_CONFIRMATION,
    BILL,
    OLT_ALERT,
    DISCONNECT,
    SERVICE_MOVE,
    PLAN_CHANGE,
    PLAN_ADMIN
}

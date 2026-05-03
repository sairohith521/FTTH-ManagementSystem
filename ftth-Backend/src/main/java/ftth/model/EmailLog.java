package ftth.model;
import ftth.model.enums.*;
import java.time.LocalDateTime;
/**
 * Domain model representing an email log entry.
 * Maps to the 'email_logs' table.
 */
public class EmailLog {

    // ===============================
    // Fields (DB columns)
    // ===============================

    private Long emailLogId;            // email_log_id (PK)
    private Long customerId;            // customer_id (FK, nullable)
    private EmailType emailType;        // email_type
    private String recipientEmail;      // recipient_email
    private String subject;             // subject
    private EmailStatus sentStatus;     // sent_status
    private String providerResponse;    // provider_response
    private LocalDateTime createdAt;    // created_at


    // ===============================
    // Constructors
    // ===============================

    // No-args constructor (required by frameworks)
    public EmailLog() {
    }

    // Constructor for NEW email log (before DB insert)
    public EmailLog(Long customerId,
                    EmailType emailType,
                    String recipientEmail,
                    String subject,
                    EmailStatus sentStatus,
                    String providerResponse) {
        this.customerId = customerId;
        this.emailType = emailType;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.sentStatus = sentStatus;
        this.providerResponse = providerResponse;
    }

    // Full constructor (used when reading from DB)
    public EmailLog(Long emailLogId,
                    Long customerId,
                    EmailType emailType,
                    String recipientEmail,
                    String subject,
                    EmailStatus sentStatus,
                    String providerResponse,
                    LocalDateTime createdAt) {
        this.emailLogId = emailLogId;
        this.customerId = customerId;
        this.emailType = emailType;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.sentStatus = sentStatus;
        this.providerResponse = providerResponse;
        this.createdAt = createdAt;
    }


    // ===============================
    // Getters and Setters
    // ===============================

    public Long getEmailLogId() {
        return emailLogId;
    }

    public void setEmailLogId(Long emailLogId) {
        this.emailLogId = emailLogId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public EmailStatus getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(EmailStatus sentStatus) {
        this.sentStatus = sentStatus;
    }

    public String getProviderResponse() {
        return providerResponse;
    }

    public void setProviderResponse(String providerResponse) {
        this.providerResponse = providerResponse;
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

    public boolean isSentSuccessfully() {
        return EmailStatus.SENT.equals(this.sentStatus);
    }

    public void markFailed(String response) {
        this.sentStatus = EmailStatus.FAILED;
        this.providerResponse = response;
    }


    // ===============================
    // toString (debugging)
    // ===============================

    @Override
    public String toString() {
        return "EmailLog{" +
                "emailLogId=" + emailLogId +
                ", customerId=" + customerId +
                ", emailType=" + emailType +
                ", recipientEmail='" + recipientEmail + '\'' +
                ", subject='" + subject + '\'' +
                ", sentStatus=" + sentStatus +
                ", providerResponse='" + providerResponse + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
package ftth.model;

import java.sql.Timestamp;

public class EmailLog {

    private long emailLogId;
    private long customerId;

    private String emailType;
    private String recipientEmail;
    private String subject;

    private String sentStatus;
    private String providerResponse;

    private Timestamp createdAt;

    // 🔹 Default Constructor
    public EmailLog() {
    }

    // 🔹 Parameterized Constructor
    public EmailLog(long emailLogId, long customerId, String emailType,
                    String recipientEmail, String subject,
                    String sentStatus, String providerResponse,
                    Timestamp createdAt) {

        this.emailLogId = emailLogId;
        this.customerId = customerId;
        this.emailType = emailType;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.sentStatus = sentStatus;
        this.providerResponse = providerResponse;
        this.createdAt = createdAt;
    }

    // 🔹 Getters and Setters

    public long getEmailLogId() {
        return emailLogId;
    }

    public void setEmailLogId(long emailLogId) {
        this.emailLogId = emailLogId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getEmailType() {
        return emailType;
    }

    public void setEmailType(String emailType) {
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

    public String getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(String sentStatus) {
        this.sentStatus = sentStatus;
    }

    public String getProviderResponse() {
        return providerResponse;
    }

    public void setProviderResponse(String providerResponse) {
        this.providerResponse = providerResponse;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // 🔹 toString()

    @Override
    public String toString() {
        return "EmailLog{" +
                "emailLogId=" + emailLogId +
                ", customerId=" + customerId +
                ", emailType='" + emailType + '\'' +
                ", recipientEmail='" + recipientEmail + '\'' +
                ", subject='" + subject + '\'' +
                ", sentStatus='" + sentStatus + '\'' +
                ", providerResponse='" + providerResponse + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
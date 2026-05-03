package ftth.repository;

import ftth.config.DbConnection;
import ftth.model.EmailLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Repository for email_logs table.
 */
public class EmailLogRepository {

    private static final String INSERT_SQL =
        "INSERT INTO email_logs (" +
        "customer_id, email_type, recipient_email, subject, sent_status, provider_response" +
        ") VALUES (?, ?, ?, ?, ?, ?)";

    public void insert(EmailLog log) {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            if (log.getCustomerId() != null) {
                ps.setLong(1, log.getCustomerId());
            } else {
                ps.setNull(1, java.sql.Types.BIGINT);
            }

            ps.setString(2, log.getEmailType().name());
            ps.setString(3, log.getRecipientEmail());
            ps.setString(4, log.getSubject());
            ps.setString(5, log.getSentStatus().name());
            ps.setString(6, log.getProviderResponse());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting email log", e);
        }
    }
}
package ftth.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ftth.config.DbConnection;

public class OltRepository {
    public String findOltTypeByCustomerId(long customerId) {

    String sql =
        "SELECT o.olt_type " +
        "FROM olts o " +
        "JOIN customer_connections cc " +
        "  ON o.service_area_id = cc.service_area_id " +
        "WHERE cc.customer_id = ? " +
        "  AND o.is_active = 1 " +
        "  AND cc.connection_status = 'ACTIVE' " +
        "LIMIT 1";

    try (Connection conn = DbConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setLong(1, customerId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("olt_type");
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException(
            "Unable to resolve OLT type for customer " + customerId, e
        );
    }

    return null; // No active OLT found
}
}

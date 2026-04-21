package ftth.util;
import ftth.config.DbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility for generating customer codes from DB.
 */
public class CustomerID {

    private static final String FIND_MAX_CODE_SQL =
        "SELECT COALESCE(" +
        "MAX(CAST(SUBSTRING(customer_code, 6) AS UNSIGNED)), 0" +
        ") AS max_no " +
        "FROM customers " +
        "WHERE customer_code LIKE 'AAHA-%'";

    /**
     * Generate next customer code like AAHA-0001.
     */
    public static String generateNextCustomerCode() {

        int next = 1;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_MAX_CODE_SQL);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                next = rs.getInt("max_no") + 1;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error generating customer code", e);
        }

        return String.format("AAHA-%04d", next);
    }
}
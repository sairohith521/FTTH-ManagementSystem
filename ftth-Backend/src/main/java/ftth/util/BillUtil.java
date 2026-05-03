package ftth.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import ftth.config.DbConnection;

/**
 * Utility class for bill-related helper functions.
 */
public final class BillUtil {

    // ===============================
    // Configuration
    // ===============================

    private static final String PREFIX = "BILL";

    private static final String FIND_MAX_BILL_NO_SQL =
        "SELECT COALESCE(" +
        "MAX(CAST(SUBSTRING(bill_no, 11) AS UNSIGNED)), 0" +
        ") AS max_no " +
        "FROM bills " +
        "WHERE bill_no LIKE CONCAT('BILL-', YEAR(CURDATE()), '-%')";

    // Prevent instantiation
    private BillUtil() {
    }

    /**
     * Generate next bill number.
     *
     * Example: BILL-2026-000001
     */
    public static String generateBillNo() {

        int year = LocalDate.now().getYear();
        int next = 1;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_MAX_BILL_NO_SQL);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                next = rs.getInt("max_no") + 1;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error generating bill number", e);
        }

        return String.format(
            "%s-%d-%06d",
            PREFIX,
            year,
            next
        );
    }
}

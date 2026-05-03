package ftth.repository;

import ftth.model.ServiceArea;
import ftth.config.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Repository class for service_areas table.
 * Contains ONLY database access logic.
 */
public class ServiceAreaRepository {

    // ===============================
    // SQL Queries
    // ===============================

    private static final String FIND_BY_PINCODE_SQL =
        "SELECT service_area_id, pincode, is_active, created_at " +
        "FROM service_areas " +
        "WHERE pincode = ?";

    private static final String INSERT_SQL =
        "INSERT INTO service_areas (pincode, is_active) VALUES (?, ?)";

    private static final String UPDATE_ACTIVE_SQL =
        "UPDATE service_areas SET is_active = ? WHERE service_area_id = ?";


    // ===============================
    // Public Repository Methods
    // ===============================

    /**
     * Find service area by pincode.
     *
     * @param pincode pincode to search
     * @return ServiceArea if found, otherwise null
     */
    public ServiceArea findByPincode(Long pincode) {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_PINCODE_SQL)) {

            ps.setString(1, String.valueOf(pincode));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToServiceArea(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error fetching service area for pincode: " + pincode, e
            );
        }

        return null;
    }

    /**
     * Insert new service area.
     */
    public void insert(ServiceArea serviceArea) {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            ps.setString(1, serviceArea.getPincode());
            ps.setBoolean(2, serviceArea.isActive());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error inserting service area: " + serviceArea.getPincode(), e
            );
        }
    }

    /**
     * Activate or deactivate a service area.
     */
    public void updateActiveStatus(Long serviceAreaId, boolean active) {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_ACTIVE_SQL)) {

            ps.setBoolean(1, active);
            ps.setLong(2, serviceAreaId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error updating service area status, id=" + serviceAreaId, e
            );
        }
    }


    // ===============================
    // Private Helper Methods
    // ===============================

    /**
     * Map ResultSet row to ServiceArea model.
     */
    private ServiceArea mapRowToServiceArea(ResultSet rs)
            throws SQLException {

        Long serviceAreaId = rs.getLong("service_area_id");
        String pincode = rs.getString("pincode");
        boolean active = rs.getBoolean("is_active");
        LocalDateTime createdAt =
            rs.getTimestamp("created_at").toLocalDateTime();

        return new ServiceArea(
            serviceAreaId,
            pincode,
            active,
            createdAt
        );
    }
     private static final String FIND_PINCODE_BY_ID_SQL =
        "SELECT pincode FROM service_areas WHERE service_area_id = ?";

    /**
     * Find pincode using service_area_id.
     */
    public String findPincodeById(Long serviceAreaId) {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(FIND_PINCODE_BY_ID_SQL)) {

            ps.setLong(1, serviceAreaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("pincode");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error fetching pincode for serviceAreaId=" + serviceAreaId,
                e
            );
        }

        return null; // not found
    }

}




   





package ftth.repository;

import ftth.config.DbConnection;
import ftth.model.Role;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class RoleRepository {

    // 🔹 Get roleId by roleCode
    public Long findRoleIdByCode(String roleCode) {

        String sql = "SELECT role_id FROM role WHERE role_code = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, roleCode);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("role_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 🔹 Get all roles (optional)
    public List<String> findAllRoles() {

        List<String> roles = new ArrayList<>();

        String sql = "SELECT role_code FROM role";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(rs.getString("role_code"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return roles;
    }

    private static final String FIND_BY_ID_SQL =
        "SELECT role_id, role_code, created_at " +
        "FROM roles " +
        "WHERE role_id = ?";

    /**
     * Find role by role_id.
     *
     * @param roleId role_id from users table
     * @return Role or null if not found
     */
    public Role findById(Long roleId) {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setLong(1, roleId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapRowToRole(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error fetching role with id: " + roleId, e
            );
        }

        return null;
    }

    private Role mapRowToRole(ResultSet rs) throws SQLException {

        Long roleId = rs.getLong("role_id");
        String roleCode = rs.getString("role_code");
        LocalDateTime createdAt =
            rs.getTimestamp("created_at").toLocalDateTime();

        return new Role(
            roleId,
            roleCode,
            createdAt
        );
    }
}



    

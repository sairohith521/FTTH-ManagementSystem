
package ftth.repository;

import ftth.config.DbConnection;
import java.sql.*;
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
}
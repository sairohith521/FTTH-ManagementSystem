package ftth.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ftth.config.DbConnection;

public class UserRepository {
public String login(String username, String password) {

    String sql = "SELECT r.role_code " +
                 "FROM user u " +
                 "JOIN role r ON u.role_id = r.role_id " +
                 "WHERE u.username = ? AND u.password_hash = ? AND u.is_active = TRUE";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, username);
        ps.setString(2, password); // later hash

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString("role_code").toUpperCase();
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null; // invalid credentials
}
public List<String[]> findAllUsers() {

    List<String[]> users = new ArrayList<>();

    String sql = "SELECT u.username, u.password_hash, r.role_code " +
                 "FROM user u " +
                 "JOIN role r ON u.role_id = r.role_id " +
                 "WHERE u.is_active = TRUE";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String username = rs.getString("username");
            String password = rs.getString("password_hash");
            String role = rs.getString("role_code");

            users.add(new String[]{username, password, role});
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return users;
}
public boolean existsByUsername(String username) {

    String sql = "SELECT COUNT(*) FROM user WHERE username = ?";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
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
public boolean insertUser(String username, String password, long roleId) {

    String sql = "INSERT INTO user (username, password_hash, role_id, is_active) VALUES (?, ?, ?, TRUE)";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, username);
        ps.setString(2, password); // later hash
        ps.setLong(3, roleId);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
public boolean updatePassword(String username, String newPassword) {

    String sql = "UPDATE user SET password_hash = ? WHERE username = ?";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, newPassword); // later hash
        ps.setString(2, username);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
public boolean updateUserRole(String username, long roleId) {

    String sql = "UPDATE user SET role_id = ? WHERE username = ?";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setLong(1, roleId);
        ps.setString(2, username);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
public boolean deleteUser(String username) {

    String sql = "DELETE FROM user WHERE username = ?";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, username);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
}
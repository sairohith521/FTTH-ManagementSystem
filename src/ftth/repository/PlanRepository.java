package ftth.repository;

import ftth.config.DbConnection;
import ftth.model.Plan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlanRepository {

    public boolean insertPlan(Plan plan) {
        String sql = """
                INSERT INTO plan_admin
                (plan_name, speed, data_limit, ott_count, price, olt_type, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, plan.getName());
            ps.setString(2, plan.getSpeed());
            ps.setString(3, plan.getDataLimit());
            ps.setInt(4, plan.getOtts());
            ps.setDouble(5, plan.getPrice());
            ps.setString(6, plan.getOltType());
            ps.setBoolean(7, plan.isActive());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding plan", e);
        }
    }

    public List<Plan> findAllPlans() {
        String sql = """
                SELECT plan_id, plan_name, speed, data_limit, ott_count, price, olt_type, is_active
                FROM plan_admin
                ORDER BY plan_id
                """;

        List<Plan> plans = new ArrayList<>();

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                plans.add(toPlan(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading plans", e);
        }

        return plans;
    }

    public List<Plan> findActivePlans() {
        String sql = """
                SELECT plan_id, plan_name, speed, data_limit, ott_count, price, olt_type, is_active
                FROM plan_admin
                WHERE is_active = TRUE
                ORDER BY plan_id
                """;

        List<Plan> plans = new ArrayList<>();

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                plans.add(toPlan(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading active plans", e);
        }

        return plans;
    }

    public Plan findPlanById(long id) {
        String sql = """
                SELECT plan_id, plan_name, speed, data_limit, ott_count, price, olt_type, is_active
                FROM plan_admin
                WHERE plan_id = ?
                """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return toPlan(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding plan", e);
        }

        return null;
    }

    public boolean updatePlan(long id, Plan updatedPlan) {
        String sql = """
                UPDATE plan_admin
                SET plan_name = ?, speed = ?, data_limit = ?, ott_count = ?, price = ?, olt_type = ?, is_active = ?
                WHERE plan_id = ?
                """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, updatedPlan.getName());
            ps.setString(2, updatedPlan.getSpeed());
            ps.setString(3, updatedPlan.getDataLimit());
            ps.setInt(4, updatedPlan.getOtts());
            ps.setDouble(5, updatedPlan.getPrice());
            ps.setString(6, updatedPlan.getOltType());
            ps.setBoolean(7, updatedPlan.isActive());
            ps.setLong(8, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating plan", e);
        }
    }

    public boolean deletePlan(long id) {
        String sql = "DELETE FROM plan_admin WHERE plan_id = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting plan", e);
        }
    }

    public boolean togglePlanStatus(long id, boolean newStatus) {
        String sql = "UPDATE plan_admin SET is_active = ? WHERE plan_id = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, newStatus);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error toggling plan status", e);
        }
    }

    private Plan toPlan(ResultSet rs) throws SQLException {
        return new Plan(
                rs.getLong("plan_id"),
                rs.getString("plan_name"),
                rs.getString("speed"),
                rs.getString("data_limit"),
                rs.getInt("ott_count"),
                rs.getDouble("price"),
                rs.getString("olt_type"),
                rs.getBoolean("is_active")
        );
    }
}

package ftth.repository;

import ftth.config.DbConnection;
import ftth.model.Plan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class PlanRepository {

public boolean insertPlan(Plan plan) {

    String sql =
        "INSERT INTO plans " +
        "(plan_code, plan_name, speed_label, data_limit_label, ott_count, " +
        " monthly_price, olt_type, is_active) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        String autoCode = plan.getPlanName().toUpperCase().replaceAll("\\s+", "_");
        ps.setString(1, autoCode);
        ps.setString(2, plan.getPlanName());
        ps.setString(3, plan.getSpeedLabel());
        ps.setString(4, plan.getDataLimitLabel());
        ps.setInt(5, plan.getOttCount());
        ps.setBigDecimal(6, plan.getMonthlyPrice());        // ✅ BigDecimal
        ps.setString(7, plan.getOltType());
        ps.setBoolean(8, plan.isActive());

        return ps.executeUpdate() > 0;

    } catch (SQLIntegrityConstraintViolationException e) {
        System.out.println(
            "Plan with name '" + plan.getPlanName() + "' already exists."
        );
        return false;

    } catch (SQLException e) {
        throw new RuntimeException("Error adding plan", e);
    }
}
public List<Plan> findAllPlans() {
        String sql =
            "SELECT plan_id, plan_name, speed_label, data_limit_label, ott_count, monthly_price, olt_type, is_active, created_at " +
            "FROM plans ORDER BY plan_id";

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

    String sql =
        "SELECT plan_id, plan_name, speed_label, data_limit_label, " +
        "ott_count, monthly_price, olt_type, is_active, created_at " +
        "FROM plans " +
        "WHERE is_active = TRUE " +
        "ORDER BY plan_id";

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
        String sql =
            "SELECT plan_id, plan_name, speed_label, data_limit_label, ott_count, monthly_price, olt_type, is_active, created_at " +
            "FROM plans WHERE plan_id = ?";

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
private Plan toPlan(ResultSet rs) throws SQLException {

    return new Plan(
        rs.getLong("plan_id"),
        rs.getString("plan_name"),
        rs.getString("speed_label"),
        rs.getString("data_limit_label"),
        rs.getInt("ott_count"),
        rs.getBigDecimal("monthly_price"),
        rs.getString("olt_type"),
        rs.getBoolean("is_active"),
        rs.getTimestamp("created_at").toLocalDateTime()
    );
}

public boolean updatePlan(long planId, Plan updatedPlan) {

    String sql =
        "UPDATE plans " +
        "SET plan_name = ?, " +
        "    speed_label = ?, " +
        "    data_limit_label = ?, " +
        "    ott_count = ?, " +
        "    monthly_price = ?, " +
        "    olt_type = ?, " +
        "    is_active = ? " +
        "WHERE plan_id = ?";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, updatedPlan.getPlanName());
        ps.setString(2, updatedPlan.getSpeedLabel());
        ps.setString(3, updatedPlan.getDataLimitLabel());
        ps.setInt(4, updatedPlan.getOttCount());
        ps.setBigDecimal(5, updatedPlan.getMonthlyPrice()); // ✅ FIXED
        ps.setString(6, updatedPlan.getOltType());
        ps.setBoolean(7, updatedPlan.isActive());
        ps.setLong(8, planId);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        throw new RuntimeException("Error updating plan with id=" + planId, e);
    }
}

public boolean deletePlan(long id) {
        String checkSql = "SELECT COUNT(*) FROM customer_connections WHERE plan_id = ? AND connection_status = 'ACTIVE'";
        String deleteSql = "DELETE FROM plans WHERE plan_id = ?";

        try (Connection con = DbConnection.getConnection()) {
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            checkPs.setLong(1, id);
            ResultSet rs = checkPs.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                System.out.println("Cannot delete this plan. Active customers are using it.");
                return false;
            }

            PreparedStatement deletePs = con.prepareStatement(deleteSql);
            deletePs.setLong(1, id);
            return deletePs.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting plan", e);
        }
    }

public boolean togglePlanStatus(long planId, boolean newStatus) {

    String sql = "UPDATE plans SET is_active = ? WHERE plan_id = ?";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setBoolean(1, newStatus);
        ps.setLong(2, planId);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        throw new RuntimeException("Error toggling plan status for planId=" + planId, e);
    }
}
   

     private static final String FIND_BY_ID_SQL =
    "SELECT plan_id, plan_name, speed_label, data_limit_label, " +
    "ott_count, monthly_price, olt_type, is_active, created_at " +
    "FROM plans WHERE plan_id = ?";

public Plan findById(Long planId) {

    try (Connection conn = DbConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {

        ps.setLong(1, planId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return toPlan(rs);   // ✅ use correct mapper
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException(
            "Error fetching plan with ID: " + planId, e
        );
    }

    return null;
}
}


          


    
package ftth.repository;

import ftth.config.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerConnectionRepository {

    public String[] findConnection(long connectionId) {
        String sql =
            "SELECT cc.connection_id, cc.customer_name, sa.pincode, " +
            "pa.plan_name, pa.price, cc.connection_status, " +
            "o.olt_code, s.splitter_number, p.port_number, cc.port_id " +
            "FROM customer_connections cc " +
            "JOIN plan_admin pa ON pa.plan_id = cc.plan_id " +
            "JOIN ports p ON p.port_id = cc.port_id " +
            "JOIN splitters s ON s.splitter_id = p.splitter_id " +
            "JOIN olts o ON o.olt_id = s.olt_id " +
            "JOIN service_areas sa ON sa.service_area_id = cc.service_area_id " +
            "WHERE cc.connection_id = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, connectionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[] {
                    String.valueOf(rs.getLong("connection_id")),
                    rs.getString("customer_name"),
                    rs.getString("pincode"),
                    rs.getString("plan_name"),
                    String.valueOf(rs.getDouble("price")),
                    rs.getString("connection_status"),
                    rs.getString("olt_code"),
                    String.valueOf(rs.getInt("splitter_number")),
                    String.valueOf(rs.getInt("port_number")),
                    String.valueOf(rs.getLong("port_id"))
                };
            }
        } catch (Exception e) {
            System.out.println("Error finding connection: " + e.getMessage());
        }
        return null;
    }

    public void listActiveConnections() {
        String sql =
            "SELECT cc.connection_id, cc.customer_name, sa.pincode, " +
            "pa.plan_name, pa.price, cc.connection_status, " +
            "o.olt_code, s.splitter_number, p.port_number " +
            "FROM customer_connections cc " +
            "JOIN plan_admin pa ON pa.plan_id = cc.plan_id " +
            "JOIN ports p ON p.port_id = cc.port_id " +
            "JOIN splitters s ON s.splitter_id = p.splitter_id " +
            "JOIN olts o ON o.olt_id = s.olt_id " +
            "JOIN service_areas sa ON sa.service_area_id = cc.service_area_id " +
            "WHERE cc.connection_status = 'ACTIVE' " +
            "ORDER BY cc.connection_id";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            boolean found = false;
            System.out.println("\n--- Active Connections ---");
            System.out.printf("%-5s %-18s %-8s %-12s %-10s %-25s%n",
                "ID", "Customer", "Pincode", "Plan", "Price", "Port");
            System.out.println("-".repeat(80));
            while (rs.next()) {
                System.out.printf("%-5d %-18s %-8s %-12s Rs.%-7.0f %s/Spl%d/Port%d%n",
                    rs.getLong("connection_id"),
                    rs.getString("customer_name"),
                    rs.getString("pincode"),
                    rs.getString("plan_name"),
                    rs.getDouble("price"),
                    rs.getString("olt_code"),
                    rs.getInt("splitter_number"),
                    rs.getInt("port_number"));
                found = true;
            }
            if (!found) System.out.println("No active connections.");
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error listing connections: " + e.getMessage());
        }
    }

    public boolean disconnectCustomer(long connectionId) {
        try (Connection con = DbConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String findSql = "SELECT port_id FROM customer_connections WHERE connection_id = ? AND connection_status = 'ACTIVE'";
                PreparedStatement findPs = con.prepareStatement(findSql);
                findPs.setLong(1, connectionId);
                ResultSet rs = findPs.executeQuery();
                if (!rs.next()) {
                    System.out.println("Connection not found or already disconnected.");
                    con.rollback();
                    return false;
                }
                long portId = rs.getLong("port_id");

                PreparedStatement disconnectPs = con.prepareStatement(
                    "UPDATE customer_connections SET connection_status = 'DISCONNECTED', disconnected_on = CURDATE() WHERE connection_id = ?");
                disconnectPs.setLong(1, connectionId);
                disconnectPs.executeUpdate();

                PreparedStatement freePs = con.prepareStatement(
                    "UPDATE ports SET port_status = 'AVAILABLE' WHERE port_id = ?");
                freePs.setLong(1, portId);
                freePs.executeUpdate();

                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            System.out.println("Error disconnecting customer: " + e.getMessage());
            return false;
        }
    }
}

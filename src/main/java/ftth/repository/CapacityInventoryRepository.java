package ftth.repository;

import ftth.config.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CapacityInventoryRepository {

    /* DTO */
    public static class CapacityRow {
        public String pincode;
        public long oltId;
        public String oltType;
        public int totalPorts;
        public int usedPorts;
        public int freePorts;
        public double utilization;
        public int splitterCount;
    }

    public List<CapacityRow> fetchAllCapacity() {

        String sql =
            "SELECT sa.pincode, o.olt_id, o.olt_type, " +
            "COUNT(CASE WHEN p.port_status IN ('AVAILABLE','ASSIGNED') THEN 1 END) AS total_ports, " +
            "SUM(CASE WHEN p.port_status = 'ASSIGNED' THEN 1 ELSE 0 END) AS used_ports, " +
            "SUM(CASE WHEN p.port_status = 'AVAILABLE' THEN 1 ELSE 0 END) AS free_ports, " +
            "ROUND( " +
            "   (SUM(CASE WHEN p.port_status = 'ASSIGNED' THEN 1 ELSE 0 END) / " +
            "    NULLIF(COUNT(CASE WHEN p.port_status IN ('AVAILABLE','ASSIGNED') THEN 1 END),0)) * 100, 1 " +
            ") AS utilization, " +
            "COUNT(DISTINCT s.splitter_id) AS splitter_count " +
            "FROM service_areas sa " +
            "JOIN olts o ON o.service_area_id = sa.service_area_id AND o.is_active = TRUE " +
            "JOIN splitters s ON s.olt_id = o.olt_id AND s.is_active = TRUE " +
            "LEFT JOIN ports p ON p.splitter_id = s.splitter_id " +
            "WHERE sa.is_active = TRUE " +
            "GROUP BY sa.pincode, o.olt_id, o.olt_type " +
            "ORDER BY utilization DESC";

        List<CapacityRow> rows = new ArrayList<>();

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CapacityRow r = new CapacityRow();
                r.pincode = rs.getString("pincode");
                r.oltId = rs.getLong("olt_id");
                r.oltType = rs.getString("olt_type");
                r.totalPorts = rs.getInt("total_ports");
                r.usedPorts = rs.getInt("used_ports");
                r.freePorts = rs.getInt("free_ports");
                r.utilization = rs.getDouble("utilization");
                r.splitterCount = rs.getInt("splitter_count");
                rows.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }
}
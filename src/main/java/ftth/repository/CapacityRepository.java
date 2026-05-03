package ftth.repository;

import ftth.config.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapacityRepository {

    private static final String CAPACITY_SQL =
        "SELECT " +
        "    sa.pincode, " +
        "    o.olt_id, " +
        "    o.olt_code, " +
        "    o.olt_type, " +
        "    COUNT(DISTINCT s.splitter_id)                                    AS splitter_count, " +
        "    COUNT(p.port_id)                                                  AS total_ports, " +
        "    SUM(CASE WHEN p.port_status = 'ASSIGNED'  THEN 1 ELSE 0 END)    AS used_ports, " +
        "    SUM(CASE WHEN p.port_status = 'AVAILABLE' THEN 1 ELSE 0 END)    AS free_ports " +
        "FROM olts o " +
        "JOIN service_areas sa ON o.service_area_id = sa.service_area_id " +
        "LEFT JOIN splitters s ON s.olt_id = o.olt_id AND s.is_active = TRUE " +
        "LEFT JOIN ports p     ON p.splitter_id = s.splitter_id " +
        "WHERE o.is_active = TRUE " +
        "GROUP BY sa.pincode, o.olt_id, o.olt_code, o.olt_type " +
        "ORDER BY sa.pincode, o.olt_code";

    public List<Map<String, Object>> getCapacityData() {

        List<Map<String, Object>> result = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(CAPACITY_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                String  pincode       = rs.getString("pincode");
                String  oltCode       = rs.getString("olt_code");
                String  oltType       = rs.getString("olt_type");
                int     splitterCount = rs.getInt("splitter_count");
                int     totalPorts    = rs.getInt("total_ports");
                int     usedPorts     = rs.getInt("used_ports");
                int     freePorts     = rs.getInt("free_ports");

                // Utilization percentage
                double util = totalPorts > 0
                    ? Math.round((usedPorts * 100.0 / totalPorts) * 10.0) / 10.0
                    : 0.0;

                // ── Warning logic ──
                // Max 3 splitters per OLT, max 3 ports per splitter
                final int MAX_SPLITTERS_PER_OLT = 3;
                boolean breach      = util >= 80.0;
                boolean allPortsFull = freePorts == 0 && totalPorts > 0;
                boolean canAddSplitter = splitterCount < MAX_SPLITTERS_PER_OLT;

                String warning = null;
                if (allPortsFull) {
                    if (canAddSplitter) {
                        warning = "ADD_SPLITTER"; // still room to add splitters
                    } else {
                        warning = "ADD_OLT";      // all 3 splitters full
                    }
                } else if (breach) {
                    warning = "CAPACITY_BREACH";  // >80% but ports still free
                }

                row.put("pincode",       pincode);
                row.put("oltCode",       oltCode);
                row.put("oltType",       oltType);
                row.put("splitterCount", splitterCount);
                row.put("totalPorts",    totalPorts);
                row.put("usedPorts",     usedPorts);
                row.put("freePorts",     freePorts);
                row.put("utilPercent",   util);
                row.put("breach",        breach);
                row.put("warning",       warning); // null = no warning
                result.add(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching capacity data", e);
        }

        return result;
    }
}

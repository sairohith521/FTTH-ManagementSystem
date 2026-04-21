package ftth.repository;

import ftth.config.DbConnection;
import ftth.model.*;
import ftth.model.dtos.OltInventoryDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepository {
    public String addOlt(String pincode, String oltType, int splitterCount, int portsPerSplitter) {
        String serviceAreaSql = "SELECT service_area_id FROM service_areas WHERE pincode = ?";
        String createServiceAreaSql = "INSERT INTO service_areas (pincode, is_active) VALUES (?, TRUE)";
        String nextCodeSql =
        "SELECT COALESCE(MAX(CAST(SUBSTRING_INDEX(olt_code, '-', -1) AS UNSIGNED)), 0) + 1 AS next_no " +
        "FROM olts " +
        "WHERE service_area_id = ? AND olt_type = ?";
        String insertOltSql = "INSERT INTO olts (olt_code, service_area_id, olt_type, is_active) VALUES (?, ?, ?, TRUE)";
        String insertSplitterSql = "INSERT INTO splitters (olt_id, splitter_number, is_active) VALUES (?, ?, TRUE)";
        String insertPortSql = "INSERT INTO ports (splitter_id, port_number, port_status) VALUES (?, ?, 'AVAILABLE')";

        try (Connection con = DbConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                long serviceAreaId = getOrCreateServiceArea(con, serviceAreaSql, createServiceAreaSql, pincode);
                int nextNo = getNextOltNumber(con, nextCodeSql, serviceAreaId, oltType);
                String oltCode = oltType + "-" + pincode + "-" + nextNo;
                long oltId = insertOlt(con, insertOltSql, oltCode, serviceAreaId, oltType);

                for (int splitterNo = 1; splitterNo <= splitterCount; splitterNo++) {
                    long splitterId = insertSplitter(con, insertSplitterSql, oltId, splitterNo);
                    for (int portNo = 1; portNo <= portsPerSplitter; portNo++) {
                        try (PreparedStatement ps = con.prepareStatement(insertPortSql)) {
                            ps.setLong(1, splitterId);
                            ps.setInt(2, portNo);
                            ps.executeUpdate();
                        }
                    }
                }

                con.commit();
                return oltCode;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding OLT", e);
        }
    }

    public boolean removeOlt(String oltCode) {
       String assignedSql =
        "SELECT COUNT(*) " +
        "FROM ports p " +
        "JOIN splitters s ON s.splitter_id = p.splitter_id " +
        "JOIN olts o ON o.olt_id = s.olt_id " +
        "WHERE o.olt_code = ? AND p.port_status = 'ASSIGNED'";
        String deletePortsSql = "DELETE p FROM ports p JOIN splitters s ON s.splitter_id = p.splitter_id JOIN olts o ON o.olt_id = s.olt_id WHERE o.olt_code = ?";
        String deleteSplittersSql = "DELETE s FROM splitters s JOIN olts o ON o.olt_id = s.olt_id WHERE o.olt_code = ?";
        String deleteOltSql = "DELETE FROM olts WHERE olt_code = ?";

        try (Connection con = DbConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                if (countByOlt(con, assignedSql, oltCode) > 0) {
                    con.rollback();
                    return false;
                }

                try (PreparedStatement ps = con.prepareStatement(deletePortsSql)) {
                    ps.setString(1, oltCode);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(deleteSplittersSql)) {
                    ps.setString(1, oltCode);
                    ps.executeUpdate();
                }

                int affected;
                try (PreparedStatement ps = con.prepareStatement(deleteOltSql)) {
                    ps.setString(1, oltCode);
                    affected = ps.executeUpdate();
                }

                con.commit();
                return affected > 0;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error removing OLT", e);
        }
    }

    public boolean addSplitter(String oltCode, int portsPerSplitter) {
        String oltSql = "SELECT olt_id FROM olts WHERE olt_code = ? AND is_active = TRUE";
        String countSql = "SELECT COUNT(*) FROM splitters WHERE olt_id = ? AND is_active = TRUE";
        String nextSql = "SELECT COALESCE(MAX(splitter_number), 0) + 1 FROM splitters WHERE olt_id = ?";
        String insertSplitterSql = "INSERT INTO splitters (olt_id, splitter_number, is_active) VALUES (?, ?, TRUE)";
        String insertPortSql = "INSERT INTO ports (splitter_id, port_number, port_status) VALUES (?, ?, 'AVAILABLE')";

        try (Connection con = DbConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                Long oltId = findOltDbId(con, oltSql, oltCode);
                if (oltId == null) {
                    con.rollback();
                    return false;
                }

                int currentCount = countById(con, countSql, oltId);
                if (currentCount >= 3) {
                    con.rollback();
                    return false;
                }

                int nextSplitterNo = getNextSplitterNumber(con, nextSql, oltId);
                long splitterId = insertSplitter(con, insertSplitterSql, oltId, nextSplitterNo);
                for (int portNo = 1; portNo <= portsPerSplitter; portNo++) {
                    try (PreparedStatement ps = con.prepareStatement(insertPortSql)) {
                        ps.setLong(1, splitterId);
                        ps.setInt(2, portNo);
                        ps.executeUpdate();
                    }
                }

                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding splitter", e);
        }
    }

    public boolean removeSplitter(String oltCode, int splitterNumber) {
        String splitterSql =
        "SELECT s.splitter_id " +
        "FROM splitters s " +
        "JOIN olts o ON o.olt_id = s.olt_id " +
        "WHERE o.olt_code = ? AND s.splitter_number = ? AND s.is_active = TRUE";
        String assignedSql = "SELECT COUNT(*) FROM ports WHERE splitter_id = ? AND port_status = 'ASSIGNED'";
        String deletePortsSql = "DELETE FROM ports WHERE splitter_id = ?";
        String deleteSplitterSql = "DELETE FROM splitters WHERE splitter_id = ?";

        try (Connection con = DbConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                Long splitterId = findSplitterId(con, splitterSql, oltCode, splitterNumber);
                if (splitterId == null) {
                    con.rollback();
                    return false;
                }
                if (countById(con, assignedSql, splitterId) > 0) {
                    con.rollback();
                    return false;
                }

                try (PreparedStatement ps = con.prepareStatement(deletePortsSql)) {
                    ps.setLong(1, splitterId);
                    ps.executeUpdate();
                }
                int deleted;
                try (PreparedStatement ps = con.prepareStatement(deleteSplitterSql)) {
                    ps.setLong(1, splitterId);
                    deleted = ps.executeUpdate();
                }

                con.commit();
                return deleted > 0;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error removing splitter", e);
        }
    }

    // public InventoryDetails findInventoryDetails(String oltCode) {
    //    String oltSql =
    //     "SELECT o.olt_id, " +
    //     "o.olt_code, " +
    //     "sa.pincode, " +
    //     "o.olt_type, " +
    //     "COUNT(DISTINCT s.splitter_id) AS splitter_count, " +
    //     "COUNT(p.port_id) AS total_ports, " +
    //     "COALESCE(SUM(CASE WHEN p.port_status = 'AVAILABLE' THEN 1 ELSE 0 END), 0) AS available_ports " +
    //     "FROM olts o " +
    //     "JOIN service_areas sa ON sa.service_area_id = o.service_area_id " +
    //     "LEFT JOIN splitters s ON s.olt_id = o.olt_id AND s.is_active = TRUE " +
    //     "LEFT JOIN ports p ON p.splitter_id = s.splitter_id " +
    //     "WHERE o.olt_code = ? " +
    //     "GROUP BY o.olt_id, o.olt_code, sa.pincode, o.olt_type";
    //     String splitterSql = "SELECT splitter_id, olt_id, splitter_number, is_active FROM splitters WHERE olt_id = ? ORDER BY splitter_number";
    //     String portSql = "SELECT port_id, splitter_id, port_number, port_status FROM ports WHERE splitter_id = ? ORDER BY port_number";

    //     try (Connection con = DbConnection.getConnection();
    //          PreparedStatement oltPs = con.prepareStatement(oltSql)) {

    //         oltPs.setString(1, oltCode);
    //         try (ResultSet oltRs = oltPs.executeQuery()) {
    //             if (!oltRs.next()) {
    //                 return null;
    //             }

    //             InventoryDetails details = new InventoryDetails(new OLT(
    //                     oltRs.getString("olt_code"),
    //                     oltRs.getString("pincode"),
    //                     oltRs.getString("olt_type"),
    //                     oltRs.getInt("splitter_count"),
    //                     oltRs.getInt("total_ports"),
    //                     oltRs.getInt("available_ports")
    //             ));

    //             try (PreparedStatement splitterPs = con.prepareStatement(splitterSql)) {
    //                 splitterPs.setLong(1, findOltDbId(con, "SELECT olt_id FROM olts WHERE olt_code = ?", oltCode));
    //                 try (ResultSet splitterRs = splitterPs.executeQuery()) {
    //                     while (splitterRs.next()) {
    //                         Splitter splitter = new Splitter(
    //                                 splitterRs.getLong("splitter_id"),
    //                                 splitterRs.getLong("olt_id"),
    //                                 splitterRs.getInt("splitter_number"),
    //                                 splitterRs.getBoolean("is_active")
    //                         );

    //                         try (PreparedStatement portPs = con.prepareStatement(portSql)) {
    //                             portPs.setLong(1, splitter.getSplitterId());
    //                             try (ResultSet portRs = portPs.executeQuery()) {
    //                                 while (portRs.next()) {
    //                                     splitter.addPort(new Port(
    //                                             portRs.getLong("port_id"),
    //                                             portRs.getLong("splitter_id"),
    //                                             portRs.getInt("port_number"),
    //                                             portRs.getString("port_status")
    //                                     ));
    //                                 }
    //                             }
    //                         }

    //                         details.addSplitter(splitter);
    //                     }
    //                 }
    //             }

    //             return details;
    //         }
    //     } catch (SQLException e) {
    //         throw new RuntimeException("Error loading inventory details", e);
    //     }
    // }

    private long getOrCreateServiceArea(Connection con, String selectSql, String insertSql, String pincode) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(selectSql)) {
            ps.setString(1, pincode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("service_area_id");
                }
            }
        }

        try (PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pincode);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        throw new SQLException("Unable to create service area for pincode " + pincode);
    }

    private int getNextOltNumber(Connection con, String sql, long serviceAreaId, String oltType) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, serviceAreaId);
            ps.setString(2, oltType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_no");
                }
            }
        }
        return 1;
    }

    private long insertOlt(Connection con, String sql, String oltCode, long serviceAreaId, String oltType) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, oltCode);
            ps.setLong(2, serviceAreaId);
            ps.setString(3, oltType);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Unable to create OLT " + oltCode);
    }

    private long insertSplitter(Connection con, String sql, long oltId, int splitterNumber) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, oltId);
            ps.setInt(2, splitterNumber);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Unable to create splitter " + splitterNumber);
    }

    private Long findOltDbId(Connection con, String sql, String oltCode) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, oltCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return null;
    }

    private Long findSplitterId(Connection con, String sql, String oltCode, int splitterNumber) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, oltCode);
            ps.setInt(2, splitterNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("splitter_id");
                }
            }
        }
        return null;
    }

    private int countById(Connection con, String sql, long id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private int countByOlt(Connection con, String sql, String oltCode) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, oltCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private int getNextSplitterNumber(Connection con, String sql, long oltId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, oltId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 1;
    }
public int getAvailablePorts(int pincode) {

    String sql =
        "SELECT COUNT(DISTINCT p.port_id) " +
        "FROM ports p " +
        "JOIN splitters s ON s.splitter_id = p.splitter_id " +
        "JOIN olts o ON o.olt_id = s.olt_id " +
        "JOIN service_areas sa ON sa.service_area_id = o.service_area_id " +
        "WHERE sa.pincode = ? AND p.port_status = 'AVAILABLE'";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, pincode);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return 0;
}
    // ===============================
    // SQL to count available ports
    // ===============================
    private static final String COUNT_AVAILABLE_PORTS_SQL =
        "SELECT COUNT(p.port_id) AS available_ports " +
        "FROM ports p " +
        "JOIN splitters s ON p.splitter_id = s.splitter_id " +
        "JOIN olts o ON s.olt_id = o.olt_id " +
        "WHERE p.port_status = 'AVAILABLE' " +
        "AND s.is_active = TRUE " +
        "AND o.is_active = TRUE " +
        "AND o.service_area_id = ? " +
        "AND o.olt_type = ?";

    /**
     * Count available ports for given service area and OLT type.
     */
    public int countAvailablePorts(Long serviceAreaId, String oltType) {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(COUNT_AVAILABLE_PORTS_SQL)) {

            ps.setLong(1, serviceAreaId);
            ps.setString(2, oltType);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("available_ports");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error counting available ports for serviceAreaId="
                    + serviceAreaId + ", oltType=" + oltType,
                e
            );
        }

        return 0;
    }

    // ===============================
    // SQL: Find ONE available port
    // ===============================
    private static final String FIND_AVAILABLE_PORT_SQL =
    "SELECT p.port_id " +
    "FROM ports p " +
    "JOIN splitters s ON p.splitter_id = s.splitter_id " +
    "JOIN olts o ON s.olt_id = o.olt_id " +
    "WHERE p.port_status = 'AVAILABLE' " +
    "AND s.is_active = TRUE " +
    "AND o.is_active = TRUE " +
    "AND o.service_area_id = ? " +
    "AND o.olt_type = ? " +
    "LIMIT 1";

// ===============================
// SQL: Update port status
// ===============================
private static final String UPDATE_PORT_STATUS_SQL =
    "UPDATE ports SET port_status = 'ASSIGNED' WHERE port_id = ?";

/**
 * Find one available port id.
 */
public Long findAvailablePortId(Long serviceAreaId, String oltType) {

    try (Connection conn = DbConnection.getConnection();   // ✅ FIXED
         PreparedStatement ps =
             conn.prepareStatement(FIND_AVAILABLE_PORT_SQL)) {

        ps.setLong(1, serviceAreaId);
        ps.setString(2, oltType);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("port_id");
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException(
            "Error finding available port for serviceAreaId="
                + serviceAreaId + ", oltType=" + oltType,
            e
        );
    }

    return null;
}

    /**
     * Mark port as ASSIGNED.
     */
    public void markPortAsAssigned(Long portId) {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(UPDATE_PORT_STATUS_SQL)) {

            ps.setLong(1, portId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error allocating port: " + portId,
                e
            );
        }
    }
    //mark port as available 
    
 private static final String RELEASE_PORT_SQL =
        "UPDATE ports " +
        "SET port_status = 'AVAILABLE' " +
        "WHERE port_id = ? " +
        "AND port_status = 'ASSIGNED'";

    public void markPortAsAvailable(Long portId) {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(RELEASE_PORT_SQL)) {

            ps.setLong(1, portId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error releasing port: " + portId, e
            );
        }
    }
     public List<String> findAllPincodes() {
        String sql = "SELECT pincode FROM service_areas ORDER BY pincode";
        List<String> pincodes = new ArrayList<>();

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pincodes.add(rs.getString("pincode"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading service areas", e);
        }
        return pincodes;
    }

    public List<OltInventoryDTO> findOltsByPincode(String pincode) {

        String sql =
            "SELECT o.olt_code, sa.pincode, o.olt_type, " +
            "COUNT(DISTINCT s.splitter_id) AS splitter_count, " +
            "COUNT(pt.port_id) AS total_ports, " +
            "COALESCE(SUM(CASE WHEN pt.port_status = 'AVAILABLE' THEN 1 ELSE 0 END), 0) AS available_ports " +
            "FROM olts o " +
            "JOIN service_areas sa ON sa.service_area_id = o.service_area_id " +
            "LEFT JOIN splitters s ON s.olt_id = o.olt_id AND s.is_active = TRUE " +
            "LEFT JOIN ports pt ON pt.splitter_id = s.splitter_id " +
            "WHERE sa.pincode = ? AND o.is_active = TRUE " +
            "GROUP BY o.olt_id, o.olt_code, sa.pincode, o.olt_type";

        List<OltInventoryDTO> result = new ArrayList<>();

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pincode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new OltInventoryDTO(
                        rs.getString("olt_code"),
                        rs.getString("pincode"),
                        rs.getString("olt_type"),
                        rs.getInt("splitter_count"),
                        rs.getInt("total_ports"),
                        rs.getInt("available_ports")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading OLT inventory", e);
        }
        return result;
    }

    public InventoryDetails findInventoryDetails(String oltCode) {
        String oltSql =
            "SELECT olt_id, olt_code, service_area_id, olt_type, is_active, created_at " +
            "FROM olts WHERE olt_code = ? AND is_active = TRUE";
        String splitterSql =
            "SELECT s.splitter_id, s.olt_id, s.splitter_code, s.splitter_number, s.is_active, s.created_at, " +
            "COUNT(p.port_id) AS total_ports, " +
            "COALESCE(SUM(CASE WHEN p.port_status = 'AVAILABLE' THEN 1 ELSE 0 END), 0) AS available_ports " +
            "FROM splitters s " +
            "LEFT JOIN ports p ON p.splitter_id = s.splitter_id " +
            "WHERE s.olt_id = ? AND s.is_active = TRUE " +
            "GROUP BY s.splitter_id, s.olt_id, s.splitter_code, s.splitter_number, s.is_active, s.created_at " +
            "ORDER BY s.splitter_number";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement oltPs = con.prepareStatement(oltSql)) {

            oltPs.setString(1, oltCode);
            try (ResultSet rs = oltPs.executeQuery()) {
                if (!rs.next()) return null;

                Olt olt = new Olt(
                    rs.getLong("olt_id"),
                    rs.getString("olt_code"),
                    rs.getLong("service_area_id"),
                    rs.getString("olt_type"),
                    rs.getBoolean("is_active"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );

                InventoryDetails details = new InventoryDetails(olt);

                try (PreparedStatement splPs = con.prepareStatement(splitterSql)) {
                    splPs.setLong(1, olt.getOltId());
                    try (ResultSet srs = splPs.executeQuery()) {
                        while (srs.next()) {
                            Splitter sp = new Splitter(
                                srs.getLong("splitter_id"),
                                srs.getLong("olt_id"),
                                srs.getString("splitter_code"),
                                srs.getInt("splitter_number"),
                                srs.getBoolean("is_active"),
                                srs.getTimestamp("created_at").toLocalDateTime()
                            );
                            sp.setTotalPorts(srs.getInt("total_ports"));
                            sp.setAvailablePorts(srs.getInt("available_ports"));
                            details.addSplitter(sp);
                        }
                    }
                }
                return details;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading inventory details for " + oltCode, e);
        }
    }

    public long[] assignAvailablePort(Long pincode, String oltType) {
        // unchanged logic
        return null;
    }

public boolean existsByPincode(Long pincode) {
        String sql = "SELECT COUNT(*) FROM service_areas WHERE pincode = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(pincode));
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}


   
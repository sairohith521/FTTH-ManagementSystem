package ftth.repository;

import ftth.config.DbConnection;
import ftth.model.InventoryDetails;
import ftth.model.OLT;
import ftth.model.Port;
import ftth.model.Splitter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepository {

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

    public List<OLT> findOltsByPincode(String pincode) {
        String sql =
        "SELECT o.olt_code, " +
        "sa.pincode, " +
        "o.olt_type, " +
        "COUNT(DISTINCT s.splitter_id) AS splitter_count, " +
        "COUNT(pt.port_id) AS total_ports, " +
        "COALESCE(SUM(CASE WHEN pt.port_status = 'AVAILABLE' THEN 1 ELSE 0 END), 0) AS available_ports " +
        "FROM olts o " +
        "JOIN service_areas sa ON sa.service_area_id = o.service_area_id " +
        "LEFT JOIN splitters s ON s.olt_id = o.olt_id AND s.is_active = TRUE " +
        "LEFT JOIN ports pt ON pt.splitter_id = s.splitter_id " +
        "WHERE sa.pincode = ? AND o.is_active = TRUE " +
        "GROUP BY o.olt_id, o.olt_code, sa.pincode, o.olt_type " +
        "ORDER BY o.olt_code";
        List<OLT> olts = new ArrayList<>();

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pincode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    olts.add(new OLT(
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

        return olts;
    }

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

    public InventoryDetails findInventoryDetails(String oltCode) {
       String oltSql =
        "SELECT o.olt_id, " +
        "o.olt_code, " +
        "sa.pincode, " +
        "o.olt_type, " +
        "COUNT(DISTINCT s.splitter_id) AS splitter_count, " +
        "COUNT(p.port_id) AS total_ports, " +
        "COALESCE(SUM(CASE WHEN p.port_status = 'AVAILABLE' THEN 1 ELSE 0 END), 0) AS available_ports " +
        "FROM olts o " +
        "JOIN service_areas sa ON sa.service_area_id = o.service_area_id " +
        "LEFT JOIN splitters s ON s.olt_id = o.olt_id AND s.is_active = TRUE " +
        "LEFT JOIN ports p ON p.splitter_id = s.splitter_id " +
        "WHERE o.olt_code = ? " +
        "GROUP BY o.olt_id, o.olt_code, sa.pincode, o.olt_type";
        String splitterSql = "SELECT splitter_id, olt_id, splitter_number, is_active FROM splitters WHERE olt_id = ? ORDER BY splitter_number";
        String portSql = "SELECT port_id, splitter_id, port_number, port_status FROM ports WHERE splitter_id = ? ORDER BY port_number";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement oltPs = con.prepareStatement(oltSql)) {

            oltPs.setString(1, oltCode);
            try (ResultSet oltRs = oltPs.executeQuery()) {
                if (!oltRs.next()) {
                    return null;
                }

                InventoryDetails details = new InventoryDetails(new OLT(
                        oltRs.getString("olt_code"),
                        oltRs.getString("pincode"),
                        oltRs.getString("olt_type"),
                        oltRs.getInt("splitter_count"),
                        oltRs.getInt("total_ports"),
                        oltRs.getInt("available_ports")
                ));

                try (PreparedStatement splitterPs = con.prepareStatement(splitterSql)) {
                    splitterPs.setLong(1, findOltDbId(con, "SELECT olt_id FROM olts WHERE olt_code = ?", oltCode));
                    try (ResultSet splitterRs = splitterPs.executeQuery()) {
                        while (splitterRs.next()) {
                            Splitter splitter = new Splitter(
                                    splitterRs.getLong("splitter_id"),
                                    splitterRs.getLong("olt_id"),
                                    splitterRs.getInt("splitter_number"),
                                    splitterRs.getBoolean("is_active")
                            );

                            try (PreparedStatement portPs = con.prepareStatement(portSql)) {
                                portPs.setLong(1, splitter.getSplitterId());
                                try (ResultSet portRs = portPs.executeQuery()) {
                                    while (portRs.next()) {
                                        splitter.addPort(new Port(
                                                portRs.getLong("port_id"),
                                                portRs.getLong("splitter_id"),
                                                portRs.getInt("port_number"),
                                                portRs.getString("port_status")
                                        ));
                                    }
                                }
                            }

                            details.addSplitter(splitter);
                        }
                    }
                }

                return details;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading inventory details", e);
        }
    }

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
    public long[] assignAvailablePort(int pincode, String oltType) {
        String sql =
            "SELECT p.port_id, sa.service_area_id " +
            "FROM ports p " +
            "JOIN splitters s ON s.splitter_id = p.splitter_id " +
            "JOIN olts o ON o.olt_id = s.olt_id " +
            "JOIN service_areas sa ON sa.service_area_id = o.service_area_id " +
            "WHERE sa.pincode = ? AND o.olt_type = ? AND p.port_status = 'AVAILABLE' " +
            "LIMIT 1";
        String updateSql = "UPDATE ports SET port_status = 'ASSIGNED' WHERE port_id = ?";

        try (Connection con = DbConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                long portId = -1, serviceAreaId = -1;
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, String.valueOf(pincode));
                    ps.setString(2, oltType);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) { con.rollback(); return null; }
                    portId = rs.getLong("port_id");
                    serviceAreaId = rs.getLong("service_area_id");
                }
                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setLong(1, portId);
                    ps.executeUpdate();
                }
                con.commit();
                return new long[]{portId, serviceAreaId};
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error assigning port", e);
        }
    }

    public boolean existsByPincode(int pincode) {

    String sql = "SELECT COUNT(*) FROM service_areas WHERE pincode = ?";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, String.valueOf(pincode));

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
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

public int getAvailablePortsByType(int pincode, String oltType) {

    String sql =
        "SELECT COUNT(DISTINCT p.port_id) " +
        "FROM ports p " +
        "JOIN splitters s ON s.splitter_id = p.splitter_id " +
        "JOIN olts o ON o.olt_id = s.olt_id " +
        "JOIN service_areas sa ON sa.service_area_id = o.service_area_id " +
        "WHERE sa.pincode = ? AND o.olt_type = ? AND p.port_status = 'AVAILABLE'";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, String.valueOf(pincode));
        ps.setString(2, oltType);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return 0;
}
}

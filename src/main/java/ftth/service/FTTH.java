package ftth.service;

import java.io.*;
import java.sql.*;
import ftth.config.DbConnection;

public class FTTH {

    private static final String DATA_FILE = "data.txt";

    // =====================================================================
    // FEASIBILITY CHECKS (still use data.txt for port infrastructure)
    // =====================================================================

    public boolean checkPincode(int userPincode) {
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                int filePin = Integer.parseInt(parts[0].trim());
                String subscriber = parts[4].trim();
                if (filePin == userPincode && subscriber.equals("empty")) return true;
            }
        } catch (Exception e) {
            System.out.println("Error reading data.txt: " + e.getMessage());
        }
        return false;
    }

    public int getAvailablePorts(int userPincode) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                int filePin = Integer.parseInt(parts[0].trim());
                String subscriber = parts[4].trim();
                if (filePin == userPincode && subscriber.equals("empty")) count++;
            }
        } catch (Exception e) {
            System.out.println("Error reading ports: " + e.getMessage());
        }
        return count;
    }

    public boolean checkSalary(double salary) {
        return salary > 30000;
    }

    // =====================================================================
    // CUSTOMER LOOKUP — from MySQL
    // Returns: [0]ID [1]Name [2]Pincode [3]Service [4]Price [5]Status
    // =====================================================================

    public String[] findCustomer(String custID) {
        try (Connection con = DbConnection.getConnection()) {
            String sql = "SELECT * FROM customers WHERE customer_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, custID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rowToArray(rs);
        } catch (Exception e) {
            System.out.println("Error finding customer: " + e.getMessage());
        }
        return null;
    }

    public void listAllCustomers() {
        try (Connection con = DbConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM customers");
            boolean found = false;
            System.out.println("\n--- Customer Records ---");
            System.out.printf("%-12s %-20s %-8s %-28s %-7s %-8s%n",
                "CustomerID", "Name", "Pincode", "Service", "Price", "Status");
            System.out.println("-".repeat(85));
            while (rs.next()) {
                System.out.printf("%-12s %-20s %-8d %-28s %-7d %-8s%n",
                    rs.getString("customer_id"), rs.getString("name"),
                    rs.getInt("pincode"), rs.getString("service"),
                    rs.getInt("price"), rs.getString("status"));
                found = true;
            }
            if (!found) System.out.println("No customers found.");
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error listing customers: " + e.getMessage());
        }
    }

    // =====================================================================
    // ADD
    // =====================================================================

    private String generateNextCustomerId() {
        try (Connection con = DbConnection.getConnection()) {
            String sql = "SELECT customer_id FROM customers ORDER BY customer_id DESC LIMIT 1";
            ResultSet rs = con.createStatement().executeQuery(sql);
            if (rs.next()) {
                int num = Integer.parseInt(rs.getString("customer_id").split("-")[1]);
                return String.format("AAHA-%04d", num + 1);
            }
            return "AAHA-0001";
        } catch (Exception e) {
            System.out.println("Error generating customer ID: " + e);
            return null;
        }
    }

    public String addCustomer(String name, int pincode, String service, int price) {
        try {
            String custID = generateNextCustomerId();
            if (custID == null) return null;

            // Assign port in data.txt
            BufferedReader br = new BufferedReader(new FileReader(DATA_FILE));
            StringBuilder sb = new StringBuilder();
            String line;
            boolean assigned = false;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    sb.append(line).append("\n");
                    continue;
                }
                String[] parts = line.split(",");
                int filePin = Integer.parseInt(parts[0].trim());
                String existingSub = parts[4].trim();

                if (!assigned && filePin == pincode && existingSub.equals("empty")) {
                    parts[4] = custID;
                    assigned = true;
                }
                sb.append(parts[0]+","+parts[1]+","+parts[2]+","+parts[3]+","+parts[4]+"\n");
            }
            br.close();

            if (!assigned) return null;

            FileWriter fw = new FileWriter(DATA_FILE);
            fw.write(sb.toString());
            fw.close();

            // Insert into MySQL
            try (Connection con = DbConnection.getConnection()) {
                String sql = "INSERT INTO customers (customer_id, name, pincode, service, price, status) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, custID);
                ps.setString(2, name);
                ps.setInt(3, pincode);
                ps.setString(4, service);
                ps.setInt(5, price);
                ps.setString(6, "ACTIVE");
                ps.executeUpdate();
                System.out.println("[OK] Customer stored in MySQL");
            }

            return custID;
        } catch (Exception e) {
            System.out.println("Error in addCustomer: " + e.getMessage());
            return null;
        }
    }

    // =====================================================================
    // MOVE
    // =====================================================================

    public boolean moveCustomer(String custID, int newPincode) {
        try {
            String[] customer = findCustomer(custID);
            if (customer == null) { System.out.println("Customer ID not found: " + custID); return false; }

            int oldPincode = Integer.parseInt(customer[2]);
            if (oldPincode == newPincode) { System.out.println("Customer is already in pincode " + newPincode); return false; }
            if (!checkPincode(newPincode)) { System.out.println("No available ports in pincode " + newPincode); return false; }

            // Update ports in data.txt
            BufferedReader br = new BufferedReader(new FileReader(DATA_FILE));
            StringBuilder sb = new StringBuilder();
            String line;
            boolean newPortAssigned = false;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) { sb.append(line).append("\n"); continue; }
                String[] parts = line.split(",");
                int filePin = Integer.parseInt(parts[0].trim());
                String sub = parts[4].trim();

                if (filePin == oldPincode && sub.equals(custID)) parts[4] = "empty";
                else if (!newPortAssigned && filePin == newPincode && sub.equals("empty")) {
                    parts[4] = custID;
                    newPortAssigned = true;
                }
                sb.append(parts[0]+","+parts[1]+","+parts[2]+","+parts[3]+","+parts[4]+"\n");
            }
            br.close();

            if (!newPortAssigned) { System.out.println("Failed to assign new port."); return false; }

            FileWriter fw = new FileWriter(DATA_FILE);
            fw.write(sb.toString());
            fw.close();

            // Update pincode in MySQL
            try (Connection con = DbConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("UPDATE customers SET pincode = ? WHERE customer_id = ?");
                ps.setInt(1, newPincode);
                ps.setString(2, custID);
                ps.executeUpdate();
                System.out.println("[OK] Customer pincode updated in MySQL");
            }

            return true;
        } catch (Exception e) {
            System.out.println("Error in moveCustomer: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // CHANGE
    // =====================================================================

    public boolean changeCustomer(String custID, String newService, int newPrice) {
        try {
            String[] customer = findCustomer(custID);
            if (customer == null) { System.out.println("Customer ID not found: " + custID); return false; }

            try (Connection con = DbConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("UPDATE customers SET service = ?, price = ? WHERE customer_id = ?");
                ps.setString(1, newService);
                ps.setInt(2, newPrice);
                ps.setString(3, custID);
                ps.executeUpdate();
                System.out.println("[OK] Customer plan updated in MySQL");
            }

            System.out.println("Service changed for " + custID);
            System.out.println("  Old Service: " + customer[3] + " @ Rs." + customer[4]);
            System.out.println("  New Service: " + newService + " @ Rs." + newPrice);
            return true;
        } catch (Exception e) {
            System.out.println("Error in changeCustomer: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // DELETE (Disconnect) — uses customer_connections table
    // =====================================================================

    public String[] findConnection(long connectionId) {
        String sql = """
            SELECT cc.connection_id, cc.customer_name, sa.pincode,
                   pa.plan_name, pa.price, cc.connection_status,
                   o.olt_code, s.splitter_number, p.port_number, cc.port_id
            FROM customer_connections cc
            JOIN plan_admin pa ON pa.plan_id = cc.plan_id
            JOIN ports p ON p.port_id = cc.port_id
            JOIN splitters s ON s.splitter_id = p.splitter_id
            JOIN olts o ON o.olt_id = s.olt_id
            JOIN service_areas sa ON sa.service_area_id = cc.service_area_id
            WHERE cc.connection_id = ?
            """;
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
        String sql = """
            SELECT cc.connection_id, cc.customer_name, sa.pincode,
                   pa.plan_name, pa.price, cc.connection_status,
                   o.olt_code, s.splitter_number, p.port_number
            FROM customer_connections cc
            JOIN plan_admin pa ON pa.plan_id = cc.plan_id
            JOIN ports p ON p.port_id = cc.port_id
            JOIN splitters s ON s.splitter_id = p.splitter_id
            JOIN olts o ON o.olt_id = s.olt_id
            JOIN service_areas sa ON sa.service_area_id = cc.service_area_id
            WHERE cc.connection_status = 'ACTIVE'
            ORDER BY cc.connection_id
            """;
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
                    System.out.println("Connection not found or already deleted.");
                    con.rollback();
                    return false;
                }
                long portId = rs.getLong("port_id");

                // Delete customer from customer_connections
                PreparedStatement deletePs = con.prepareStatement("DELETE FROM customer_connections WHERE connection_id = ?");
                deletePs.setLong(1, connectionId);
                deletePs.executeUpdate();

                // Free the port
                PreparedStatement freePs = con.prepareStatement("UPDATE ports SET port_status = 'AVAILABLE' WHERE port_id = ?");
                freePs.setLong(1, portId);
                freePs.executeUpdate();

                con.commit();
                System.out.println("[OK] Customer deleted and port freed.");
                return true;
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            System.out.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    private String[] rowToArray(ResultSet rs) throws SQLException {
        return new String[] {
            rs.getString("customer_id"),
            rs.getString("name"),
            String.valueOf(rs.getInt("pincode")),
            rs.getString("service"),
            String.valueOf(rs.getInt("price")),
            rs.getString("status")
        };
    }
}

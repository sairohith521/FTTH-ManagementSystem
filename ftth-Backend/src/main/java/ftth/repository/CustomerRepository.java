package ftth.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ftth.model.Customer;
import ftth.model.enums.CustomerStatus;
import ftth.config.DbConnection;

public class CustomerRepository {

    public String save(Customer customer) {

    String insertSql =
        "INSERT INTO customers " +
        "(full_name, email, salary, status) " +
        "VALUES (?, ?, ?, ?)";

    String updateCodeSql =
        "UPDATE customers SET customer_code = ? WHERE customer_id = ?";

    try (Connection conn = DbConnection.getConnection()) {

        // ✅ 1️⃣ Insert customer
        try (PreparedStatement ps =
                 conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getEmail());
            ps.setBigDecimal(3, customer.getSalary());
            ps.setString(4, customer.getStatus().name());

            ps.executeUpdate();

            // ✅ 2️⃣ Get generated customer_id
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long customerId = rs.getLong(1);

                    // ✅ 3️⃣ Generate customer code
                    String customerCode =
                        String.format("AAHA-%04d", customerId);

                    // ✅ 4️⃣ Update customer_code
                    try (PreparedStatement updatePs =
                             conn.prepareStatement(updateCodeSql)) {

                        updatePs.setString(1, customerCode);
                        updatePs.setLong(2, customerId);
                        updatePs.executeUpdate();
                    }

                    // ✅ update object also
                    customer.setCustomerId(customerId);
                    customer.setCustomerCode(customerCode);

                    return customerCode;
                }
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error saving customer", e);
    }

    throw new RuntimeException("Failed to generate customer ID");
}
public void printCustomerCard(Customer c) {

    String sql = "SELECT p.plan_name, sa.pincode " +
                 "FROM customer_connections cc " +
                 "JOIN plans p ON p.plan_id = cc.plan_id " +
                 "JOIN service_areas sa ON sa.service_area_id = cc.service_area_id " +
                 "WHERE cc.customer_id = ? AND cc.connection_status = 'ACTIVE'";

    String planName = "N/A";
    String pincode = "N/A";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setLong(1, c.getCustomerId());
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            planName = rs.getString("plan_name");
            pincode = rs.getString("pincode");
        }

    } catch (SQLException e) {
        System.out.println("Error fetching plan/pincode");
    }

    System.out.println("\n+----------------------------------+");
    System.out.println(" Customer Code : " + c.getCustomerCode());
    System.out.println(" Name          : " + c.getFullName());
    System.out.println(" Email         : " + c.getEmail());
    System.out.println(" Status        : " + c.getStatus());
    System.out.println(" Plan Name     : " + planName);
    System.out.println(" Pincode       : " + pincode);
    System.out.println("+----------------------------------+");
}
public Customer findByCustomerCode(String customerCode) {

       String sql =
        "SELECT c.customer_id, c.customer_code, c.full_name, " +
        "c.email, c.salary, c.status, c.created_at, sa.pincode " +
        "FROM customers c " +
        "LEFT JOIN customer_connections cc " +
        "ON c.customer_id = cc.customer_id " +
        "LEFT JOIN service_areas sa " +
        "ON cc.service_area_id = sa.service_area_id " +
        "WHERE c.customer_code = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, customerCode);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error finding customer", e);
        }

        return null;
    }
     public Customer findById(Long customerId) {

    String sql =
        "SELECT customer_id, customer_code, full_name, email, salary, status " +
        "FROM customers " +
        "WHERE customer_id = ?";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setLong(1, customerId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getLong("customer_id"));
                c.setCustomerCode(rs.getString("customer_code"));
                c.setFullName(rs.getString("full_name"));
                c.setEmail(rs.getString("email"));
                c.setSalary(rs.getBigDecimal("salary"));
                c.setStatus(CustomerStatus.valueOf(rs.getString("status")));
                return c;
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException(
            "Error fetching customer by id=" + customerId, e
        );
    }

    return null;
}

    // ✅ List all customers
    public List<Customer> findAll() {

       String sql =
    "SELECT c.customer_id, c.customer_code, c.full_name, " +
    "c.email, c.salary, c.status, c.created_at, sa.pincode " +
    "FROM customers c " +
    "LEFT JOIN customer_connections cc ON c.customer_id = cc.customer_id " +
    "LEFT JOIN service_areas sa ON cc.service_area_id = sa.service_area_id " +
    "ORDER BY c.customer_code";

        List<Customer> list = new ArrayList<>();

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error listing customers", e);
        }

        return list;
    }
    public Customer findByCode(String customerCode) {

    String sql =
        "SELECT customer_id, customer_code, full_name, email, salary, status, created_at " +
        "FROM customers WHERE customer_code = ?";

    try (Connection conn = DbConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, customerCode);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapRow(rs); // ✅ reuse mapper
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException(
            "Error finding customer with code=" + customerCode, e
        );
    }

    return null;
}
private Customer mapRow(ResultSet rs) throws SQLException {

    Customer c = new Customer();

    c.setCustomerId(rs.getLong("customer_id"));
    c.setCustomerCode(rs.getString("customer_code"));
    c.setFullName(rs.getString("full_name"));
    c.setEmail(rs.getString("email"));
    c.setSalary(rs.getBigDecimal("salary"));
    c.setStatus(CustomerStatus.valueOf(rs.getString("status")));
    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

    return c;
}
public boolean updateCustomerPlan(String custCode, long planId) {

    String sql = "UPDATE customers SET plan_id = ? WHERE customer_code = ?";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setLong(1, planId);
        ps.setString(2, custCode);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
public boolean disconnectCustomer(long customerId) {

   String sql =
        "UPDATE customer_connections " +
        "SET connection_status = 'DISCONNECTED', " +
        "disconnected_on = CURRENT_DATE " +
        "WHERE customer_id = ?";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setLong(1, customerId);
        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
public boolean updatePlan(long customerId, long newPlanId) {

   String sql =
        "UPDATE customer_connections " +
        "SET plan_id = ? " +
        "WHERE customer_id = ? " +
        "AND connection_status = 'ACTIVE'";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setLong(1, newPlanId);
        ps.setLong(2, customerId);

        int rows = ps.executeUpdate();
        return rows > 0;

    } catch (Exception e) {
        throw new RuntimeException("Failed to update customer plan", e);
    }
}
public boolean updateServiceArea(long customerId, int newPincode) {

    String findServiceAreaSql =
        "SELECT service_area_id " +
        "FROM service_areas " +
        "WHERE pincode = ? " +
        "AND is_active = TRUE";

    String updateConnectionSql =
        "UPDATE customer_connections " +
        "SET service_area_id = ? " +
        "WHERE customer_id = ? " +
        "AND connection_status = 'ACTIVE'";

    try (Connection con = DbConnection.getConnection()) {   // ✅ FIXED
        con.setAutoCommit(false);

        // 1️⃣ Find service_area_id from pincode
        Long serviceAreaId = null;

        try (PreparedStatement ps = con.prepareStatement(findServiceAreaSql)) {
            ps.setInt(1, newPincode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    serviceAreaId = rs.getLong("service_area_id");
                }
            }
        }

        if (serviceAreaId == null) {
            con.rollback();
            return false; // pincode not found
        }

        // 2️⃣ Update customer connection
        int updated;
        try (PreparedStatement ps =
                     con.prepareStatement(updateConnectionSql)) {

            ps.setLong(1, serviceAreaId);
            ps.setLong(2, customerId);
            updated = ps.executeUpdate();
        }

        con.commit();
        return updated > 0;

    } catch (Exception e) {
        throw new RuntimeException("Failed to move customer", e);
    }
}
public Long getCurrentPlanId(long customerId) {

    String sql =
        "SELECT plan_id " +
        "FROM customer_connections " +
        "WHERE customer_id = ? " +
        "AND connection_status = 'ACTIVE'";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setLong(1, customerId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("plan_id");
            }
        }

    } catch (Exception e) {
        throw new RuntimeException("Failed to fetch current planId", e);
    }

    return null; // no active connection
}

private static final String FIND_BY_EMAIL_SQL =
        "SELECT customer_id, customer_code, full_name, email, salary, status, created_at " +
        "FROM customers " +
        "WHERE email = ?";

   private static final String INSERT_SQL =
    "INSERT INTO customers " +
    "(full_name, email, salary, status, customer_code) " +
    "VALUES (?, ?, ?, ?, ?)";


    // ===============================
    // Public Repository Methods
    // ===============================

    /**
     * Find customer by email.
     *
     * @param email customer email
     * @return Customer if found, else null
     */
    public Customer findByEmail(String email) {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL_SQL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error fetching customer by email: " + email, e
            );
        }

        return null;
    }

    /**
     * Insert new customer.
     */
    public void insert(Customer customer) {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(INSERT_SQL,
                             PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getEmail());
            ps.setBigDecimal(3, customer.getSalary());
            ps.setString(4, customer.getStatus().name());
            ps.setString(5,customer.getCustomerCode());

            ps.executeUpdate();

            // get generated customer_id
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    customer.setCustomerId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error inserting customer: " + customer.getEmail(), e
            );
        }
    }
    // ===============================
    // Private Mapping Method
    // ===============================

    private Customer mapRowToCustomer(ResultSet rs)
            throws SQLException {

        Long customerId = rs.getLong("customer_id");
        String customerCode = rs.getString("customer_code");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        BigDecimal salary = rs.getBigDecimal("salary");
        CustomerStatus status =
            CustomerStatus.valueOf(rs.getString("status"));
        LocalDateTime createdAt =
            rs.getTimestamp("created_at").toLocalDateTime();

        return new Customer(
            customerId,
            customerCode,
            fullName,
            email,
            salary,
            status,
            createdAt
        );
    }

}



    
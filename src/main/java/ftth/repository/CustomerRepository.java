package ftth.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import ftth.model.Customer;
import ftth.config.DbConnection;

public class CustomerRepository {

    public void save(Customer customer) {

        String sql = "INSERT INTO customers " +
                "(customer_code, full_name, email, pincode, salary, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getCustomerCode());
            ps.setString(2, customer.getFullName());
            ps.setString(3, customer.getEmail());
            ps.setInt(4, customer.getPincode());
            ps.setDouble(5, customer.getSalary());
            ps.setString(6, customer.getStatus());

            ps.executeUpdate();

            System.out.println("✅ Customer inserted into DB");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Customer findByCode(String customerCode) {

    String sql = "SELECT * FROM customers WHERE customer_code = ?";

    try (Connection conn = DbConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, customerCode);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Customer customer = new Customer();

            customer.setCustomerId(rs.getLong("customer_id"));
            customer.setCustomerCode(rs.getString("customer_code"));
            customer.setFullName(rs.getString("full_name"));
            customer.setEmail(rs.getString("email"));
            customer.setPincode(rs.getInt("pincode"));
            customer.setSalary(rs.getDouble("salary"));
            customer.setStatus(rs.getString("status"));
            customer.setCreatedAt(rs.getTimestamp("created_at"));

            return customer;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

    public Integer findPincodeByCustomerCode(String customerCode) {
        String sql = "SELECT pincode FROM customers WHERE customer_code = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customerCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("pincode");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updatePincodeByCustomerCode(String customerCode, int newPincode) {
        String sql = "UPDATE customers SET pincode = ? WHERE customer_code = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newPincode);
            ps.setString(2, customerCode);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
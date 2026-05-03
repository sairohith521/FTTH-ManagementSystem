package ftth.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import ftth.model.enums.CustomerStatus;
import ftth.util.CustomerID;

/**
 * Domain model representing a customer.
 * Maps to the 'customers' table.
 */
public class Customer {

    // ===============================
    // Fields (DB columns)
    // ===============================

    private Long customerId;          // customer_id (PK)
    private String customerCode;      // customer_code
    private String fullName;          // full_name
    private String email;             // email
    private BigDecimal salary;        // salary
    private CustomerStatus status;    // status (ENUM)
    private LocalDateTime createdAt;  // created_at


    // ===============================
    // Constructors
    // ===============================

    // No-args constructor (required by frameworks)
    public Customer() {
    }

    // Constructor for NEW customer (before DB insert)
    public Customer(String fullName,
                    String email,
                    BigDecimal salary) {
        this.fullName = fullName;
        this.email = email;
        this.salary = salary;
        this.status = CustomerStatus.ACTIVE;
        this.customerCode=CustomerID.generateNextCustomerCode();
    }

    // Full constructor (used when reading from DB)
    public Customer(Long customerId,
                    String customerCode,
                    String fullName,
                    String email,
                    BigDecimal salary,
                    CustomerStatus status,
                    LocalDateTime createdAt) {
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.fullName = fullName;
        this.email = email;
        this.salary = salary;
        this.status = status;
        this.createdAt = createdAt;
    }


    // ===============================
    // Getters and Setters
    // ===============================

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    // ===============================
    // Domain helper methods
    // ===============================

    public boolean isActive() {
        return CustomerStatus.ACTIVE.equals(this.status);
    }

    public void deactivate() {
        this.status = CustomerStatus.INACTIVE;
    }

    public void markDeleted() {
        this.status = CustomerStatus.DELETED;
    }


    // ===============================
    // toString (debugging)
    // ===============================

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", customerCode='" + customerCode + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", salary=" + salary +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
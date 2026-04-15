package ftth.model;

import java.sql.Timestamp;

public class Customer {

    private long customerId;
    private String customerCode;
    private String fullName;
    private String email;
    private double salary;
    private String status;
    private Timestamp createdAt;
    private long planId;
    private int pincode;

    public Customer() {
    }

    // 🔹 Parameterized Constructor
    public Customer(long customerId, String customerCode, String fullName,
                    String email, double salary, String status,long planId, Timestamp createdAt) {
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.fullName = fullName;
        this.email = email;
        this.salary = salary;
        this.status = status;
        this.createdAt = createdAt;
        this.planId=planId;
    }

    // 🔹 Getters and Setters

    public long getCustomerId() {
        return customerId;
    }
    public int getPincode() {
    return pincode;
}

public void setPincode(int pincode) {
    this.pincode = pincode;
}
    public long getPlanId() {
        return planId;
    }
    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public void setCustomerId(long customerId) {
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

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // 🔹 toString() (VERY IMPORTANT FOR DEBUGGING)

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", customerCode='" + customerCode + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
            ", pincode=" + pincode +
                ", salary=" + salary +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
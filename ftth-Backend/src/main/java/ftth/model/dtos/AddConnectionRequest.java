package ftth.model.dtos;
/**
 * DTO used to carry user input data
 * from Controller -> Service when creating a new customer connection.
 *
 * This class is NOT persisted to database.
 */
public class AddConnectionRequest {

    // ===============================
    // Fields (raw user input)
    // ===============================

    private String customerName;
    private long planId;
    private double salary;
    private long pincode;
    private String oltType;
    private String email;


    // ===============================
    // Constructors
    // ===============================

    // No-args constructor (optional but good practice)
    public AddConnectionRequest() {
    }

    // All-args constructor (used by Controller)
    public AddConnectionRequest(String customerName,
                                long planId,
                                double salary,
                                long pincode,
                                String oltType,
                                String email) {
        this.customerName = customerName;
        this.planId = planId;
        this.salary = salary;
        this.pincode = pincode;
        this.oltType = oltType;
        this.email = email;
    }


    // ===============================
    // Getters and Setters
    // ===============================

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public long getPincode() {
        return pincode;
    }

    public void setPincode(long pincode) {
        this.pincode = pincode;
    }

    public String getOltType() {
        return oltType;
    }

    public void setOltType(String oltType) {
        this.oltType = oltType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    // ===============================
    // toString (for debugging/logging)
    // ===============================

    @Override
    public String toString() {
        return "AddConnectionRequest{" +
                "customerName='" + customerName + '\'' +
                ", planId=" + planId +
                ", salary=" + salary +
                ", pincode=" + pincode +
                ", oltType='" + oltType + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

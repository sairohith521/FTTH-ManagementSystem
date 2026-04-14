package ftth.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Bill {

    private long billId;
    private String billNo;

    private long customerId;
    private long connectionId;

    private Date billDate;
    private Date dueDate;

    private double planCharge;
    private double gstAmount;
    private double totalAmount;

    private String billStatus;

    private Timestamp createdAt;

    // 🔹 Default Constructor
    public Bill() {
    }

    // 🔹 Parameterized Constructor
    public Bill(long billId, String billNo, long customerId, long connectionId,
                Date billDate, Date dueDate,
                double planCharge, double gstAmount, double totalAmount,
                String billStatus, Timestamp createdAt) {

        this.billId = billId;
        this.billNo = billNo;
        this.customerId = customerId;
        this.connectionId = connectionId;
        this.billDate = billDate;
        this.dueDate = dueDate;
        this.planCharge = planCharge;
        this.gstAmount = gstAmount;
        this.totalAmount = totalAmount;
        this.billStatus = billStatus;
        this.createdAt = createdAt;
    }

    // 🔹 Getters and Setters

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public double getPlanCharge() {
        return planCharge;
    }

    public void setPlanCharge(double planCharge) {
        this.planCharge = planCharge;
    }

    public double getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(double gstAmount) {
        this.gstAmount = gstAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // 🔹 toString()

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", billNo='" + billNo + '\'' +
                ", customerId=" + customerId +
                ", connectionId=" + connectionId +
                ", billDate=" + billDate +
                ", dueDate=" + dueDate +
                ", planCharge=" + planCharge +
                ", gstAmount=" + gstAmount +
                ", totalAmount=" + totalAmount +
                ", billStatus='" + billStatus + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
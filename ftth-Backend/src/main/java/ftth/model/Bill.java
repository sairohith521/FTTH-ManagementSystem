package ftth.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import ftth.model.enums.BillStatus;

/**
 * Domain model representing a bill.
 * Maps to the 'bills' table.
 */
public class Bill {

    // ===============================
    // Fields (DB columns)
    // ===============================

    private Long billId;                // bill_id (PK)
    private String billNo;              // bill_no
    private Long customerId;            // customer_id (FK)
    private Long connectionId;          // connection_id (FK)

    private LocalDate billDate;         // bill_date
    private LocalDate dueDate;          // due_date

    private BigDecimal planCharge;      // plan_charge
    private BigDecimal gstAmount;       // gst_amount
    private BigDecimal totalAmount;     // total_amount

    private BillStatus billStatus;      // bill_status
    private LocalDateTime createdAt;    // created_at


    // ===============================
    // Constructors
    // ===============================

    // No-args constructor (required by frameworks)
    public Bill() {
    }

    // Constructor for NEW bill generation
    public Bill(String billNo,
                Long customerId,
                Long connectionId,
                LocalDate billDate,
                LocalDate dueDate,
                BigDecimal planCharge,
                BigDecimal gstAmount) {

        this.billNo = billNo;
        this.customerId = customerId;
        this.connectionId = connectionId;
        this.billDate = billDate;
        this.dueDate = dueDate;
        this.planCharge = planCharge;
        this.gstAmount = gstAmount;
        this.totalAmount = planCharge.add(gstAmount);
        this.billStatus = BillStatus.GENERATED;
    }

    // Full constructor (used when reading from DB)
    public Bill(Long billId,
                String billNo,
                Long customerId,
                Long connectionId,
                LocalDate billDate,
                LocalDate dueDate,
                BigDecimal planCharge,
                BigDecimal gstAmount,
                BigDecimal totalAmount,
                BillStatus billStatus,
                LocalDateTime createdAt) {

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


    // ===============================
    // Getters and Setters
    // ===============================

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Long connectionId) {
        this.connectionId = connectionId;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getPlanCharge() {
        return planCharge;
    }

    public void setPlanCharge(BigDecimal planCharge) {
        this.planCharge = planCharge;
        recalculateTotal();
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
        recalculateTotal();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BillStatus getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(BillStatus billStatus) {
        this.billStatus = billStatus;
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

    public boolean isPaid() {
        return BillStatus.PAID.equals(this.billStatus);
    }

    public boolean isOverdue(LocalDate today) {
        return !isPaid() && today.isAfter(this.dueDate);
    }

    public void markPaid() {
        this.billStatus = BillStatus.PAID;
    }

    public void markOverdue() {
        this.billStatus = BillStatus.OVERDUE;
    }

    private void recalculateTotal() {
        if (planCharge != null && gstAmount != null) {
            this.totalAmount = planCharge.add(gstAmount);
        }
    }


    // ===============================
    // toString (debugging)
    // ===============================

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
                ", billStatus=" + billStatus +
                ", createdAt=" + createdAt +
                '}';
    }
}
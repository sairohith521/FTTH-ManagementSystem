package ftth.service;
import java.time.LocalDate;
import ftth.model.*;
import ftth.model.enums.BillStatus;

import java.math.BigDecimal;
import java.sql.*;
import ftth.repository.*;
import ftth.util.BillUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillService {

    private final BillRepository billRepository;

    public BillService(BillRepository billRepository) {
        this.billRepository =billRepository;
    }
    public Bill generateBill(Customer customer,
                         CustomerConnection connection,
                         Plan plan) {

    // ✅ Use BigDecimal for money
    BigDecimal planCharge = plan.getMonthlyPrice();

    BigDecimal gstAmount =
        planCharge.multiply(new BigDecimal("0.18"));

    // ✅ Create bill using proper constructor
    Bill bill = new Bill(
        BillUtil.generateBillNo(),              // bill_no
        customer.getCustomerId(),               // customer_id
        connection.getConnectionId(),           // connection_id
        LocalDate.now(),                        // bill_date
        LocalDate.now().plusMonths(1),          // due_date
        planCharge,
        gstAmount
    );

    return bill;
}


    // ✅ 1️⃣ Generate Monthly Bill
    public Bill generateMonthlyBill(Customer customer,
                                long connectionId,
                                Plan plan) {

    // 1️⃣ Determine bill date (10th rule)
    LocalDate today = LocalDate.now();

    LocalDate billDate =
        (today.getDayOfMonth() <= 10)
            ? today.withDayOfMonth(10)
            : today.plusMonths(1).withDayOfMonth(10);

    LocalDate dueDate = billDate.plusDays(5);

    // 2️⃣ Use BigDecimal for money
    BigDecimal planCharge = plan.getMonthlyPrice();
    BigDecimal gstAmount =
        planCharge.multiply(new BigDecimal("0.18"));

    // 3️⃣ Create Bill using constructor (status = GENERATED)
    Bill bill = new Bill(
        BillUtil.generateBillNo(),
        customer.getCustomerId(),
        connectionId,
        billDate,
        dueDate,
        planCharge,
        gstAmount
    );

    // 4️⃣ Persist bill
    billRepository.insert(bill);

    return bill;
}

    // ✅ 2️⃣ Print Bill
    public void printBill(Bill bill, Customer customer) {

    System.out.println("\n+==========================================+");
    System.out.println("|         AAHA TELECOM - INVOICE           |");
    System.out.println("+==========================================+");

    System.out.printf("| %-14s : %-24s |%n", "Bill No", bill.getBillNo());
    System.out.printf("| %-14s : %-24s |%n", "Bill Date", bill.getBillDate());
    System.out.printf("| %-14s : %-24s |%n", "Due Date", bill.getDueDate());

    System.out.println("+------------------------------------------+");

    System.out.printf("| %-14s : %-24s |%n", "Customer ID", customer.getCustomerCode());
    System.out.printf("| %-14s : %-24s |%n", "Name", customer.getFullName());

    System.out.println("+------------------------------------------+");

    System.out.printf("| %-14s : Rs %-20s |%n",
            "Plan Charge",
            bill.getPlanCharge().setScale(2)
    );

    System.out.printf("| %-14s : Rs %-20s |%n",
            "GST (18%)",
            bill.getGstAmount().setScale(2)
    );

    System.out.println("| ---------------------------------------- |");

    System.out.printf("| %-14s : Rs %-20s |%n",
            "TOTAL",
            bill.getTotalAmount().setScale(2)
    );

    System.out.println("+==========================================+");
}

    // ===============================
    // CREATE — FIRST BILL
    // ===============================
    public Bill generateFirstBill(Long customerId,
                                  Long connectionId,
                                  Plan plan) {

        BigDecimal planCharge = plan.getMonthlyPrice();
        BigDecimal gstAmount =
            planCharge.multiply(new BigDecimal("0.18"));

        Bill bill = new Bill(
            BillUtil.generateBillNo(),
            customerId,
            connectionId,
            LocalDate.now(),
            LocalDate.now().plusMonths(1),
            planCharge,
            gstAmount
        );

        billRepository.insert(bill);
        return bill;
    }

    // ===============================
    // READ
    // ===============================
    public Bill getBillById(Long billId) {
        return billRepository.findById(billId);
    }

    public List<Bill> getBillsForCustomer(Long customerId) {
        return billRepository.findByCustomerId(customerId);
    }

    // ===============================
    // UPDATE — PAY BILL
    // ===============================
    public void payBill(Long billId) {

        Bill bill = billRepository.findById(billId);

        if (bill == null) {
            throw new RuntimeException("Bill not found");
        }

        if (bill.getBillStatus() == BillStatus.PAID) {
            throw new RuntimeException("Bill already paid");
        }

        billRepository.markAsPaid(billId);
    }

    // ===============================
    // UPDATE — OVERDUE CHECK
    // ===============================
    public void markOverdueIfRequired(Long billId) {

        Bill bill = billRepository.findById(billId);

        if (bill != null &&
            bill.getBillStatus() == BillStatus.GENERATED &&
            LocalDate.now().isAfter(bill.getDueDate())) {

            billRepository.markAsOverdue(billId);
        }
    }
}




    
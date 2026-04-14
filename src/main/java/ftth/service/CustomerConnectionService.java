package ftth.service;

import java.time.LocalDate;

import ftth.model.Customer;
import ftth.model.Plan;
import ftth.repository.CustomerRepository;
import ftth.util.InputUtil;

public class CustomerConnectionService {

    private FTTH ftth;
    private EmailService email;
    private InventoryService inventoryService;
    private PlanService planService;
    private CustomerRepository customerRepo;


    public CustomerConnectionService(FTTH ftth,CustomerRepository customerRepo,
                                     EmailService email,
                                     InventoryService inventoryService,
                                     PlanService planService) {
        this.ftth = ftth;
        this.email = email;
        this.inventoryService = inventoryService;
        this.planService = planService;
        this.customerRepo=customerRepo;
    }

    public void addCustomer(String name, Long planId,
                        int pincode, double salary,
                        boolean confirm,String gmail) {


Plan selectedPlan = planService.findPlanById(planId);

if (selectedPlan == null) {
    System.out.println("Invalid plan selected.");
    return;
}

String service = selectedPlan.getName();
int price = (int) selectedPlan.getPrice();
long planID=selectedPlan.getId();
    // 🔹 Pincode check (KEEP SAME for now)
    // if (!ftth.checkPincode(pincode)) {
    //     System.out.println("Service NOT available in pincode " + pincode);
    //     email.sendNoOLTEmail(pincode);
    //     return;
    // }
    if (!inventoryService.checkPincode(pincode)) {
    System.out.println("Service NOT available in pincode " + pincode);
    email.sendNoOLTEmail(pincode);
    return;
}

    int ports = inventoryService.getAvailablePorts(pincode);

if (ports <= 0) {
    System.out.println("No ports available.");
    email.sendNoOLTEmail(pincode);
    return;
}

    // 🔹 Salary check
    if (!ftth.checkSalary(salary)) {
        System.out.println("Salary below eligibility.");
        return;
    }

    if (!confirm) return;

    // 🔥 NEW PART (DB SAVE)
    Customer customer = new Customer();

    String custID = "AAHA-" + System.currentTimeMillis(); // simple unique id

    customer.setCustomerCode(custID);
    customer.setFullName(name);
    customer.setEmail(gmail);
    customer.setSalary(salary);
    customer.setStatus("ACTIVE");
    customer.setPlanId(planID);
    customerRepo.save(customer);

    // 🔹 Billing logic (UNCHANGED)
    LocalDate orderDate = LocalDate.now();
    LocalDate billDate = (orderDate.getDayOfMonth() <= 10)
            ? orderDate.withDayOfMonth(10)
            : orderDate.plusMonths(1).withDayOfMonth(10);

    // 🔹 Output (UNCHANGED)
    System.out.println("\nOrder Confirmed!");
    System.out.println("Customer ID  : " + custID);
    System.out.println("Name         : " + name);
    System.out.println("Service      : " + service);
    System.out.println("Price        : Rs." + price);
    System.out.println("Billing Date : " + billDate);

    // 🔹 Email (UNCHANGED)
    email.sendOrderConfirmationEmail(
            "user@gmail.com", name, pincode, service, price);
}
    public void moveCustomer(String custID, int newPin, boolean confirm) {

    // 🔹 Find customer
    String[] customer = ftth.findCustomer(custID);

    if (customer == null) {
        System.out.println("Customer ID not found.");
        return;
    }

    System.out.println("Current Pincode : " + customer[2]);
    System.out.println("Current Port    : " +
            customer[3] + "/" + customer[4] + "/Port" + customer[5]);

    // 🔹 Check new location
    if (!ftth.checkPincode(newPin)) {
        System.out.println("No available ports in pincode " + newPin);
        email.sendNoOLTEmail(newPin);
        return;
    }

    if (!confirm) return;

    // 🔹 Move logic
    boolean moved = ftth.moveCustomer(custID, newPin);

    if (moved) {
        System.out.println("Customer " + custID +
                " moved to pincode " + newPin + " successfully.");
    } else {
        System.out.println("Move failed.");
    }
}
public Customer getCustomer(String customerCode) {
    return customerRepo.findByCode(customerCode);
}
public void changePlan(String custID, long planId, boolean confirm) {

    // 🔹 Find customer from DB
    Customer customer = customerRepo.findByCode(custID);

    if (customer == null) {
        System.out.println("Customer ID not found.");
        return;
    }

    // 🔹 Get current plan
    Plan currentPlan = planService.findPlanById(customer.getPlanId());

    if (currentPlan != null) {
        System.out.println("Current Service : " 
            + currentPlan.getName() + " @ Rs." + currentPlan.getPrice());
    }

    // 🔹 Get new plan from DB
    Plan newPlan = planService.findPlanById(planId);

    if (newPlan == null) {
        System.out.println("Invalid plan selected.");
        return;
    }

    if (!confirm) return;

    // 🔹 Update DB
    boolean changed = customerRepo.updateCustomerPlan(custID, planId);

    if (changed) {
        System.out.println("\nService updated successfully.");
        System.out.println("New Plan : " + newPlan.getName());
        System.out.println("Price    : Rs." + newPlan.getPrice());
    } else {
        System.out.println("Change failed.");
    }
}
public void disconnectCustomer(String custID, boolean confirm) {

    // 🔹 Find customer
    String[] customer = ftth.findCustomer(custID);

    if (customer == null) {
        System.out.println("Customer ID not found.");
        return;
    }

    // 🔹 Show details
    System.out.println("Customer : " + customer[1]);
    System.out.println("Pincode  : " + customer[2]);
    System.out.println("Port     : " +
            customer[3] + "/" + customer[4] + "/Port" + customer[5]);
    System.out.println("Service  : " + customer[6]);

    if (!confirm) return;

    // 🔹 Disconnect logic
    boolean deleted = ftth.deleteCustomer(custID);

    if (deleted) {
        System.out.println("Customer " + custID +
                " disconnected. Port is now free.");
    } else {
        System.out.println("Disconnect failed.");
    }
}
public void listAllCustomers() {
    ftth.listAllCustomers();
}
public void lookupCustomerById(String custID) {

    String[] customer = ftth.findCustomer(custID);

    if (customer == null) {
        System.out.println("Not found.");
        return;
    }

    System.out.println("\nID      : " + customer[0]);
    System.out.println("Name    : " + customer[1]);
    System.out.println("Pincode : " + customer[2]);
    System.out.println("OLT     : " + customer[3]);
    System.out.println("SPL     : " + customer[4]);
    System.out.println("Port    : " + customer[5]);
    System.out.println("Service : " + customer[6]);
    System.out.println("Price   : Rs." + customer[7] + "/month");
    System.out.println("Status  : " + customer[8]);
}
}
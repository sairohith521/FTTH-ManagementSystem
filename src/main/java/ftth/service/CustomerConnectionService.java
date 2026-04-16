package ftth.service;

import java.time.LocalDate;

import ftth.model.Customer;
import ftth.model.Plan;
import ftth.repository.CustomerConnectionRepository;
import ftth.repository.CustomerRepository;
import ftth.util.InputUtil;

public class CustomerConnectionService {

    private FTTH ftth;
    private EmailService email;
    private InventoryService inventoryService;
    private PlanService planService;
    private CustomerRepository customerRepo;

    private CustomerConnectionRepository connectionRepo;


    public CustomerConnectionService(FTTH ftth,CustomerRepository customerRepo,
                                     EmailService email,
                                     InventoryService inventoryService,
                                     PlanService planService) {
        this.ftth = ftth;
        this.email = email;
        this.inventoryService = inventoryService;
        this.planService = planService;
        this.customerRepo=customerRepo;
        this.connectionRepo = new CustomerConnectionRepository();
    }

    public void addCustomer(String name, Long planId,
                        int pincode, double salary,
                        boolean confirm, String gmail, String oltType) {


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

    if (!confirm) {
        System.out.println("Cancelled.");
        return;
    }

    // 🔥 DB SAVE
    Customer customer = new Customer();
    customer.setFullName(name);
    customer.setEmail(gmail);
    customer.setSalary(salary);
    customer.setStatus("ACTIVE");
    customer.setPlanId(planID);
    customer.setPincode(pincode);
    String custID = customerRepo.save(customer);

    if (custID == null) {
        System.out.println("Failed to save customer.");
        return;
    }

    // 🔹 Assign port and create connection
    long[] portInfo = inventoryService.assignAvailablePort(pincode, oltType);
    if (portInfo == null) {
        System.out.println("Failed to assign port. Customer saved but no connection created.");
        return;
    }
    connectionRepo.createConnection(name, planID, portInfo[0], portInfo[1]);

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
            gmail, name, pincode, service, price);
}
    public void moveCustomer(String custID, int newPin, String oltType, boolean confirm) {

    // Find active connection from DB
    String[] conn = connectionRepo.findActiveConnectionByCustomerCode(custID);

    if (conn == null) {
        System.out.println("No active connection found for customer '" + custID + "'.");
        return;
    }

    int currentPin = Integer.parseInt(conn[2]);
    if (currentPin == newPin) {
        System.out.println("Customer is already in pincode " + newPin + ".");
        return;
    }

    System.out.println("Current Pincode : " + conn[2]);
    System.out.println("Current OLT     : " + conn[10]);
    System.out.println("Current Port    : " + conn[6] + "/Spl" + conn[7] + "/Port" + conn[8]);

    if (!inventoryService.checkPincode(newPin)) {
        System.out.println("Service NOT available in pincode " + newPin);
        email.sendNoOLTEmail(newPin);
        return;
    }

    int available = inventoryService.getAvailablePorts(newPin);
    if (available <= 0) {
        System.out.println("No available ports in pincode " + newPin);
        email.sendNoOLTEmail(newPin);
        return;
    }
    System.out.println("Available ports in " + newPin + " : " + available);

    if (!confirm) {
        System.out.println("Cancelled.");
        return;
    }

    long[] newPortInfo = inventoryService.assignAvailablePort(newPin, oltType);
    if (newPortInfo == null) {
        System.out.println("Failed to assign new port in pincode " + newPin + " for OLT type " + oltType + ".");
        return;
    }

    long connectionId = Long.parseLong(conn[0]);
    long oldPortId = Long.parseLong(conn[9]);

    boolean moved = connectionRepo.moveConnection(connectionId, oldPortId, newPortInfo[0], newPortInfo[1]);

    if (moved) {
        System.out.println("Customer " + custID + " moved to pincode " + newPin + " successfully.");
    } else {
        System.out.println("Move failed.");
    }
}
public Customer getCustomer(String customerCode) {
    return customerRepo.findByCode(customerCode);
}
public String[] findActiveConnectionByCode(String customerCode) {
    return connectionRepo.findActiveConnectionByCustomerCode(customerCode);
}
public Long getActivePlanId(String customerCode) {
    return connectionRepo.findActivePlanIdByCustomerCode(customerCode);
}
public void changePlan(String custID, long planId, boolean confirm) {

    if (!confirm) {
        System.out.println("Cancelled.");
        return;
    }

    boolean changed = connectionRepo.updateConnectionPlan(custID, planId);

    Plan newPlan = planService.findPlanById(planId);
    if (changed) {
        System.out.println("\nService updated successfully.");
        System.out.println("New Plan : " + newPlan.getName());
        System.out.println("Price    : Rs." + newPlan.getPrice());
    } else {
        System.out.println("Change failed. No active connection found for this customer.");
    }
}
public void disconnectConnection(long connectionId, boolean confirm) {
    String[] conn = connectionRepo.findConnection(connectionId);
    if (conn == null) {
        System.out.println("Connection not found.");
        return;
    }
    if ("DISCONNECTED".equals(conn[5])) {
        System.out.println("Connection is already disconnected.");
        return;
    }
    System.out.println("Customer : " + conn[1]);
    System.out.println("Pincode  : " + conn[2]);
    System.out.println("Plan     : " + conn[3] + " @ Rs." + conn[4]);
    System.out.println("Port     : " + conn[6] + "/Spl" + conn[7] + "/Port" + conn[8]);

    if (!confirm) {
        System.out.println("Cancelled.");
        return;
    }

    boolean ok = connectionRepo.disconnectCustomer(connectionId);
    if (ok) {
        System.out.println("Connection " + connectionId + " disconnected. Port is now free.");
    } else {
        System.out.println("Disconnect failed.");
    }
}
public void listActiveConnections() {
    connectionRepo.listActiveConnections();
}
public String[] findConnection(long connectionId) {
    return connectionRepo.findConnection(connectionId);
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
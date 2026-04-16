package ftth.controller;

import java.util.List;
import java.util.Scanner;

import ftth.service.FTTH;
import ftth.service.PlanService;
import ftth.util.InputUtil;
import ftth.service.EmailService;
import ftth.service.InventoryService;
import ftth.model.Customer;
import ftth.model.Plan;
import ftth.service.CustomerConnectionService;
import ftth.service.Customercreen;

public class CSRController {
    private CustomerConnectionService customerConnectionService;
    private PlanService planService;
    private InventoryService inventoryService;

    public CSRController(CustomerConnectionService customerConnectionService, PlanService planService, InventoryService inventoryService) {
        this.customerConnectionService = customerConnectionService;
        this.planService = planService;
        this.inventoryService = inventoryService;
    }

    // 🔹 MAIN HANDLER (NO STATIC ❌)
    public boolean handle(String option, Scanner sc) {

        switch (option) {

            case "1":
                doAdd(sc);
                return false;

            case "2":
                doMove(sc);
                return false;

            case "3":
                doChange(sc);
                return false;

            case "4":
                doDelete(sc);
                return false;

            case "5":
                // Customercreen.show(sc, ftth, email);
                doLookup(sc);
                return false;

            case "0":
                System.out.println("Logged out.");
                return true;

            default:
                System.out.println("Invalid option.");
                return false;
        }
    }

    // =========================================================
    // 🔥 METHODS (move your logic here)
    // =========================================================

    private void doAdd(Scanner sc) {

    System.out.println("\n--- New Connection ---");
    List<Plan> plans = planService.getActivePlans();
    if (plans.isEmpty()) {
        System.out.println("No plans available. Contact admin.");
        return;
    }
    System.out.println("Plans Available:");
    for (Plan p : plans) {
        System.out.println(p.getId() + ". " + p.getName()
            + " | " + p.getSpeed()
            + " | Rs." + p.getPrice());
    }
    String name = InputUtil.readValidName(sc, "Enter Customer Name: ");

    Plan selectedPlan;
    while (true) {
        long planId = InputUtil.readLong(sc, "Select Plan ID: ");
        selectedPlan = planService.findPlanById(planId);
        if (selectedPlan != null && selectedPlan.isActive()) break;
        System.out.println("Invalid plan ID. Please select from the list above.");
    }

    double salary = InputUtil.readDouble(sc, "Enter Salary: ");
    if (salary <= 30000) {
        System.out.println("Salary below eligibility. Minimum salary required: Rs.30,000.");
        return;
    }

    int pincode = InputUtil.readInt(sc, "Enter Pincode: ");
    if (!inventoryService.checkPincode(pincode)) {
        System.out.println("Service NOT available in pincode " + pincode + ".");
        return;
    }

    String oltType;
    while (true) {
        System.out.print("Select OLT Type [1] OLT300  [2] OLT500: ");
        String oltChoice = sc.nextLine().trim();
        if ("1".equals(oltChoice)) { oltType = "OLT300"; break; }
        else if ("2".equals(oltChoice)) { oltType = "OLT500"; break; }
        else { System.out.println("Invalid choice. Enter 1 or 2."); }
    }

    int ports = inventoryService.getAvailablePortsByType(pincode, oltType);
    if (ports <= 0) {
        System.out.println("No " + oltType + " ports available in pincode " + pincode + ".");
        return;
    }
    System.out.println("Available " + oltType + " ports in " + pincode + " : " + ports);

    String gmail = InputUtil.readEmail(sc, "Enter your Email: ");

    System.out.println("\n--- Order Summary ---");
    System.out.println("Name     : " + name);
    System.out.println("Plan     : " + selectedPlan.getName() + " @ Rs." + selectedPlan.getPrice());
    System.out.println("Pincode  : " + pincode);
    System.out.println("OLT Type : " + oltType);
    System.out.println("Email    : " + gmail);
    System.out.print("Confirm order? (y/n): ");
    boolean confirm = sc.nextLine().equalsIgnoreCase("y");

    customerConnectionService.addCustomer(name, selectedPlan.getId(), pincode, salary, confirm, gmail, oltType);
}

   private void doMove(Scanner sc) {

    System.out.println("\n--- Move Customer ---");

    Customer cust;
    String custID;
    while (true) {
        System.out.print("Enter Customer ID (0 to cancel): ");
        custID = sc.nextLine().trim().toUpperCase();
        if ("0".equals(custID)) { System.out.println("Cancelled."); return; }
        cust = customerConnectionService.getCustomer(custID);
        if (cust != null) break;
        System.out.println("Customer ID '" + custID + "' not found. Try again.");
    }

    String[] conn = customerConnectionService.findActiveConnectionByCode(custID);
    if (conn == null) {
        System.out.println("No active connection found for '" + custID + "'.");
        return;
    }

    int currentPin = Integer.parseInt(conn[2]);
    System.out.println("Current Pincode : " + currentPin);
    System.out.println("Current OLT     : " + conn[10]);
    System.out.println("Current Port    : " + conn[6] + "/Spl" + conn[7] + "/Port" + conn[8]);

    int newPin;
    while (true) {
        newPin = InputUtil.readInt(sc, "Enter New Pincode (0 to cancel): ");
        if (newPin == 0) { System.out.println("Cancelled."); return; }
        if (newPin != currentPin) break;
        System.out.println("Customer is already in pincode " + currentPin + ". Enter a different pincode.");
    }

    String oltType;
    while (true) {
        System.out.print("Select OLT Type [1] OLT300  [2] OLT500: ");
        String oltChoice = sc.nextLine().trim();
        if ("1".equals(oltChoice)) { oltType = "OLT300"; break; }
        else if ("2".equals(oltChoice)) { oltType = "OLT500"; break; }
        else { System.out.println("Invalid choice. Enter 1 or 2."); }
    }

    System.out.print("Confirm move from " + currentPin + " to " + newPin + "? (y/n): ");
    boolean confirm = sc.nextLine().equalsIgnoreCase("y");

    customerConnectionService.moveCustomer(custID, newPin, oltType, confirm);
}

    private void doChange(Scanner sc) {

    System.out.println("\n--- Change Service ---");

    Customer cust;
    String custID;
    while (true) {
        System.out.print("Enter Customer ID (0 to cancel): ");
        custID = sc.nextLine().trim().toUpperCase();
        if ("0".equals(custID)) { System.out.println("Cancelled."); return; }
        cust = customerConnectionService.getCustomer(custID);
        if (cust != null) break;
        System.out.println("Customer ID '" + custID + "' not found. Try again.");
    }

    Long currentPlanId = customerConnectionService.getActivePlanId(custID);
    Plan currentPlan = currentPlanId != null ? planService.findPlanById(currentPlanId) : null;

    if (currentPlan == null) {
        System.out.println("No active connection found for this customer.");
        return;
    }

    System.out.println("\nCurrent Plan : " + currentPlan.getName() + " | " + currentPlan.getSpeed() + " | Rs." + currentPlan.getPrice());

    List<Plan> plans = planService.getActivePlans();
    plans.removeIf(p -> p.getId() == currentPlan.getId());

    if (plans.isEmpty()) {
        System.out.println("No other plans available to switch to.");
        return;
    }

    System.out.println("\nAvailable Plans:");
    for (Plan p : plans) {
        System.out.println(p.getId() + ". " + p.getName() + " | " + p.getSpeed() + " | Rs." + p.getPrice());
    }

    Plan selectedPlan;
    while (true) {
        long planId = InputUtil.readLong(sc, "Select New Plan ID: ");
        if (planId == currentPlan.getId()) {
            System.out.println("Customer is already on this plan. Select a different one.");
            continue;
        }
        selectedPlan = planService.findPlanById(planId);
        if (selectedPlan != null && selectedPlan.isActive()) break;
        System.out.println("Invalid plan ID. Please select from the list above.");
    }

    System.out.println("\nChange from : " + currentPlan.getName() + " @ Rs." + currentPlan.getPrice());
    System.out.println("Change to   : " + selectedPlan.getName() + " @ Rs." + selectedPlan.getPrice());
    System.out.print("Confirm change? (y/n): ");
    boolean confirm = sc.nextLine().equalsIgnoreCase("y");

    customerConnectionService.changePlan(custID, selectedPlan.getId(), confirm);
}

   private void doDelete(Scanner sc) {

    System.out.println("\n--- Disconnect Customer ---");
    customerConnectionService.listActiveConnections();

    while (true) {
        long connId = InputUtil.readLong(sc, "Enter Connection ID to disconnect (0 to cancel): ");
        if (connId == 0) {
            System.out.println("Cancelled.");
            return;
        }

        String[] conn = customerConnectionService.findConnection(connId);
        if (conn == null) {
            System.out.println("Connection ID " + connId + " not found. Please enter a valid ID from the list above.");
            continue;
        }
        if ("DISCONNECTED".equals(conn[5])) {
            System.out.println("Connection " + connId + " is already disconnected.");
            continue;
        }

        System.out.println("\nYou are about to disconnect:");
        System.out.println("  Customer : " + conn[1]);
        System.out.println("  Pincode  : " + conn[2]);
        System.out.println("  Plan     : " + conn[3] + " @ Rs." + conn[4]);
        System.out.println("  OLT Type : " + conn[10]);
        System.out.println("  Port     : " + conn[6] + "/Spl" + conn[7] + "/Port" + conn[8]);

        System.out.print("Confirm disconnect? (y/n): ");
        boolean confirm = sc.nextLine().equalsIgnoreCase("y");

        customerConnectionService.disconnectConnection(connId, confirm);
        return;
    }
}
private void doLookup(Scanner sc) {

    System.out.println("\n--- Customer Lookup ---");
    System.out.println("  [1] Look up by Customer ID");
    System.out.println("  [2] List all customers");

    System.out.print("Choose: ");
    String sub = sc.nextLine().trim();

    if (sub.equals("1")) {
        System.out.print("Enter Customer ID: ");
        String custID = sc.nextLine().trim().toUpperCase();

        // 🔥 call service
        customerConnectionService.lookupCustomerById(custID);

    } else if (sub.equals("2")) {

        // 🔥 call service
        customerConnectionService.listAllCustomers();

    } else {
        System.out.println("Invalid choice.");
    }
}
}
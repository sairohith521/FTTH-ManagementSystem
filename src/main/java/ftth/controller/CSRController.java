package ftth.controller;

import java.util.List;
import java.util.Scanner;

import ftth.service.FTTH;
import ftth.service.PlanService;
import ftth.util.InputUtil;
import ftth.service.EmailService;
import ftth.model.Plan;
import ftth.service.CustomerConnectionService;
import ftth.service.Customercreen;

public class CSRController {
    private CustomerConnectionService customerConnectionService;
    private PlanService planService;
    // 🔹 Constructor (Dependency Injection)
    public CSRController(CustomerConnectionService customerConnectionService,PlanService planService) {
        this.customerConnectionService = customerConnectionService;
        this.planService=planService;
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
    System.out.println("Plans Available:");
    // System.out.println("  1. 300 MBPS, 60 GB/Month  -> Rs. 499");
    // System.out.println("  2. 500 MBPS, Unlimited    -> Rs. 1499");
    List<Plan> plans = planService.getActivePlans();
    for (Plan p : plans) {
    System.out.println(p.getId() + ". " + p.getName()
        + " | " + p.getSpeed()
        + " | Rs." + p.getPrice());
    }
    String name = InputUtil.readValidName(sc, "Enter Customer Name: ");

    long planId = InputUtil.readLong(sc, "Select Plan ID: ");

    int pincode = InputUtil.readInt(sc, "Enter Pincode: ");
    double salary = InputUtil.readDouble(sc, "Enter Salary: ");

    System.out.print("Confirm order? (y/n): ");
    boolean confirm = sc.nextLine().equalsIgnoreCase("y");
    String gmail = InputUtil.readEmail(sc, "Enter your Email: ");
    // 🔥 call service
    customerConnectionService.addCustomer(name, planId, pincode, salary, confirm,gmail);
}

   private void doMove(Scanner sc) {

    System.out.println("\n--- Move Customer ---");

    System.out.print("Enter Customer ID: ");
    String custID = sc.nextLine().trim().toUpperCase();

    int newPin = InputUtil.readInt(sc, "Enter New Pincode: ");

    System.out.print("Confirm move? (y/n): ");
    boolean confirm = sc.nextLine().equalsIgnoreCase("y");

    // 🔥 call service
    customerConnectionService.moveCustomer(custID, newPin, confirm);
}

    private void doChange(Scanner sc) {

    System.out.println("\n--- Change Service ---");

    System.out.print("Enter Customer ID: ");
    String custID = sc.nextLine().trim().toUpperCase();

    // 🔹 Fetch plans from DB
    List<Plan> plans = planService.getActivePlans();

    if (plans.isEmpty()) {
        System.out.println("No plans available.");
        return;
    }

    // 🔹 Display plans
    System.out.println("\nAvailable Plans:");
    for (Plan p : plans) {
        System.out.println(p.getId() + ". " + p.getName()
                + " | " + p.getSpeed()
                + " | Rs." + p.getPrice());
    }

    // 🔹 Take planId input
    long planId = InputUtil.readLong(sc, "Select New Plan ID: ");

    // 🔹 Validate selection
    Plan selectedPlan = planService.findPlanById(planId);
    if (selectedPlan == null) {
        System.out.println("Invalid plan selected.");
        return;
    }

    System.out.print("Confirm change? (y/n): ");
    boolean confirm = sc.nextLine().equalsIgnoreCase("y");

    // 🔥 call service (UPDATED PARAM)
    customerConnectionService.changePlan(custID, planId, confirm);
}

   private void doDelete(Scanner sc) {

    System.out.println("\n--- Disconnect Customer ---");
    customerConnectionService.listActiveConnections();

    long connId = InputUtil.readLong(sc, "Enter Connection ID to disconnect: ");

    System.out.print("Confirm disconnect? (y/n): ");
    boolean confirm = sc.nextLine().equalsIgnoreCase("y");

    customerConnectionService.disconnectConnection(connId, confirm);
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
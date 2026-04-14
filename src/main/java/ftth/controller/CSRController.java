package ftth.controller;

import java.util.Scanner;

import ftth.service.FTTH;
import ftth.util.InputUtil;
import ftth.service.EmailService;
import ftth.service.CustomerConnectionService;
import ftth.service.Customercreen;

public class CSRController {
    private CustomerConnectionService customerConnectionService;
    // 🔹 Constructor (Dependency Injection)
    public CSRController(CustomerConnectionService customerConnectionService) {
        this.customerConnectionService = customerConnectionService;
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
    System.out.println("  1. 300 MBPS, 60 GB/Month  -> Rs. 499");
    System.out.println("  2. 500 MBPS, Unlimited    -> Rs. 1499");

    System.out.print("Enter Customer Name: ");
    String name = sc.nextLine();

    System.out.print("Which Service (1/2): ");
    String choice = sc.nextLine();

    int pincode = InputUtil.readInt(sc, "Enter Pincode: ");
    double salary = InputUtil.readDouble(sc, "Enter Salary: ");

    System.out.print("Confirm order? (y/n): ");
    boolean confirm = sc.nextLine().equalsIgnoreCase("y");

    // 🔥 call service
    customerConnectionService.addCustomer(name, choice, pincode, salary, confirm);
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

    System.out.println("Available Plans:");
    System.out.println("  1. 300 MBPS, 60 GB/Month  -> Rs. 499");
    System.out.println("  2. 500 MBPS, Unlimited    -> Rs. 1499");

    System.out.print("Select New Plan (1/2): ");
    String choice = sc.nextLine();

    System.out.print("Confirm change? (y/n): ");
    boolean confirm = sc.nextLine().equalsIgnoreCase("y");

    // 🔥 call service
    customerConnectionService.changePlan(custID, choice, confirm);
}

   private void doDelete(Scanner sc) {

    System.out.println("\n--- Disconnect Customer ---");

    System.out.print("Enter Customer ID: ");
    String custID = sc.nextLine().trim().toUpperCase();

    System.out.print("Confirm disconnect? (y/n): ");
    boolean confirm = sc.nextLine().equalsIgnoreCase("y");

    // 🔥 call service
    customerConnectionService.disconnectCustomer(custID, confirm);
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
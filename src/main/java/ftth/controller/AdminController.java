package ftth.controller;

import java.util.List;
import java.util.Scanner;

import ftth.model.Plan;
import ftth.service.*;
import ftth.util.InputUtil;

public class AdminController {
    private UserManagerService userManagerService;
    private InventoryService inventoryService;
    private CustomerConnectionService customerConnectionService;
    private PlanService planService;

    // 🔹 Constructor (Dependency Injection)
    public AdminController(CustomerConnectionService customerConnectionService,InventoryService inventoryService,PlanService planService,UserManagerService userManagerService){
        this.userManagerService=userManagerService;
        this.inventoryService=inventoryService;
        this.customerConnectionService=customerConnectionService;
        this.planService=planService;
    }

    // 🔹 MAIN HANDLER (NO STATIC ❌)
    public boolean handle(String option, Scanner sc, String currentUser) {

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

            case "6":
                doInventory(sc);
                return false;

            case "7":
                doMaint(sc);
                return false;

            case "8":
                doCapacity(sc);
                return false;

            case "9":
                doPlanAdmin(sc);
                return false;

            case "A":
                doUserMgmt(sc,userManagerService,currentUser);
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
    // 🔥 METHODS MOVED FROM MAIN
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

    System.out.println("Available Plans:");
    System.out.println("  1. 300 MBPS, 60 GB/Month  -> Rs. 499");
    System.out.println("  2. 500 MBPS, Unlimited    -> Rs. 1499");

    System.out.print("Select New Plan (1/2): ");
    String choice = sc.nextLine();

    System.out.print("Confirm change? (y/n): ");
    boolean confirm = sc.nextLine().equalsIgnoreCase("y");

    // 🔥 call service (UPDATED PARAM)
    customerConnectionService.changePlan(custID, planId, confirm);
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

    static void doInventory(Scanner sc) {
    InventoryController inventory = new InventoryController();
    inventory.menu(); 
}

    private void doMaint(Scanner sc) {

    System.out.println("\n--- Maintenance ---");
    System.out.println("  (Maintenance module — extend as needed.)");
    System.out.print("Press Enter to continue...");

    sc.nextLine();
}

    private void doCapacity(Scanner sc) {

    System.out.println("\n--- Capacity Dashboard ---");

    int[] pincodes = {560001, 560002, 110001};

    // 🔥 call service
    // inventoryService.showCapacity(pincodes);

    System.out.print("\nPress Enter to continue...");
    sc.nextLine();
}

     static void doPlanAdmin(Scanner sc) {
         PlanAdmin admin = new PlanAdmin(sc);
         admin.handleMenu();
    }
    private  void doUserMgmt(Scanner sc, UserManagerService um, String currentUser) {
        while (true) {
            System.out.println("\n--- User Management ---");
            // System.out.println("  [1] List Users");
            System.out.println("  [1] Add User");
            System.out.println("  [2] Change Password");
            System.out.println("  [3] Change Role");
            System.out.println("  [4] Delete User");
            System.out.println("  [5] Back");
            System.out.print("Choose: ");
            String sub = sc.nextLine().trim();

            switch (sub) {

                // case "1":
                //     um.listUsers();
                //     break;

                case "1": {
                    System.out.print("  New Username : ");
                    String uname = sc.nextLine().trim();
                    System.out.print("  Password     : ");
                    String pass  = sc.nextLine().trim();
                    System.out.println("  Role options : CSR | MAINT");
                    System.out.print("  Role         : ");
                    String role  = sc.nextLine().trim();
                    um.addUser(uname, pass, role);
                    break;
                }

                case "2": {
                    System.out.print("  Username     : ");
                    String uname    = sc.nextLine().trim();
                    System.out.print("  New Password : ");
                    String newPass  = sc.nextLine().trim();
                    boolean ok = um.changePassword(uname, newPass);
                    if (ok) System.out.println(" Password updated for '" + uname + "'.");
                    else    System.out.println(" Failed.");
                    break;
                }

                case "3": {
                    System.out.print("  Username     : ");
                    String uname   = sc.nextLine().trim();
                    System.out.println("  Role options :  CSR | MAINT");
                    System.out.print("  New Role     : ");
                    String newRole = sc.nextLine().trim();
                    boolean ok = um.changeRole(uname, newRole);
                    if (ok) System.out.println(" Role updated for '" + uname + "'.");
                    else    System.out.println(" Failed.");
                    break;
                }

                case "4": {
                    System.out.print("  Username to delete: ");
                    String uname = sc.nextLine().trim();
                    if (uname.equalsIgnoreCase(currentUser)) {
                        System.out.println(" You cannot delete your own account.");
                        break;
                    }
                    System.out.print("  Confirm delete '" + uname + "'? (y/n): ");
                    if (!sc.nextLine().equalsIgnoreCase("y")) break;
                    um.deleteUser(uname);
                    break;
                }

                case "5":
                    return;

                default:
                    System.out.println(" Invalid option.");
                    break;
            }
        }
    }
}
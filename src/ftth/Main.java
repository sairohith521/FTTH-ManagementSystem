package ftth;
import ftth.controller.InventoryController;
import ftth.controller.PlanAdmin;
import ftth.service.CustomerScreen;
import ftth.service.EmailService;
import ftth.service.FTTH;
import ftth.service.UserManager;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        UserManager.initDefaults();

        Scanner      sc    = new Scanner(System.in);
        FTTH         ftth  = new FTTH();
        EmailService email = new EmailService();
        UserManager  um    = new UserManager();

        while (true) {
            System.out.println("\n============================================");
            System.out.println("       Aaha Telecom — Please Log In");
            System.out.println("============================================");
            System.out.print("  Username : ");
            String username = sc.nextLine().trim();
            System.out.print("  Password : ");
            String password = sc.nextLine().trim();

            String role = um.login(username, password);
            if (role == null) {
                System.out.println("\n  Invalid credentials. Please try again.");
                continue;
            }

            System.out.println("\n  Login successful! Role: " + role);

            boolean logout = false;
            while (!logout) {
                printMenu(role);
                System.out.print("Select Option: ");
                String option = sc.nextLine().trim().toUpperCase();

                switch (role) {
                    case "ADMIN": logout = handleAdmin(option, sc, ftth, email, um, username); break;
                    case "CSR":   logout = handleCSR  (option, sc, ftth, email);               break;
                    case "MAINT": logout = handleMaint(option, sc, ftth);                      break;
                    default:
                        System.out.println("Unknown role. Logging out.");
                        logout = true;
                }
            }
        }
    }

    static void printMenu(String role) {
        System.out.println("\n============================================");
        System.out.println("          Welcome to Aaha Telecom");
        System.out.println("============================================");
        switch (role) {
            case "ADMIN":
                System.out.println("  [1] Add (New Install)");
                System.out.println("  [2] Move");
                System.out.println("  [3] Change Plan");
                System.out.println("  [4] Disconnect");
                System.out.println("  [5] Customers (Lookup / Config / Bill)");
                System.out.println("  [6] Inventory Admin");
                System.out.println("  [7] Maintenance");
                System.out.println("  [8] Capacity Dashboard");
                System.out.println("  [9] Plan Admin");
                System.out.println("  [A] User Management");
                System.out.println("  [0] Logout");
                break;
            case "CSR":
                System.out.println("  [1] Add (New Install)");
                System.out.println("  [2] Move");
                System.out.println("  [3] Change Plan");
                System.out.println("  [4] Disconnect");
                System.out.println("  [5] Customers (Lookup / Config / Bill)");
                System.out.println("  [0] Logout");
                break;
            case "MAINT":
                System.out.println("  [1] Inventory Admin");
                System.out.println("  [2] Maintenance");
                System.out.println("  [3] Capacity Dashboard");
                System.out.println("  [4] Plan Admin");
                System.out.println("  [0] Logout");
                break;
        }
        System.out.println("--------------------------------------------");
    }

    static boolean handleAdmin(String option, Scanner sc, FTTH ftth,
                                EmailService email, UserManager um,
                                String currentUser) {
        switch (option) {
            case "1": doAdd       (sc, ftth, email); return false;
            case "2": doMove      (sc, ftth, email); return false;
            case "3": doChange    (sc, ftth);        return false;
            case "4": doDelete    (sc, ftth);        return false;
            case "5": CustomerScreen.show(sc, ftth, email); return false;
            case "6": doInventory (sc, ftth);        return false;
            case "7": doMaint     (sc);              return false;
            case "8": doCapacity  (sc, ftth);        return false;
            case "9": doPlanAdmin (sc);              return false;
            case "A": doUserMgmt  (sc, um, currentUser); return false;
            case "0":
                System.out.println("  Logged out.");
                return true;
            default:
                System.out.println("  Invalid option.");
                return false;
        }
    }

    static boolean handleCSR(String option, Scanner sc, FTTH ftth, EmailService email) {
        switch (option) {
            case "1": doAdd    (sc, ftth, email); return false;
            case "2": doMove   (sc, ftth, email); return false;
            case "3": doChange (sc, ftth);        return false;
            case "4": doDelete (sc, ftth);        return false;
            case "5": CustomerScreen.show(sc, ftth, email); return false;
            case "0":
                System.out.println("  Logged out.");
                return true;
            default:
                System.out.println("  Invalid option.");
                return false;
        }
    }

    static boolean handleMaint(String option, Scanner sc, FTTH ftth) {
        switch (option) {
            case "1": doInventory(sc, ftth); return false;
            case "2": doMaint    (sc);       return false;
            case "3": doCapacity (sc, ftth); return false;
            case "4": doPlanAdmin(sc);       return false;
            case "0":
                System.out.println("  Logged out.");
                return true;
            default:
                System.out.println("  Invalid option.");
                return false;
        }
    }

    // customer array: [0]ID [1]Name [2]Pincode [3]Service [4]Price [5]Status

    static void doAdd(Scanner sc, FTTH ftth, EmailService email) {
        System.out.println("\n--- New Connection ---");
        System.out.println("Plans Available:");
        System.out.println("  1. 300 MBPS 60 GB/Month  -> Rs. 499");
        System.out.println("  2. 500 MBPS Unlimited    -> Rs. 1499");

        System.out.print("Enter Customer Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Customer Email: ");
        String customerEmail = sc.nextLine().trim();
        if (customerEmail.isEmpty() || !customerEmail.contains("@")) {
            System.out.println("Invalid email address.");
            return;
        }

        System.out.print("Which Service (1/2): ");
        String choice = sc.nextLine();
        String service = ""; int price = 0;
        if      (choice.equals("1")) { service = "300 MBPS 60 GB/Month"; price = 499;  }
        else if (choice.equals("2")) { service = "500 MBPS Unlimited";   price = 1499; }
        else { System.out.println("Invalid choice."); return; }

        int    pincode = readInt   (sc, "Enter Pincode: ");
        double salary  = readDouble(sc, "Enter Salary: ");

        if (!ftth.checkPincode(pincode)) {
            System.out.println(" Service NOT available in pincode " + pincode + ".");
            System.out.println(" Sending alert to OLT provider...");
            email.sendNoOLTEmail(pincode);
            return;
        }
        int ports = ftth.getAvailablePorts(pincode);
        if (ports <= 0) {
            System.out.println(" No ports available in pincode " + pincode + ".");
            email.sendNoOLTEmail(pincode);
            return;
        }
        if (!ftth.checkSalary(salary)) {
            System.out.println(" Salary below Rs.30,000. Not eligible.");
            return;
        }

        System.out.println(" Feasibility   : PASS");
        System.out.println(" Salary Check  : PASS");
        System.out.println(" Available Ports: " + ports);
        System.out.println(" ONT shipping on: " + LocalDate.now().plusDays(1));

        System.out.print("Confirm order? (y/n): ");
        if (!sc.nextLine().equalsIgnoreCase("y")) return;

        String custID = ftth.addCustomer(name, pincode, service, price);
        if (custID == null) { System.out.println(" Failed to assign port."); return; }

        LocalDate orderDate = LocalDate.now();
        LocalDate billDate  = (orderDate.getDayOfMonth() <= 10)
                ? orderDate.withDayOfMonth(10)
                : orderDate.plusMonths(1).withDayOfMonth(10);

        System.out.println("\n Order Confirmed!");
        System.out.println("   Customer ID  : " + custID);
        System.out.println("   Name         : " + name);
        System.out.println("   Service      : " + service);
        System.out.println("   Price        : Rs." + price + "/month");
        System.out.println("   Billing Date : " + billDate);
        System.out.println(" Sending confirmation email...");
        email.sendOrderConfirmationEmail(customerEmail, name, pincode, service, price);
    }

    static void doMove(Scanner sc, FTTH ftth, EmailService email) {
        System.out.println("\n--- Move Customer ---");
        System.out.print("Enter Customer ID (e.g. AAHA-0001): ");
        String custID = sc.nextLine().trim().toUpperCase();

        String[] customer = ftth.findCustomer(custID);
        if (customer == null) { System.out.println(" Customer ID not found."); return; }

        System.out.println("Current Pincode : " + customer[2]);

        int newPin = readInt(sc, "Enter New Pincode: ");

        if (!ftth.checkPincode(newPin)) {
            System.out.println(" No available ports in pincode " + newPin + ". Move not possible.");
            System.out.println(" Sending alert to OLT provider...");
            email.sendNoOLTEmail(newPin);
            return;
        }

        System.out.print("Confirm move? (y/n): ");
        if (!sc.nextLine().equalsIgnoreCase("y")) return;

        boolean moved = ftth.moveCustomer(custID, newPin);
        if (moved) System.out.println(" Customer " + custID + " moved to pincode " + newPin + " successfully.");
        else       System.out.println(" Move failed.");
    }

    static void doChange(Scanner sc, FTTH ftth) {
        System.out.println("\n--- Change Service ---");
        System.out.print("Enter Customer ID: ");
        String custID = sc.nextLine().trim().toUpperCase();

        String[] customer = ftth.findCustomer(custID);
        if (customer == null) { System.out.println(" Customer ID not found."); return; }

        System.out.println("Current Service : " + customer[3] + " @ Rs." + customer[4]);
        System.out.println("Available Plans:");
        System.out.println("  1. 300 MBPS 60 GB/Month  -> Rs. 499");
        System.out.println("  2. 500 MBPS Unlimited    -> Rs. 1499");
        System.out.print("Select New Plan (1/2): ");
        String choice = sc.nextLine();
        String newService = ""; int newPrice = 0;
        if      (choice.equals("1")) { newService = "300 MBPS 60 GB/Month"; newPrice = 499;  }
        else if (choice.equals("2")) { newService = "500 MBPS Unlimited";   newPrice = 1499; }
        else { System.out.println("Invalid choice."); return; }

        System.out.print("Confirm change? (y/n): ");
        if (!sc.nextLine().equalsIgnoreCase("y")) return;

        boolean changed = ftth.changeCustomer(custID, newService, newPrice);
        if (changed) System.out.println(" Service updated successfully.");
        else         System.out.println(" Change failed.");
    }

    static void doDelete(Scanner sc, FTTH ftth) {
        System.out.println("\n--- Disconnect Customer ---");
        System.out.print("Enter Customer ID: ");
        String custID = sc.nextLine().trim().toUpperCase();

        String[] customer = ftth.findCustomer(custID);
        if (customer == null) { System.out.println(" Customer ID not found."); return; }

        System.out.println("Customer : " + customer[1]);
        System.out.println("Pincode  : " + customer[2]);
        System.out.println("Service  : " + customer[3]);

        System.out.print("  Confirm disconnect? This will free the port. (y/n): ");
        if (!sc.nextLine().equalsIgnoreCase("y")) return;

        boolean deleted = ftth.deleteCustomer(custID);
        if (deleted) System.out.println(" Customer " + custID + " disconnected. Port is now free.");
        else         System.out.println(" Disconnect failed.");
    }

    static void doLookup(Scanner sc, FTTH ftth) {
        System.out.println("\n--- Customer Lookup ---");
        System.out.println("  [1] Look up by Customer ID");
        System.out.println("  [2] List all customers");
        System.out.print("Choose: ");
        String sub = sc.nextLine().trim();

        if (sub.equals("1")) {
            System.out.print("Enter Customer ID: ");
            String custID   = sc.nextLine().trim().toUpperCase();
            String[] customer = ftth.findCustomer(custID);
            if (customer == null) { System.out.println(" Not found."); return; }
            System.out.println("\n  ID      : " + customer[0]);
            System.out.println("  Name    : " + customer[1]);
            System.out.println("  Pincode : " + customer[2]);
            System.out.println("  Service : " + customer[3]);
            System.out.println("  Price   : Rs." + customer[4] + "/month");
            System.out.println("  Status  : " + customer[5]);
        } else if (sub.equals("2")) {
            ftth.listAllCustomers();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    static void doInventory(Scanner sc, FTTH ftth) {
        InventoryController inventory = new InventoryController(sc);
        inventory.menu();
    }

    static void doMaint(Scanner sc) {
        System.out.println("\n--- Maintenance ---");
        System.out.println("  (Maintenance module — extend as needed.)");
        System.out.print("Press Enter to continue...");
        sc.nextLine();
    }

    static void doCapacity(Scanner sc, FTTH ftth) {
        System.out.println("\n--- Capacity Dashboard ---");
        int[] pincodes = {560001, 560002, 110001};
        System.out.printf("%-10s  %-15s%n", "Pincode", "Available Ports");
        System.out.println("-".repeat(28));
        for (int pin : pincodes) {
            int avail = ftth.getAvailablePorts(pin);
            System.out.printf("%-10d  %-15d%n", pin, avail);
        }
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    static void doPlanAdmin(Scanner sc) {
        PlanAdmin admin = new PlanAdmin(sc);
        admin.handleMenu();
    }

    static void doUserMgmt(Scanner sc, UserManager um, String currentUser) {
        while (true) {
            System.out.println("\n--- User Management ---");
            System.out.println("  [1] Add User");
            System.out.println("  [2] Change Password");
            System.out.println("  [3] Change Role");
            System.out.println("  [4] Delete User");
            System.out.println("  [5] Back");
            System.out.print("Choose: ");
            String sub = sc.nextLine().trim();

            switch (sub) {
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

    static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                int val = sc.nextInt(); sc.nextLine();
                return val;
            } else {
                System.out.println("  Please enter numbers only.");
                sc.nextLine();
            }
        }
    }

    static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextDouble()) {
                double val = sc.nextDouble(); sc.nextLine();
                return val;
            } else {
                System.out.println("  Please enter a valid number.");
                sc.nextLine();
            }
        }
    }
}

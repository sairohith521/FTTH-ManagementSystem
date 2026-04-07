package ftth;
import java.time.LocalDate;
import java.util.Scanner;
import ftth.service.EmailService;
import ftth.service.FTTH;

public class Main {
    public static void main(String[] args) {

        Scanner sc    = new Scanner(System.in);
        FTTH ftth     = new FTTH();
        EmailService email = new EmailService();

        while (true) {

            System.out.println("\n============================================");
            System.out.println("          Welcome to Aaha Telecom");
            System.out.println("============================================");
            System.out.println("  1. New Connection  (ADD)");
            System.out.println("  2. Move Customer   (MOVE)");
            System.out.println("  3. Change Service  (CHANGE)");
            System.out.println("  4. Delete Customer (DELETE)");
          //  System.out.println("  5. View All Customers");
            System.out.println("  5. Exit");
            System.out.println("--------------------------------------------");
            System.out.print("Select Option: ");
            String option = sc.nextLine().trim();

            switch (option) {

                // ==========================================================
                // 1. ADD — New Connection
                // ==========================================================
                case "1": {
                    System.out.println("\n--- New Connection ---");
                    System.out.println("Plans Available:");
                    System.out.println("  1. 300 MBPS, 60 GB/Month  -> Rs. 499");
                    System.out.println("  2. 500 MBPS, Unlimited    -> Rs. 1499");

                    System.out.print("Enter Customer Name: ");
                    String name = sc.nextLine();

                    System.out.print("Which Service (1/2): ");
                    String choice = sc.nextLine();
                    String service = "";
                    int price = 0;
                    if (choice.equals("1"))      { service = "300 MBPS, 60 GB/Month"; price = 499; }
                    else if (choice.equals("2")) { service = "500 MBPS, Unlimited";   price = 1499; }
                    else { System.out.println("Invalid choice."); break; }

                    int pincode = readInt(sc, "Enter Pincode: ");
                    double salary = readDouble(sc, "Enter Salary: ");

                    // Feasibility checks
                    if (!ftth.checkPincode(pincode)) {
                        System.out.println("❌ Service NOT available in pincode " + pincode + ".");
                        System.out.println("📧 Sending alert to OLT provider...");
                        email.sendNoOLTEmail(pincode);
                        break;
                    }
                    int ports = ftth.getAvailablePorts(pincode);
                    if (ports <= 0) {
                        System.out.println("❌ No ports available in pincode " + pincode + ".");
                        email.sendNoOLTEmail(pincode);
                        break;
                    }
                    if (!ftth.checkSalary(salary)) {
                        System.out.println("❌ Salary below Rs.30,000. Not eligible.");
                        break;
                    }

                    System.out.println("✅ Feasibility: PASS");
                    System.out.println("✅ Salary Check: PASS");
                    System.out.println("✅ Available Ports: " + ports);
                    System.out.println("📦 ONT shipping on: " + LocalDate.now().plusDays(1));

                    System.out.print("Confirm order? (y/n): ");
                    if (!sc.nextLine().equalsIgnoreCase("y")) break;

                    // ADD customer — generates CustomerID + tags port
                    String custID = ftth.addCustomer(name, pincode, service, price);
                    if (custID == null) {
                        System.out.println("❌ Failed to assign port.");
                        break;
                    }

                    LocalDate orderDate = LocalDate.now();
                    LocalDate billDate  = (orderDate.getDayOfMonth() <= 10)
                            ? orderDate.withDayOfMonth(10)
                            : orderDate.plusMonths(1).withDayOfMonth(10);

                    System.out.println("\n🎉 Order Confirmed!");
                    System.out.println("   Customer ID  : " + custID);
                    System.out.println("   Name         : " + name);
                    System.out.println("   Service      : " + service);
                    System.out.println("   Price        : Rs." + price + "/month");
                    System.out.println("   Billing Date : " + billDate);
                    System.out.println("📧 Sending confirmation email...");
                    email.sendOrderConfirmationEmail(name, pincode, service, price);
                    break;
                }

                // ==========================================================
                // 2. MOVE — Customer moves to new pincode
                // ==========================================================
                case "2": {
                    System.out.println("\n--- Move Customer ---");
                    System.out.print("Enter Customer ID (e.g. AAHA-0001): ");
                    String custID = sc.nextLine().trim().toUpperCase();

                    // Show current details
                    String[] customer = ftth.findCustomer(custID);
                    if (customer == null) {
                        System.out.println("❌ Customer ID not found.");
                        break;
                    }
                    System.out.println("Current Pincode : " + customer[2]);
                    System.out.println("Current Port    : " + customer[3]+"/"+customer[4]+"/Port"+customer[5]);

                    int newPin = readInt(sc, "Enter New Pincode: ");

                    // Check feasibility of new pincode
                    if (!ftth.checkPincode(newPin)) {
                        System.out.println("❌ No available ports in pincode " + newPin + ". Move not possible.");
                        System.out.println("📧 Sending alert to OLT provider...");
                        email.sendNoOLTEmail(newPin);
                        break;
                    }

                    System.out.print("Confirm move? (y/n): ");
                    if (!sc.nextLine().equalsIgnoreCase("y")) break;

                    boolean moved = ftth.moveCustomer(custID, newPin);
                    if (moved) {
                        System.out.println("✅ Customer " + custID + " moved to pincode " + newPin + " successfully.");
                    } else {
                        System.out.println("❌ Move failed.");
                    }
                    break;
                }

                // ==========================================================
                // 3. CHANGE — Customer changes service plan
                // ==========================================================
                case "3": {
                    System.out.println("\n--- Change Service ---");
                    System.out.print("Enter Customer ID: ");
                    String custID = sc.nextLine().trim().toUpperCase();

                    String[] customer = ftth.findCustomer(custID);
                    if (customer == null) {
                        System.out.println("❌ Customer ID not found.");
                        break;
                    }
                    System.out.println("Current Service : " + customer[6] + " @ Rs." + customer[7]);
                    System.out.println("Available Plans:");
                    System.out.println("  1. 300 MBPS, 60 GB/Month  -> Rs. 499");
                    System.out.println("  2. 500 MBPS, Unlimited    -> Rs. 1499");
                    System.out.print("Select New Plan (1/2): ");
                    String choice = sc.nextLine();
                    String newService = "";
                    int newPrice = 0;
                    if (choice.equals("1"))      { newService = "300 MBPS, 60 GB/Month"; newPrice = 499; }
                    else if (choice.equals("2")) { newService = "500 MBPS, Unlimited";   newPrice = 1499; }
                    else { System.out.println("Invalid choice."); break; }

                    System.out.print("Confirm change? (y/n): ");
                    if (!sc.nextLine().equalsIgnoreCase("y")) break;

                    boolean changed = ftth.changeCustomer(custID, newService, newPrice);
                    if (changed) System.out.println("✅ Service updated successfully.");
                    else         System.out.println("❌ Change failed.");
                    break;
                }

                // ==========================================================
                // 4. DELETE — Customer cancels, port is freed
                // ==========================================================
                case "4": {
                    System.out.println("\n--- Delete Customer ---");
                    System.out.print("Enter Customer ID: ");
                    String custID = sc.nextLine().trim().toUpperCase();

                    String[] customer = ftth.findCustomer(custID);
                    if (customer == null) {
                        System.out.println("❌ Customer ID not found.");
                        break;
                    }
                    System.out.println("Customer : " + customer[1]);
                    System.out.println("Pincode  : " + customer[2]);
                    System.out.println("Port     : " + customer[3]+"/"+customer[4]+"/Port"+customer[5]);
                    System.out.println("Service  : " + customer[6]);

                    System.out.print("⚠️  Confirm delete? This will free the port. (y/n): ");
                    if (!sc.nextLine().equalsIgnoreCase("y")) break;

                    boolean deleted = ftth.deleteCustomer(custID);
                    if (deleted) System.out.println("✅ Customer " + custID + " deleted. Port is now free.");
                    else         System.out.println("❌ Delete failed.");
                    break;
                }

                // ==========================================================
                // 5. VIEW ALL CUSTOMERS
                // ==========================================================
                // case "5": {
                //     ftth.listAllCustomers();
                //     break;
                // }

                // ==========================================================
                // 6. EXIT
                // ==========================================================
                case "5": {
                    System.out.println("Goodbye!");
                    sc.close();
                    return;
                }

                default:
                    System.out.println("Invalid option. Choose 1-6.");
            }
        }
    }

    // =====================================================================
    // INPUT HELPERS
    // =====================================================================

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                int val = sc.nextInt();
                sc.nextLine();
                return val;
            } else {
                System.out.println("Please enter numbers only.");
                sc.nextLine();
            }
        }
    }

    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextDouble()) {
                double val = sc.nextDouble();
                sc.nextLine();
                return val;
            } else {
                System.out.println("Please enter a valid number.");
                sc.nextLine();
            }
        }
    }
}


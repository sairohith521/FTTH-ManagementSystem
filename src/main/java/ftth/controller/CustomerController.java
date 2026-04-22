// package ftth.controller;
// import java.util.List;
// import java.util.Scanner;
// import ftth.service.*;
// import ftth.util.InputUtil;
// import ftth.model.*;
// public class CustomerController {
//     private final CustomerService customerService;
//     private final CustomerConnectionService customerConnectionService;
//     private final PlanService planService;


//     public CustomerController(CustomerService customerService,PlanService planService,CustomerConnectionService customerConnectionService) {
//         this.customerService = customerService;
//         this.planService=planService;
//         this.customerConnectionService=customerConnectionService;
//     }

//     // ============================
//     // ENTRY FROM ADMIN CONTROLLER
//     // ============================
//     public void menu(Scanner sc,User currentUser) {

//         System.out.println("\n--- Customer Lookup ---");
//         System.out.println("[1] Look up by Customer ID");
//         System.out.println("[2] List all customers");
//         System.out.print("Choose: ");

//         String choice = sc.nextLine().trim();

//         switch (choice) {
//             case "1":
//                 lookupById(sc,currentUser);
//                 break;
//             case "2":
//                 listAll();
//                 break;
//             default:
//                 System.out.println("Invalid choice.");
//         }
//     }

//     // ============================
//     // STEP 1: LOOKUP
//     // ============================
//     private void lookupById(Scanner sc,User currentUser) {

//         System.out.print("Enter Customer ID: ");
//         String customerCode = sc.nextLine().trim().toUpperCase();

//         Customer customer =
//                 customerService.lookupCustomerByCode(customerCode);

//         if (customer == null) {
//             System.out.println("No customer found. Returning to menu.");
//             return;
//         }

//         // ✅ SHOW CUSTOMER CARD
//         printCustomerCard(customer);

//         // ✅ CALL CUSTOMER ACTION MENU
//         customerActionMenu(sc, customer,currentUser);
//     }

//     private void listAll() {
//         List<Customer> customers = customerService.listAllCustomers();
//         if (customers.isEmpty()) {
//             System.out.println("No customers found.");
//             return;
//         }
        
//     printCustomerListHeader();

//     for (Customer c : customers) {
//         printCustomerRow(c);
//     }

//     }

//     // ============================
//     // STEP 2: CUSTOMER ACTION MENU
//     // ============================
//     private void customerActionMenu(Scanner sc, Customer customer,User currentUser) {

//         boolean back = false;

//         while (!back) {

//             System.out.println("\nWhat would you like to do?");
//             System.out.println("[1] Change Plan");
//             System.out.println("[2] Move (New Pincode)");
//             System.out.println("[3] Disconnect");
//             System.out.println("[4] Generate Bill");
//             System.out.println("[0] Back to Main Menu");
//             System.out.print("Select Option: ");

//             String opt = sc.nextLine().trim();

//             switch (opt) {
//                 case "1":
//                     changePlan(sc, customer,currentUser);
//                     customer = customerService
//                             .lookupCustomerByCode(customer.getCustomerCode());
//                     printCustomerCard(customer);
//                     break;

//                 case "2":
//                     moveCustomer(sc, customer);
//                     customer = customerService
//                             .lookupCustomerByCode(customer.getCustomerCode());
//                     printCustomerCard(customer);
//                     break;

//                 case "3":
//                     disconnectCustomer(sc, customer);
//                     back = true;
//                     break;

//                 case "4":
//                     // generateBill(sc, customer);
//                     break;

//                 case "0":
//                     back = true;
//                     break;

//                 default:
//                     System.out.println("Invalid option.");
//             }
//         }
//     }

//     // ============================
//     // ACTIONS (CALL SERVICE ONLY)
//     // ============================
//    // ============================
// // CHANGE PLAN (CALL SERVICE ONLY)
// // ============================
// private void changePlan(Scanner sc, Customer customer, User currentUser) {

//     // 1️⃣ Get active connection
//     CustomerConnection connection =
//         customerConnectionService.getActiveConnectionByCustomerCode(
//             customer.getCustomerCode()
//         );

//     if (connection == null) {
//         System.out.println("No active connection found for this customer.");
//         return;
//     }

//     // 2️⃣ Get current plan
//     Plan currentPlan =
//         planService.findPlanById(connection.getPlanId());

//     System.out.println(
//         "\nCurrent Plan : " +
//         currentPlan.getPlanName() +
//         " | Speed: " + currentPlan.getSpeedLabel() +
//         " | Data: " + currentPlan.getDataLimitLabel() +
//         " | OTTs: " + currentPlan.getOttCount() +
//         " | OLT: " + currentPlan.getOltType() +
//         " | Rs." + currentPlan.getMonthlyPrice()
//     );

//     // 3️⃣ Show available plans (excluding current)
//     List<Plan> plans = planService.getActivePlans();
//     plans.removeIf(p ->
//         p.getPlanId().equals(currentPlan.getPlanId())
//     );

//     if (plans.isEmpty()) {
//         System.out.println("No other plans available to switch to.");
//         return;
//     }

//     System.out.println("\nAvailable Plans:");
//     for (Plan p : plans) {
//         System.out.println(
//             p.getPlanId() + ". " +
//             p.getPlanName() +
//             " | Speed: " + p.getSpeedLabel() +
//             " | Data: " + p.getDataLimitLabel() +
//             " | OTTs: " + p.getOttCount() +
//             " | Rs." + p.getMonthlyPrice() +
//             " | OLT: " + p.getOltType()
//         );
//     }

//     // 4️⃣ Select new plan
//     Plan selectedPlan;
//     while (true) {
//         long planId = InputUtil.readLong(sc, "Select New Plan ID: ");
//         selectedPlan = planService.findPlanById(planId);

//         if (selectedPlan == null || !selectedPlan.isActive()) {
//             System.out.println("Invalid plan ID.");
//             continue;
//         }

//         if (selectedPlan.getPlanId().equals(currentPlan.getPlanId())) {
//             System.out.println("Already on this plan. Choose a different one.");
//             continue;
//         }

//         break;
//     }

//     // 5️⃣ Confirm
//     System.out.println(
//         "\nChange from : " +
//         currentPlan.getPlanName() +
//         " @ Rs." + currentPlan.getMonthlyPrice()
//     );
//     System.out.println(
//         "Change to   : " +
//         selectedPlan.getPlanName() +
//         " @ Rs." + selectedPlan.getMonthlyPrice()
//     );

//     System.out.print("Confirm change? (y/n): ");
//     if (!sc.nextLine().equalsIgnoreCase("y")) {
//         System.out.println("Cancelled.");
//         return;
//     }

//     // 6️⃣ Call SERVICE (single responsibility)
//     customerConnectionService.changePlan(
//         connection.getConnectionId(),
//         selectedPlan.getPlanId(),
//         currentUser.getUserId()
//     );

//     System.out.println("✅ Plan changed successfully.");
// }

//     private void moveCustomer(Scanner sc, Customer customer) {

//         System.out.print("Enter new pincode: ");
//         int newPin = Integer.parseInt(sc.nextLine().trim());

//         boolean success =
//                 customerService.moveCustomer(customer, newPin);

//         System.out.println(
//                 success ? "Customer moved successfully."
//                         : "Move failed."
//         );
//     }

//     private void disconnectCustomer(Scanner sc, Customer customer) {

//         System.out.print("Type customer ID to confirm disconnect: ");
//         String confirm = sc.nextLine().trim().toUpperCase();

//         if (!confirm.equals(customer.getCustomerCode())) {
//             System.out.println("ID mismatch. Cancelled.");
//             return;
//         }

//         boolean success = customerService.disconnect(customer);

//         System.out.println(
//                 success ? "Customer disconnected."
//                         : "Disconnect failed."
//         );
//     }

//     // private void generateBill(Scanner sc, Customer customer) {

//     //     var bill = billService.generateMonthlyBill(customer);

//     //     billService.printBill(bill);

//     //     System.out.print("Email bill? (y/n): ");
//     //     if (sc.nextLine().equalsIgnoreCase("y")) {
//     //         emailService.sendBillEmail(customer, bill);
//     //     }
//     // }

//     // ============================
//     // UI HELPER
//     // ============================
//    private void printCustomerCard(Customer c) {

//     System.out.println("\n+----------------------------------+");
//     System.out.println(" Customer ID : " + c.getCustomerCode());
//     System.out.println(" Name        : " + c.getFullName());
//     System.out.println(" Email       : " + c.getEmail());
//     System.out.println(" Salary      : Rs. " + c.getSalary());
//     System.out.println(" Status      : " + c.getStatus());
//     System.out.println("+----------------------------------+");
// }
//     private void printCustomerListHeader() {
//     System.out.printf(
//         "%-12s | %-20s | %-25s | %-6s | %-8s%n",
//         "Customer ID", "Name", "Email", "Pin", "Status"
//     );
//     System.out.println("=".repeat(80));
// }
// private void printCustomerRow(Customer c) {
//     System.out.printf(
//         "%-12s | %-20s | %-25s | %-10s%n",
//         c.getCustomerCode(),
//         c.getFullName(),
//         c.getEmail(),
//         c.getStatus()
//     );
// }
// }


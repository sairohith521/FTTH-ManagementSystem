package ftth.service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * CustomerScreen — Aaha Telecom FTTH
 *
 * Features:
 *   - Lookup by Customer Name OR Customer ID
 *   - Change Plan
 *   - Move (new pincode)
 *   - Disconnect
 *   - Bill Generation + optional Email
 *
 * Called from Main via: CustomerScreen.show(sc, ftth, email);
 */
public class Customercreen {

    public static void show(Scanner sc, FTTH ftth, EmailService email) {
        System.out.println("\n============================================");
        System.out.println("          Customer Screen");
        System.out.println("============================================");

        // --- Step 1: Lookup ---
        String[] customer = lookupCustomer(sc, ftth);
        if (customer == null) {
            System.out.println("  No customer found. Returning to menu.");
            return;
        }

        // --- Step 2: Display customer card ---
        printCustomerCard(customer);

        // --- Step 3: Action sub-menu ---
        boolean back = false;
        while (!back) {
            System.out.println("\n  What would you like to do?");
            System.out.println("  [1] Change Plan");
            System.out.println("  [2] Move (New Pincode)");
            System.out.println("  [3] Disconnect");
            System.out.println("  [4] Generate Bill");
            System.out.println("  [0] Back to Main Menu");
            System.out.println("  --------------------------------------------");
            System.out.print("  Select Option: ");
            String opt = sc.nextLine().trim();

            switch (opt) {
                case "1":
                    doChangePlan(sc, ftth, customer);
                    customer = ftth.findCustomer(customer[0]);
                    if (customer != null) printCustomerCard(customer);
                    break;

                case "2":
                    doMove(sc, ftth, email, customer);
                    customer = ftth.findCustomer(customer[0]);
                    if (customer != null) printCustomerCard(customer);
                    break;

                case "3":
                    doDisconnect(sc, ftth, customer);
                    back = true;
                    break;

                case "4":
                    doGenerateBill(sc, ftth, email, customer);
                    break;

                case "0":
                    back = true;
                    break;

                default:
                    System.out.println("  Invalid option. Please try again.");
            }
        }
    }

    private static String[] lookupCustomer(Scanner sc, FTTH ftth) {
        System.out.println("\n  Search by:");
        System.out.println("  [1] Customer ID  (e.g. AAHA-0001)");
        System.out.println("  [2] Customer Name");
        System.out.print("  Choose: ");
        String choice = sc.nextLine().trim();

        if (choice.equals("1")) {
            System.out.print("  Enter Customer ID: ");
            String id = sc.nextLine().trim().toUpperCase();
            String[] cust = ftth.findCustomer(id);
            if (cust == null) System.out.println("  Customer ID '" + id + "' not found.");
            return cust;

        } else if (choice.equals("2")) {
            System.out.print("  Enter Customer Name (or part of it): ");
            String nameQuery = sc.nextLine().trim().toLowerCase();

            List<String[]> matches = findByName(nameQuery);
            if (matches.isEmpty()) {
                System.out.println("  No customers found matching '" + nameQuery + "'.");
                return null;
            }
            if (matches.size() == 1) {
                System.out.println("  Found: " + matches.get(0)[1] + " (" + matches.get(0)[0] + ")");
                return matches.get(0);
            }

            System.out.println("\n  Multiple customers found:");
            System.out.printf("  %-4s  %-12s  %-20s  %-8s  %-8s%n",
                    "#", "ID", "Name", "Pincode", "Status");
            System.out.println("  " + "-".repeat(58));
            for (int i = 0; i < matches.size(); i++) {
                String[] m = matches.get(i);
                System.out.printf("  %-4d  %-12s  %-20s  %-8s  %-8s%n",
                        (i + 1), m[0], m[1], m[2], m[8]);
            }
            System.out.print("\n  Enter # to select (0 to cancel): ");
            try {
                int sel = Integer.parseInt(sc.nextLine().trim());
                if (sel == 0) return null;
                if (sel >= 1 && sel <= matches.size()) return matches.get(sel - 1);
            } catch (NumberFormatException ignored) {}
            System.out.println("  Invalid selection.");
            return null;

        } else {
            System.out.println("  Invalid choice.");
            return null;
        }
    }

    private static List<String[]> findByName(String nameQuery) {
        List<String[]> results = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("customers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 9) continue;
                if (parts[1].trim().toLowerCase().contains(nameQuery)) {
                    results.add(parts);
                }
            }
        } catch (Exception e) {
            System.out.println("  Error searching customers: " + e.getMessage());
        }
        return results;
    }

    private static void printCustomerCard(String[] c) {
        System.out.println("\n  +-----------------------------------------+");
        System.out.printf ("  |  %-41s|%n", "Customer Details");
        System.out.println("  +-----------------------------------------+");
        System.out.printf ("  |  %-12s : %-26s|%n", "ID",       c[0]);
        System.out.printf ("  |  %-12s : %-26s|%n", "Name",     c[1]);
        System.out.printf ("  |  %-12s : %-26s|%n", "Pincode",  c[2]);
        System.out.printf ("  |  %-12s : %-26s|%n", "OLT/SPL",  c[3]+"/"+c[4]+"/Port"+c[5]);
        System.out.printf ("  |  %-12s : %-26s|%n", "Service",  c[6]);
        System.out.printf ("  |  %-12s : Rs.%-23s|%n", "Price", c[7]+"/month");
        System.out.printf ("  |  %-12s : %-26s|%n", "Status",   c[8]);
        System.out.println("  +-----------------------------------------+");
    }

    private static void doChangePlan(Scanner sc, FTTH ftth, String[] customer) {
        if (customer[8].equals("DELETED")) {
            System.out.println("  Cannot change plan - customer is disconnected.");
            return;
        }
        System.out.println("\n  --- Change Plan ---");
        System.out.println("  Current : " + customer[6] + " @ Rs." + customer[7] + "/month");
        System.out.println("\n  Available Plans:");
        System.out.println("    [1] 300 MBPS, 60 GB/Month   -> Rs. 499");
        System.out.println("    [2] 500 MBPS, Unlimited     -> Rs. 1499");
        System.out.print("  Select New Plan (1/2): ");
        String choice = sc.nextLine().trim();

        String newService; int newPrice;
        if      (choice.equals("1")) { newService = "300 MBPS, 60 GB/Month"; newPrice = 499;  }
        else if (choice.equals("2")) { newService = "500 MBPS, Unlimited";   newPrice = 1499; }
        else { System.out.println("  Invalid choice."); return; }

        if (newService.equals(customer[6])) {
            System.out.println("  Customer is already on this plan.");
            return;
        }

        System.out.println("\n  Change from : " + customer[6] + " (Rs." + customer[7] + ")");
        System.out.println("  Change to   : " + newService   + " (Rs." + newPrice   + ")");
        System.out.print("  Confirm? (y/n): ");
        if (!sc.nextLine().equalsIgnoreCase("y")) { System.out.println("  Cancelled."); return; }

        boolean ok = ftth.changeCustomer(customer[0], newService, newPrice);
        if (ok) System.out.println("  Plan updated successfully.");
        else    System.out.println("  Plan change failed.");
    }

    private static void doMove(Scanner sc, FTTH ftth, EmailService email, String[] customer) {
        if (customer[8].equals("DELETED")) {
            System.out.println("  Cannot move - customer is disconnected.");
            return;
        }
        System.out.println("\n  --- Move Customer ---");
        System.out.println("  Current Pincode : " + customer[2]);

        System.out.print("  Enter New Pincode: ");
        int newPin;
        try {
            newPin = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  Invalid pincode.");
            return;
        }

        if (newPin == Integer.parseInt(customer[2])) {
            System.out.println("  Customer is already in pincode " + newPin + ".");
            return;
        }

        if (!ftth.checkPincode(newPin)) {
            System.out.println("  No available ports in pincode " + newPin + ".");
            System.out.println("  Sending alert to OLT provider...");
            email.sendNoOLTEmail(newPin);
            return;
        }

        int available = ftth.getAvailablePorts(newPin);
        System.out.println("  Available ports in " + newPin + " : " + available);
        System.out.print("  Confirm move from " + customer[2] + " to " + newPin + "? (y/n): ");
        if (!sc.nextLine().equalsIgnoreCase("y")) { System.out.println("  Cancelled."); return; }

        boolean ok = ftth.moveCustomer(customer[0], newPin);
        if (ok) System.out.println("  Customer moved to pincode " + newPin + " successfully.");
        else    System.out.println("  Move failed.");
    }

    private static void doDisconnect(Scanner sc, FTTH ftth, String[] customer) {
        if (customer[8].equals("DELETED")) {
            System.out.println("  Customer is already disconnected.");
            return;
        }
        System.out.println("\n  --- Disconnect Customer ---");
        System.out.println("  WARNING: This will FREE the port and mark the customer DELETED.");
        System.out.println("  Customer : " + customer[1] + " (" + customer[0] + ")");
        System.out.println("  Pincode  : " + customer[2]);
        System.out.println("  Port     : " + customer[3]+"/"+customer[4]+"/Port"+customer[5]);
        System.out.println("  Service  : " + customer[6]);
        System.out.print("\n  Type customer ID to confirm disconnect: ");
        String confirm = sc.nextLine().trim().toUpperCase();

        if (!confirm.equals(customer[0])) {
            System.out.println("  ID mismatch. Disconnect cancelled.");
            return;
        }

        boolean ok = ftth.deleteCustomer(customer[0]);
        if (ok) {
            System.out.println("  Customer " + customer[0] + " disconnected. Port is now free.");
        } else {
            System.out.println("  Disconnect failed.");
        }
    }

    private static void doGenerateBill(Scanner sc, FTTH ftth, EmailService email, String[] customer) {
        if (customer[8].equals("DELETED")) {
            System.out.println("  Cannot generate bill - customer is disconnected.");
            return;
        }

        LocalDate today    = LocalDate.now();
        LocalDate billDate = (today.getDayOfMonth() <= 10)
                ? today.withDayOfMonth(10)
                : today.plusMonths(1).withDayOfMonth(10);
        LocalDate dueDate  = billDate.plusDays(5);

        int price = 0;
        try { price = Integer.parseInt(customer[7].trim()); } catch (Exception ignored) {}

        int gst     = (int) Math.round(price * 0.18);
        int total   = price + gst;

        String billNo = "BILL-" + customer[0] + "-" + today.format(DateTimeFormatter.ofPattern("yyyyMM"));

        System.out.println("\n  +==========================================+");
        System.out.println("  |         AAHA TELECOM - INVOICE           |");
        System.out.println("  +==========================================+");
        System.out.printf ("  |  %-14s : %-24s|%n", "Bill No",    billNo);
        System.out.printf ("  |  %-14s : %-24s|%n", "Date",       today);
        System.out.printf ("  |  %-14s : %-24s|%n", "Due Date",   dueDate);
        System.out.println("  +==========================================+");
        System.out.printf ("  |  %-14s : %-24s|%n", "Customer ID", customer[0]);
        System.out.printf ("  |  %-14s : %-24s|%n", "Name",        customer[1]);
        System.out.printf ("  |  %-14s : %-24s|%n", "Pincode",     customer[2]);
        System.out.printf ("  |  %-14s : %-24s|%n", "Service",     customer[6]);
        System.out.println("  +==========================================+");
        System.out.printf ("  |  %-14s : Rs. %-20d|%n", "Plan Charge", price);
        System.out.printf ("  |  %-14s : Rs. %-20d|%n", "GST (18%)",   gst);
        System.out.println("  |  " + "-".repeat(40) + "|");
        System.out.printf ("  |  %-14s : Rs. %-20d|%n", "TOTAL DUE",   total);
        System.out.println("  +==========================================+");

        System.out.print("\n  Email this bill to customer? (y/n): ");
        if (sc.nextLine().equalsIgnoreCase("y")) {
            email.sendBillEmail(customer[1], customer[0], billNo,
                    customer[6], price, gst, total, billDate.toString(), dueDate.toString());
        }
    }
}

package ftth.controller;

import ftth.model.InventoryDetails;
import ftth.model.OLT;
import ftth.model.Port;
import ftth.model.Splitter;
import ftth.service.InventoryService;
import ftth.util.ValidationUtil;

import java.util.List;
import java.util.Scanner;

public class InventoryController {

    private final Scanner sc = new Scanner(System.in);
    private final InventoryService service = new InventoryService();

    public void menu() {
        while (true) {
            System.out.println("\n--- Inventory Admin ---");
            System.out.println("1. View Inventory Summary");
            System.out.println("2. Add OLT");
            System.out.println("3. Remove OLT");
            System.out.println("4. Add Splitter to OLT");
            System.out.println("5. Remove Splitter from OLT");
            System.out.println("6. View OLT Details");
            System.out.println("7. Back to Main Menu");
            System.out.print("\nSelect option : ");

            int ch;
            try {
                ch = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid numeric choice.");
                continue;
            }

            try {
                switch (ch) {
                    case 1 -> viewInventorySummary();
                    case 2 -> addOLTFlow();
                    case 3 -> removeOLTFlow();
                    case 4 -> splitterAddFlow();
                    case 5 -> splitterRemoveFlow();
                    case 6 -> viewOltDetailsFlow();
                    case 7 -> {
                        return;
                    }
                    default -> System.out.println("Enter valid choice");
                }
            } catch (RuntimeException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    private void viewInventorySummary() {
        System.out.println("===== Inventory Summary =====");
        List<String> pins = service.getUniquePincodes();
        if (pins.isEmpty()) {
            System.out.println("No service areas found.");
            return;
        }

        for (String pin : pins) {
            List<OLT> list = service.getByPincode(pin);
            System.out.println("Pincode: " + pin);
            if (list.isEmpty()) {
                System.out.println("  No OLTs found");
            } else {
                for (OLT o : list) {
                    System.out.println(o);
                }
            }
            System.out.println();
        }
    }

    private void addOLTFlow() {
        System.out.print("Enter Pincode: ");
        String pin = sc.nextLine().trim();
        if (!ValidationUtil.isValidPincode(pin)) {
            System.out.println("[ERROR] Enter valid pincode.");
            return;
        }

        System.out.println("OLT Types: [1] OLT500 (1GBPS) [2] OLT300 (400MBPS)");
        System.out.print("Select OLT Type [1/2]: ");
        String typeInput = sc.nextLine().trim();
        String type;
        if ("1".equals(typeInput)) {
            type = "OLT500";
        } else if ("2".equals(typeInput)) {
            type = "OLT300";
        } else {
            System.out.println("[ERROR] Enter valid OLT type.");
            return;
        }

        System.out.print("Number of Splitters (1-" + service.getMaxSplitters() + "): ");
        int split;
        try {
            split = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Enter numeric splitter count.");
            return;
        }

        String id = service.addOLT(pin, type, split);
        System.out.println();
        System.out.println("[SUCCESS] OLT Added: " + id + " with " + split + " splitters and "
                + service.getPortsPerSplitter() + " ports per splitter.");
    }

    private void removeOLTFlow() {
        System.out.print("Enter Pincode: ");
        String pin = sc.nextLine().trim();

        List<OLT> list = service.getByPincode(pin);
        if (list.isEmpty()) {
            System.out.println("No OLTs found for pincode " + pin + ".");
            return;
        }

        for (OLT o : list) {
            System.out.println(o);
        }

        System.out.print("Enter OLT ID to remove: ");
        String id = sc.nextLine().trim();
        System.out.print("Confirm removal of " + id + " ? (y/n): ");
        String option = sc.nextLine().trim();
        if (!option.equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            return;
        }

        boolean res = service.removeOLT(id);
        if (res) {
            System.out.println("[SUCCESS] Removed " + id + " successfully.");
        } else {
            System.out.println("[FAILED] Cannot remove " + id + " - assigned ports still exist or OLT not found.");
        }
    }

    private void splitterAddFlow() {
        System.out.print("Enter Pincode: ");
        String pin = sc.nextLine().trim();

        List<OLT> list = service.getByPincode(pin);
        if (list.isEmpty()) {
            System.out.println("No OLTs found for pincode " + pin + ".");
            return;
        }

        for (OLT o : list) {
            System.out.println(o);
        }

        System.out.print("Enter OLT ID: ");
        String id = sc.nextLine().trim();

        if (service.addSplitter(id)) {
            System.out.println("[SUCCESS] Splitter added to " + id + " successfully.");
        } else {
            System.out.println("[FAILED] Max splitters reached or OLT not found.");
        }
    }

    private void splitterRemoveFlow() {
        System.out.print("Enter Pincode: ");
        String pin = sc.nextLine().trim();

        List<OLT> list = service.getByPincode(pin);
        if (list.isEmpty()) {
            System.out.println("No OLTs found for pincode " + pin + ".");
            return;
        }

        for (OLT o : list) {
            System.out.println(o);
        }

        System.out.print("Enter OLT ID: ");
        String id = sc.nextLine().trim();
        InventoryDetails details = service.getInventoryDetails(id);
        if (details == null) {
            System.out.println("OLT not found.");
            return;
        }

        for (Splitter splitter : details.getSplitters()) {
            int totalPorts = splitter.getPorts().size();
            int availablePorts = 0;
            for (Port port : splitter.getPorts()) {
                if ("AVAILABLE".equalsIgnoreCase(port.getStatus())) {
                    availablePorts++;
                }
            }
            System.out.println("  Splitter " + splitter.getSplitterNumber()
                    + " | Ports: " + availablePorts + "/" + totalPorts + " available");
        }

        System.out.print("Enter Splitter Number to remove: ");
        int splitterNumber;
        try {
            splitterNumber = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Enter numeric splitter number.");
            return;
        }

        if (service.removeSplitter(id, splitterNumber)) {
            System.out.println("[SUCCESS] Splitter " + splitterNumber + " removed from " + id + ".");
        } else {
            System.out.println("[FAILED] Cannot remove splitter - it may have assigned ports or not exist.");
        }
    }

    private void viewOltDetailsFlow() {
        System.out.print("Enter OLT ID: ");
        String id = sc.nextLine().trim();
        InventoryDetails details = service.getInventoryDetails(id);
        if (details == null) {
            System.out.println("OLT not found.");
            return;
        }

        OLT olt = details.getOlt();
        System.out.println("\n=== OLT Details ===");
        System.out.println("OLT ID        : " + olt.getOltId());
        System.out.println("Pincode       : " + olt.getPincode());
        System.out.println("OLT Type      : " + olt.getType());
        System.out.println("Splitter Count: " + olt.getSplitterCount());
        System.out.println("Ports         : " + olt.getAvailablePorts() + "/" + olt.getTotalPorts() + " available");

        for (Splitter splitter : details.getSplitters()) {
            System.out.println("\nSplitter " + splitter.getSplitterNumber());
            for (Port port : splitter.getPorts()) {
                System.out.println("  Port " + port.getPortNumber() + " -> " + port.getStatus());
            }
        }
    }
}


package ftth.controller;
import ftth.model.*;
import ftth.model.dtos.OltInventoryDTO;
import ftth.service.InventoryService;
import ftth.util.InputUtil;
import ftth.util.ValidationUtil;

import java.util.List;
import java.util.Scanner;

public class InventoryController {

    private final Scanner sc = new Scanner(System.in);
    private final InventoryService service ;
    public InventoryController(InventoryService service){
        this.service=service;
    }

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
        case 1:
            viewInventorySummary();
            break;
        case 2:
            addOLTFlow();
            break;
        case 3:
            removeOLTFlow();
            break;
        case 4:
            splitterAddFlow();
            break;
        case 5:
            splitterRemoveFlow();
            break;
        case 6:
            viewOltDetailsFlow();
            break;
        case 7:
            return;
        default:
            System.out.println("Enter valid choice");
    }

            } catch (RuntimeException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

 private void viewInventorySummary() {

        List<String> pins = service.getUniquePincodes();

        System.out.println("\n=== Inventory Summary ===");
        System.out.printf("%-10s %-20s %-10s %-12s %-15s%n",
            "Pincode", "OLT Code", "Type", "Splitters", "Ports (Avail/Total)");
        System.out.println("-".repeat(70));

        for (String pin : pins) {
            List<OltInventoryDTO> list = service.getByPincode(pin);

            if (list.isEmpty()) {
                System.out.printf("%-10s %-20s%n", pin, "No OLTs found");
            } else {
                for (OltInventoryDTO o : list) {
                    System.out.printf("%-10s %-20s %-10s %-12d %-15s%n",
                        pin,
                        o.getOltCode(),
                        o.getOltType(),
                        o.getSplitterCount(),
                        o.getAvailablePorts() + "/" + o.getTotalPorts() + " available");
                }
            }
        }
        System.out.println();
    }

    private void addOLTFlow() {

    // 1️⃣ Read pincode
    String pin = InputUtil.readString(sc, "Enter Pincode: ");
    if (!ValidationUtil.isValidPincode(pin)) {
        System.out.println("[ERROR] Enter valid pincode.");
        return;
    }

    // 2️⃣ Read OLT type
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

    // 3️⃣ Read splitter count
    System.out.print(
        "Number of Splitters (1-" + service.getMaxSplitters() + "): "
    );

    int split;
    try {
        split = Integer.parseInt(sc.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("[ERROR] Enter numeric splitter count.");
        return;
    }

    if (split < 1 || split > service.getMaxSplitters()) {
        System.out.println(
            "[ERROR] Splitter count must be between 1 and "
            + service.getMaxSplitters() + "."
        );
        return;
    }

    // 4️⃣ Call service
    String id = service.addOLT(pin, type, split);

    // 5️⃣ Success message
    System.out.println();
    System.out.println(
        "[SUCCESS] OLT Added: " + id +
        " with " + split + " splitters and " +
        service.getPortsPerSplitter() +
        " ports per splitter."
    );
}

    private void removeOLTFlow() {

    System.out.print("Enter Pincode: ");
    String pin = sc.nextLine().trim();

    List<OltInventoryDTO> list = service.getByPincode(pin);
    if (list.isEmpty()) {
        System.out.println("No OLTs found for pincode " + pin + ".");
        return;
    }

    // Display OLT inventory summary
    for (OltInventoryDTO o : list) {
        System.out.println(
            o.getOltCode()
            + " | Type: " + o.getOltType()
            + " | Splitters: " + o.getSplitterCount()
            + " | Ports: " + o.getAvailablePorts()
            + "/" + o.getTotalPorts()
        );
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
        System.out.println(
            "[FAILED] Cannot remove " + id +
            " - assigned ports still exist or OLT not found."
        );
    }
}

   private void splitterAddFlow() {

    System.out.print("Enter Pincode: ");
    String pin = sc.nextLine().trim();

    List<OltInventoryDTO> list = service.getByPincode(pin);
    if (list.isEmpty()) {
        System.out.println("No OLTs found for pincode " + pin + ".");
        return;
    }

    // Display available OLTs
    for (OltInventoryDTO o : list) {
        System.out.println(
            o.getOltCode()
            + " | Type: " + o.getOltType()
            + " | Splitters: " + o.getSplitterCount()
            + " | Ports: " + o.getAvailablePorts()
            + "/" + o.getTotalPorts()
        );
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

    // 1️⃣ Read pincode
    System.out.print("Enter Pincode: ");
    String pin = sc.nextLine().trim();

    // 2️⃣ Get OLT inventory summary (DTO)
    List<OltInventoryDTO> list = service.getByPincode(pin);
    if (list.isEmpty()) {
        System.out.println("No OLTs found for pincode " + pin + ".");
        return;
    }

    // 3️⃣ Display OLTs
    for (OltInventoryDTO o : list) {
        System.out.println(
            o.getOltCode()
            + " | Type: " + o.getOltType()
            + " | Splitters: " + o.getSplitterCount()
            + " | Ports: " + o.getAvailablePorts()
            + "/" + o.getTotalPorts()
        );
    }

    // 4️⃣ Read OLT code
    System.out.print("Enter OLT ID: ");
    String oltCode = sc.nextLine().trim();

    // 5️⃣ Load full inventory details (ENTITY)
    InventoryDetails details = service.getInventoryDetails(oltCode);
    if (details == null) {
        System.out.println("OLT not found.");
        return;
    }

    for (Splitter splitter : details.getSplitters()) {
        System.out.println(
            "Splitter " + splitter.getSplitterNumber()
            + " | Ports: " + splitter.getAvailablePorts()
            + "/" + splitter.getTotalPorts() + " available"
        );
    }


    // 7️⃣ Read splitter number
    System.out.print("Enter Splitter Number to remove: ");
    int splitterNumber;
    try {
        splitterNumber = Integer.parseInt(sc.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("[ERROR] Enter numeric splitter number.");
        return;
    }

    // 8️⃣ Remove splitter
    boolean success = service.removeSplitter(oltCode, splitterNumber);

    System.out.println(
        success
            ? "[SUCCESS] Splitter " + splitterNumber + " removed from " + oltCode + "."
            : "[FAILED] Cannot remove splitter - it may have assigned ports or not exist."
    );
}
private void viewOltDetailsFlow() {

    System.out.print("Enter Pincode: ");
    String pin = sc.nextLine().trim();

    List<OltInventoryDTO> list = service.getByPincode(pin);
    if (list.isEmpty()) {
        System.out.println("No OLTs found for pincode " + pin + ".");
        return;
    }

    System.out.println("\n--- OLTs in Pincode " + pin + " ---");
    System.out.printf("%-5s %-20s %-10s %-12s %-15s%n",
        "#", "OLT Code", "Type", "Splitters", "Ports (Avail/Total)");
    System.out.println("-".repeat(65));
    for (int i = 0; i < list.size(); i++) {
        OltInventoryDTO o = list.get(i);
        System.out.printf("%-5d %-20s %-10s %-12d %-15s%n",
            (i + 1), o.getOltCode(), o.getOltType(),
            o.getSplitterCount(),
            o.getAvailablePorts() + "/" + o.getTotalPorts() + " available");
    }

    System.out.print("\nEnter OLT Code: ");
    String oltCode = sc.nextLine().trim();

    InventoryDetails details = service.getInventoryDetails(oltCode);
    if (details == null) {
        System.out.println("OLT not found.");
        return;
    }

    Olt olt = details.getOlt();
    int totalPorts = details.getSplitters().size() * service.getPortsPerSplitter();
    int availablePorts = service.getAvailablePortsByType(olt.getServiceAreaId(), olt.getOltType());

    System.out.println("\n+---------------------------------------+");
    System.out.println("|           OLT Details                 |");
    System.out.println("+---------------------------------------+");
    System.out.printf("| %-14s : %-20s|%n", "OLT Code", olt.getOltCode());
    System.out.printf("| %-14s : %-20s|%n", "OLT Type", olt.getOltType());
    System.out.printf("| %-14s : %-20d|%n", "Splitters", details.getSplitters().size());
    System.out.printf("| %-14s : %-20s|%n", "Ports", availablePorts + "/" + totalPorts + " available");
    System.out.printf("| %-14s : %-20s|%n", "Status", olt.isActive() ? "ACTIVE" : "INACTIVE");
    System.out.println("+---------------------------------------+");

    System.out.println("\n--- Splitter Breakdown ---");
    System.out.printf("%-12s %-22s %-18s%n", "Splitter #", "Code", "Ports");
    System.out.println("-".repeat(55));
    for (Splitter splitter : details.getSplitters()) {
        System.out.printf("%-12d %-22s %d/%d available%n",
            splitter.getSplitterNumber(),
            splitter.getSplitterCode() != null ? splitter.getSplitterCode() : "-",
            splitter.getAvailablePorts(),
            splitter.getTotalPorts());
    }
    System.out.println();
}
}


    
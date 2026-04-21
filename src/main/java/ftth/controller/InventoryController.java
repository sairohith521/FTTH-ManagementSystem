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

        for (String pin : pins) {
            System.out.println("Pincode: " + pin);

            List<OltInventoryDTO> list = service.getByPincode(pin);

            if (list.isEmpty()) {
                System.out.println("  No OLTs found");
            } else {
                for (OltInventoryDTO o : list) {
                    System.out.println(o.getOltCode()
                        + " | " + o.getOltType()
                        + " | " + o.getAvailablePorts()
                        + "/" + o.getTotalPorts());
                }
            }
        }
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

    //// 6️⃣ Show splitters and port availability (CORRECT WAY)
for (Splitter splitter : details.getSplitters()) {

    int totalPorts = service.getPortsPerSplitter();
    int availablePorts =
        service.getAvailablePortsByType(
            details.getOlt().getServiceAreaId(),
            details.getOlt().getOltType()
        );

    System.out.println(
        "Splitter " + splitter.getSplitterNumber()
        + " | Ports: " + availablePorts + "/" + totalPorts + " available"
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

    System.out.print("Enter OLT ID: ");
    String oltCode = sc.nextLine().trim();

    // 1️⃣ Load inventory details
    InventoryDetails details = service.getInventoryDetails(oltCode);
    if (details == null) {
        System.out.println("OLT not found.");
        return;
    }

    // 2️⃣ Print OLT summary (ONLY fields that exist)
    Olt olt = details.getOlt();

    System.out.println("\n=== OLT Details ===");
    System.out.println("OLT Code      : " + olt.getOltCode());
    System.out.println("OLT Type      : " + olt.getOltType());
    System.out.println("Splitter Count: " + details.getSplitters().size());

    // ✅ Inventory counts via service (NOT OLT entity)
    int availablePorts =
        service.getAvailablePortsByType(
            olt.getServiceAreaId(),
            olt.getOltType()
        );

    int totalPorts =
        details.getSplitters().size() * service.getPortsPerSplitter();

    System.out.println(
        "Ports         : " +
        availablePorts + "/" + totalPorts + " available"
    );

    // 3️⃣ Print splitter info (NO ports list)
    System.out.println("\n--- Splitter Details ---");
    for (Splitter splitter : details.getSplitters()) {
        System.out.println(
            "Splitter " + splitter.getSplitterNumber() +
            " | Ports per splitter: " +
            service.getPortsPerSplitter()
        );
    }
}
}


    
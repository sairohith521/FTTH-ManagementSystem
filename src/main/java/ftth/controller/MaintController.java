package ftth.controller;

import java.util.Scanner;
import ftth.service.*;

public class MaintController {
    private InventoryService inventoryService;
    private PlanService planService;
    // 🔹 Constructor (Dependency Injection)
    public MaintController(InventoryService inventoryService,PlanService planService) {
        this.inventoryService = inventoryService;
        this.planService=planService;
    }

    // 🔹 MAIN HANDLER (NO STATIC ❌)
    public boolean handle(String option, Scanner sc) {

        switch (option) {

            case "1":
                doInventory(sc);
                return false;

            case "2":
                doMaint(sc);
                return false;

            case "3":
                doCapacity(sc);
                return false;

            case "4":
                doPlanAdmin(sc);
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
    // inventoryService.(pincodes);

    System.out.print("\nPress Enter to continue...");
    sc.nextLine();
}

     static void doPlanAdmin(Scanner sc) {
         PlanAdmin admin = new PlanAdmin(sc);
         admin.handleMenu();
    }
}
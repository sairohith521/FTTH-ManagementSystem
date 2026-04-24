package ftth.controller;

import java.util.Scanner;

import ftth.model.User;
import ftth.service.CapacityService;

public class MaintController {
    private final PlanAdmin planAdmin;
    private final InventoryController inventoryController;
    private final CapacityService capacityService;
    // 🔹 Constructor (Dependency Injection)
    public MaintController(InventoryController inventoryController,PlanAdmin planAdmin,CapacityService capacityService) {
        this.planAdmin=planAdmin;
        this.inventoryController=inventoryController;
        this.capacityService=capacityService;
    }

    // 🔹 MAIN HANDLER (NO STATIC ❌)
    public boolean handle(String option, Scanner sc,User currUser) {

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
                doPlanAdmin(sc,currUser);
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

    void doInventory(Scanner sc) {
    inventoryController.menu(); 
}

    private void doMaint(Scanner sc) {

    System.out.println("\n--- Maintenance ---");
    System.out.println("  (Maintenance module — extend as needed.)");
    System.out.print("Press Enter to continue...");

    sc.nextLine();
}

    private void doCapacity(Scanner sc) {

    System.out.println("\n--- Capacity Dashboard ---");
    capacityService.showCapacityDashboard();
}

     void doPlanAdmin(Scanner sc,User currUser) {
         planAdmin.handleMenu(currUser);
    }
}
package ftth.controller;
import java.util.Scanner;
import ftth.model.User;
import ftth.service.*;

public class AdminController {
    private final UserManagerService um;
    private final PlanAdmin planAdmin;
    private final CustomerScreenController customerScreenController;
    private final CustomerConnectionController customerConnectionController;
    private final InventoryController inventoryController;
    private final CapacityService capacityService;

    // 🔹 Constructor (Dependency Injection)
    public AdminController(InventoryController inventoryController,PlanAdmin planAdmin,CustomerScreenController customerScreenController,CustomerConnectionController customerConnectionController,UserManagerService userManagerService,CapacityService capacityService){
        this.um=userManagerService;
        this.customerScreenController = customerScreenController;
        this.customerConnectionController=customerConnectionController;
        this.planAdmin=planAdmin;
        this.inventoryController=inventoryController;
        this.capacityService=capacityService;
    }

    // 🔹 MAIN HANDLER (NO STATIC ❌)
    public boolean handle(String option, Scanner sc, User currentUser) {
        switch (option) {

            case "1":
                doAdd(sc,currentUser);
                return false;

            case "2":
                doMove(sc,currentUser);
                return false;

            case "3":
                doChange(sc,currentUser);
                return false;

            case "4":
                doDelete(sc,currentUser);
                return false;

            case "5":
                doLookup(sc,currentUser);
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
                doPlanAdmin(sc,currentUser);
                return false;

            case "A":
                doUserMgmt(sc,currentUser);
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
    // 🔥 METHODS 
    // =========================================================

    private void doAdd(Scanner sc,User currUser) {
    customerConnectionController.handleAdd(sc,currUser);
}

private void doMove(Scanner sc,User currUser) {
    customerConnectionController.updateCustomerConnection(sc,currUser);
}

   private void doChange(Scanner sc,User currUser) {
    customerConnectionController.doChangePlan(sc, currUser);
   }
   private void doDelete(Scanner sc,User currUser) {
customerConnectionController.doDisconnect(sc, currUser);   
}
private void doLookup(Scanner sc,User currentUser) {
    customerScreenController.menu(sc,currentUser);
}
private void doInventory(Scanner sc) {
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

   void doPlanAdmin(Scanner sc,User currentUser) {
         planAdmin.handleMenu(currentUser);
    }
    private  void doUserMgmt(Scanner sc, User currentUser) {
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
                    if (uname.equalsIgnoreCase(currentUser.getUsername())) {
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
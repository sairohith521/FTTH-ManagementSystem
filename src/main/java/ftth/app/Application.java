package ftth.app;

import java.util.Scanner;
import ftth.service.*;
import ftth.util.InputUtil;
import ftth.controller.*;
import ftth.repository.CustomerRepository;
/**
 * Main — Aaha Telecom FTTH Management System
 *
 * Roles & Menus
 * ─────────────
 * ADMIN : All options + User Management
 * CSR   : Add / Move / Change / Disconnect / Customer Lookup
 * MAINT : Inventory Admin / Maintenance / Capacity Dashboard / Plan Admin
 */

public class Application {

    private Scanner sc = new Scanner(System.in);

    private FTTH ftth = new FTTH();
       // 🔥 SERVICES
    private EmailService emailService = new EmailService();
    private UserManagerService userManagerService = new UserManagerService();
    private InventoryService inventoryService = new InventoryService();
    private PlanService planService = new PlanService();
    private CustomerRepository customerRepo = new CustomerRepository();
    private CustomerConnectionService connectionService = new CustomerConnectionService(ftth, customerRepo,emailService, inventoryService, planService);
       // 🔥 CONTROLLERS
    private AdminController adminController;
    private CSRController csrController;
    private MaintController maintController;

      public Application() {
        adminController = new AdminController(connectionService, inventoryService, planService, userManagerService);
        csrController = new CSRController(connectionService, planService, inventoryService);
        maintController = new MaintController(inventoryService, planService);
    }

    public void start() {
        //User Manager Controller
        // UserManagerService.initDefaults();
        while (true) {
            // 🔹 LOGIN
            System.out.println("\n===== LOGIN =====");
            String username = InputUtil.readValidUsername(sc, "Username: ");
            String password = InputUtil.readPassword("Password: ");

            String role = userManagerService.login(username, password);//reading from file need to change to db 

            if (role == null) {
                System.out.println("Invalid credentials");
                continue;
            }

            System.out.println("Login successful: " + role);

            // 🔹 MENU LOOP
            boolean logout = false;

            while (!logout) {
                printMenu(role);
                String option = InputUtil.readMenuOption(sc, "Option: ");
                switch (role) {
                    case "ADMIN":
                        logout = adminController.handle(option, sc, username);
                        break;

                    case "CSR":
                        logout = csrController.handle(option, sc);
                        break;

                    case "MAINT":
                        logout = maintController.handle(option, sc);
                        break;

                    default:
                        logout = true;
                }
            }
        }
    }

    // 🔹 MENU (belongs here)
    private void printMenu(String role) {
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
}
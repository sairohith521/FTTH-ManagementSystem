package ftth.controller;

import ftth.model.Role;
import ftth.model.User;
import ftth.service.UserManagerService;
import ftth.util.InputUtil;

import java.util.Scanner;

public class UserManagementController {
    private User currentUser;
    private final UserManagerService userManagerService;
    private final AdminController adminController;
    private final CSRController csrController;
    private final MaintController maintController;
    public UserManagementController(UserManagerService userManagerService,AdminController adminController,CSRController csrController,MaintController maintController) {
        this.userManagerService = userManagerService;
        this.adminController=adminController;
        this.csrController=csrController;
        this.maintController=maintController;
    }
    public void start(Scanner sc) {
        while (true) {
            currentUser = login(sc);
            if (currentUser == null) continue;
            Role role = userManagerService.getRole(currentUser);
            boolean logout = false;
            while (!logout) {
                printMenu(role.getRoleCode());
                String option = InputUtil.readMenuOption(sc, "Option: ");
                switch (role.getRoleCode()){
                    case "ADMIN":
                        logout = adminController.handle(option, sc, currentUser);
                        break;
                    case "CSR":
                        logout = csrController.handle(option, sc, currentUser);
                        break;
                    case "MAINT":
                        logout = maintController.handle(option, sc, currentUser);
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
    public User login(Scanner sc) {

        System.out.println("\n===== LOGIN =====");
        String username = InputUtil.readValidUsername(sc, "Username: ");
        String password = InputUtil.readPassword("Password: ");

        User user = userManagerService.login(username, password);

        if (user == null) {
            System.out.println("Invalid credentials.");
            return null;
        }

        Role role = userManagerService.getRole(user);
        System.out.println("Login successful. Role: " + role.getRoleCode());

        return user;
    }
}
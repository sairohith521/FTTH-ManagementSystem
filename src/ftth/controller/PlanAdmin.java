package ftth.controller;
import ftth.model.Plan;
import ftth.service.PlanService;
import java.util.*;

public class PlanAdmin {

    private PlanService service;
    private Scanner sc;

    // 🔹 Constructor
    public PlanAdmin() {
        service = new PlanService();
        sc = new Scanner(System.in);
    }

    // 🔹 Main menu handler
    public void handleMenu() {

    while (true) {   // 🔥 ADD THIS

        System.out.println("\n--- PLAN ADMIN MENU ---");
        System.out.println("1. View Plans");
        System.out.println("2. Add Plan");
        System.out.println("3. Update Plan");
        System.out.println("4. Disable Plan");
        System.out.println("5. Exit");

        System.out.print("Enter choice: ");

        String input = sc.nextLine().trim();
        int choice = Integer.parseInt(input);

        switch (choice) {

            case 1:
                service.viewPlans();
                break; 

            case 2:
                addPlanFlow();
                break;

            case 3:
                updatePlanFlow();
                break;

            case 4:
                disablePlanFlow();
                break;

            case 5:
                System.out.println("Exiting...");
                return;

            default:
                System.out.println("Invalid choice");
        }
    }   // 🔥 LOOP END
}

    // 🔹 Add Plan Flow
    private void addPlanFlow() {

        System.out.print("Enter Plan ID: ");
        String id = sc.nextLine();

        System.out.print("Enter Plan Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Speed: ");
        String speed = sc.nextLine();

        System.out.print("Enter Data Limit: ");
        String data = sc.nextLine();

        System.out.print("Enter OTT count: ");
        int otts = sc.nextInt();

        System.out.print("Enter Price: ");
        double price = sc.nextDouble();
        sc.nextLine();

        System.out.print("Enter OLT Type: ");
        String olt = sc.nextLine();

        Plan plan = new Plan(id, name, speed, data, otts, price, olt, true);

        service.addPlan(plan);

        System.out.println("✅ Plan added successfully!");
    }

    // 🔹 Update Plan Flow
    private void updatePlanFlow() {

        System.out.print("Enter Plan ID to update: ");
        String id = sc.nextLine();

        Plan existing = service.findPlanById(id);

        if (existing == null) {
            System.out.println("❌ Plan not found!");
            return;
        }

        System.out.print("Enter new name: ");
        String name = sc.nextLine();

        System.out.print("Enter new speed: ");
        String speed = sc.nextLine();

        System.out.print("Enter new data limit: ");
        String data = sc.nextLine();

        System.out.print("Enter new OTT count: ");
        int otts = sc.nextInt();

        System.out.print("Enter new price: ");
        double price = sc.nextDouble();
        sc.nextLine();

        System.out.print("Enter new OLT type: ");
        String olt = sc.nextLine();

        Plan updated = new Plan(id, name, speed, data, otts, price, olt, true);

        boolean success = service.updatePlan(id, updated);

        if (success)
            System.out.println("✅ Plan updated successfully!");
        else
            System.out.println("❌ Update failed!");
    }

    // 🔹 Disable Plan Flow
    private void disablePlanFlow() {

        System.out.print("Enter Plan ID to disable: ");
        String id = sc.nextLine();

        boolean success = service.disablePlan(id);

        if (success)
            System.out.println("✅ Plan disabled!");
        else
            System.out.println("❌ Plan not found!");
    }
}

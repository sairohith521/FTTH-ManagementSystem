package ftth.controller;

import ftth.model.Plan;
import ftth.service.PlanService;

import java.util.Scanner;

public class PlanAdmin {

    private final PlanService service;
    private final Scanner sc;

    public PlanAdmin() {
        service = new PlanService();
        sc = new Scanner(System.in);
    }

    public void handleMenu() {
        while (true) {
            System.out.println("\n--- PLAN ADMIN MENU ---");
            System.out.println("1. View Plans");
            System.out.println("2. Add Plan");
            System.out.println("3. Update Plan");
            System.out.println("4. Delete Plan");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");

            String input = sc.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice.");
                continue;
            }

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
                    deletePlanFlow();
                    break;

                case 5:
                    System.out.println("Exiting...");
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private void addPlanFlow() {
        System.out.print("Enter Plan Code: ");
        String code = sc.nextLine().trim();

        System.out.print("Enter Plan Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter Speed: ");
        String speed = sc.nextLine().trim();

        System.out.print("Enter Data Limit: ");
        String data = sc.nextLine().trim();

        System.out.print("Enter OTT count: ");
        int otts = readInt();

        System.out.print("Enter Price: ");
        double price = readDouble();

        System.out.print("Enter OLT Type: ");
        String olt = sc.nextLine().trim();

        Plan plan = new Plan(code, name, speed, data, otts, price, olt, true);

        boolean added = service.addPlan(plan);
        if (added) {
            System.out.println("Plan added successfully.");
        } else {
            System.out.println("Failed to add plan.");
        }
    }

    private void updatePlanFlow() {
        System.out.print("Enter Plan ID to update: ");
        long id = readLong();

        Plan existing = service.findPlanById(id);
        if (existing == null) {
            System.out.println("Plan not found.");
            return;
        }

        System.out.print("Enter new plan code: ");
        String code = sc.nextLine().trim();

        System.out.print("Enter new name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter new speed: ");
        String speed = sc.nextLine().trim();

        System.out.print("Enter new data limit: ");
        String data = sc.nextLine().trim();

        System.out.print("Enter new OTT count: ");
        int otts = readInt();

        System.out.print("Enter new price: ");
        double price = readDouble();

        System.out.print("Enter new OLT type: ");
        String olt = sc.nextLine().trim();

        Plan updated = new Plan(id, code, name, speed, data, otts, price, olt, existing.isActive());
        boolean success = service.updatePlan(id, updated);

        if (success) {
            System.out.println("Plan updated successfully.");
        } else {
            System.out.println("Update failed.");
        }
    }

    private void deletePlanFlow() {
        System.out.print("Enter Plan ID to delete: ");
        long id = readLong();

        boolean success = service.deletePlan(id);
        if (success) {
            System.out.println("Plan deleted successfully.");
        } else {
            System.out.println("Plan not found.");
        }
    }

    private int readInt() {
        while (true) {
            String value = sc.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }

    private long readLong() {
        while (true) {
            String value = sc.nextLine().trim();
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid id: ");
            }
        }
    }

    private double readDouble() {
        while (true) {
            String value = sc.nextLine().trim();
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid amount: ");
            }
        }
    }
}

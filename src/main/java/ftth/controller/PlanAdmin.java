package ftth.controller;

import ftth.model.Plan;
import ftth.service.PlanService;

import java.util.Scanner;

public class PlanAdmin {

    private final PlanService service;
    private final Scanner sc;

    public PlanAdmin(Scanner sc) {
        service = new PlanService();
        this.sc = sc;
    }

    public void handleMenu() {
        while (true) {
            try {
                System.out.println("\n--- PLAN ADMIN MENU ---");
                System.out.println("1. View Plans");
                System.out.println("2. Add Plan");
                System.out.println("3. Update Plan");
                System.out.println("4. Enable / Disable Plan");
                System.out.println("5. Delete Plan");
                System.out.println("6. Exit");

                System.out.print("Enter choice: ");

                if (!sc.hasNextLine()) return;
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
                        service.viewActivePlans();
                        break;
                    case 2:
                        addPlanFlow();
                        break;
                    case 3:
                        updatePlanFlow();
                        break;
                    case 4:
                        togglePlanFlow();
                        break;
                    case 5:
                        deletePlanFlow();
                        break;
                    case 6:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice");
                }
            } catch (java.util.NoSuchElementException e) {
                return;
            } catch (RuntimeException e) {
                System.out.println("[ERROR] " + e.getMessage());
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

        System.out.print("Enter OLT Type OLT300/OLT500: ");
        String olt = sc.nextLine().trim();

        Plan plan = new Plan(code, name, speed, data, otts, price, olt, true);

        boolean added = service.addPlan(plan);
        if (added) System.out.println("Plan added successfully.");
        else       System.out.println("Failed to add plan.");
    }

    private void updatePlanFlow() {
        service.viewActivePlans();

        System.out.print("\nEnter Plan ID to update: ");
        long id = readLong();

        Plan existing = service.findPlanById(id);
        if (existing == null) {
            System.out.println("Plan not found.");
            return;
        }

        System.out.println("Current: " + existing);

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

        System.out.print("Enter new OLT type OLT300/OLT500: ");
        String olt = sc.nextLine().trim();

        Plan updated = new Plan(id, code, name, speed, data, otts, price, olt, existing.isActive());
        boolean success = service.updatePlan(id, updated);

        if (success) System.out.println("Plan updated successfully.");
        else         System.out.println("Update failed.");
    }

    private void togglePlanFlow() {
        // Show all plans so user can see current status
        service.viewAllPlans();

        System.out.print("\nEnter Plan ID to Enable/Disable: ");
        long id = readLong();

        Plan plan = service.findPlanById(id);
        if (plan == null) {
            System.out.println("Plan not found.");
            return;
        }

        if (plan.isActive()) {
            System.out.print("Plan '" + plan.getName() + "' is currently ENABLED. Disable it? (y/n): ");
        } else {
            System.out.print("Plan '" + plan.getName() + "' is currently DISABLED. Enable it? (y/n): ");
        }

        String confirm = sc.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            return;
        }

        boolean success = service.togglePlan(id);
        if (success) {
            String newState = plan.isActive() ? "DISABLED" : "ENABLED";
            System.out.println("Plan '" + plan.getName() + "' is now " + newState + ".");
        } else {
            System.out.println("Failed to toggle plan status.");
        }
    }

    private void deletePlanFlow() {
        service.viewAllPlans();

        System.out.print("\nEnter Plan ID to delete: ");
        long id = readLong();

        Plan plan = service.findPlanById(id);
        if (plan == null) {
            System.out.println("Plan not found.");
            return;
        }

        System.out.println("Plan: " + plan);
        System.out.print("Are you sure you want to permanently delete '" + plan.getName() + "'? (y/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            return;
        }

        boolean success = service.deletePlan(id);
        if (success) System.out.println("Plan '" + plan.getName() + "' deleted permanently.");
        else         System.out.println("Failed to delete plan.");
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

package ftth.controller;

import ftth.model.Plan;
import ftth.service.PlanService;

import java.util.Scanner;

public class PlanAdmin {

    private final PlanService service;
    private final Scanner sc;
    private static final String[] VALID_OLTS = {"OLT300", "OLT500"};

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
        String name = readPlanName("Enter Plan Name: ");
        String speed = readSpeed("Enter Speed (MBPS): ");
        String data = readDataLimit("Enter Data Limit (GB): ");

        System.out.print("Enter OTT count: ");
        int otts = readInt();

        double price = readPrice("Enter Price: ");
        String olt = readOlt("Enter OLT Type (OLT300/OLT500): ");

        Plan plan = new Plan(name, speed, data, otts, price, olt, true);

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

        String name = readPlanName("Enter new name: ");
        String speed = readSpeed("Enter new Speed (MBPS): ");
        String data = readDataLimit("Enter new Data Limit (GB): ");

        System.out.print("Enter new OTT count: ");
        int otts = readInt();

        double price = readPrice("Enter new Price: ");
        String olt = readOlt("Enter new OLT Type (OLT300/OLT500): ");

        Plan updated = new Plan(id, name, speed, data, otts, price, olt, existing.isActive());
        boolean success = service.updatePlan(id, updated);

        if (success) System.out.println("Plan updated successfully.");
        else         System.out.println("Update failed.");
    }

    private void togglePlanFlow() {
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

    // --- Validation helpers ---

    private String readPlanName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String name = sc.nextLine().trim();
            if (!name.isEmpty() && name.matches("^[a-zA-Z0-9 ]+$")) {
                return name;
            }
            System.out.println("Enter valid plan name (no special characters allowed).");
        }
    }

    private String readSpeed(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                double speed = Double.parseDouble(input);
                if (speed > 0) return speed + " MBPS";
            } catch (NumberFormatException ignored) {}
            System.out.println("Enter valid speed (numbers only).");
        }
    }

    private String readDataLimit(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("unlimited")) {
                return "Unlimited Internet";
            }
            try {
                double limit = Double.parseDouble(input);
                if (limit > 0) return limit + " GB";
            } catch (NumberFormatException ignored) {}
            System.out.println("Enter valid data limit (enter 'Unlimited' or a number).");
        }
    }

    private double readPrice(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                double price = Double.parseDouble(input);
                if (price > 0) return price;
            } catch (NumberFormatException ignored) {}
            System.out.println("Enter valid price (numbers only).");
        }
    }

    private String readOlt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim().toUpperCase();
            for (String valid : VALID_OLTS) {
                if (valid.equals(input)) return input;
            }
            System.out.println("Enter valid OLT type (OLT300 or OLT500 only).");
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
}

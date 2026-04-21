package ftth.controller;

import ftth.model.Plan;
import ftth.model.User;
import ftth.service.PlanService;
import ftth.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class PlanAdmin {

    private final PlanService service;
    private final Scanner sc;

    public PlanAdmin(PlanService planService, Scanner sc) {
        service = planService;
        this.sc = sc;
    }

    public void handleMenu() {
        handleMenu(null);
    }

    public void handleMenu(User currUser) {
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
                try { choice = Integer.parseInt(input); }
                catch (NumberFormatException e) { System.out.println("Invalid choice."); continue; }

                switch (choice) {
                    case 1: service.viewActivePlans(); break;
                    case 2: addPlanFlow(currUser); break;
                    case 3: updatePlanFlow(currUser); break;
                    case 4: togglePlanFlow(currUser); break;
                    case 5: deletePlanFlow(currUser); break;
                    case 6: System.out.println("Exiting..."); return;
                    default: System.out.println("Invalid choice");
                }
            } catch (java.util.NoSuchElementException e) {
                return;
            } catch (RuntimeException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    private void addPlanFlow(User currUser) {
        String planName = readPlanName();
        String speedLabel = readSpeed();
        String dataLimitLabel = readDataLimit();

        System.out.print("Enter OTT Count: ");
        int ottCount = readInt();

        System.out.print("Enter Monthly Price: ");
        BigDecimal monthlyPrice = readBigDecimal();

        String oltType = readOltType();

        System.out.println("\n--- New Plan Summary ---");
        System.out.printf("%-6s | %-15s | %-10s | %-15s | %-5s | %-10s | %-8s%n",
            "ID", "Name", "Speed", "Data", "OTT", "Price", "OLT");
        System.out.println("-".repeat(80));
        System.out.printf("%-6s | %-15s | %-10s | %-15s | %-5d | %-10s | %-8s%n",
            "NEW", planName, speedLabel, dataLimitLabel, ottCount, monthlyPrice, oltType);

        System.out.print("\nConfirm add? (y/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("y")) { System.out.println("Cancelled."); return; }

        Plan plan = new Plan(null, planName, speedLabel, dataLimitLabel, ottCount, monthlyPrice, oltType, true);
        if (currUser != null) service.createPlan(plan, currUser);
        else service.createPlan(plan);
        System.out.println("Plan added successfully.");
    }

    private void updatePlanFlow(User currUser) {
        service.viewActivePlans();

        System.out.print("\nEnter Plan ID to update: ");
        long planId = readLong();

        Plan existing = service.findPlanById(planId);
        if (existing == null) { System.out.println("Plan not found."); return; }

        System.out.println("\nCurrent: " + existing.getPlanName()
            + " | " + existing.getSpeedLabel()
            + " | " + existing.getDataLimitLabel()
            + " | OTTs:" + existing.getOttCount()
            + " | Rs." + existing.getMonthlyPrice()
            + " | " + existing.getOltType());

        String planName = readPlanName();
        String speedLabel = readSpeed();
        String dataLimitLabel = readDataLimit();

        System.out.print("Enter new OTT Count: ");
        int ottCount = readInt();

        System.out.print("Enter new Monthly Price: ");
        BigDecimal monthlyPrice = readBigDecimal();

        String oltType = readOltType();

        System.out.println("\n--- Update Summary ---");
        System.out.printf("%-6s | %-15s | %-10s | %-15s | %-5s | %-10s | %-8s%n",
            "ID", "Name", "Speed", "Data", "OTT", "Price", "OLT");
        System.out.println("-".repeat(80));
        System.out.printf("%-6d | %-15s | %-10s | %-15s | %-5d | %-10s | %-8s%n",
            planId, planName, speedLabel, dataLimitLabel, ottCount, monthlyPrice, oltType);

        System.out.print("\nConfirm update? (y/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("y")) { System.out.println("Cancelled."); return; }

        Plan updated = new Plan(null, planName, speedLabel, dataLimitLabel, ottCount, monthlyPrice, oltType, existing.isActive());
        if (currUser != null) service.updatePlan(planId, updated, currUser);
        else service.updatePlan(planId, updated);
        System.out.println("Plan updated successfully.");
    }

    private void togglePlanFlow(User currUser) {
        service.viewAllPlans();

        System.out.print("\nEnter Plan ID to Enable/Disable: ");
        long id = readLong();

        Plan plan = service.findPlanById(id);
        if (plan == null) { System.out.println("Plan not found."); return; }

        if (plan.isActive()) System.out.print("Plan '" + plan.getPlanName() + "' is ENABLED. Disable it? (y/n): ");
        else                 System.out.print("Plan '" + plan.getPlanName() + "' is DISABLED. Enable it? (y/n): ");

        if (!sc.nextLine().trim().equalsIgnoreCase("y")) { System.out.println("Cancelled."); return; }

        boolean success = currUser != null ? service.togglePlan(id, currUser) : service.togglePlan(id);
        if (success) System.out.println("Plan '" + plan.getPlanName() + "' is now " + (plan.isActive() ? "DISABLED" : "ENABLED") + ".");
        else         System.out.println("Failed to toggle plan status.");
    }

    private void deletePlanFlow(User currUser) {
        service.viewAllPlans();

        System.out.print("\nEnter Plan ID to delete: ");
        long id = readLong();

        Plan plan = service.findPlanById(id);
        if (plan == null) { System.out.println("Plan not found."); return; }

        System.out.println("\nYou are about to delete:");
        System.out.printf("%-6s | %-15s | %-10s | %-15s | %-5s | %-10s | %-8s%n",
            "ID", "Name", "Speed(MBPS)", "Data(GB)", "OTT", "Price", "OLT");
        System.out.println("-".repeat(85));
        System.out.printf("%-6d | %-15s | %-10s | %-15s | %-5d | %-10s | %-8s%n",
            plan.getPlanId(), plan.getPlanName(), plan.getSpeedLabel(), plan.getDataLimitLabel(),
            plan.getOttCount(), plan.getMonthlyPrice(), plan.getOltType());

        System.out.print("\nAre you sure? (y/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("y")) { System.out.println("Cancelled."); return; }

        boolean success = currUser != null ? service.deletePlan(id, currUser) : service.deletePlan(id);
        if (success) System.out.println("Plan '" + plan.getPlanName() + "' deleted permanently.");
        else         System.out.println("Failed to delete plan.");
    }

    // ── Input helpers ─────────────────────────────────────────────

    private String readPlanName() {
        while (true) {
            System.out.print("Enter Plan Name: ");
            String name = sc.nextLine().trim();
            if (ValidationUtil.isValidPlanName(name)) return name;
            System.out.println("Invalid name. Only letters, numbers and spaces (2-50 chars).");
        }
    }

    private String readSpeed() {
        while (true) {
            System.out.print("Enter Speed (MBPS): ");
            String val = sc.nextLine().trim().toUpperCase().replace("MBPS", "").trim();
            if (ValidationUtil.isValidSpeed(val)) return val + "MBPS";
            System.out.println("Invalid. Enter a number (e.g. 300, 56.78).");
        }
    }

    private String readDataLimit() {
        while (true) {
            System.out.print("Enter Data Limit (GB) or 'Unlimited': ");
            String val = sc.nextLine().trim();
            String stripped = val.toUpperCase().replace("GB", "").trim();
            if (val.equalsIgnoreCase("unlimited")) return "Unlimited";
            if (ValidationUtil.isValidDataLimit(stripped)) return stripped + "GB";
            System.out.println("Invalid. Enter a number (e.g. 60, 2.5) or 'Unlimited'.");
        }
    }

    private String readOltType() {
        while (true) {
            System.out.println("Select OLT Type: [1] OLT300  [2] OLT500");
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();
            if ("1".equals(choice)) return "OLT300";
            if ("2".equals(choice)) return "OLT500";
            System.out.println("Invalid. Enter 1 or 2.");
        }
    }

    private int readInt() {
        while (true) {
            String v = sc.nextLine().trim();
            try { return Integer.parseInt(v); }
            catch (NumberFormatException e) { System.out.print("Enter a valid number: "); }
        }
    }

    private long readLong() {
        while (true) {
            String v = sc.nextLine().trim();
            try { return Long.parseLong(v); }
            catch (NumberFormatException e) { System.out.print("Enter a valid id: "); }
        }
    }

    private BigDecimal readBigDecimal() {
        while (true) {
            String v = sc.nextLine().trim();
            try { return new BigDecimal(v); }
            catch (NumberFormatException e) { System.out.print("Enter a valid amount: "); }
        }
    }
}

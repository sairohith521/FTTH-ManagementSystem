package ftth.controller;
import ftth.model.Plan;
import ftth.model.User;
import ftth.service.PlanService;
import ftth.util.InputUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class PlanAdmin {

    private final PlanService service;
    private final Scanner sc;
    public PlanAdmin(PlanService planService,Scanner sc) {
        service = planService;
        this.sc=sc;
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
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid choice.");
                    continue;
                }

                switch (choice) {
                    case 1: viewActivePlans(); break;
                    case 2: addPlanFlow(currUser); break;
                    case 3: updatePlanFlow(currUser); break;
                    case 4: togglePlanFlow(currUser); break;
                    case 5: deletePlanFlow(currUser); break;
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
private void viewActivePlans() {

    List<Plan> plans = service.getActivePlans();

    if (plans.isEmpty()) {
        System.out.println("No enabled plans available.");
        return;
    }

    System.out.println("---- ENABLED PLANS ----");
    System.out.printf(
        "%-6s | %-15s | %-10s | %-18s | %-5s | %-10s | %-8s%n",
        "ID", "Name", "Speed", "Data", "OTT", "Price", "OLT"
    );
    System.out.println("=".repeat(85));

    for (Plan p : plans) {
        System.out.printf(
            "%-6d | %-15s | %-10s | %-18s | %-5d | %-10s | %-8s%n",
            p.getPlanId(),
            p.getPlanName(),
            p.getSpeedLabel(),
            p.getDataLimitLabel(),
            p.getOttCount(),
            p.getMonthlyPrice(),
            p.getOltType()
        );
    }
}
private void addPlanFlow(User currUser) {

    // 1️⃣ Read inputs
    String planName = InputUtil.readPlanName(sc);
    String speedLabel = InputUtil.readSpeed(sc);
    String dataLimitLabel = InputUtil.readDataLimit(sc);

    System.out.print("Enter OTT Count: ");
    int ottCount = InputUtil.readInt(sc);

    System.out.print("Enter Monthly Price: ");
    BigDecimal monthlyPrice = InputUtil.readBigDecimal(sc); // ✅ BigDecimal

   String oltType = InputUtil.readOltType(sc).toUpperCase();

    // 2️⃣ Show summary
    System.out.println("\n--- New Plan Summary ---");
    System.out.println("Name      : " + planName);
    System.out.println("Speed     : " + speedLabel);
    System.out.println("Data      : " + dataLimitLabel);
    System.out.println("OTTs      : " + ottCount);
    System.out.println("Price     : Rs." + monthlyPrice);
    System.out.println("OLT Type  : " + oltType);

    System.out.print("Confirm add? (y/n): ");
    if (!sc.nextLine().trim().equalsIgnoreCase("y")) {
        System.out.println("Cancelled.");
        return;
    }

    // 3️⃣ Create Plan object (FINAL MODEL)
    Plan plan = new Plan(
        planName,
        speedLabel,
        dataLimitLabel,
        ottCount,
        monthlyPrice,
        oltType,
        true
    );

    // 4️⃣ Call service
    service.createPlan(plan,currUser);

    System.out.println("✅ Plan added successfully.");
}

private void updatePlanFlow(User currUser) {
       // 1️⃣ Show active plans
    service.printActivePlans(service.getActivePlans());

    System.out.print("\nEnter Plan ID to update: ");
    long planId = InputUtil.readLong(sc);

    // 2️⃣ Fetch existing plan
    Plan existing = service.findPlanById(planId);
    if (existing == null) {
        System.out.println("Plan not found.");
        return;
    }

    System.out.println("\nCurrent Plan:");
    System.out.println(existing.getPlanName()
        + " | Speed: " + existing.getSpeedLabel()
        + " | Data: " + existing.getDataLimitLabel()
        + " | OTTs: " + existing.getOttCount()
        + " | Rs." + existing.getMonthlyPrice()
        + " | OLT: " + existing.getOltType()
    );

    // 3️⃣ Read updated values
    String planName = InputUtil.readPlanName(sc);
    String speedLabel = InputUtil.readSpeed(sc);
    String dataLimitLabel = InputUtil.readDataLimit(sc);

    System.out.print("Enter new OTT Count: ");
    int ottCount = InputUtil.readInt(sc);

    System.out.print("Enter new Monthly Price: ");
    BigDecimal monthlyPrice = InputUtil.readBigDecimal(sc); // ✅ BigDecimal

    String oltType = InputUtil.readOLTType(sc);

    // 4️⃣ Summary
    System.out.println("\n--- Update Summary ---");
    System.out.println("Name      : " + planName);
    System.out.println("Speed     : " + speedLabel);
    System.out.println("Data      : " + dataLimitLabel);
    System.out.println("OTTs      : " + ottCount);
    System.out.println("Price     : Rs." + monthlyPrice);
    System.out.println("OLT Type  : " + oltType);

    System.out.print("Confirm update? (y/n): ");
    if (!sc.nextLine().trim().equalsIgnoreCase("y")) {
        System.out.println("Cancelled.");
        return;
    }

    // 5️⃣ Build updated Plan (KEEP active)
    Plan updated = new Plan(
        planName,
        speedLabel,
        dataLimitLabel,
        ottCount,
        monthlyPrice,
        oltType,
        existing.isActive()
    );

    // 6️⃣ Call service
    service.updatePlan(planId, updated,currUser);

    System.out.println("✅ Plan updated successfully.");
}

private void togglePlanFlow(User currUser) {
        service.viewAllPlans();

        System.out.print("\nEnter Plan ID to Enable/Disable: ");
        long id =InputUtil.readLong(sc);

        Plan plan = service.findPlanById(id);
        if (plan == null) {
            System.out.println("Plan not found.");
            return;
        }

        if (plan.isActive()) {
            System.out.print("Plan '" + plan.getPlanName() + "' is currently ENABLED. Disable it? (y/n): ");
        } else {
            System.out.print("Plan '" + plan.getPlanName() + "' is currently DISABLED. Enable it? (y/n): ");
        }

        String confirm = sc.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            return;
        }

        boolean success = service.togglePlan(id,currUser);
        if (success) {
            String newState = plan.isActive() ? "DISABLED" : "ENABLED";
            System.out.println("Plan '" + plan.getPlanName() + "' is now " + newState + ".");
        } else {
            System.out.println("Failed to toggle plan status.");
        }
       
    }
 private void deletePlanFlow(User currUser) {
        service.viewAllPlans();

        System.out.print("\nEnter Plan ID to delete: ");
        long id = InputUtil.readLong(sc);

        Plan plan = service.findPlanById(id);
        if (plan == null) {
            System.out.println("Plan not found.");
            return;
        }

        System.out.println("\nPlan to delete:");
        System.out.printf("  %-10s : %d%n", "ID", plan.getPlanId());
        System.out.printf("  %-10s : %s%n", "Name", plan.getPlanName());
        System.out.printf("  %-10s : %s%n", "Speed", plan.getSpeedLabel());
        System.out.printf("  %-10s : %s%n", "Data", plan.getDataLimitLabel());
        System.out.printf("  %-10s : Rs.%s%n", "Price", plan.getMonthlyPrice());
        System.out.printf("  %-10s : %s%n", "OLT", plan.getOltType());
        System.out.print("\nAre you sure you want to permanently delete '" + plan.getPlanName() + "'? (y/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            return;
        }

        boolean success = service.deletePlan(id,currUser);
        if (success) System.out.println("Plan '" + plan.getPlanName() + "' deleted permanently.");
        else         System.out.println("Failed to delete plan.");
    }
}

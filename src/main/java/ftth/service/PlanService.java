package ftth.service;

import ftth.model.Plan;
import ftth.repository.PlanRepository;

import java.util.List;

public class PlanService {

    private final PlanRepository repo;

    public PlanService() {
        repo = new PlanRepository();
    }

    public boolean addPlan(Plan plan) {
        return repo.insertPlan(plan);
    }

    public List<Plan> getAllPlans() {
        return repo.findAllPlans();
    }

    public List<Plan> getActivePlans() {
        return repo.findActivePlans();
    }

    public void viewActivePlans() {
        List<Plan> plans = repo.findActivePlans();
        if (plans.isEmpty()) {
            System.out.println("No enabled plans available.");
            return;
        }
        System.out.println("---- ENABLED PLANS ----");
        for (Plan p : plans) {
            System.out.println(p);
        }
    }

    public void viewAllPlans() {
        List<Plan> plans = repo.findAllPlans();
        if (plans.isEmpty()) {
            System.out.println("No plans available.");
            return;
        }
        System.out.println("---- ALL PLANS ----");
        System.out.printf("%-6s | %-15s | %-10s | %-18s | %-5s | %-10s | %-8s | %-8s%n",
            "ID", "Name", "Speed", "Data", "OTT", "Price", "OLT", "Status");
<<<<<<< Updated upstream
        System.out.println("=".repeat(95));
=======
        System.out.println("=".repeat(93));
>>>>>>> Stashed changes
        for (Plan p : plans) {
            System.out.printf("%-6d | %-15s | %-10s | %-18s | %-5d | %-10s | %-8s | %-8s%n",
                p.getPlanId(),
                p.getPlanName(),
                p.getSpeedLabel(),
                p.getDataLimitLabel(),
                p.getOttCount(),
                p.getMonthlyPrice(),
                p.getOltType(),
<<<<<<< Updated upstream
                p.isActive() ? "Active" : "Disabled"
            );
=======
                p.isActive() ? "ENABLED" : "DISABLED");
>>>>>>> Stashed changes
        }
    }

    public Plan findPlanById(long id) {
        return repo.findPlanById(id);
    }

    public boolean updatePlan(long id, Plan updatedPlan) {
        return repo.updatePlan(id, updatedPlan);
    }

    public boolean togglePlan(long id) {
        Plan plan = repo.findPlanById(id);
        if (plan == null) return false;
        boolean newStatus = !plan.isActive();
        return repo.togglePlanStatus(id, newStatus);
    }

<<<<<<< Updated upstream
    public boolean deletePlan(long id) {
        return repo.deletePlan(id);
=======
public boolean deletePlan(long planId, User currUser) {
        Plan plan = repo.findById(planId);
        boolean deleted = repo.deletePlan(planId);
        if (deleted && plan != null) {
            emailService.sendPlanAdminEmail("PLAN_DELETED", plan, currUser.getUserId());
        }
        return deleted;
    }

public Plan getActivePlan(Long planId) {

        if (planId == null) {
            throw new IllegalArgumentException("Plan ID cannot be null");
        }

        Plan plan = repo.findById(planId);

        if (plan == null) {
            throw new RuntimeException("Plan not found with ID: " + planId);
        }

        if (!plan.isActive()) {
            throw new RuntimeException(
                "Plan with ID " + planId + " is not active"
            );
        }

        return plan;
    }

public Plan findPlanById(Long planId) {
        return repo.findById(planId);
>>>>>>> Stashed changes
    }
}

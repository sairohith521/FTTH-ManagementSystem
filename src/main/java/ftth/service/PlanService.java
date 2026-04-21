package ftth.service;

import ftth.model.Plan;
import ftth.model.User;
import ftth.repository.PlanRepository;

import java.util.List;

public class PlanService {

    private final PlanRepository repo;
    private final EmailService emailService;

    public PlanService() {
        this.repo = new PlanRepository();
        this.emailService = new EmailService();
    }

    public PlanService(PlanRepository repo, EmailService emailService) {
        this.repo = repo;
        this.emailService = emailService;
    }

    public boolean addPlan(Plan plan) {
        return repo.insertPlan(plan);
    }

    public void createPlan(Plan plan) {
        if (!repo.insertPlan(plan)) throw new RuntimeException("Plan creation failed");
    }

    public void createPlan(Plan plan, User currUser) {
        if (!repo.insertPlan(plan)) throw new RuntimeException("Plan creation failed");
        if (currUser != null) emailService.sendPlanAdminEmail("PLAN_ADDED", plan, currUser.getUserId());
    }

    public List<Plan> getAllPlans() { return repo.findAllPlans(); }
    public List<Plan> getActivePlans() { return repo.findActivePlans(); }

    public void viewActivePlans() {
        printTable("---- ENABLED PLANS ----", repo.findActivePlans(), false);
    }

    public void viewAllPlans() {
        printTable("---- ALL PLANS ----", repo.findAllPlans(), true);
    }

    public void printActivePlans(List<Plan> plans) {
        printTable("---- ENABLED PLANS ----", plans, false);
    }

    private void printTable(String header, List<Plan> plans, boolean showStatus) {
        if (plans.isEmpty()) { System.out.println("No plans available."); return; }
        System.out.println(header);
        if (showStatus) {
            System.out.printf("%-6s | %-15s | %-10s | %-15s | %-5s | %-10s | %-8s | %-8s%n",
                "ID", "Name", "Speed", "Data", "OTT", "Price", "OLT", "Status");
            System.out.println("=".repeat(95));
            for (Plan p : plans) {
                System.out.printf("%-6d | %-15s | %-10s | %-15s | %-5d | %-10s | %-8s | %-8s%n",
                    p.getPlanId(), p.getPlanName(), p.getSpeedLabel(), p.getDataLimitLabel(),
                    p.getOttCount(), p.getMonthlyPrice(), p.getOltType(),
                    p.isActive() ? "Active" : "Disabled");
            }
        } else {
            System.out.printf("%-6s | %-15s | %-10s | %-15s | %-5s | %-10s | %-8s%n",
                "ID", "Name", "Speed", "Data", "OTT", "Price", "OLT");
            System.out.println("=".repeat(85));
            for (Plan p : plans) {
                System.out.printf("%-6d | %-15s | %-10s | %-15s | %-5d | %-10s | %-8s%n",
                    p.getPlanId(), p.getPlanName(), p.getSpeedLabel(), p.getDataLimitLabel(),
                    p.getOttCount(), p.getMonthlyPrice(), p.getOltType());
            }
        }
    }

    public void updatePlan(long planId, Plan updatedPlan) {
        if (repo.findById(planId) == null) throw new RuntimeException("Plan not found with id=" + planId);
        if (!repo.updatePlan(planId, updatedPlan)) throw new RuntimeException("Plan update failed for id=" + planId);
    }

    public void updatePlan(long planId, Plan updatedPlan, User currUser) {
        updatePlan(planId, updatedPlan);
        if (currUser != null) emailService.sendPlanAdminEmail("PLAN_UPDATED", updatedPlan, currUser.getUserId());
    }

    public boolean togglePlan(long id) {
        Plan plan = repo.findPlanById(id);
        if (plan == null) return false;
        return repo.togglePlanStatus(id, !plan.isActive());
    }

    public boolean togglePlan(long id, User currUser) {
        Plan plan = repo.findPlanById(id);
        if (plan == null) return false;
        boolean toggled = repo.togglePlanStatus(id, !plan.isActive());
        if (toggled && currUser != null)
            emailService.sendPlanAdminEmail(!plan.isActive() ? "PLAN_ACTIVATED" : "PLAN_DEACTIVATED", plan, currUser.getUserId());
        return toggled;
    }

    public boolean deletePlan(long id) { return repo.deletePlan(id); }

    public boolean deletePlan(long id, User currUser) {
        Plan plan = repo.findById(id);
        boolean deleted = repo.deletePlan(id);
        if (deleted && currUser != null && plan != null)
            emailService.sendPlanAdminEmail("PLAN_DELETED", plan, currUser.getUserId());
        return deleted;
    }

    public Plan findPlanById(long id) { return repo.findById(id); }
    public Plan findPlanById(Long id) { return repo.findById(id); }

    public Plan getActivePlan(Long planId) {
        if (planId == null) throw new IllegalArgumentException("Plan ID cannot be null");
        Plan plan = repo.findById(planId);
        if (plan == null) throw new RuntimeException("Plan not found with ID: " + planId);
        if (!plan.isActive()) throw new RuntimeException("Plan with ID " + planId + " is not active");
        return plan;
    }
}

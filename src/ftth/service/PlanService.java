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
        for (Plan p : plans) {
            System.out.println(p);
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

    public boolean deletePlan(long id) {
        return repo.deletePlan(id);
    }
}

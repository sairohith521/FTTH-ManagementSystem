package ftth.service;
import ftth.model.Plan;
import ftth.repository.PlanRepository;
import java.util.*;

public class PlanService {

    private List<Plan> plans;
    private PlanRepository repo;

    public PlanService() {
        repo = new PlanRepository();
        plans = repo.loadAllPlans();  // 🔥 load from file
    }

    public void addPlan(Plan plan) {
        plans.add(plan);
        repo.saveAllPlans(plans);   // 🔥 persist
    }

    public List<Plan> getAllPlans() {
        return plans;
    }

    public void viewPlans() {
         if (plans.isEmpty()) {
        System.out.println("No plans available ❌");
        return;
    }

    System.out.println("---- ALL PLANS ----");
        for (Plan p : plans) {
            System.out.println(p);
        }
    }

    public Plan findPlanById(String id) {
        for (Plan p : plans) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    public boolean updatePlan(String id, Plan updatedPlan) {
        Plan existing = findPlanById(id);
        if (existing == null) return false;

        existing.setName(updatedPlan.getName());
        existing.setSpeed(updatedPlan.getSpeed());
        existing.setDataLimit(updatedPlan.getDataLimit());
        existing.setOtts(updatedPlan.getOtts());
        existing.setPrice(updatedPlan.getPrice());
        existing.setOltType(updatedPlan.getOltType());

        repo.saveAllPlans(plans);   // 🔥 persist
        return true;
    }

    public boolean disablePlan(String id) {
        Plan plan = findPlanById(id);
        if (plan == null) return false;

        plan.setActive(false);
        repo.saveAllPlans(plans);   // 🔥 persist
        return true;
    }
}
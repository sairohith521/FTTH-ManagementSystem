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

    public void viewPlans() {
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

    public boolean deletePlan(long id) {
        return repo.deletePlan(id);
    }
}

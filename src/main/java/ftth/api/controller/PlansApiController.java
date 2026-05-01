package ftth.api.controller;

import ftth.model.Plan;
import ftth.repository.EmailLogRepository;
import ftth.repository.PlanRepository;
import ftth.service.EmailService;
import ftth.service.PlanService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/plans")
public class PlansApiController {

    private final PlanService planService;

    public PlansApiController() {
        PlanRepository planRepo = new PlanRepository();
        EmailService emailService = new EmailService(new EmailLogRepository());
        this.planService = new PlanService(planRepo, emailService);
    }

    @GetMapping
    public List<Map<String, Object>> getActivePlans() {
        List<Plan> plans = planService.getActivePlans();
        return plans.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("planId", p.getPlanId());
            m.put("planName", p.getPlanName());
            m.put("speedLabel", p.getSpeedLabel());
            m.put("dataLimitLabel", p.getDataLimitLabel());
            m.put("ottCount", p.getOttCount());
            m.put("monthlyPrice", p.getMonthlyPrice());
            m.put("oltType", p.getOltType());
            return m;
        }).toList();
    }
}
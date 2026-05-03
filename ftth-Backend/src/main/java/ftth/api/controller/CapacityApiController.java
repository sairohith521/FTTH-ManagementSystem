package ftth.api.controller;

import ftth.repository.CapacityRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/capacity")
@CrossOrigin(origins = "*")
public class CapacityApiController {

    private final CapacityRepository capacityRepository;

    public CapacityApiController() {
        this.capacityRepository = new CapacityRepository();
    }

    // ----------------------------------------------------------------
    // GET /api/capacity
    // Full capacity dashboard — all OLTs with utilization and warnings
    //
    // Response example:
    // {
    //   "threshold": 80,
    //   "totalOlts": 4,
    //   "breachCount": 1,
    //   "olts": [
    //     {
    //       "pincode": "560001",
    //       "oltCode": "OLT-001",
    //       "oltType": "OLT300",
    //       "splitterCount": 2,
    //       "totalPorts": 6,
    //       "usedPorts": 5,
    //       "freePorts": 1,
    //       "utilPercent": 83.3,
    //       "breach": true,
    //       "warning": "CAPACITY_BREACH"   // or "ADD_SPLITTER" / "ADD_OLT" / null
    //     }
    //   ]
    // }
    // ----------------------------------------------------------------
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCapacityDashboard() {
        try {
            List<Map<String, Object>> olts = capacityRepository.getCapacityData();

            long breachCount = olts.stream()
                .filter(o -> Boolean.TRUE.equals(o.get("breach")))
                .count();

            long addSplitterCount = olts.stream()
                .filter(o -> "ADD_SPLITTER".equals(o.get("warning")))
                .count();

            long addOltCount = olts.stream()
                .filter(o -> "ADD_OLT".equals(o.get("warning")))
                .count();

            Map<String, Object> response = new HashMap<>();
            response.put("threshold",       80);
            response.put("totalOlts",       olts.size());
            response.put("breachCount",     breachCount);
            response.put("addSplitterCount",addSplitterCount);
            response.put("addOltCount",     addOltCount);
            response.put("olts",            olts);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(err);
        }
    }

    // ----------------------------------------------------------------
    // GET /api/capacity/breaches
    // Only OLTs that have a breach or warning — useful for alerts
    // ----------------------------------------------------------------
    @GetMapping("/breaches")
    public ResponseEntity<Map<String, Object>> getBreaches() {
        try {
            List<Map<String, Object>> all = capacityRepository.getCapacityData();

            List<Map<String, Object>> breaches = all.stream()
                .filter(o -> o.get("warning") != null)
                .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("threshold",    80);
            response.put("breachCount",  breaches.size());
            response.put("olts",         breaches);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(err);
        }
    }
}

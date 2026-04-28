package ftth.api.controller;

import ftth.model.InventoryDetails;
import ftth.model.Olt;
import ftth.model.dtos.OltInventoryDTO;
import ftth.repository.InventoryRepository;
import ftth.service.InventoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryApiController {

    private final InventoryService service;

    public InventoryApiController() {
        this.service = new InventoryService(new InventoryRepository());
    }

    @GetMapping("/pincodes")
    public List<String> getPincodes() {
        return service.getUniquePincodes();
    }

    @GetMapping("/olts")
    public List<OltInventoryDTO> getOltsByPincode(@RequestParam(value = "pincode") String pincode) {
        return service.getByPincode(pincode);
    }

    @GetMapping("/olts/{oltCode}/details")
    public ResponseEntity<Map<String, Object>> getOltDetails(@PathVariable(value = "oltCode") String oltCode) {
        InventoryDetails details = service.getInventoryDetails(oltCode);
        if (details == null) {
            return ResponseEntity.notFound().build();
        }

        Olt olt = details.getOlt();
        int totalPorts = details.getSplitters().size() * service.getPortsPerSplitter();
        int availablePorts = service.getAvailablePortsByType(olt.getServiceAreaId(), olt.getOltType());

        Map<String, Object> result = new HashMap<>();
        result.put("oltCode", olt.getOltCode());
        result.put("oltType", olt.getOltType());
        result.put("active", olt.isActive());
        result.put("totalPorts", totalPorts);
        result.put("availablePorts", availablePorts);

        List<Map<String, Object>> splitterList = details.getSplitters().stream().map(s -> {
            Map<String, Object> m = new HashMap<>();
            m.put("splitterNumber", s.getSplitterNumber());
            m.put("splitterCode", s.getSplitterCode());
            m.put("totalPorts", s.getTotalPorts());
            m.put("availablePorts", s.getAvailablePorts());
            return m;
        }).toList();

        result.put("splitters", splitterList);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/olts")
    public ResponseEntity<Map<String, String>> addOlt(@RequestBody Map<String, Object> body) {
        String pincode = (String) body.get("pincode");
        String type = (String) body.get("oltType");
        int splitterCount = (int) body.get("splitterCount");

        String oltCode = service.addOLT(pincode, type, splitterCount);
        Map<String, String> result = new HashMap<>();
        result.put("oltCode", oltCode);
        result.put("message", "OLT added successfully");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/olts/{oltCode}")
    public ResponseEntity<Map<String, String>> removeOlt(@PathVariable(value = "oltCode") String oltCode) {
        boolean removed = service.removeOLT(oltCode);
        Map<String, String> result = new HashMap<>();
        if (removed) {
            result.put("message", "OLT removed successfully");
            return ResponseEntity.ok(result);
        }
        result.put("message", "Cannot remove OLT - assigned ports exist or OLT not found");
        return ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/olts/{oltCode}/splitters")
    public ResponseEntity<Map<String, String>> addSplitter(@PathVariable(value = "oltCode") String oltCode) {
        boolean added = service.addSplitter(oltCode);
        Map<String, String> result = new HashMap<>();
        if (added) {
            result.put("message", "Splitter added successfully");
            return ResponseEntity.ok(result);
        }
        result.put("message", "Max splitters reached or OLT not found");
        return ResponseEntity.badRequest().body(result);
    }

    @DeleteMapping("/olts/{oltCode}/splitters/{splitterNumber}")
    public ResponseEntity<Map<String, String>> removeSplitter(
            @PathVariable(value = "oltCode") String oltCode,
            @PathVariable(value = "splitterNumber") int splitterNumber) {
        boolean removed = service.removeSplitter(oltCode, splitterNumber);
        Map<String, String> result = new HashMap<>();
        if (removed) {
            result.put("message", "Splitter removed successfully");
            return ResponseEntity.ok(result);
        }
        result.put("message", "Cannot remove splitter - assigned ports exist or not found");
        return ResponseEntity.badRequest().body(result);
    }

    @GetMapping("/config")
    public Map<String, Integer> getConfig() {
        Map<String, Integer> config = new HashMap<>();
        config.put("maxSplitters", service.getMaxSplitters());
        config.put("portsPerSplitter", service.getPortsPerSplitter());
        return config;
    }
}

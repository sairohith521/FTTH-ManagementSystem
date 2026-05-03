package ftth.repository;

import ftth.config.DbConnection;
import ftth.model.dtos.CapacityRow;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapacityRepository {

    private final CapacityInventoryRepository capacityInventoryRepository;

    public CapacityRepository() {
        this.capacityInventoryRepository = new CapacityInventoryRepository();
    }

    public List<Map<String, Object>> getCapacityData() {
        List<CapacityRow> rows = capacityInventoryRepository.fetchAllCapacity();
        List<Map<String, Object>> result = new ArrayList<>();

        for (CapacityRow row : rows) {
            Map<String, Object> oltData = new HashMap<>();
            oltData.put("pincode", row.getPincode());
            oltData.put("oltCode", "OLT-" + String.format("%03d", row.getOltId()));
            oltData.put("oltType", row.getOltType());
            oltData.put("splitterCount", row.getSplitterCount());
            oltData.put("totalPorts", row.getTotalPorts());
            oltData.put("usedPorts", row.getUsedPorts());
            oltData.put("freePorts", row.getFreePorts());
            oltData.put("utilPercent", row.getUtilization());

            // Determine breach and warning status
            boolean breach = row.getUtilization() > 80;
            oltData.put("breach", breach);

            String warning = null;
            if (breach) {
                warning = "CAPACITY_BREACH";
            } else if (row.getUtilization() > 70) {
                warning = "ADD_SPLITTER";
            } else if (row.getUtilization() > 60) {
                warning = "ADD_OLT";
            }
            oltData.put("warning", warning);

            result.add(oltData);
        }

        return result;
    }
}
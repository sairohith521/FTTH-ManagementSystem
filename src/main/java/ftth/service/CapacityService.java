package ftth.service;

import ftth.repository.CapacityInventoryRepository;
import java.util.ArrayList;
import java.util.List;

public class CapacityService {

    private static final double THRESHOLD = 80.0;
    private static final int MAX_SPLITTERS = 3;

    private final CapacityInventoryRepository repo =
            new CapacityInventoryRepository();

    public void showCapacityDashboard() {

        List<CapacityInventoryRepository.CapacityRow> rows =
                repo.fetchAllCapacity();

        if (rows.isEmpty()) {
            System.out.println("No inventory data found.");
            return;
        }

        List<String> alerts = new ArrayList<>();

        /* ===== ALERT LOGIC ===== */
        for (CapacityInventoryRepository.CapacityRow r : rows) {

            if (r.totalPorts == 0) continue;

            // Threshold breach (80–99%)
            if (r.utilization >= THRESHOLD && r.utilization < 100.0) {
                alerts.add(
                    " Capacity at " + r.utilization + "% for " +
                    r.oltType + " OLT (pincode " + r.pincode + ")"
                );
            }

            // Fully exhausted
            if (r.utilization == 100.0) {
                if (r.splitterCount < MAX_SPLITTERS) {
                    alerts.add(
                        " Add SPLITTER to " + r.oltType +
                        " OLT (pincode " + r.pincode + ")"
                    );
                } else {
                    alerts.add(
                        " Add NEW OLT at pincode " + r.pincode
                    );
                }
            }
        }

        /* ===== DASHBOARD ===== */
        System.out.println("\n=== Capacity Dashboard (Threshold: 80%) ===");

        if (!alerts.isEmpty()) {
            System.out.println("⚠ " + alerts.size() + " ALERT(S):");
            for (String a : alerts) {
                System.out.println("  " + a);
            }
        } else {
            System.out.println("✅ No capacity breaches detected.");
        }

        /* ===== TABLE ===== */
        System.out.println();
        System.out.printf(
            "%-8s %-8s %-6s %-6s %-6s %-7s%n",
            "Pincode", "OLT", "Total", "Used", "Free", "Util"
        );
        System.out.println("------------------------------------------------");

        for (CapacityInventoryRepository.CapacityRow r : rows) {
            System.out.printf(
                "%-8s %-8s %-6d %-6d %-6d %-6.1f%%%n",
                r.pincode, r.oltType,
                r.totalPorts, r.usedPorts,
                r.freePorts, r.utilization
            );
        }
    }
}

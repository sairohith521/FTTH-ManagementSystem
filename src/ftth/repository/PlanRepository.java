package ftth.repository;

import ftth.model.Plan;
import java.io.*;
import java.util.*;

public class PlanRepository {

    private static final String FILE_PATH = "plan.txt";

    // 🔹 Save all plans to file
    public void saveAllPlans(List<Plan> plans) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Plan p : plans) {
                bw.write(convertToString(p));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Read all plans from file
    public List<Plan> loadAllPlans() {
    List<Plan> plans = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;

        while ((line = br.readLine()) != null) {

            System.out.println("Reading line: " + line); // DEBUG

            try {
                Plan p = convertToPlan(line);
                if (p != null) {
                    plans.add(p);
                }
            } catch (Exception e) {
                System.out.println("❌ Failed to parse line: " + line);
                e.printStackTrace();
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return plans;
}

    // 🔹 Convert Plan → String
    private String convertToString(Plan p) {
        return p.getId() + "," + p.getName() + "," + p.getSpeed() + "," +
               p.getDataLimit() + "," + p.getOtts() + "," +
               p.getPrice() + "," + p.getOltType() + "," + p.isActive();
    }

    // 🔹 Convert String → Plan
    private Plan convertToPlan(String line) {
        String[] data = line.split(",");

        return new Plan(
            data[0],
            data[1],
            data[2],
            data[3],
            Integer.parseInt(data[4]),
            Double.parseDouble(data[5]),
            data[6],
            Boolean.parseBoolean(data[7])
        );
    }
}
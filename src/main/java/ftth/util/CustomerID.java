package ftth.util;
import java.io.*;

public class CustomerID {

    private static final String COUNTER_FILE = "customer_counter.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";

    // ✅ Generate next unique Customer ID like AAHA-0001, AAHA-0002 ...
    public static String generateID() {
        int next = Math.max(readCounter(), readMaxIdFromCustomers()) + 1;
        saveCounter(next);
        return String.format("AAHA-%04d", next);
    }

    // ✅ Read current counter from file
    private static int readCounter() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(COUNTER_FILE));
            String line = br.readLine();
            br.close();
            if (line != null && !line.trim().isEmpty()) {
                return Integer.parseInt(line.trim());
            }
        } catch (Exception e) {
            // File doesn't exist yet, start from 0
        }
        return 0;
    }

    // ✅ Save updated counter back to file
    private static void saveCounter(int counter) {
        try {
            FileWriter fw = new FileWriter(COUNTER_FILE);
            fw.write(String.valueOf(counter));
            fw.close();
        } catch (Exception e) {
            System.out.println("Error saving counter: " + e.getMessage());
        }
    }

    private static int readMaxIdFromCustomers() {
        int max = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 2);
                if (parts.length == 0) continue;
                String id = parts[0].trim();
                if (!id.startsWith("AAHA-")) continue;
                int value = Integer.parseInt(id.substring(5));
                if (value > max) max = value;
            }
        } catch (Exception ignored) {
            // Fall back to counter file value when customers file is missing or malformed.
        }
        return max;
    }
}

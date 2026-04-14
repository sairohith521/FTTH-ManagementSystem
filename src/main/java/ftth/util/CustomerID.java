package ftth.util;
import java.io.*;

public class CustomerID {

    private static final String COUNTER_FILE = "customer_counter.txt";

    // ✅ Generate next unique Customer ID like AAHA-0001, AAHA-0002 ...
    public static String generateID() {
        int counter = readCounter();
        counter++;
        saveCounter(counter);
        return String.format("AAHA-%04d", counter);
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
}

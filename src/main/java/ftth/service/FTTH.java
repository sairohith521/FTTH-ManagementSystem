package ftth.service;
import java.io.*;

import ftth.util.CustomerID;

public class FTTH {

    private static final String DATA_FILE     = "data.txt";
    private static final String CUSTOMER_FILE = "customers.txt";

    // =====================================================================
    // FEASIBILITY CHECKS
    // =====================================================================

    // ✅ Check if pincode exists AND has at least 1 empty port
    public boolean checkPincode(int userPincode) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(DATA_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                int filePin = Integer.parseInt(parts[0].trim());
                String subscriber = parts[4].trim();
                if (filePin == userPincode && subscriber.equals("empty")) {
                    br.close();
                    return true;
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error reading data.txt: " + e.getMessage());
        }
        return false;
    }

    // ✅ Count empty ports for a pincode
    public int getAvailablePorts(int userPincode) {
        int count = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(DATA_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                int filePin = Integer.parseInt(parts[0].trim());
                String subscriber = parts[4].trim();
                if (filePin == userPincode && subscriber.equals("empty")) count++;
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error reading ports: " + e.getMessage());
        }
        return count;
    }

    // ✅ Salary check
    public boolean checkSalary(double salary) {
        return salary > 30000;
    }

    // =====================================================================
    // ADD - New customer activation, assigns CustomerID + tags port
    // =====================================================================

    public String addCustomer(String name, int pincode, String service, int price) {
        try {
            // Step 1: Read data.txt and find the first empty port for this pincode
            BufferedReader br = new BufferedReader(new FileReader(DATA_FILE));
            StringBuilder sb = new StringBuilder();
            String line;
            boolean assigned = false;

            String assignedOLT  = "";
            String assignedSPL  = "";
            String assignedPort = "";

            // Step 2: Generate unique Customer ID
            String custID = CustomerID.generateID();

            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    sb.append(line).append("\n");
                    continue;
                }
                String[] parts = line.split(",");
                int filePin      = Integer.parseInt(parts[0].trim());
                String existingSub = parts[4].trim();

                // Tag the first empty port with CustomerID
                if (!assigned && filePin == pincode && existingSub.equals("empty")) {
                    assignedOLT  = parts[1].trim();
                    assignedSPL  = parts[2].trim();
                    assignedPort = parts[3].trim();
                    parts[4]     = custID;             // tag port with Customer ID
                    assigned     = true;
                }
                sb.append(parts[0]+","+parts[1]+","+parts[2]+","+parts[3]+","+parts[4]+"\n");
            }
            br.close();

            if (!assigned) return null;

            // Step 3: Write updated data.txt
            FileWriter fw = new FileWriter(DATA_FILE);
            fw.write(sb.toString());
            fw.close();

            // Step 4: Save customer record to customers.txt
            saveCustomerRecord(custID, name, pincode, assignedOLT, assignedSPL, assignedPort, service, price, "ACTIVE");

            return custID;

        } catch (Exception e) {
            System.out.println("Error in addCustomer: " + e.getMessage());
            return null;
        }
    }

    // =====================================================================
    // MOVE - Customer moves from pincode A to pincode B
    // =====================================================================

    public boolean moveCustomer(String custID, int newPincode) {
        try {
            // Step 1: Find customer record
            String[] customer = findCustomer(custID);
            if (customer == null) {
                System.out.println("Customer ID not found: " + custID);
                return false;
            }
            if (customer[8].equals("DELETED")) {
                System.out.println("Customer is not active.");
                return false;
            }

            int oldPincode = Integer.parseInt(customer[2]);

            if (oldPincode == newPincode) {
                System.out.println("Customer is already in pincode " + newPincode);
                return false;
            }

            // Step 2: Check new pincode has space
            if (!checkPincode(newPincode)) {
                System.out.println("No available ports in pincode " + newPincode);
                return false;
            }

            // Step 3: Read data.txt — free old port, tag new port
            BufferedReader br = new BufferedReader(new FileReader(DATA_FILE));
            StringBuilder sb = new StringBuilder();
            String line;
            boolean newPortAssigned = false;

            String newOLT  = "";
            String newSPL  = "";
            String newPort = "";

            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    sb.append(line).append("\n");
                    continue;
                }
                String[] parts = line.split(",");
                int    filePin = Integer.parseInt(parts[0].trim());
                String sub     = parts[4].trim();

                // Free old port
                if (filePin == oldPincode && sub.equals(custID)) {
                    parts[4] = "empty";
                }
                // Tag new port (first empty slot in new pincode)
                else if (!newPortAssigned && filePin == newPincode && sub.equals("empty")) {
                    newOLT  = parts[1].trim();
                    newSPL  = parts[2].trim();
                    newPort = parts[3].trim();
                    parts[4] = custID;
                    newPortAssigned = true;
                }
                sb.append(parts[0]+","+parts[1]+","+parts[2]+","+parts[3]+","+parts[4]+"\n");
            }
            br.close();

            if (!newPortAssigned) {
                System.out.println("Failed to assign new port.");
                return false;
            }

            // Step 4: Write updated data.txt
            FileWriter fw = new FileWriter(DATA_FILE);
            fw.write(sb.toString());
            fw.close();

            // Step 5: Update customer record
            updateCustomerRecord(custID, newPincode, newOLT, newSPL, newPort, "ACTIVE");

            System.out.println("Port freed  : Pincode " + oldPincode + " -> " + customer[3] + "/" + customer[4] + "/Port" + customer[5] + " is now EMPTY");
            System.out.println("Port assigned: Pincode " + newPincode + " -> " + newOLT + "/" + newSPL + "/Port" + newPort + " tagged to " + custID);

            return true;

        } catch (Exception e) {
            System.out.println("Error in moveCustomer: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // CHANGE - Customer changes their service plan
    // =====================================================================

    public boolean changeCustomer(String custID, String newService, int newPrice) {
        try {
            String[] customer = findCustomer(custID);
            if (customer == null) {
                System.out.println("Customer ID not found: " + custID);
                return false;
            }
            if (customer[8].equals("DELETED")) {
                System.out.println("Customer is not active.");
                return false;
            }

            // Only service and price change, port stays the same
            updateCustomerServiceRecord(custID, newService, newPrice);

            System.out.println("Service changed for " + custID);
            System.out.println("  Old Service: " + customer[6] + " @ Rs." + customer[7]);
            System.out.println("  New Service: " + newService + " @ Rs." + newPrice);

            return true;

        } catch (Exception e) {
            System.out.println("Error in changeCustomer: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // DELETE - Customer cancels, port is freed
    // =====================================================================

    public boolean deleteCustomer(String custID) {
        try {
            String[] customer = findCustomer(custID);
            if (customer == null) {
                System.out.println("Customer ID not found: " + custID);
                return false;
            }
            if (customer[8].equals("DELETED")) {
                System.out.println("Customer is already deleted.");
                return false;
            }

            int pincode = Integer.parseInt(customer[2]);

            // Step 1: Free the port in data.txt
            BufferedReader br = new BufferedReader(new FileReader(DATA_FILE));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    sb.append(line).append("\n");
                    continue;
                }
                String[] parts = line.split(",");
                int    filePin = Integer.parseInt(parts[0].trim());
                String sub     = parts[4].trim();

                if (filePin == pincode && sub.equals(custID)) {
                    parts[4] = "empty";  // free the port
                }
                sb.append(parts[0]+","+parts[1]+","+parts[2]+","+parts[3]+","+parts[4]+"\n");
            }
            br.close();

            FileWriter fw = new FileWriter(DATA_FILE);
            fw.write(sb.toString());
            fw.close();

            // Step 2: Mark customer as DELETED in customers.txt
            markCustomerDeleted(custID);

            System.out.println("Port freed: Pincode " + pincode + " -> " + customer[3] + "/" + customer[4] + "/Port" + customer[5] + " is now EMPTY");

            return true;

        } catch (Exception e) {
            System.out.println("Error in deleteCustomer: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // CUSTOMER LOOKUP
    // =====================================================================

    // Returns: [0]ID [1]Name [2]Pincode [3]OLT [4]SPL [5]Port [6]Service [7]Price [8]Status
    public String[] findCustomer(String custID) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(CUSTOMER_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts[0].trim().equals(custID)) {
                    br.close();
                    return parts;
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error finding customer: " + e.getMessage());
        }
        return null;
    }

    // Print all customers
    public void listAllCustomers() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(CUSTOMER_FILE));
            String line;
            boolean found = false;
            System.out.println("\n--- Customer Records ---");
            System.out.printf("%-12s %-15s %-8s %-6s %-6s %-5s %-28s %-7s %-8s%n",
                "CustomerID","Name","Pincode","OLT","SPL","Port","Service","Price","Status");
            System.out.println("-".repeat(100));
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                System.out.printf("%-12s %-15s %-8s %-6s %-6s %-5s %-28s %-7s %-8s%n",
                    p[0],p[1],p[2],p[3],p[4],p[5],p[6],p[7],p[8]);
                found = true;
            }
            br.close();
            if (!found) System.out.println("No customers found.");
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error listing customers: " + e.getMessage());
        }
    }

    // =====================================================================
    // INTERNAL FILE HELPERS
    // =====================================================================

    private void saveCustomerRecord(String id, String name, int pin,
            String olt, String spl, String port,
            String service, int price, String status) {
        try {
            FileWriter fw = new FileWriter(CUSTOMER_FILE, true); // append
            fw.write(id+","+name+","+pin+","+olt+","+spl+","+port+","+service+","+price+","+status+"\n");
            fw.close();
        } catch (Exception e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    private void updateCustomerRecord(String custID, int newPin,
            String newOLT, String newSPL, String newPort, String status) {
        rewriteCustomers(custID, newPin, newOLT, newSPL, newPort, null, -1, status);
    }

    private void updateCustomerServiceRecord(String custID, String newService, int newPrice) {
        rewriteCustomers(custID, -1, null, null, null, newService, newPrice, null);
    }

    private void markCustomerDeleted(String custID) {
        rewriteCustomers(custID, -1, null, null, null, null, -1, "DELETED");
    }

    // Generic rewriter for customers.txt
    private void rewriteCustomers(String custID, int newPin,
            String newOLT, String newSPL, String newPort,
            String newService, int newPrice, String newStatus) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(CUSTOMER_FILE));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    sb.append(line).append("\n");
                    continue;
                }
                String[] p = line.split(",");
                if (p[0].trim().equals(custID)) {
                    if (newPin    > 0)    p[2] = String.valueOf(newPin);
                    if (newOLT    != null) p[3] = newOLT;
                    if (newSPL    != null) p[4] = newSPL;
                    if (newPort   != null) p[5] = newPort;
                    if (newService!= null) p[6] = newService;
                    if (newPrice  > 0)    p[7] = String.valueOf(newPrice);
                    if (newStatus != null) p[8] = newStatus;
                }
                sb.append(p[0]+","+p[1]+","+p[2]+","+p[3]+","+p[4]+","+p[5]+","+p[6]+","+p[7]+","+p[8]+"\n");
            }
            br.close();
            FileWriter fw = new FileWriter(CUSTOMER_FILE);
            fw.write(sb.toString());
            fw.close();
        } catch (Exception e) {
            System.out.println("Error rewriting customers.txt: " + e.getMessage());
        }
    }
}

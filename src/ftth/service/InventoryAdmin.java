package ftth.service;
import java.util.Scanner;
import java.io.*;
 
public class InventoryAdmin {
 
    public void addInventory(Scanner sc) {
 
        System.out.println("\n--- Inventory Admin ---");
 
        System.out.print("Enter Pincode: ");
        int pincode = sc.nextInt();
        sc.nextLine();
 
        System.out.println("Select OLT Type:");
        System.out.println("  1. OLT300");
        System.out.println("  2. OLT500");
        System.out.print("Enter choice (1 or 2): ");
        String oltChoice = sc.nextLine().trim();
 
        String oltType = "";
        if (oltChoice.equals("1")) {
            oltType = "OLT300";
        } else if (oltChoice.equals("2")) {
            oltType = "OLT500";
        } else {
            System.out.println("Invalid OLT choice.");
            return;
        }
 
        System.out.print("How many Splitters? (1 to 3): ");
        int splitterCount = sc.nextInt();
        sc.nextLine();
 
        if (splitterCount < 1 || splitterCount > 3) {
            System.out.println("Invalid. Enter between 1 and 3.");
            return;
        }
 
        try {
            FileWriter fw = new FileWriter("data.txt", true);
 
            for (int s = 1; s <= splitterCount; s++) {
                for (int port = 1; port <= 3; port++) {
                    fw.write(pincode + "," + oltType + ",SPL" + s + "," + port + ",empty\n");
                }
            }
 
            fw.close();
 
            System.out.println("Done! Added " + (splitterCount * 3) + " ports for pincode " + pincode);
 
        } catch (Exception e) {
            System.out.println("Error writing to data.txt: " + e.getMessage());
        }
    }
}
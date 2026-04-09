package ftth.controller;

import ftth.service.InventoryService;
import ftth.util.ValidationUtil;
import ftth.model.OLT;
import java.util.*;

public class InventoryController {

    private Scanner sc = new Scanner(System.in);
    private InventoryService service = new InventoryService();

    public void menu() {

        while (true) {

            System.out.println("\n--- Inventory Admin ---");
            System.out.println("1. View Inventory Summary");
            System.out.println("2. Add OLT");
            System.out.println("3. Remove OLT");
            System.out.println("4. Add Splitter to OLT");
            System.out.println("5. Remove Splitter from OLT");
            System.out.println("6. View OLT Details");
            System.out.println("7. Back to Main Menu");
            System.out.print("\nSelect option :");

            int ch = Integer.parseInt(sc.nextLine());

            switch (ch) {

                case 1:
                    // System.out.print("Enter Pincode: ");
                    // String pin = sc.nextLine();
                    System.out.println("===== Inventory Summary ===== ");
                    List<String>pins=service.getUniquePincodes();
                  for(String pin:pins){
                    List<OLT> list = service.getByPincode(pin);
                    System.out.println("Pincode:"+pin);
                    if (list.isEmpty()) {
                        System.out.println("❌ No OLTs found");
                    } else {
                        for (OLT o : list) System.out.println(o);
                    }
                    System.out.println();
                }
                    break;

                case 2:
                    System.out.println("=== Add OLT to Pincode === ");
                    addOLTFlow();
                    break;

                case 3:
                    System.out.println("=== Remove OLT ===");
                    removeOLTFlow();
                    break;

                case 4:
                    System.out.println("=== Add Splitter to OLT ===");
                    splitterAddFlow();
                    break;

                case 5:
                    System.out.println("=== Remove Splitter from OLT ===");
                    splitterRemoveFlow();
                    break;
                case 6:
                    System.out.println("=== OLT Details ===");
                    //OTL details code 
                    break;

                case 7:
                    return;
                default:
                    System.err.println("Enter valid choice");
            }
        }
    }

    private void addOLTFlow() {
        System.out.print("Enter Pincode: ");
        String pin = sc.nextLine();
        boolean valid=ValidationUtil.isValidPincode(pin);
        if(!valid){
            System.out.println("[ERROR] Enter Valid Pincode!");
            return;
        }
        System.out.println("OLT Types: [1] OLT500 (1GBPS) [2] OLT300 (400MBPS)");
        System.out.print("Select OLT Type [1/2]: ");
        int choice=0;
        choice = sc.nextInt();
        sc.nextLine();
        while(choice!=1 && choice!=2){
        System.out.println("OLT Types: [1] OLT500 (1GBPS) [2] OLT300 (400MBPS)");
        System.err.print("Enter valid Choice!: ");
        choice = sc.nextInt();
        sc.nextLine();
        }
        String type = (choice == 1) ? "OLT500" : (choice == 2) ? "OLT300" : "";
        System.out.print("Number of Splitters (1-3): ");
        int split=0;
        split = Integer.parseInt(sc.nextLine());
        while(split<=0 && split>3){
         System.out.print("Number of Splitters (1-3): ");
         System.err.print("Enter valid Splitters!: ");  
         split = Integer.parseInt(sc.nextLine());
        }
        
        String id = service.generateOLTId(type, pin);

        service.addOLT(new OLT(id, pin, type, split, false));
         System.out.println();
        System.out.println("[SUCCESS] OLT Added: " + id+" - "+split+" Splitters Occupied!");
    }

    private void removeOLTFlow() {

        System.out.print("Enter Pincode: ");
        String pin = sc.nextLine();

        List<OLT> list = service.getByPincode(pin);
        while(true){
        for (OLT o : list) System.out.println(o);

        System.out.print("Enter OLT ID to remove: ");
        String id = sc.nextLine();
        System.out.print("  Confirm removal of "+id+" ? (y/n): ");
        String option=sc.nextLine();
        if (!option.trim().equalsIgnoreCase("y")) continue;
        boolean res = service.removeOLT(id);

        if (res) System.out.println("[SUCCESS] Removed "+id+" OLT successfully.");
        else System.out.println("[FAILED] Cannot remove "+id+" - has active customers.");
        break;
        }
        System.out.println();
    }

    private void splitterAddFlow() {

        System.out.print("Enter Pincode: ");
        String pin = sc.nextLine();

        List<OLT> list = service.getByPincode(pin);

        for (OLT o : list) System.out.println(o);

        System.out.print("Enter OLT ID: ");
        String id = sc.nextLine();

        if (service.addSplitter(id))
            System.out.println(" [SUCCESS] Splitter added to "+id+" successfully!");
        else
            System.out.println(" [FAILED] Max capacity reached, can't add splitter to "+id+" OLT.");
    }

    private void splitterRemoveFlow() {

        System.out.print("Enter Pincode: ");
        String pin = sc.nextLine();

        List<OLT> list = service.getByPincode(pin);

        for (OLT o : list) System.out.println(o);

        System.out.print("Enter OLT ID: ");
        String id = sc.nextLine();

        if (service.removeSplitter(id))
            System.out.println(" [SUCCESS] Splitter removes from "+id+" successfully!");
        else
            System.out.println(" [FAILED] None to remove, can't remove splitter from "+id+" OLT.");
    }
}
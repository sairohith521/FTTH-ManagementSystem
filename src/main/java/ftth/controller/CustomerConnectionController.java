package ftth.controller;

import java.util.List;
import java.util.Scanner;

import ftth.model.CustomerConnection;
import ftth.model.Plan;
import ftth.model.ServiceArea;
import ftth.model.User;
import ftth.model.dtos.AddConnectionRequest;
import ftth.service.CustomerConnectionService;
import ftth.service.InventoryService;
import ftth.service.PlanService;
import ftth.service.ServiceAreaService;
import ftth.util.InputUtil;

public class CustomerConnectionController {
    private final CustomerConnectionService customerConnectionService;
    private final PlanService planService;
    private final InventoryService inventoryService;
    private final ServiceAreaService serviceAreaService;
 
    public CustomerConnectionController(ServiceAreaService serviceAreaService,CustomerConnectionService customerConnectionService,PlanService planService,InventoryService inventoryService){
        this.customerConnectionService=customerConnectionService;
        this.planService=planService;
        this.inventoryService=inventoryService;
        this.serviceAreaService=serviceAreaService;
        
    }
 public void handleAdd(Scanner sc,User currentUser){
       System.out.println("\n--- New Connection ---");
       List<Plan> plans = planService.getActivePlans();
    if (plans.isEmpty()) {
        System.out.println("No plans available. Contact admin.");
        return;
    }
    System.out.println("Plans Available:");
    planService.printActivePlans(plans);
    String name = InputUtil.readValidName(sc, "Enter Customer Name: ");
    Plan selectedPlan;
    while (true) {
        long planId = InputUtil.readLong(sc, "Select Plan ID: ");
        selectedPlan = planService.findPlanById(planId);
        if (selectedPlan != null && selectedPlan.isActive()) break;
        System.out.println("Invalid plan ID. Please select from the list above.");
    }

    double salary = InputUtil.readDouble(sc, "Enter Salary: ");
    if (salary <= 30000) {
        System.out.println("Salary below eligibility. Minimum salary required: Rs.30,000.");
        return;
    }

    Long pincode = InputUtil.readLong(sc, "Enter Pincode: ");
    if (!inventoryService.checkPincode(pincode) ){
        System.out.println("Service NOT available in pincode " + pincode + ".");
        return;
    }
String oltType = selectedPlan.getOltType();

if (oltType == null) {
    System.out.println(
        "[ERROR] Unable to determine OLT type for selected plan."
    );
    return;
}

System.out.println("OLT Type (from plan): " + oltType);
    ServiceArea serviceArea=serviceAreaService.findByPincode(pincode);

    int ports = inventoryService.getAvailablePortsByType(serviceArea.getServiceAreaId(), oltType);
    if (ports <= 0) {
        System.out.println("No " + oltType + " ports available in pincode " + pincode + ".");
        return;
    }
    System.out.println("Available " + oltType + " ports in " + pincode + " : " + ports);

    String gmail = InputUtil.readEmail(sc, "Enter your Email: ");
    
    System.out.println("\n--- Order Summary ---");
    System.out.println("Name     : " + name);
    System.out.println("Plan     : " + selectedPlan.getPlanName() + " @ Rs." + selectedPlan.getMonthlyPrice());
    System.out.println("Pincode  : " + pincode);
    System.out.println("OLT Type : " + oltType);
    System.out.println("Email    : " + gmail);
    System.out.print("Confirm connection? (y/n): ");
        if (!sc.nextLine().equalsIgnoreCase("y")) {
            System.out.println("Operation cancelled.");
            return;
        }
          AddConnectionRequest req = new AddConnectionRequest(
            name,
            selectedPlan.getPlanId(),
            salary,
            pincode,
            oltType,
            gmail
        );
    customerConnectionService.createConnection(req,currentUser.getUserId());
    System.out.println(" Connection created successfully.");
    }
 public void updateCustomerConnection(Scanner sc, User currentUser) {

    System.out.println("\n--- Move Customer ---");

    // 1️⃣ List active connections
    customerConnectionService.listActiveConnections();

    // 2️⃣ Read connection ID
    long connectionId = InputUtil.readLong(sc, "Enter Connection ID (0 to cancel): ");

    if (connectionId == 0) {
        System.out.println("Cancelled.");
        return;
    }

    // 3️⃣ Fetch active connection
    CustomerConnection connection = customerConnectionService.getConnectionById(connectionId);

    if (connection == null || !connection.isActive()) {
        System.out.println("No active connection found for ID " + connectionId + ".");
        return;
    }

    if (connection == null) {
        System.out.println("No active connection found.");
        return;
    }
    String pincode=serviceAreaService.getPincode(connection.getServiceAreaId());
    Long planId=connection.getPlanId();
    System.out.println("Current Pincode : " + pincode);
    // 3️⃣ Read new pincode
    long newPincode =
        InputUtil.readLong(sc, "Enter New Pincode (0 to cancel): ");

    if (newPincode == 0) {
        System.out.println("Cancelled.");
        return;
    }

    // 4️⃣ Get OLT type from current plan
    Plan currplan = planService.findPlanById(planId);
    String oltType=currplan.getOltType();

    // 5️⃣ Validate OLT type availability at new pincode
    ServiceArea newArea = serviceAreaService.findByPincode(newPincode);
    if (newArea == null) {
        System.out.println("Service not available in pincode " + newPincode + ".");
        return;
    }
    int availablePorts = inventoryService.getAvailablePortsByType(newArea.getServiceAreaId(), oltType);
    if (availablePorts <= 0) {
        System.out.println("No " + oltType + " ports available in pincode " + newPincode + ".");
        System.out.println("Customer's plan (" + currplan.getPlanName() + ") requires " + oltType + ".");
        return;
    }
    System.out.println("OLT Type (from plan): " + oltType);
    System.out.println("Available " + oltType + " ports in " + newPincode + " : " + availablePorts);

    // 6️⃣ Confirm
    System.out.print("Confirm move? (y/n): ");
    if (!sc.nextLine().equalsIgnoreCase("y")) {
        System.out.println("Cancelled.");
        return;
    }

    // 7️⃣ Delegate to service
    customerConnectionService.updateCustomerConnection(
        connection,
        newPincode,
        oltType,
        currentUser.getUserId()
    );

    System.out.println("✅ Customer moved successfully.");
}
public void doChangePlan(Scanner sc, User currentUser) {

    System.out.println("\n--- Change Service ---");

    // 1️⃣ List active connections
    customerConnectionService.listActiveConnections();

    // 2️⃣ Read connection ID
    long connectionId = InputUtil.readLong(sc, "Enter Connection ID (0 to cancel): ");

    if (connectionId == 0) {
        System.out.println("Cancelled.");
        return;
    }

    // 3️⃣ Get active connection
    CustomerConnection connection = customerConnectionService.getConnectionById(connectionId);

    if (connection == null || !connection.isActive()) {
        System.out.println("No active connection found for ID " + connectionId + ".");
        return;
    }

    // 3️⃣ Get current plan
    Plan currentPlan =
        planService.findPlanById(connection.getPlanId());

    System.out.println(
        "\nCurrent Plan : " +
        currentPlan.getPlanName() +
        " | " + currentPlan.getSpeedLabel() +
        " | Rs." + currentPlan.getMonthlyPrice()
    );

    // 4️⃣ Filter plans: exclude current, only show plans whose OLT type has ports in this pincode
    Long serviceAreaId = connection.getServiceAreaId();
    List<Plan> plans = planService.getActivePlans();
    plans.removeIf(p -> p.getPlanId().equals(currentPlan.getPlanId())
                     || inventoryService.getAvailablePortsByType(serviceAreaId, p.getOltType()) <= 0);

    if (plans.isEmpty()) {
        System.out.println("No other plans available with ports in this pincode.");
        return;
    }

    System.out.println("\nAvailable Plans:");
    for (Plan p : plans) {
        System.out.println(
            p.getPlanId() + ". " +
            p.getPlanName() +
            " | " + p.getSpeedLabel() +
            " | Rs." + p.getMonthlyPrice() +
            " | OLT: " + p.getOltType()
        );
    }

    // 5️⃣ Select new plan
    Plan selectedPlan;
    while (true) {
        long planId = InputUtil.readLong(sc, "Select New Plan ID: ");
        selectedPlan = planService.findPlanById(planId);

        if (selectedPlan == null || !selectedPlan.isActive()) {
            System.out.println("Invalid plan ID.");
            continue;
        }

        if (selectedPlan.getPlanId().equals(currentPlan.getPlanId())) {
            System.out.println("Already on this plan. Select a different one.");
            continue;
        }

        if (inventoryService.getAvailablePortsByType(serviceAreaId, selectedPlan.getOltType()) <= 0) {
            System.out.println("No " + selectedPlan.getOltType() + " ports available. Pick another plan.");
            continue;
        }

        break;
    }

    // 6️⃣ Confirm
    boolean oltChange = !currentPlan.getOltType().equals(selectedPlan.getOltType());
    System.out.println(
        "\nChange from : " + currentPlan.getPlanName() +
        " @ Rs." + currentPlan.getMonthlyPrice() +
        " (" + currentPlan.getOltType() + ")"
    );
    System.out.println(
        "Change to   : " + selectedPlan.getPlanName() +
        " @ Rs." + selectedPlan.getMonthlyPrice() +
        " (" + selectedPlan.getOltType() + ")"
    );
    if (oltChange) {
        System.out.println("Note: OLT type will change. Port will be reallocated.");
    }

    System.out.print("Confirm change? (y/n): ");
    if (!sc.nextLine().equalsIgnoreCase("y")) {
        System.out.println("Cancelled.");
        return;
    }

    // 7️⃣ Delegate to service
    customerConnectionService.changePlan(
        connection.getConnectionId(),
        selectedPlan.getPlanId(),
        currentUser.getUserId()
    );

    System.out.println("✅ Plan changed successfully.");
}
public void doDisconnect(Scanner sc, User currentUser) {

    System.out.println("\n--- Disconnect Customer ---");

    // 1️⃣ List active connections (optional helper)
    customerConnectionService.listActiveConnections();

    // 2️⃣ Read connection ID
    long connectionId = InputUtil.readLong(sc, "Enter Connection ID (0 to cancel): ");

    if (connectionId == 0) {
        System.out.println("Cancelled.");
        return;
    }

    // 3️⃣ Fetch connection
    CustomerConnection connection =
        customerConnectionService.getConnectionById(connectionId);

    if (connection == null) {
        System.out.println("Connection not found.");
        return;
    }

    if (!connection.isActive()) {
        System.out.println("Connection is already disconnected.");
        return;
    }

    // 4️⃣ Show summary
    System.out.println("\nYou are about to disconnect:");
    System.out.println("  Connection ID : " + connection.getConnectionId());
    System.out.println("  Customer ID   : " + connection.getCustomerId());
    System.out.println("  Service Area  : " + connection.getServiceAreaId());
    System.out.println("  Port ID       : " + connection.getPortId());

    // 5️⃣ Confirm
    System.out.print("Confirm disconnect? (y/n): ");
    if (!sc.nextLine().equalsIgnoreCase("y")) {
        System.out.println("Cancelled.");
        return;
    }

    // 6️⃣ Delegate to service
    customerConnectionService.disconnect(
        connection.getConnectionId(),
        currentUser.getUserId()
    );

    System.out.println("✅ Customer disconnected successfully.");
}
}


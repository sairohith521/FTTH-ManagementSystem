package ftth.app;
import java.util.Scanner;
import ftth.service.*;
import ftth.controller.*;
import ftth.repository.*;
/**
 * Main — Aaha Telecom FTTH Management System
 *
 * Roles & Menus
 * ─────────────
 * ADMIN : All options + User Management
 * CSR   : Add / Move / Change / Disconnect / Customer Lookup
 * MAINT : Inventory Admin / Maintenance / Capacity Dashboard / Plan Admin
 */

public class Application {
           private final Scanner sc;
           // ===============================
           // Repositories
           // ===============================
           private final CustomerRepository customerRepository;
           private final CustomerConnectionRepository customerConnectionRepository;
           private final PlanRepository planRepository;
           private final ServiceAreaRepository serviceAreaRepository;
           private final InventoryRepository inventoryRepository;
           private final BillRepository billRepository;
           private final EmailLogRepository emailLogRepository;
           private final UserRepository userRepository;
           private final RoleRepository roleRepository;
           private final CapacityInventoryRepository capacityInventoryRepository;
           // ===============================
           // Services
           // ===============================
           private final CapacityService capacityService;
           private final PlanService planService;
           private final InventoryService inventoryService;
           private final EmailService emailService;
           private final UserManagerService userManagerService;
           private final CustomerConnectionService customerConnectionService;
           private final ServiceAreaService serviceAreaService;
           private final CustomerService customerService;
           private final BillService billService;
           // ===============================
           // Controllers
           // ===============================
           private final CustomerConnectionController customerConnectionController;
           private final AdminController adminController;
           private final CSRController csrController;
           private final MaintController maintController;
           private final UserManagementController userManagementController;
           private final CustomerScreenController customerScreenController;
           private final PlanAdmin planAdmin;
           private final InventoryController inventoryController;
           // ===============================
           // Constructor (DI container)
           // ===============================
           public Application() {
               // ---------- shared utilities ----------
               this.sc = new Scanner(System.in);
               // ---------- repositories ----------
               this.customerRepository = new CustomerRepository();
               this.customerConnectionRepository = new CustomerConnectionRepository();
               this.planRepository = new PlanRepository();
               this.serviceAreaRepository = new ServiceAreaRepository();
               this.inventoryRepository = new InventoryRepository();
               this.billRepository = new BillRepository();
               this.emailLogRepository = new EmailLogRepository();
               this.userRepository = new UserRepository();
               this.roleRepository = new RoleRepository();
               this.capacityInventoryRepository=new CapacityInventoryRepository();
               // ---------- services ----------
               this.emailService = new EmailService(emailLogRepository);
               this.planService = new PlanService(planRepository,emailService);
               this.customerService=new CustomerService(customerRepository);
               this.inventoryService = new InventoryService(inventoryRepository);
               this.userManagerService = new UserManagerService(userRepository,roleRepository);
               this.serviceAreaService=new ServiceAreaService(serviceAreaRepository);
               this.billService=new BillService(billRepository);
               this.capacityService=new CapacityService(capacityInventoryRepository);
               this.customerConnectionService = new CustomerConnectionService(customerService,customerRepository,customerConnectionRepository,planService,inventoryService,billRepository,serviceAreaService,emailService);
               // ---------- controllers ----------
               this.planAdmin=new PlanAdmin(planService,sc);
               this.inventoryController=new InventoryController(inventoryService);
               this.customerScreenController=new CustomerScreenController(customerService,billService, emailService, planService, customerConnectionService);
               this.customerConnectionController =new CustomerConnectionController(serviceAreaService,customerConnectionService,planService,inventoryService);
               this.adminController =new AdminController(inventoryController,planAdmin,customerScreenController,customerConnectionController,userManagerService,capacityService);
               this.csrController =new CSRController(customerScreenController,customerConnectionController);
               this.maintController =new MaintController(inventoryController,planAdmin,capacityService);
               this.userManagementController=new UserManagementController(userManagerService,adminController,csrController,maintController);  
           }

      public void start(){
           userManagementController.start(sc);
       }
}
package ftth.controller;
import java.util.Scanner;
import ftth.model.User;
public class CSRController {
    private final CustomerScreenController customerScreenController;
    private final CustomerConnectionController customerConnectionController;
   

    public CSRController(CustomerScreenController customerScreenController,CustomerConnectionController customerConnectionController) {
        this.customerScreenController=customerScreenController;
        this.customerConnectionController=customerConnectionController;
    }

    // 🔹 MAIN HANDLER (NO STATIC ❌)
    public boolean handle(String option, Scanner sc,User currentUser) {

        switch (option) {

            case "1":
                doAdd(sc,currentUser);
                return false;

            case "2":
                doMove(sc,currentUser);
                return false;

            case "3":
                doChange(sc,currentUser);
                return false;

            case "4":
                doDelete(sc,currentUser);
                return false;

            case "5": 
                doLookup(sc,currentUser);
                return false;

            case "0":
                System.out.println("Logged out.");
                return true;

            default:
                System.out.println("Invalid option.");
                return false;
        }
    }

    // =========================================================
    // 🔥 METHODS (move your logic here)
    // =========================================================

      private void doAdd(Scanner sc,User currUser) {
        customerConnectionController.handleAdd(sc,currUser);
      }

private void doMove(Scanner sc,User currUser) {
    customerConnectionController.updateCustomerConnection(sc,currUser);
}

   private void doChange(Scanner sc,User currUser) {
    customerConnectionController.doChangePlan(sc, currUser);
   }
   private void doDelete(Scanner sc,User currUser) {
customerConnectionController.doDisconnect(sc, currUser);   
}
private void doLookup(Scanner sc,User currUser) {
  customerScreenController.menu(sc,currUser);
}
}
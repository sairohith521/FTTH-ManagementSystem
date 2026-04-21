package ftth.service;
import java.util.List;
import ftth.repository.*;
import ftth.model.*;
public class CustomerService {

    private final CustomerRepository repo;
    private final PlanRepository planRepo;


    public CustomerService(CustomerRepository customerRepository) {
        this.repo = customerRepository;
        this.planRepo=new PlanRepository();
    }

    public Customer lookupCustomerByCode(String code) {
        return repo.findByCustomerCode(code);
    }

    public List<Customer> listAllCustomers() {
        return repo.findAll();
    }

    public boolean changePlan(Customer customer, String planChoice) {
        long planId = Long.parseLong(planChoice);
        Plan plan=planRepo.findPlanById(planId);
        return planRepo.updatePlan(customer.getCustomerId(), plan);
    }
   public Long getCurrentPlanId(Customer customer) {
    return repo.getCurrentPlanId(customer.getCustomerId());
}
    public boolean moveCustomer(Customer customer, int newPincode) {
        return repo.updateServiceArea(customer.getCustomerId(), newPincode);
    }

    public boolean disconnect(Customer customer) {
        return repo.disconnectCustomer(customer.getCustomerId());
    }
    /**
     * Find existing customer by email.
     */
    public Customer findByEmail(String email) {
        return repo.findByEmail(email);
    }

    /**
     * Find existing customer or create a new one.
     */
public Customer findOrCreateCustomer(String name,
                                         String email,
                                         double salary) {

        Customer customer = repo.findByEmail(email);

        if (customer != null) {
            return customer;
        }

        Customer newCustomer =
            new Customer(name, email, java.math.BigDecimal.valueOf(salary));

        repo.insert(newCustomer);

        return newCustomer;
    }
    public Customer createCustomer(Customer customer) {

    String customerCode = repo.save(customer);

    if (customerCode == null) {
        throw new RuntimeException("Customer creation failed");
    }

    return customer;
}


}

    
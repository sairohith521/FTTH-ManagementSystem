package ftth.model;

public class Plan {

    private String id;          // P1, P2, etc.
    private String name;        // e.g., IPL Hungama
    private String speed;       // e.g., 1GBPS, 500MBPS
    private String dataLimit;   // e.g., Unlimited, 30 GB/day
    private int otts;           // number of OTTs
    private double price;       // monthly price
    private String oltType;     // OLT300 / OLT500
    private boolean active;     // true = active, false = disabled

    // 🔹 Constructor
    public Plan(String id, String name, String speed, String dataLimit,
                int otts, double price, String oltType, boolean active) {
        this.id = id;
        this.name = name;
        this.speed = speed;
        this.dataLimit = dataLimit;
        this.otts = otts;
        this.price = price;
        this.oltType = oltType;
        this.active = active;
    }

    // 🔹 Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpeed() { return speed; }
    public String getDataLimit() { return dataLimit; }
    public int getOtts() { return otts; }
    public double getPrice() { return price; }
    public String getOltType() { return oltType; }
    public boolean isActive() { return active; }

    // 🔹 Setters
    public void setName(String name) { this.name = name; }
    public void setSpeed(String speed) { this.speed = speed; }
    public void setDataLimit(String dataLimit) { this.dataLimit = dataLimit; }
    public void setOtts(int otts) { this.otts = otts; }
    public void setPrice(double price) { this.price = price; }
    public void setOltType(String oltType) { this.oltType = oltType; }
    public void setActive(boolean active) { this.active = active; }

    // 🔹 toString (for printing)
    @Override
    public String toString() {
        return "[" + id + "] " + name + " | " + speed + " | " + dataLimit +
               " | " + otts + " OTTs | INR " + price + "/mo | " +
               oltType + " [" + (active ? "Active" : "Disabled") + "]";
    }
}
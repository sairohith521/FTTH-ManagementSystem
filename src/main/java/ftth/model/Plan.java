package ftth.model;

public class Plan {

    private long id;            // numeric DB primary key
    private String code;        // business code, e.g. P1, IPL1
    private String name;        // e.g., IPL Hungama
    private String speed;       // e.g., 1GBPS, 500MBPS
    private String dataLimit;   // e.g., Unlimited, 30 GB/day
    private int otts;           // number of OTTs
    private double price;       // monthly price
    private String oltType;     // OLT300 / OLT500
    private boolean active;     // true = active, false = disabled

    public Plan(long id, String code, String name, String speed, String dataLimit,
                int otts, double price, String oltType, boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.speed = speed;
        this.dataLimit = dataLimit;
        this.otts = otts;
        this.price = price;
        this.oltType = oltType;
        this.active = active;
    }

    public Plan(String code, String name, String speed, String dataLimit,
                int otts, double price, String oltType, boolean active) {
        this(0L, code, name, speed, dataLimit, otts, price, oltType, active);
    }

    public long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getSpeed() { return speed; }
    public String getDataLimit() { return dataLimit; }
    public int getOtts() { return otts; }
    public double getPrice() { return price; }
    public String getOltType() { return oltType; }
    public boolean isActive() { return active; }

    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setSpeed(String speed) { this.speed = speed; }
    public void setDataLimit(String dataLimit) { this.dataLimit = dataLimit; }
    public void setOtts(int otts) { this.otts = otts; }
    public void setPrice(double price) { this.price = price; }
    public void setOltType(String oltType) { this.oltType = oltType; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "[" + id + "] " + code + " | " + name + " | " + speed + " | " + dataLimit +
               " | " + otts + " OTTs | INR " + price + "/mo | " +
               oltType + " [" + (active ? "Active" : "Disabled") + "]";
    }
}

package ftth.model;

public class Plan {

    private long id;
    private String name;
    private String speed;
    private String dataLimit;
    private int otts;
    private double price;
    private String oltType;
    private boolean active;

    public Plan(long id, String name, String speed, String dataLimit,
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

    public Plan(String name, String speed, String dataLimit,
                int otts, double price, String oltType, boolean active) {
        this(0L, name, speed, dataLimit, otts, price, oltType, active);
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getSpeed() { return speed; }
    public String getDataLimit() { return dataLimit; }
    public int getOtts() { return otts; }
    public double getPrice() { return price; }
    public String getOltType() { return oltType; }
    public boolean isActive() { return active; }

    public void setName(String name) { this.name = name; }
    public void setSpeed(String speed) { this.speed = speed; }
    public void setDataLimit(String dataLimit) { this.dataLimit = dataLimit; }
    public void setOtts(int otts) { this.otts = otts; }
    public void setPrice(double price) { this.price = price; }
    public void setOltType(String oltType) { this.oltType = oltType; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " | " + speed + " | " + dataLimit +
               " | " + otts + " OTTs | INR " + price + "/mo | " +
               oltType + " [" + (active ? "Active" : "Disabled") + "]";
    }
}

package ftth.model;

public class OLT {

    private String oltId;
    private String pincode;
    private String type;
    private int splitterCount;
    private boolean hasCustomer;

    public OLT() {}

    public OLT(String oltId, String pincode, String type, int splitterCount, boolean hasCustomer) {
        this.oltId = oltId;
        this.pincode = pincode;
        this.type = type;
        this.splitterCount = splitterCount;
        this.hasCustomer = hasCustomer;
    }

    public String getOltId() { return oltId; }
    public String getPincode() { return pincode; }
    public String getType() { return type; }
    public int getSplitterCount() { return splitterCount; }
    public boolean isHasCustomer() { return hasCustomer; }

    public void setSplitterCount(int splitterCount) { this.splitterCount = splitterCount; }
    public void setHasCustomer(boolean hasCustomer) { this.hasCustomer = hasCustomer; }

    @Override
    public String toString() {
        return "  "+oltId + " (" + type + ") | Splitters: " + splitterCount +
               " | " + (hasCustomer ? "In Use" : "Free");
    }
}
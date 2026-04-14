package ftth.model;

public class OLT {

    private final String oltId;
    private final String pincode;
    private final String type;
    private final int splitterCount;
    private final int totalPorts;
    private final int availablePorts;

    public OLT(String oltId, String pincode, String type, int splitterCount, int totalPorts, int availablePorts) {
        this.oltId = oltId;
        this.pincode = pincode;
        this.type = type;
        this.splitterCount = splitterCount;
        this.totalPorts = totalPorts;
        this.availablePorts = availablePorts;
    }

    public String getOltId() {
        return oltId;
    }

    public String getPincode() {
        return pincode;
    }

    public String getType() {
        return type;
    }

    public int getSplitterCount() {
        return splitterCount;
    }

    public int getTotalPorts() {
        return totalPorts;
    }

    public int getAvailablePorts() {
        return availablePorts;
    }

    public int getAssignedPorts() {
        return totalPorts - availablePorts;
    }

    @Override
    public String toString() {
        return "  " + oltId + " (" + type + ") | Splitters: " + splitterCount
                + " | Ports: " + availablePorts + "/" + totalPorts + " available";
    }
}

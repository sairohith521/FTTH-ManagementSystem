package ftth.model.dtos;

public class CapacityRow {

    private String pincode;
    private long oltId;
    private String oltType;
    private int totalPorts;
    private int usedPorts;
    private int freePorts;
    private double utilization;
    private int splitterCount;

    // =========================
    // Getters and Setters
    // =========================

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public long getOltId() {
        return oltId;
    }

    public void setOltId(long oltId) {
        this.oltId = oltId;
    }

    public String getOltType() {
        return oltType;
    }

    public void setOltType(String oltType) {
        this.oltType = oltType;
    }

    public int getTotalPorts() {
        return totalPorts;
    }

    public void setTotalPorts(int totalPorts) {
        this.totalPorts = totalPorts;
    }

    public int getUsedPorts() {
        return usedPorts;
    }

    public void setUsedPorts(int usedPorts) {
        this.usedPorts = usedPorts;
    }

    public int getFreePorts() {
        return freePorts;
    }

    public void setFreePorts(int freePorts) {
        this.freePorts = freePorts;
    }

    public double getUtilization() {
        return utilization;
    }

    public void setUtilization(double utilization) {
        this.utilization = utilization;
    }

    public int getSplitterCount() {
        return splitterCount;
    }

    public void setSplitterCount(int splitterCount) {
        this.splitterCount = splitterCount;
    }
}

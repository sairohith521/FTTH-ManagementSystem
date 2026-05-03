package ftth.model.dtos;
public class OltInventoryDTO {

    // ✅ Fields (must be at class level)
    private String oltCode;
    private String pincode;
    private String oltType;
    private int splitterCount;
    private int totalPorts;
    private int availablePorts;

    // ✅ No-args constructor
    public OltInventoryDTO() {
    }

    // ✅ All-args constructor
    public OltInventoryDTO(String oltCode,
                           String pincode,
                           String oltType,
                           int splitterCount,
                           int totalPorts,
                           int availablePorts) {
        this.oltCode = oltCode;
        this.pincode = pincode;
        this.oltType = oltType;
        this.splitterCount = splitterCount;
        this.totalPorts = totalPorts;
        this.availablePorts = availablePorts;
    }

    public String getOltCode() {
        return oltCode;
    }

    public void setOltCode(String oltCode) {
        this.oltCode = oltCode;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getOltType() {
        return oltType;
    }

    public void setOltType(String oltType) {
        this.oltType = oltType;
    }

    public int getSplitterCount() {
        return splitterCount;
    }

    public void setSplitterCount(int splitterCount) {
        this.splitterCount = splitterCount;
    }

    public int getTotalPorts() {
        return totalPorts;
    }

    public void setTotalPorts(int totalPorts) {
        this.totalPorts = totalPorts;
    }

    public int getAvailablePorts() {
        return availablePorts;
    }

    public void setAvailablePorts(int availablePorts) {
        this.availablePorts = availablePorts;
    }
}

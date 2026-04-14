package ftth.model;

public class Port {

    private final long portId;
    private final long splitterId;
    private final int portNumber;
    private final String status;

    public Port(long portId, long splitterId, int portNumber, String status) {
        this.portId = portId;
        this.splitterId = splitterId;
        this.portNumber = portNumber;
        this.status = status;
    }

    public long getPortId() {
        return portId;
    }

    public long getSplitterId() {
        return splitterId;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public String getStatus() {
        return status;
    }
}

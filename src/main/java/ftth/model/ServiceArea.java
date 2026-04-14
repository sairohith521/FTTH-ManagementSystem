package ftth.model;

public class ServiceArea {

    private final long serviceAreaId;
    private final String pincode;
    private final boolean active;

    public ServiceArea(long serviceAreaId, String pincode, boolean active) {
        this.serviceAreaId = serviceAreaId;
        this.pincode = pincode;
        this.active = active;
    }

    public long getServiceAreaId() {
        return serviceAreaId;
    }

    public String getPincode() {
        return pincode;
    }

    public boolean isActive() {
        return active;
    }
}

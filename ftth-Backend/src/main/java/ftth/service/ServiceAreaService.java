package ftth.service;

import ftth.model.ServiceArea;
import ftth.repository.ServiceAreaRepository;

/**
 * Service layer for service area related business logic.
 */
public class ServiceAreaService {

    private final ServiceAreaRepository serviceAreaRepository;

    // ✅ Constructor injection
    public ServiceAreaService(ServiceAreaRepository serviceAreaRepository) {
        this.serviceAreaRepository = serviceAreaRepository;
    }

    /**
     * Find service area by pincode.
     *
     * @param pincode service area pincode
     * @return ServiceArea or null if not found
     */
    public ServiceArea findByPincode(Long pincode) {
        return serviceAreaRepository.findByPincode(pincode);
    }
    /**
     * Get active service area by pincode.
     * Throws exception if not found or inactive.
     */
    public ServiceArea getActiveServiceArea(long pincode) {

        ServiceArea area =
            serviceAreaRepository.findByPincode(pincode);

        if (area == null) {
            throw new RuntimeException(
                "Service not available in pincode: " + pincode
            );
        }

        if (!area.isActive()) {
            area=null;
            throw new RuntimeException(
                "Service area is inactive for pincode: " + pincode
            );
        }

        return area;
    }
    /**
     * Check whether service is available for given pincode.
     *
     * @param pincode pincode entered by user
     * @return true if service area exists and is active
     */
    public boolean isServiceAvailable(long pincode) {
        ServiceArea area = serviceAreaRepository.findByPincode(pincode);
        return area != null && area.isActive();
    }
    
 public String getPincode(Long serviceAreaId) {

        if (serviceAreaId == null) {
            throw new IllegalArgumentException("Service area id cannot be null");
        }

        return serviceAreaRepository.findPincodeById(serviceAreaId);
    }


}
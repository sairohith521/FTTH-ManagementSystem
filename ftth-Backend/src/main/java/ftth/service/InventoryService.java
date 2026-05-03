package ftth.service;
import ftth.model.*;
import ftth.model.dtos.OltInventoryDTO;
import ftth.repository.InventoryRepository;
import java.util.List;

public class InventoryService {

    private static final int MAX_SPLITTERS = 3;
    private static final int PORTS_PER_SPLITTER = 3;

    private final InventoryRepository repo;

    public InventoryService(InventoryRepository repo) {
        this.repo = repo;
    }

    public String addOLT(String pincode, String type, int splitterCount) {
        if (splitterCount <= 0 || splitterCount > MAX_SPLITTERS) {
            throw new IllegalArgumentException("Splitters must be between 1 and " + MAX_SPLITTERS + ".");
        }
        return repo.addOlt(pincode, type, splitterCount, PORTS_PER_SPLITTER);
    }

    public boolean removeOLT(String id) {
        return repo.removeOlt(id);
    }

    public boolean addSplitter(String id) {
        return repo.addSplitter(id, PORTS_PER_SPLITTER);
    }

    public boolean removeSplitter(String oltId, int splitterNumber) {
        return repo.removeSplitter(oltId, splitterNumber);
    }

    public int getMaxSplitters() {
        return MAX_SPLITTERS;
    }

    public int getPortsPerSplitter() {
        return PORTS_PER_SPLITTER;
    }
public boolean checkPincode(Long pincode) {
    return repo.existsByPincode(pincode);
}

public int getAvailablePorts(int pincode) {
    return repo.getAvailablePorts(pincode);
}

public int getAvailablePortsByType(Long serviceAreaId, String oltType) {
        return repo.countAvailablePorts(serviceAreaId,oltType);
    }


public long[] assignAvailablePort(long pincode, String oltType) {
    return repo.assignAvailablePort(pincode, oltType);
}

/**
     * Allocate ONE available port for given service area and OLT type.
     * Throws exception if no port is available.
     */
public Long allocatePort(Long serviceAreaId, String oltType) {
        Long portId = repo.findAvailablePortId(serviceAreaId, oltType);
        if (portId == null) {
            throw new RuntimeException(
                "No available ports for OLT type: " + oltType
            );
        }
        repo.markPortAsAssigned(portId);
        return portId; // important for customer_connections
    }
    

    /**
     * Release an allocated port (ASSIGNED → AVAILABLE).
     */
public void releasePort(Long portId) {

        if (portId == null) {
            throw new IllegalArgumentException("Port ID cannot be null");
        }

        repo.markPortAsAvailable(portId);
    }
     public List<OltInventoryDTO> getByPincode(String pincode) {
        return repo.findOltsByPincode(pincode);
    }

    public List<String> getUniquePincodes() {
        return repo.findAllPincodes();
    }

    public InventoryDetails getInventoryDetails(String oltId) {
        return repo.findInventoryDetails(oltId);
    }

    public long[] assignAvailablePort(Long pincode, String oltType) {
        return repo.assignAvailablePort(pincode, oltType);
    }

}


   
    


    
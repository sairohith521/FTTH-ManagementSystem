package ftth.service;

import ftth.model.InventoryDetails;
import ftth.model.OLT;
import ftth.repository.InventoryRepository;

import java.util.List;

public class InventoryService {

    private static final int MAX_SPLITTERS = 3;
    private static final int PORTS_PER_SPLITTER = 3;

    private final InventoryRepository repo;

    public InventoryService() {
        repo = new InventoryRepository();
    }

    public List<OLT> getByPincode(String pin) {
        return repo.findOltsByPincode(pin);
    }

    public List<String> getUniquePincodes() {
        return repo.findAllPincodes();
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

    public InventoryDetails getInventoryDetails(String oltId) {
        return repo.findInventoryDetails(oltId);
    }

    public int getMaxSplitters() {
        return MAX_SPLITTERS;
    }

    public int getPortsPerSplitter() {
        return PORTS_PER_SPLITTER;
    }
    public boolean checkPincode(int pincode) {
    return repo.existsByPincode(pincode);
}

public int getAvailablePorts(int pincode) {
    return repo.getAvailablePorts(pincode);
}
}

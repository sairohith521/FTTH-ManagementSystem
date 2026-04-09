package ftth.service;

import ftth.model.OLT;
import ftth.repository.InventoryRepository;
import java.util.*;

public class InventoryService {

    private List<OLT> olts;
    private InventoryRepository repo;

    private static final int MAX_SPLITTERS = 3;

    public InventoryService() {
        repo = new InventoryRepository();
        olts = repo.loadAll();
    }

    public List<OLT> getByPincode(String pin) {
        List<OLT> res = new ArrayList<>();

        for (OLT o : olts) {
            if (o.getPincode().equals(pin)) res.add(o);
        }
        return res;
    }
    public List<String> getUniquePincodes() {

    Set<String> set = new HashSet<>();

    for (OLT o : olts) {
        set.add(o.getPincode());
    }

    return new ArrayList<>(set);
}

    public void addOLT(OLT olt) {
        olts.add(olt);
        repo.saveAll(olts);
    }

    public boolean removeOLT(String id) {

        Iterator<OLT> it = olts.iterator();

        while (it.hasNext()) {
            OLT o = it.next();

            if (o.getOltId().equals(id)) {

                if (o.isHasCustomer()) return false;

                it.remove();
                repo.saveAll(olts);
                return true;
            }
        }
        return false;
    }

    public boolean addSplitter(String id) {

        for (OLT o : olts) {
            if (o.getOltId().equals(id)) {

                if (o.getSplitterCount() >= MAX_SPLITTERS) return false;

                o.setSplitterCount(o.getSplitterCount() + 1);
                repo.saveAll(olts);
                return true;
            }
        }
        return false;
    }

    public boolean removeSplitter(String id) {

        for (OLT o : olts) {
            if (o.getOltId().equals(id)) {

                if (o.getSplitterCount() == 0) return false;

                o.setSplitterCount(o.getSplitterCount() - 1);
                repo.saveAll(olts);
                return true;
            }
        }
        return false;
    }

    // 🔥 ID generator
    public String generateOLTId(String type, String pin) {
        int count = 1;

        for (OLT o : olts) {
            if (o.getPincode().equals(pin)) count++;
        }

        return type + "-" + pin + "-" + count;
    }
}
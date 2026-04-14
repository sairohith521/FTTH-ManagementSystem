package ftth.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryDetails {

    private final OLT olt;
    private final List<Splitter> splitters = new ArrayList<>();

    public InventoryDetails(OLT olt) {
        this.olt = olt;
    }

    public OLT getOlt() {
        return olt;
    }

    public List<Splitter> getSplitters() {
        return Collections.unmodifiableList(splitters);
    }

    public void addSplitter(Splitter splitter) {
        splitters.add(splitter);
    }
}

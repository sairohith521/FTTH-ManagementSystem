package ftth.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Splitter {

    private final long splitterId;
    private final long oltDbId;
    private final int splitterNumber;
    private final boolean active;
    private final List<Port> ports = new ArrayList<>();

    public Splitter(long splitterId, long oltDbId, int splitterNumber, boolean active) {
        this.splitterId = splitterId;
        this.oltDbId = oltDbId;
        this.splitterNumber = splitterNumber;
        this.active = active;
    }

    public long getSplitterId() {
        return splitterId;
    }

    public long getOltDbId() {
        return oltDbId;
    }

    public int getSplitterNumber() {
        return splitterNumber;
    }

    public boolean isActive() {
        return active;
    }

    public List<Port> getPorts() {
        return Collections.unmodifiableList(ports);
    }

    public void addPort(Port port) {
        ports.add(port);
    }
}

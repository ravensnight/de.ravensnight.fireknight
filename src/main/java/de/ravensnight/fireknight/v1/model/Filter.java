package de.ravensnight.fireknight.v1.model;

import java.util.ArrayList;
import java.util.List;

import de.ravensnight.fireknight.common.PortSpec;

public class Filter {
    private final List<PortSpec> ports = new ArrayList<>();
    private String net = null;

    public void add(PortSpec p) {
        this.ports.add(p);
    }

    public List<PortSpec> getPorts() {
        return ports;
    }
    
    public String getNet() {
        return net;
    }
    
    public void setNet(String net) {
        this.net = net;
    }
            
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (net != null) {
            b.append(this.net);
        }

        if (this.ports.size() > 0) {
            b.append(":");
            b.append(String.valueOf(this.ports));
        }

        return b.toString();
    }
}

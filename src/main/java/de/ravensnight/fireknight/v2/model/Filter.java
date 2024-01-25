package de.ravensnight.fireknight.v2.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.ravensnight.fireknight.common.NamedProtocol;
import de.ravensnight.fireknight.common.PortSpec;

public class Filter {

    private final Set<NamedProtocol> protocols = new HashSet<>();
    private final List<PortSpec> srcPorts = new ArrayList<>();
    private final List<PortSpec> dstPorts = new ArrayList<>();

    private Net srcNet = null;
    private Net dstNet = null;

    Filter() {
    }

    void add(NamedProtocol p) {
        this.protocols.add(p);
    }

    void addSrcPort(PortSpec p) {
        this.srcPorts.add(p);
    }

    void addDstPort(PortSpec p) {
        this.dstPorts.add(p);
    }

    public Set<NamedProtocol> getProtocols() {
        return protocols;
    }

    public List<PortSpec> getSrcPorts() {
        return srcPorts;
    }

    public Net getSrcNet() {
        return srcNet;
    }

    public void setSrcNet(Net srcNet) {
        this.srcNet = srcNet;
    }

    public Net getDstNet() {
        return dstNet;
    }

    public void setDstNet(Net dstNet) {
        this.dstNet = dstNet;
    }

    public List<PortSpec> getDstPorts() {
        return dstPorts;
    }
    
}

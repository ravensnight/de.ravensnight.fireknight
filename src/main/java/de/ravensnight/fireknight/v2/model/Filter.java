package de.ravensnight.fireknight.v2.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.ravensnight.fireknight.common.NamedProtocol;
import de.ravensnight.fireknight.common.Clonable;
import de.ravensnight.fireknight.common.PortSpec;

public class Filter implements Clonable<Filter> {

    private final Set<NamedProtocol> protocols = new HashSet<>();
    private final List<PortSpec> srcPorts = new ArrayList<>();
    private final List<PortSpec> dstPorts = new ArrayList<>();

    private Net srcNet = null;
    private Net dstNet = null;

    public Filter() {
    }

    public void add(NamedProtocol p) {
        this.protocols.add(p);
    }

    public void addSrcPort(PortSpec p) {
        this.srcPorts.add(p);
    }

    public void addDstPort(PortSpec p) {
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

    @Override
    public Filter clone() {

        Filter clone = new Filter();
        clone.protocols.addAll(this.protocols);
        clone.dstNet = this.dstNet == null ? null : this.dstNet.clone();
        clone.srcNet = this.srcNet == null ? null : this.srcNet.clone();

        for (PortSpec p : this.srcPorts) {
            clone.srcPorts.add(p.clone());
        }

        for (PortSpec p : this.dstPorts) {
            clone.dstPorts.add(p.clone());
        }

        return clone;
    }
    
}

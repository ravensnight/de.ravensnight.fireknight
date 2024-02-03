package de.ravensnight.fireknight.v2.model;

import de.ravensnight.fireknight.common.PortSpec;

public class Modifier {

    private PortSpec srcPort = null;
    private PortSpec dstPort = null;

    private final String name;
    private Net srcNet = null;
    private Net dstNet = null;

    Modifier(String name) {
        this.name = name;
    }

    public void setSrcPort(PortSpec p) {
        this.srcPort = p;
    }

    public void setDstPort(PortSpec p) {
        this.dstPort = p;
    }

    public PortSpec getSrcPort() {
        return srcPort;
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

    public PortSpec getDstPort() {
        return dstPort;
    }

    public String getName() {
        return name;
    }

    public boolean isSNAT() {
        return (this.srcNet != null) || (this.srcPort != null);
    }

    public boolean isDNAT() {
        return (this.dstNet != null) || (this.dstPort != null);
    }
}

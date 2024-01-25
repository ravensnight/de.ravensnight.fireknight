package de.ravensnight.fireknight.v2.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.ravensnight.fireknight.common.AbstractBuilder;
import de.ravensnight.fireknight.common.NamedProtocol;
import de.ravensnight.fireknight.common.PortSpec;
import de.ravensnight.fireknight.util.Receiver;

public abstract class FilterHelper<T> extends AbstractBuilder<T> {

    private Net dstNet = null;
    private Net srcNet = null;
    private final Set<NamedProtocol> protocols = new HashSet<>();
    private final List<PortSpec> srcPorts = new ArrayList<>();
    private final List<PortSpec> dstPorts = new ArrayList<>();
        
    FilterHelper(Receiver<T> receiver) {
        super(receiver);
    }

    public FilterHelper<T> proto(String p) {
        this.protocols.add(NamedProtocol.valueOf(p));
        return this;
    }

    public NetBuilder srcNet() {
        return new NetBuilder(new Receiver<Net>() {

            @Override
            public void receive(Net object) {
                srcNet = object;
            }
            
        });
    }

    public NetBuilder dstNet() {
        return new NetBuilder(new Receiver<Net>() {

            @Override
            public void receive(Net object) {
                dstNet = object;
            }            
        });
    }

    public FilterHelper<T> srcPort(String port) {
        this.srcPorts.add(PortSpec.create(Integer.parseInt(port)));
        return this;
    }

    public FilterHelper<T> srcPortRange(String from, String to) {
        this.srcPorts.add(PortSpec.create(Integer.parseInt(from), Integer.parseInt(to)));
        return this;
    }

    public FilterHelper<T> dstPort(String port) {
        this.dstPorts.add(PortSpec.create(Integer.parseInt(port)));
        return this;
    }

    protected Net getSrcNet() {
        return srcNet;
    }

    protected Net getDstNet() {
        return dstNet;
    }

    protected Set<NamedProtocol> getProtocols() {
        return protocols;
    }

    protected List<PortSpec> getSrcPorts() {
        return srcPorts;
    }

    protected List<PortSpec> getDstPorts() {
        return dstPorts;
    }


}

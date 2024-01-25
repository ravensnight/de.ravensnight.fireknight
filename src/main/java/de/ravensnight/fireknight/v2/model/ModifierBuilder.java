package de.ravensnight.fireknight.v2.model;

import de.ravensnight.fireknight.common.Builder;
import de.ravensnight.fireknight.common.PortSpec;
import de.ravensnight.fireknight.util.Receiver;

public class ModifierBuilder implements Builder {

    private String name = null;
    private PortSpec srcPort = null;
    private PortSpec dstPort = null;
    private Net srcNet = null;
    private Net dstNet = null;
    
    private Receiver<Modifier> receiver;
    
    ModifierBuilder(Receiver<Modifier> receiver) {
        this.receiver = receiver;
    }

    public ModifierBuilder name(String name) {
        this.name = name;
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

    public ModifierBuilder srcPort(String port) {
        this.srcPort = PortSpec.create(Integer.parseInt(port));
        return this;
    }

    public ModifierBuilder dstPort(String port) {
        this.dstPort = PortSpec.create(Integer.parseInt(port));
        return this;
    }

    public void build() {
        Modifier m = new Modifier(this.name);

        m.setSrcNet(this.srcNet);
        m.setSrcPort(this.srcPort);
        m.setDstNet(this.dstNet);
        m.setDstPort(this.dstPort);

        this.receiver.receive(m);
    }

}

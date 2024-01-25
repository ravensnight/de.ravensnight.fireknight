package de.ravensnight.fireknight.v2.model;

import de.ravensnight.fireknight.common.AbstractBuilder;
import de.ravensnight.fireknight.util.Receiver;

public class InterfaceBuilder extends AbstractBuilder<InterfaceDef> {

    private String name = null;
    private Net net = null;

    InterfaceBuilder(Receiver<InterfaceDef> receiver) {
        super(receiver);
    }

    public void name(String name) {
        this.name = name;
    }

    public NetBuilder net() {
        return new NetBuilder(new Receiver<Net>() {
            @Override
            public void receive(Net object) {
                net = object;
            }            
        });
    }

    @Override
    protected InterfaceDef makeBean() {
        InterfaceDef d = new InterfaceDef(this.name);
        d.setNet(this.net);
        return d;
    }

}

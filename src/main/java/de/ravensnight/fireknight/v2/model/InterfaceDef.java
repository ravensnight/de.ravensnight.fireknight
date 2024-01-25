package de.ravensnight.fireknight.v2.model;

public class InterfaceDef {
    private final String name;
    private Net net;

    InterfaceDef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public Net getNet() {
        return net;
    }
    public void setNet(Net addr) {
        this.net = addr;
    }

    
}

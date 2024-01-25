package de.ravensnight.fireknight.v1.model;

import de.ravensnight.fireknight.common.PortSpec;

public class Address {
    private String  address = null;
    private PortSpec port = null;

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public PortSpec getPort() {
        return port;
    }
    public void setPort(PortSpec port) {
        this.port = port;
    }
}

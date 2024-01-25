package de.ravensnight.fireknight.v1.model;

import de.ravensnight.fireknight.common.AbstractBuilder;
import de.ravensnight.fireknight.common.NamedPort;
import de.ravensnight.fireknight.common.PortSpec;
import de.ravensnight.fireknight.util.Receiver;

public class AddressBuilder extends AbstractBuilder<Address> {

    private Address address = null;
    private final RuleScope scope;

    public AddressBuilder(RuleScope scope, Receiver<Address> receiver) {
        super(receiver);
        this.scope = scope;
        this.address = new Address();
    }

    public void addrDirect(String a) {
        this.address.setAddress(a);
    }

    public void addrRef(String a) {
        String addr = this.scope.getReference(a);
        this.address.setAddress(addr);
    }

    public void port(int p) {
        address.setPort(PortSpec.create(p));
    }

    public void port(String p) {
        NamedPort np = NamedPort.valueOf(p);
        address.setPort(PortSpec.create(np));
    }

    @Override
    protected Address makeBean() {
        return this.address;
    }
}

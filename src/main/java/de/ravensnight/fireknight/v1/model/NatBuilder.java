package de.ravensnight.fireknight.v1.model;

import de.ravensnight.fireknight.common.AbstractBuilder;
import de.ravensnight.fireknight.common.BuildException;
import de.ravensnight.fireknight.common.NamedProtocol;
import de.ravensnight.fireknight.util.Receiver;

public class NatBuilder extends AbstractBuilder<NatRule> implements ProtocolHandler {

    private NatRule result = null;
    private final RuleScope scope;

    NatBuilder(RuleScope scope, Receiver<NatRule> receiver) {
        super(receiver);
        this.scope = scope;
        result = new NatRule();
    }

    public AddressBuilder origin() {
        return new AddressBuilder(this.scope, new Receiver<Address>() {
            @Override
            public void receive(Address object) {
                result.setAddrOrigin(object);
            }
        });
    }    

    public AddressBuilder override() {
        return new AddressBuilder(this.scope, new Receiver<Address>() {
            @Override
            public void receive(Address object) {
                result.setAddrOverride(object);
            }            
        });
    }

    public void masquerade() {
        this.result.setAddrOverride(null);
    }

    public void dnat() {
        this.result.setPreroute(true);
    }

    public void snat() {
        this.result.setPreroute(false);
    }

    public FilterBuilder srcFilter() {
        return new FilterBuilder(this.scope, new Receiver<Filter>() {

            @Override
            public void receive(Filter object) {
                result.setSrcFilter(object);
            }            
        });
    }

    @Override
    public NatRule makeBean() throws BuildException {
        return this.result;
    }

    @Override
    public void protocol(String protocol) {
        if (protocol == null) return;
        
        NamedProtocol p = NamedProtocol.valueOf(protocol);        
        this.result.add(p);
    }

}

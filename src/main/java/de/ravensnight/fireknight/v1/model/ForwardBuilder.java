package de.ravensnight.fireknight.v1.model;

import de.ravensnight.fireknight.common.AbstractBuilder;
import de.ravensnight.fireknight.common.BuildException;
import de.ravensnight.fireknight.common.NamedProtocol;
import de.ravensnight.fireknight.util.Receiver;

public class ForwardBuilder extends AbstractBuilder<ForwardRule> implements ProtocolHandler {

    private ForwardRule fwd = null;
    private RuleScope scope;
    
    public ForwardBuilder(RuleScope scope, Receiver<ForwardRule> receiver) {
        super(receiver);
        this.scope = scope;
        this.fwd = new ForwardRule();
    }

    public FilterBuilder srcFilter() {
        return new FilterBuilder(scope, new Receiver<Filter>() {

            @Override
            public void receive(Filter object) {
                fwd.setSrcFilter(object);
            }            
        });
    }

    public void outInterface(String out) {
        this.fwd.setOutInterface(out);
    }

    @Override
    public ForwardRule makeBean() throws BuildException {
        return this.fwd;
    }

    @Override
    public void protocol(String protocol) {
        if (protocol == null) return;
        NamedProtocol p = NamedProtocol.valueOf(protocol);
        this.fwd.add(p);
    }
}

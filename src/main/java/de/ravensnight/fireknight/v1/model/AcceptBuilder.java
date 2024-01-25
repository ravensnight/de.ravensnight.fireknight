package de.ravensnight.fireknight.v1.model;

import de.ravensnight.fireknight.common.AbstractBuilder;
import de.ravensnight.fireknight.common.NamedProtocol;
import de.ravensnight.fireknight.util.Receiver;

public class AcceptBuilder extends AbstractBuilder<AcceptRule> implements ProtocolHandler {

    private AcceptRule rule = null;
    private final RuleScope scope;

    AcceptBuilder(RuleScope scope, Receiver<AcceptRule> receiver) {
        super(receiver);
        this.scope = scope;
        this.rule = new AcceptRule();
    }

    public void direction(Direction dir) {
        this.rule.setDirection(dir);
    }

    public void protocol(String protocol) {
        NamedProtocol p = NamedProtocol.valueOf(protocol);
        this.rule.add(p);
    }

    public FilterBuilder inner() {
        return new FilterBuilder(this.scope, new Receiver<Filter>() {

            @Override
            public void receive(Filter object) {
                rule.setInner(object);
            }        
        });
    }

    public FilterBuilder outer() {
        return new FilterBuilder(this.scope, new Receiver<Filter>() {

            @Override
            public void receive(Filter object) {
                rule.setOuter(object);
            }
        });
    }

    @Override
    protected AcceptRule makeBean() {
        return this.rule;
    }
}

package de.ravensnight.fireknight.v2.model;

import de.ravensnight.fireknight.common.AbstractBuilder;
import de.ravensnight.fireknight.util.Receiver;

public class RuleBuilder extends AbstractBuilder<Rule> {

    private Rule rule = null;

    RuleBuilder(Receiver<Rule> receiver, Rule.Type type) {
        super(receiver);
        this.rule = new Rule(type);
    }

    public RuleBuilder from(String intf) {
        this.rule.setIntFrom(intf);
        return this;
    }   

    public RuleBuilder to(String intf) {
        this.rule.setIntTo(intf);
        return this;
    }   

    public RuleBuilder service(String filter) {
        this.rule.setService(filter);
        return this;
    }

    public RuleBuilder modify(String modifier) {
        this.rule.setModifier(modifier);
        return this;
    }

    @Override
    protected Rule makeBean() {
        return this.rule;
    }
}

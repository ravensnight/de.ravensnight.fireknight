package de.ravensnight.fireknight.v1.model;

import java.util.HashMap;
import java.util.Map;

import de.ravensnight.fireknight.common.AbstractBuilder;
import de.ravensnight.fireknight.common.BuildException;
import de.ravensnight.fireknight.util.Receiver;

public class ScopeBuilder extends AbstractBuilder<Scope> implements RuleScope {

    private final Map<String, String> references = new HashMap<>();
    private Scope script;

    public ScopeBuilder(Receiver<Scope> receiver) {
        super(receiver);
        this.script = new Scope();
    }

    public void setInterface(String name) {
        this.script.setInterfaceName(name);
    }

    public void define(String name, String net) {
        this.references.put(name, net);
    }

    public AcceptBuilder accept() {
        return new AcceptBuilder(this, new Receiver<AcceptRule>() {

            @Override
            public void receive(AcceptRule object) {
                script.add(object);
            }            
        });
    }

    public ForwardBuilder forward() {
        return new ForwardBuilder(this, new Receiver<ForwardRule>() {

            @Override
            public void receive(ForwardRule object) {
                script.add(object);
            }            
        });
    }

    public NatBuilder nat() {
        return new NatBuilder(this, new Receiver<NatRule>() {

            @Override
            public void receive(NatRule object) {
                script.add(object);
            }            
        });
    }

    @Override
    public Scope makeBean() throws BuildException {
        return this.script;
    }

    @Override
    public String getInterface() {
        return this.script.getInterfaceName();
    }

    @Override
    public String getReference(String key) {
        return this.references.get(key);
    }
}

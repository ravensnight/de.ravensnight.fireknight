package de.ravensnight.fireknight.v2.model;

import de.ravensnight.fireknight.common.AbstractBuilder;
import de.ravensnight.fireknight.util.Receiver;
import de.ravensnight.fireknight.v2.model.Rule.Type;

public class ScriptBuilder extends AbstractBuilder<Script> {

    private Script script = null;

    public ScriptBuilder(Receiver<Script> receiver) {
        super(receiver);
        this.script = new Script();
    }

    public InterfaceBuilder intf() {
        return new InterfaceBuilder(new Receiver<InterfaceDef>() {

            @Override
            public void receive(InterfaceDef object) {
                script.add(object);
            }            
        });        
    }

    public ServiceBuilder service() {
        return new ServiceBuilder(new Receiver<Service>() {

            @Override
            public void receive(Service object) {
                script.add(object);
            }
            
        });
    }

    public ModifierBuilder modifier() {
        return new ModifierBuilder(new Receiver<Modifier>() {

            @Override
            public void receive(Modifier object) {
                script.add(object);
            }
            
        });
    }

    
    public RuleBuilder forward() {
        return new RuleBuilder(new Receiver<Rule>() {

            @Override
            public void receive(Rule object) {
                script.add(object);
            }
           
        }, Type.FORWARD);
    }

    public RuleBuilder provide() {
        return new RuleBuilder(new Receiver<Rule>() {

            @Override
            public void receive(Rule object) {
                script.add(object);
            }
           
        }, Type.PROVIDE);
    }

    public RuleBuilder consume() {
        return new RuleBuilder(new Receiver<Rule>() {

            @Override
            public void receive(Rule object) {
                script.add(object);
            }
           
        }, Type.CONSUME);
    }

    @Override
    protected Script makeBean() {
        return this.script;
    }
}

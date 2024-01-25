package de.ravensnight.fireknight.v1.model;

import de.ravensnight.fireknight.common.AbstractBuilder;
import de.ravensnight.fireknight.common.BuildException;
import de.ravensnight.fireknight.util.Receiver;

public class ScriptBuilder extends AbstractBuilder<Script> {

    private Script script;

    public ScriptBuilder(Receiver<Script> receiver) {
        super(receiver);
        this.script = new Script();
    }

    public ScopeBuilder scope() {
        return new ScopeBuilder(new Receiver<Scope>() {

            @Override
            public void receive(Scope object) {
                script.add(object);
            }            
        });
    }

    @Override
    public Script makeBean() throws BuildException {
        return this.script;
    }
}

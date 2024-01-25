package de.ravensnight.fireknight.v1.model;

import de.ravensnight.fireknight.common.AbstractBuilder;
import de.ravensnight.fireknight.common.BuildException;
import de.ravensnight.fireknight.common.NamedPort;
import de.ravensnight.fireknight.common.PortSpec;
import de.ravensnight.fireknight.util.Receiver;

public class FilterBuilder extends AbstractBuilder<Filter> {

    private final RuleScope scope;
    private Filter filter;

    public FilterBuilder(RuleScope scope, Receiver<Filter> receiver) {
        super(receiver);
        this.scope = scope;
        this.filter = new Filter();
    }

    @Override
    public Filter makeBean() throws BuildException {
        return this.filter;
    }

    public void portRange(int from, int to) {
        this.filter.add(PortSpec.create(from, to));
    }

    public void port(int num) {
        this.filter.add(PortSpec.create(num));
    }

    public void port(String name) {
        NamedPort p = NamedPort.valueOf(name);
        this.filter.add(PortSpec.create(p));
    }

    public void netDirect(String net) {
        this.filter.setNet(net);
    }

    public void netRef(String ref) {
        String net = this.scope.getReference(ref);
        this.filter.setNet(net);
    }
}


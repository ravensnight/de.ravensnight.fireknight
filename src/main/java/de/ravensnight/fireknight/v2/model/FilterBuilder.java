package de.ravensnight.fireknight.v2.model;

import de.ravensnight.fireknight.common.NamedProtocol;
import de.ravensnight.fireknight.common.PortSpec;
import de.ravensnight.fireknight.util.Receiver;

public class FilterBuilder extends FilterHelper<Filter> {
    
    FilterBuilder(Receiver<Filter> receiver) {
        super(receiver);
    }

    public Filter makeBean() {
        Filter m = new Filter();

        m.setSrcNet(getSrcNet());
        m.setDstNet(getDstNet());

        for (NamedProtocol p : getProtocols()) {
            m.add(p);
        }

        for (PortSpec d : getDstPorts()) {
            m.addDstPort(d);
        }

        for (PortSpec s : getSrcPorts()) {
            m.addSrcPort(s);
        }

        return m;
    }
}

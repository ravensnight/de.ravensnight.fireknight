package de.ravensnight.fireknight.v2.model;

import de.ravensnight.fireknight.common.NamedProtocol;
import de.ravensnight.fireknight.common.PortSpec;
import de.ravensnight.fireknight.util.Receiver;

public class ServiceBuilder extends FilterHelper<Service> {

    private String name = null;
    private Filter serverFilter = null;
    private Filter clientFilter = null;

    ServiceBuilder(Receiver<Service> receiver) {
        super(receiver);
    }

    public FilterBuilder server() {
        return new FilterBuilder(new Receiver<Filter>() {

            @Override
            public void receive(Filter object) {
                serverFilter = object;
            }
        });
    }

    public FilterBuilder client() {
        return new FilterBuilder(new Receiver<Filter>() {

            @Override
            public void receive(Filter object) {
                clientFilter = object;
            }
        });
    }

    public ServiceBuilder name(String name)
    {
        this.name = name;
        return this;
    }

    protected Filter clone(Filter source) {
        Filter res = new Filter();

        // clone protocols
        if ((source == null) || source.getProtocols().isEmpty()) {
            for (NamedProtocol p : getProtocols()) {
                res.add(p);
            }
        } else {
            for (NamedProtocol p : source.getProtocols()) {
                res.add(p);
            }
        }

        if ((source == null) || source.getSrcPorts().isEmpty()) {
            for (PortSpec port : getSrcPorts()) {
                res.addSrcPort(port);
            }
        } else {
            for (PortSpec port : source.getSrcPorts()) {
                res.addSrcPort(port);
            }
        }

        if ((source == null) || source.getDstPorts().isEmpty()) {
            for (PortSpec port : getDstPorts()) {
                res.addDstPort(port);
            }
        } else {
            for (PortSpec port : source.getDstPorts()) {
                res.addDstPort(port);
            }
        }

        if ((source == null) || (source.getDstNet() == null)) {
            res.setDstNet(getDstNet());
        } else {
            res.setDstNet(source.getDstNet());
        }

        if ((source == null) || (source.getSrcNet() == null)) {
            res.setSrcNet(getSrcNet());
        } else {
            res.setSrcNet(source.getSrcNet());
        }

        return res;
    }

    @Override
    protected Service makeBean() {
        Service s = new Service(this.name);

        s.setClientFilter(clone(this.clientFilter));
        s.setServerFilter(clone(this.serverFilter));

        return s;
    }

}

package de.ravensnight.fireknight.v1;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.ravensnight.fireknight.common.Action;
import de.ravensnight.fireknight.common.Chain;
import de.ravensnight.fireknight.common.Feature;
import de.ravensnight.fireknight.common.NamedProtocol;
import de.ravensnight.fireknight.common.PortSpec;
import de.ravensnight.fireknight.common.ShellScriptWriter;
import de.ravensnight.fireknight.util.Permutation;
import de.ravensnight.fireknight.util.Pair;
import de.ravensnight.fireknight.util.Receiver;
import de.ravensnight.fireknight.v1.model.AcceptRule;
import de.ravensnight.fireknight.v1.model.Address;
import de.ravensnight.fireknight.v1.model.Filter;
import de.ravensnight.fireknight.v1.model.ForwardRule;
import de.ravensnight.fireknight.v1.model.NatRule;
import de.ravensnight.fireknight.v1.model.Scope;
import de.ravensnight.fireknight.v1.model.Script;

public class IPTablesGenerator {

    private final ShellScriptWriter out;
    private final Set<Feature> features = new HashSet<>();
    private String suspectQueueName = "SUSPECT";

    public IPTablesGenerator(Writer writer) {
        this.out = new ShellScriptWriter(writer);
    }

    public void enable(Feature... feature) {
        for (Feature f : feature) {
            this.features.add(f);
        }        
    }

    public String getSuspectQueueName() {
        return suspectQueueName;
    }

    public void setSuspectQueueName(String suspectQueueName) {
        this.suspectQueueName = suspectQueueName;
    }

    public boolean isEnabled(Feature d) {
        return this.features.contains(d);
    }

    protected String param(String option, String value) {
        if (value == null) return "";
        return String.format(" %s %s", option, value);
    }

    protected String getSuspectQueue(Chain chain) {

        if (isEnabled(Feature.LOG_SUSPECT)) {
            switch (chain) {
                case INPUT:
                    return String.format("%s_INP", getSuspectQueueName());

                case OUTPUT:
                    return String.format("%s_OUT", getSuspectQueueName());

                case FORWARD:
                    return String.format("%s_FWD", getSuspectQueueName());
                
                default:
                    break;
            }
        } 

        return null;
    }



    protected void onInput(final String intf, final NamedProtocol p, final String states, Filter inner, Filter outer) throws IOException {
        final String outerNet = (outer == null) ? null : outer.getNet(); 
        final String innerNet = (inner == null) ? null : inner.getNet();
        List<PortSpec> outerPorts = null, innerPorts = null;

        if (inner != null) {
            innerPorts = inner.getPorts();
        }

        if (outer != null) {
            outerPorts = outer.getPorts();
        }

        Permutation<PortSpec> loop = Permutation.create(outerPorts, innerPorts);
        loop.loop(new Receiver<Pair<PortSpec,PortSpec>>() {

            @Override
            public void receive(Pair<PortSpec, PortSpec> pair) {
                try {
                    out.iptables(Chain.INPUT, p, states, intf, null, outerNet, pair.getElem1(), innerNet, pair.getElem2(), Action.ACCEPT, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }            
        });
    }

    protected void onOutput(final String intf, final NamedProtocol p, final String states, Filter inner, Filter outer) throws IOException {
        final String outerNet = (outer == null) ? null : outer.getNet(); 
        final String innerNet = (inner == null) ? null : inner.getNet();
        List<PortSpec> outerPorts = null, innerPorts = null;

        if (inner != null) {
            innerPorts = inner.getPorts();
        }

        if (outer != null) {
            outerPorts = outer.getPorts();
        }

        Permutation<PortSpec> loop = Permutation.create(innerPorts, outerPorts);
        loop.loop(new Receiver<Pair<PortSpec,PortSpec>>() {

            @Override
            public void receive(Pair<PortSpec, PortSpec> pair) {
                try {
                    out.iptables(Chain.OUTPUT, p, states, null, intf, innerNet, pair.getElem1(), outerNet, pair.getElem2(), Action.ACCEPT, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }            
        });
    }

    public void write(Script script) throws IOException {

        String ruleState = null;

        // Feature: Cleanup first
        cleanup();

        // Feature: Log Suspect
        setupSuspectLog();

        // Feature: States
        if (isEnabled(Feature.ACCEPT_ESTABLISHED_RELATED)) {
            ruleState = "NEW";
            out.newLine();
        }

        for (Scope s : script.getInterfaces()) {
            String intf = s.getInterfaceName();

            out.comment("Interface: " + intf);

            if (ruleState != null) {
                out.iptables(Chain.INPUT, null, "ESTABLISHED,RELATED", intf, null, null, null, null, null, Action.ACCEPT, null);
                out.iptables(Chain.OUTPUT, null, "ESTABLISHED,RELATED", null, intf, null, null, null, null, Action.ACCEPT, null);
            }

            for (AcceptRule r : s.getAccept()) {                           
                if (r.getProtocols().isEmpty()) {
                    handleAccept(intf, r, null, ruleState);
                } else {
                    for (NamedProtocol p : r.getProtocols()) {                
                        handleAccept(intf, r, p, ruleState);
                    }
                }
            }

            Set<String> outIntf = new HashSet<>();
            for (ForwardRule r : s.getForward()) {                           
                if (r.getProtocols().isEmpty()) {
                    handleForward(intf, r, null, ruleState);
                } else {
                    for (NamedProtocol p : r.getProtocols()) {                
                        handleForward(intf, r, p, ruleState);
                    }
                }    
                outIntf.add(r.getOutInterface());
            }

            if (ruleState != null) {
                for (String o : outIntf) {
                    if (intf.equals(o)) {
                        out.iptables(Chain.FORWARD, null, "ESTABLISHED,RELATED", intf, o, null, null, null, null, Action.ACCEPT, null);
                    } else {
                        out.iptables(Chain.FORWARD, null, "ESTABLISHED,RELATED", intf, o, null, null, null, null, Action.ACCEPT, null);
                        out.iptables(Chain.FORWARD, null, "ESTABLISHED,RELATED", o, intf, null, null, null, null, Action.ACCEPT, null);
                    }
                }
            }

            for (NatRule r : s.getNat()) {                           
                if (r.getProtocols().isEmpty()) {
                    handleNat(intf, r, null, ruleState);
                } else {
                    for (NamedProtocol p : r.getProtocols()) {                
                        handleNat(intf, r, p, ruleState);
                    }
                }
            }

            out.newLine();
        }

        // finally route packages to destination
        out.comment("Route suspect packages");
        for (Chain c : Chain.values()) {
            String q = getSuspectQueue(c);
            if (q == null) continue;

            out.command("iptables", String.format("-A %s -j %s", c.name(), q));
        }

        out.flush();
    }

    private void handleAccept(String intf, AcceptRule r, NamedProtocol p, String ruleState) throws IOException {
        switch (r.getDirection()) {
            case IN:
                onInput(intf, p, ruleState, r.getInner(), r.getOuter());
                break;

            case OUT:
                onOutput(intf, p, ruleState, r.getInner(), r.getOuter());
                break;

            case BOTH:
                onInput(intf, p, ruleState, r.getInner(), r.getOuter());
                onOutput(intf, p, ruleState, r.getInner(), r.getOuter());
                break;

            default:
                break;
        }
    }

    private void handleForward(String intf, ForwardRule r, NamedProtocol p, String ruleState) throws IOException {

        String net = null;
        List<PortSpec> ports = null;

        if (r.getSrcFilter() != null) {
            net = r.getSrcFilter().getNet();
            ports = r.getSrcFilter().getPorts();
        }

        if ((ports == null) || (ports.size() == 0)) {
            out.iptables(Chain.FORWARD, p, ruleState, intf, r.getOutInterface(), null, null, net, null, Action.ACCEPT, null);
        } else {
            for (PortSpec port : ports) {
                out.iptables(Chain.FORWARD, p, ruleState, intf, r.getOutInterface(), null, null, net, port, Action.ACCEPT, null);
            }
        }
    }

    private void handleNat(String intf, NatRule r, NamedProtocol p, String ruleState) throws IOException {

        String srcNet = null;
        List<PortSpec> srcPorts = null;

        if (r.getSrcFilter() != null) {
            srcPorts = r.getSrcFilter().getPorts();
            srcNet = r.getSrcFilter().getNet();
        } 

        if ((srcPorts == null) || (srcPorts.size() == 0)) {
            if (r.isPreroute()) {   // DNAT
                dnat(intf, p, ruleState, srcNet, null, r.getAddrOrigin(), r.getAddrOverride());
            } else {
                snat(intf, p, ruleState, srcNet, null, r.getAddrOverride());
            }
        }

    }

    private void dnat(String intfIn, NamedProtocol p, String ruleState, String srcNet, PortSpec srcPort, Address addrOrigin, Address addrOverride) throws IOException {
        StringBuilder ext = new StringBuilder();
    
        String dstAddr = null;
        PortSpec dstPort = null;

        if (addrOrigin != null) {
            dstAddr = addrOrigin.getAddress();
            dstPort = addrOrigin.getPort();
        }

        if (addrOverride != null) {
            ext.append(" --to-destination ");

            if (addrOverride.getAddress() != null) {
                ext.append(addrOverride.getAddress());
            }

            if (addrOverride.getPort() != null) {
                ext.append(":");
                ext.append(addrOverride.getPort().getFrom());
            }
        }

        out.iptables(Chain.PREROUTING, p, ruleState, intfIn, null, srcNet, srcPort, dstAddr, dstPort, Action.DNAT, ext.toString());

    }

    private void snat(String intfOut, NamedProtocol p, String ruleState, String srcNet, PortSpec srcPort, Address addrOverride) throws IOException {
        StringBuilder ext = new StringBuilder();
        Action action = Action.SNAT;

        if (addrOverride == null) {
            action = Action.MASQUERADE;
        } else {
            ext.append(" --to-source ");
            if (addrOverride.getAddress() != null) {
                ext.append(addrOverride.getAddress());
            }

            if (addrOverride.getPort() != null) {
                ext.append(":");
                ext.append(addrOverride.getPort().getFrom());
            }            
        }

        out.iptables(Chain.POSTROUTING, p, ruleState, null, intfOut, srcNet, srcPort, null, null, action, ext.toString());
    }

    private void setupSuspectLog() throws IOException {
        if (isEnabled(Feature.LOG_SUSPECT)) {
            out.comment("Setup suspect queues");

            for (Chain c : Chain.values()) {
                String q = getSuspectQueue(c);
                if (q == null) continue;

                out.command("iptables", String.format("-N %s", q));
                out.command("iptables", String.format("-A %s -m limit --limit 20/min -j LOG --log-prefix \"[IPT:%s]\" --log-level 4", q, q));

                if (isEnabled(Feature.DROP_SUSPECT)) {
                    out.command("iptables", String.format("-A %s -j DROP", q));
                }
            }       
            
            out.newLine();
        }
    }

    private void cleanup() throws IOException {
        if (isEnabled(Feature.CLEANUP)) {
            out.comment("Initial cleanup");

            out.command("iptables", "-t nat -F");
            out.command("iptables", "-t mangle -F");
            out.command("iptables", "-F");
            out.command("iptables", "-X");

            out.newLine();
        }
    }
}

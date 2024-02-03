package de.ravensnight.fireknight.v2;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.ravensnight.fireknight.util.Pair;
import de.ravensnight.fireknight.util.Permutation;
import de.ravensnight.fireknight.util.Receiver;
import de.ravensnight.fireknight.v2.model.Filter;
import de.ravensnight.fireknight.v2.model.FilterBuilder;
import de.ravensnight.fireknight.v2.model.InterfaceDef;
import de.ravensnight.fireknight.v2.model.Modifier;
import de.ravensnight.fireknight.v2.model.Net;
import de.ravensnight.fireknight.v2.model.Rule;
import de.ravensnight.fireknight.v2.model.Script;
import de.ravensnight.fireknight.v2.model.Service;
import de.ravensnight.fireknight.common.Action;
import de.ravensnight.fireknight.common.Chain;
import de.ravensnight.fireknight.common.Feature;
import de.ravensnight.fireknight.common.NamedProtocol;
import de.ravensnight.fireknight.common.PortSpec;
import de.ravensnight.fireknight.common.ShellScriptWriter;

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

    protected String getLogQueue(Chain chain) {

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

    protected String getDropQueue(Chain chain) {

        if (isEnabled(Feature.DROP_SUSPECT)) {
            switch (chain) {
                case INPUT:
                case OUTPUT:
                case FORWARD:
                    return "DROP";                

                default:
                    break;
            }
        }

        return null;
    }

    private static Net resolve(Script script, Net net, String intfFrom, String intfTo) {

        if ((net == null) || (net.isDefault())) return net;
        
        InterfaceDef from = script.getInterfaceDef(intfFrom);
        InterfaceDef to = script.getInterfaceDef(intfTo);

        Net result = null;

        switch (net.getType()) {
            case INTF_IN_ADDR:
                if (from == null) return null;
                result = from.getNet().addrOnly();
                break;

            case INTF_OUT_ADDR:
                if (to == null) return null;
                result = to.getNet().addrOnly();
                break;

            case INTF_IN_NET:
                if (from == null) return null;
                result = from.getNet().clone();
                break;

            case INTF_OUT_NET: 
                if (to == null) return null;
                result = to.getNet().clone();
                break;

            case MASQUERADE:
            default:
                break;
        }

        if (result != null) {
            result.setInvert(net.invert());
        }

        return result;
    }

    private void handleFilter(Script script, final String from, final String to, Filter filter, final String ruleState, final Chain chain) throws IOException {

        List<PortSpec> srcPorts = null, dstPorts = null;;
        final Net srcNet, dstNet;
        Set<NamedProtocol> proto = null;

        if (filter != null) {
            proto = filter.getProtocols();
            srcPorts = filter.getSrcPorts();
            srcNet = resolve(script, filter.getSrcNet(), from, to);
            dstPorts = filter.getDstPorts();
            dstNet = resolve(script, filter.getDstNet(), from, to);
        } else {
            srcNet = null;
            dstNet = null;
        }

        Permutation<PortSpec> perm = Permutation.create(srcPorts, dstPorts);

        if ((proto == null) || proto.isEmpty()) {
            perm.loop(new Receiver<Pair<PortSpec,PortSpec>>() {

                @Override
                public void receive(Pair<PortSpec, PortSpec> object) {
                    try {   
                        out.iptables(chain, null, ruleState, from, to, srcNet, object.getElem1(), dstNet, object.getElem2(), Action.ACCEPT, null);
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }
                
            });
        } else {
            for (final NamedProtocol p : proto) {
                perm.loop(new Receiver<Pair<PortSpec,PortSpec>>() {

                    @Override
                    public void receive(Pair<PortSpec, PortSpec> object) {
                        try {   
                            out.iptables(chain, p, ruleState, from, to, srcNet, object.getElem1(), dstNet, object.getElem2(), Action.ACCEPT, null);
                        } catch (IOException e) {
                            // ignore.
                        }
                    }
                    
                });
            }
        }
    }


    private void handleRule(Script s, Rule r, String ruleState) throws IOException, GeneratorException {
        String intFrom = r.getIntFrom();
        String intTo = r.getIntTo();    
        String service = r.getService();
        String state = ruleState;
        
        Service svc = s.getService(service);
        if (svc == null) {
            throw new GeneratorException(String.format("Service with name %s not defined.", service));
        }

        Filter f = null;
        Chain transportChain = null;

        // created filters
        switch (r.getType()) {
            case CONSUME:
                f = svc.getClientFilter();
                transportChain = Chain.OUTPUT;
                break;

            case FORWARD:
                f = svc.getServerFilter();
                transportChain = Chain.FORWARD;
                state = null;
                break;

            case PROVIDE:
                f = svc.getServerFilter();
                transportChain = Chain.INPUT;
                break;

            default:
                throw new GeneratorException("Unknown Rule type " + r.getType().name());
        }

        // create nat
        if (r.getModifier() != null) {
            Modifier m = s.getModifier(r.getModifier());
            if (m != null) {            
                if (m.isDNAT()) {
                    
                    // Build a new filter in case there is some PREROUTING modifier
                    Filter n = f.clone();
                    if (m.getDstNet() != null) n.setDstNet(m.getDstNet());
                    if (m.getDstPort() != null) {
                        n.getDstPorts().clear();
                        n.addDstPort(m.getDstPort());
                    }

                    handleNat(s, intFrom, intTo, f, m.getDstNet(), m.getDstPort(), Chain.PREROUTING);
                    f = n;
                }
        
                if (m.isSNAT()) {
                    handleNat(s, intFrom, intTo, f, m.getSrcNet(), m.getSrcPort(), Chain.POSTROUTING);
                }
            }
        }        
    
        // handle filter
        handleFilter(s, intFrom, intTo, f, state, transportChain);
    }

    private void handleNat(Script s, final String intFrom, final String intTo, Filter f, final Net newNet, final PortSpec newPort, final Chain chain) {

        Net net = resolve(s, newNet, intFrom, intTo);        
        final Net srcNet, dstNet;
        final Action action;
        final String ext;

        List<PortSpec> srcPorts = null, dstPorts = null;
        Set<NamedProtocol> protocols = null;

        if (chain == Chain.POSTROUTING) {
            if (newNet.isMasquerade()) {
                action = Action.MASQUERADE;
                ext = null;
            } else {
                action = Action.SNAT;
                StringBuilder b = new StringBuilder();
                b.append("--to ");
                b.append(net.addrOnly().toString());
                if (newPort != null) {
                    b.append(":");
                    b.append(newPort.getFrom());
                }

                ext = b.toString();
            }
        }
        else if (chain == Chain.PREROUTING) {
            action = Action.DNAT;

            StringBuilder b = new StringBuilder();
            b.append("--to-destination ");
            b.append(net.addrOnly().toString());
            if (newPort != null) {
                b.append(":");
                b.append(newPort.getFrom());
            }

            ext = b.toString();
        } 
        else {
            return;
        }

        if (f != null) {
            srcNet = resolve(s, f.getSrcNet(), intFrom, intTo);
            dstNet = resolve(s, f.getDstNet(), intFrom, intTo);
            srcPorts = f.getSrcPorts();
            dstPorts = f.getDstPorts();
            protocols = f.getProtocols();
        } else {
            srcNet = null;
            dstNet = null;
        }

        Permutation<PortSpec> perm = Permutation.create(srcPorts, dstPorts);

        if ((protocols == null) || (protocols.size() == 0)) {
            perm.loop(new Receiver<Pair<PortSpec, PortSpec>>() {

                @Override
                public void receive(Pair<PortSpec, PortSpec> pair) {
                    try {
                        out.iptables(chain, null, null, intFrom, intTo, srcNet, pair.getElem1(), dstNet, pair.getElem2(), action, ext);
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }
                
            });

        } else {
            for (final NamedProtocol p : protocols) {
                perm.loop(new Receiver<Pair<PortSpec, PortSpec>>() {

                    @Override
                    public void receive(Pair<PortSpec, PortSpec> pair) {
                        try {
                            out.iptables(chain, p, null, intFrom, intTo, srcNet, pair.getElem1(), dstNet, pair.getElem2(), action, ext);
                        } catch (IOException e) {
                            e.printStackTrace(System.err);
                        }
                    }                    
                });
            }
        }
        
    }

    public void write(Script script) throws IOException, GeneratorException {

        String ruleState = null;

        this.out.setSudo(this.isEnabled(Feature.SUDO));
        this.out.setEcho(this.isEnabled(Feature.ECHO));

        // Feature: Cleanup first
        cleanup();

        // Feature: Log Suspect
        setupSuspectLog();

        // Feature: States
        if (isEnabled(Feature.ACCEPT_ESTABLISHED_RELATED)) {
            out.comment("Accept ESTABLISHED/RELATED traffic");

            ruleState = "NEW";
            out.iptables(Chain.INPUT, null, "ESTABLISHED,RELATED", null, null, null, null, null, null, Action.ACCEPT, null);
            out.iptables(Chain.OUTPUT, null, "ESTABLISHED,RELATED", null, null, null, null, null, null, Action.ACCEPT, null);
            out.iptables(Chain.FORWARD, null, "ESTABLISHED,RELATED", null, null, null, null, null, null, Action.ACCEPT, null);            
            out.newLine();
        }

        out.comment("Append rules to relevant queues");
        for (Rule r : script.getRules()) {
            handleRule(script, r, ruleState);
        }

        // finally route packages to destination
        out.comment("Route suspect packages");
        for (Chain c : Chain.values()) {
            String q = getLogQueue(c);
            if (q == null) {
                q = getDropQueue(c);
            }

            if (q != null) {
                out.command("iptables", String.format("-A %s -j %s", c.name(), q));
            }
        }

        out.flush();
    }

    private void setupSuspectLog() throws IOException {
        if (isEnabled(Feature.LOG_SUSPECT)) {
            out.comment("Setup suspect queues");

            for (Chain c : Chain.values()) {
                String q = getLogQueue(c);
                if (q == null) continue;

                out.command("iptables", String.format("-N %s", q));
                out.command("iptables", String.format("-A %s -m limit --limit 20/min -j LOG --log-prefix \"[IPT:%s]\" --log-level 4", q, q));

                String d = getDropQueue(c);
                if (d != null) {
                    out.command("iptables", String.format("-A %s -j %s", q, d));
                } else {
                    out.command("iptables", String.format("-A %s -j ACCEPT", q));
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

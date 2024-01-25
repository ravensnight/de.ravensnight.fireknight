package de.ravensnight.fireknight.v1.model;

import java.util.ArrayList;
import java.util.List;

public class Scope {

    private final List<AcceptRule> accept = new ArrayList<>();
    private final List<ForwardRule> forward = new ArrayList<>();
    private final List<NatRule> nat = new ArrayList<>();
    private String interfaceName = null;

    /**
     * Append a new rule.
     * @param rule
     */
    void add(AcceptRule rule) {
        this.accept.add(rule);
    }

    void add(ForwardRule rule) {
        this.forward.add(rule);
    }

    void add(NatRule rule) {
        this.nat.add(rule);
    }

    public List<AcceptRule> getAccept() {
        return this.accept;
    }

    public List<ForwardRule> getForward() {
        return this.forward;
    }

    public List<NatRule> getNat() {
        return this.nat;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

}

package de.ravensnight.fireknight.v2.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Script {

    private final Map<String, InterfaceDef> interfaces = new HashMap<>();
    private final Map<String, Service> services = new HashMap<>();
    private final Map<String, Modifier> modifiers = new HashMap<>();

    private final List<Rule> chains = new ArrayList<>();

    void add(InterfaceDef intf) {
        this.interfaces.put(intf.getName(), intf);
    }

    void add(Service service) {
        this.services.put(service.getName(), service);
    }

    void add(Modifier modifier) {
        this.modifiers.put(modifier.getName(), modifier);
    }

    void add(Rule rule) {
        this.chains.add(rule);
    }

    public List<Rule> getRules() {
        return this.chains;
    }

    public Set<String> getFilterNames() {
        return this.services.keySet();
    }

    public Service getService(String name) {
        return this.services.get(name);
    }

    public Set<String> getModifierNames() {
        return this.modifiers.keySet();
    }

    public Modifier getModifier(String name) {
        return this.modifiers.get(name);
    }

    public Set<String> getInterfaceNames() {
        return this.interfaces.keySet();
    }

    public InterfaceDef getInterfaceDef(String name) {
        return this.interfaces.get(name);
    }
 }

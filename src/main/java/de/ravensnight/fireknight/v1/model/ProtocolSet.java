package de.ravensnight.fireknight.v1.model;

import java.util.HashSet;
import java.util.Set;

import de.ravensnight.fireknight.common.NamedProtocol;

public class ProtocolSet {
    private final Set<NamedProtocol> protocols = new HashSet<>();

    public void add(NamedProtocol p ) {
        this.protocols.add(p);
    }

    public Set<NamedProtocol> getProtocols() {
        return this.protocols;
    }
}

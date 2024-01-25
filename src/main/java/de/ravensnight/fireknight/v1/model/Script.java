package de.ravensnight.fireknight.v1.model;

import java.util.ArrayList;
import java.util.List;

public class Script {

    private final List<Scope> scopes = new ArrayList<>();

    public void add(Scope object) {
        this.scopes.add(object);
    }

    public List<Scope> getInterfaces() {
        return this.scopes;
    }
}

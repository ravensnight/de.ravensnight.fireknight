package de.ravensnight.fireknight.v2.model;

import java.util.ArrayList;
import java.util.List;

public class Rule {

    public enum Type {
        PROVIDE, CONSUME, FORWARD
    }

    private String intFrom = null;
    private String intTo = null;
    private String service = null;
    private String modifier = null;

    private final Type type;
    
    Rule(Type type) {
        this.type = type;
    }

    public String getIntFrom() {
        return intFrom;
    }
    public void setIntFrom(String intFrom) {
        this.intFrom = intFrom;
    }
    public String getIntTo() {
        return intTo;
    }    
    public void setIntTo(String intTo) {
        this.intTo = intTo;
    }
    public String getService() {
        return service;
    }
    public void setService(String service) {
        this.service = service;
    }
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
    
    public String getModifier() {
        return this.modifier;
    }

    public Type getType() {
        return type;
    }
}



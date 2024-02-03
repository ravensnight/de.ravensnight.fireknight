package de.ravensnight.fireknight.common;

public class PortSpec implements Invertable, Clonable<PortSpec> {

    private final int from;
    private final int to;
    private boolean invert = false;
    
    private PortSpec(int p1, int p2) {
        this.from = p1;
        this.to = p2;
    }

    public void setInvert(boolean v) {
        this.invert = v;
    }

    public static PortSpec create(int from, int to) {
        return new PortSpec(from, to);
    }

    public static PortSpec create(int single) {
        return new PortSpec(single, single);
    }    

    public static PortSpec create(NamedPort named) {
        return create(named.getPort());
    }

    public boolean isRange() {
        return this.from != this.to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    @Override
    public String toString() {
        if (isRange()) {
            return String.format("%d:%d", this.from, this.to);
        } else {
            return String.format("%d", this.from);
        }
    }

    @Override
    public boolean invert() {
        return this.invert;
    }

    public PortSpec clone() {
        PortSpec p = new PortSpec(this.from, this.to);
        p.invert = this.invert;
        
        return p;
    }
}
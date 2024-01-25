package de.ravensnight.fireknight.v2.model;

import de.ravensnight.fireknight.common.AbstractBuilder;
import de.ravensnight.fireknight.util.Receiver;
import de.ravensnight.fireknight.v2.model.Net.Type;

public class NetBuilder extends AbstractBuilder<Net> {

    private String addr = null;
    private Integer mask = null;
    private Net.Type type = Type.DEFAULT;
    private boolean invert = false;

    public NetBuilder(Receiver<Net> receiver) {
        super(receiver);
    }

    public NetBuilder addr(String addr) {
        this.addr = addr;   
        this.type  = Type.DEFAULT;     
        return this;
    }

    public NetBuilder mask(String mask) {
        if (mask == null) this.mask = null;
        this.mask = Integer.parseInt(mask);
        this.type = Type.DEFAULT;
        return this;
    }

    public NetBuilder intfInNet() {
        this.type = Type.INTF_IN_NET;
        return this;
    }

    public NetBuilder intfInAddr() {
        this.type = Type.INTF_IN_ADDR;
        return this;
    }

    public NetBuilder intfOutNet() {
        this.type = Type.INTF_OUT_NET;
        return this;
    }

    public NetBuilder intfOutAddr() {
        this.type = Type.INTF_OUT_ADDR;
        return this;
    }

    public NetBuilder not() {
        this.invert = true;
        return this;
    }

    public NetBuilder masquerade() {
        this.type = Type.MASQUERADE;
        return this;
    }

    @Override
    protected Net makeBean() {
        Net n = null;
        if (this.type == Type.DEFAULT) {
            try {
                n = Net.parse(addr);                
                n.setMask(this.mask);
            } catch (NetParseException e) {
                return null;
            }
        } else {
            n = Net.create(this.type);
        }

        if (this.type != Type.MASQUERADE) {
            n.setInvert(this.invert);
        }
        return n;
    }

}

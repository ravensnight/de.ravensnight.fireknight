package de.ravensnight.fireknight.v2.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ravensnight.fireknight.common.Invertable;

public class Net implements Invertable {

    private static Pattern regex = Pattern.compile("([1-2]?[0-9]?[0-9])\\.([1-2]?[0-9]?[0-9])\\.([1-2]?[0-9]?[0-9])\\.([1-2]?[0-9]?[0-9])(/[1-3]?[0-9])?");

    public enum Type {
        DEFAULT,
        MASQUERADE,
        INTF_IN_NET,
        INTF_IN_ADDR,
        INTF_OUT_NET,
        INTF_OUT_ADDR
    }

    private final Long addr;
    private Integer mask;
    private final Type type;
    private boolean invert = false;

    Net(long addr, Integer mask) {
        this.addr = addr;
        this.mask = mask;
        this.type = Type.DEFAULT;
    }

    Net(Type type) {
        this.addr = null;
        this.mask = null;
        this.type = type;
    }

    public static Net parse(String value) throws NetParseException {
        if (value == null) {
            throw new NetParseException("Net value must not be NULL");
        }

        Matcher m = regex.matcher(value);
        if (!m.matches()) {
            throw new NetParseException("Value is not a network definiton: " + value);
        }

        int s1 = Integer.parseInt(m.group(1));
        int s2 = Integer.parseInt(m.group(2));
        int s3 = Integer.parseInt(m.group(3));
        int s4 = Integer.parseInt(m.group(4));

        String val = m.group(5);
        Integer mask = null;
        if (val != null) mask = Integer.parseInt(val.substring(1));

        long net = 0;
        net |= (s1 << 24);
        net |= (s2 << 16);
        net |= (s3 << 8);
        net |= s4;

        return new Net(net, mask);
    }

    public static Net create(Type ref) {
        return new Net(ref);
    }

    public void setInvert(boolean val) {
        this.invert = val;
    }

    public boolean invert() {
        return this.invert;
    }

    public boolean isRef() {
        switch (this.type) {
            case DEFAULT:
            case MASQUERADE:
                return false;

            default:
                return true;
        }
    }

    public boolean isDefault() {
        return this.type == Type.DEFAULT;
    }

    public boolean isMasquerade() {
        return this.type == Type.MASQUERADE;
    }

    public Type getType() {
        return this.type;
    }

    public Long getAddr() {
        return addr;
    }

    public void setMask(Integer m) {
        this.mask = m;
    }

    public Integer getMask() {
        return mask;
    }

    public Net addrOnly() {
        return new Net(this.getAddr(), null);
    }

    public Net clone() {
        if (this.isRef()) {
            return new Net(this.type);
        } else {
            return new Net(this.getAddr(), this.getMask());
        }
    }

    public static String toAddress(long value) {
        StringBuilder b = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            int v = (int)((value >> (i * 8)) & 0xFF);
            b.append(v);
            if (i > 0) {
                b.append(".");
            }
        }

        return b.toString();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        if (this.isDefault()) {
            if (this.mask == null) {
                b.append(toAddress(this.addr));
            } else {
                long a = 0;
                int zero = 32-mask;
                for (int i = 0; i < 32; i++) {
                    if (i >= zero) {
                        a |= (1 << i);
                    }
                }

                b.append(toAddress(this.addr & a));
                b.append("/");
                b.append(this.mask);
            }
        } else {
            b.append(this.type.name());
        }

        return b.toString();
    }
}

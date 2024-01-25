package de.ravensnight.fireknight.v2.model;

public class NetParseException extends Exception {
    public NetParseException(String m) {
        super(m);
    }

    public NetParseException(String m, Throwable t) {
        super(m, t);
    }
}

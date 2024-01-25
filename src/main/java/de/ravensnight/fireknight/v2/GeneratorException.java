package de.ravensnight.fireknight.v2;

public class GeneratorException extends Exception {
    public GeneratorException(String msg) {        
        super(msg);
    }

    public GeneratorException(String msg, Throwable t) {
        super(msg, t);
    }

}

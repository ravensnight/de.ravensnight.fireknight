package de.ravensnight.fireknight.common;

public class BuildException extends RuntimeException {
    public BuildException(String msg) {
        super(msg);
    }

    public BuildException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

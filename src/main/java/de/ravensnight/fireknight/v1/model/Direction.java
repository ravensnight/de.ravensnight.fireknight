package de.ravensnight.fireknight.v1.model;

public enum Direction {
    IN, OUT, BOTH;

    @Override
    public String toString() {
        return this.name();
    }
}

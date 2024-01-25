package de.ravensnight.fireknight.v1.model;

import java.util.HashSet;
import java.util.Set;

import de.ravensnight.fireknight.common.NamedProtocol;

public class AcceptRule extends ProtocolSet {
    private Direction direction = Direction.IN;
    private Filter inner = null;
    private Filter outer = null;
    private final Set<NamedProtocol> protocols = new HashSet<>();

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Filter getInner() {
        return inner;
    }

    public void setInner(Filter inner) {
        this.inner = inner;
    }

    public Filter getOuter() {
        return outer;
    }

    public void setOuter(Filter outer) {
        this.outer = outer;
    }

    @Override
    public String toString() {
        String tag = String.format("?%s?", this.protocols);

        switch (this.direction) {
            case IN:
                tag = String.format("<%s-", this.protocols); break;

            case OUT:
                tag = String.format("-%s>", this.protocols); break;

            case BOTH:
                tag = String.format("<%s>", this.protocols); break;

            default:
                break;
        }

        return String.format("%s %s %s", inner == null ? "*" : inner, tag, outer == null ? "*" : outer);
    }
}

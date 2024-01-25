package de.ravensnight.fireknight.util;

public class ParamBuilder {

    private final Object key;
    private Character shortKey = null;
    private String longKey = null;
    private boolean flag = false;
    private boolean required = false;
    private String description = null;

    private final ParamReader target;

    public ParamBuilder(Object key, ParamReader target) {
        this.key = key;
        this.target = target;
    }

    public ParamBuilder flag() {
        this.flag = true;
        return this;
    }

    public ParamBuilder description(String descr) {
        this.description = descr;
        return this;
    }

    public ParamBuilder param() {
        this.flag = false;
        return this;
    }

    public ParamBuilder required() {
        this.required = true;
        return this;
    }

    public ParamBuilder optional() {
        this.required = false;
        return this;
    }

    public ParamBuilder shortKey(char shortKey) {
        this.shortKey = shortKey;
        return this;
    }

    public ParamBuilder longKey(String longKey) {
        this.longKey = longKey;
        return this;
    }

    public void build() {
        this.target.register(this.key, this.shortKey, this.longKey, this.flag, this.required, this.description);
    }
}

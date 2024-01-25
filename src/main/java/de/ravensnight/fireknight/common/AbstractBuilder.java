package de.ravensnight.fireknight.common;

import de.ravensnight.fireknight.util.Receiver;

public abstract class AbstractBuilder<T> implements Builder {

    private final Receiver<T> receiver;

    protected AbstractBuilder(Receiver<T> receiver) {
        this.receiver = receiver;
    }

    @Override
    public void build() throws BuildException {
        T object = makeBean();
        this.receiver.receive(object);
    }

    protected abstract T makeBean();
}

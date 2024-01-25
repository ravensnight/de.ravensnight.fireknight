package de.ravensnight.fireknight.util;

import java.util.Collection;

public class Permutation<T> {
    
    private final Collection<T> l1;
    private final Collection<T> l2;

    private Permutation(Collection<T> l1, Collection<T> l2) {
        this.l1 = l1;
        this.l2 = l2;
    }

    public static <T> Permutation<T> create(Collection<T> l1, Collection<T> l2) {
        return new Permutation<T>(l1, l2);
    }

    protected void loop2(T elem1, Receiver<Pair<T, T>> handler) {
        if ((this.l2 == null) || (this.l2.isEmpty())) {
            handler.receive(new Pair<T,T>(elem1, null));
        } else {
            for (T elem2 : this.l2) {
                handler.receive(new Pair<T,T>(elem1, elem2));
            }
        }
    }

    public void loop(Receiver<Pair<T, T>> handler) {
        if ((this.l1 == null) || (this.l1.isEmpty())) {
            loop2(null, handler);
        } else {
            for (T elem1 : this.l1) {
                loop2(elem1, handler);
            }
        }
    }
}

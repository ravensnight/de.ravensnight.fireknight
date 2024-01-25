package de.ravensnight.fireknight.util;

public class Pair<S,T> {
    private S elem1 = null;
    private T elem2 = null;

    public Pair() {}

    public Pair(S elem1, T elem2) {
        this.elem1 = elem1;
        this.elem2 = elem2;
    }

    public S getElem1() {
        return elem1;
    }
    public void setElem1(S elem1) {
        this.elem1 = elem1;
    }
    public T getElem2() {
        return elem2;
    }
    public void setElem2(T elem2) {
        this.elem2 = elem2;
    }

    
}

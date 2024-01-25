package de.ravensnight.fireknight.util;

public interface Receiver<T> {

    /**
     * Handle the object created by builder
     * @param object
     */
    public void receive(T object);    
}

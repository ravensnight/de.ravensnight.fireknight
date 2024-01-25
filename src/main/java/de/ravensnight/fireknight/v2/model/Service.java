package de.ravensnight.fireknight.v2.model;

public class Service {

    private final String name;
    private Filter clientFilter = null;
    private Filter serverFilter = null;

    Service(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    void setClientFilter(Filter clientFilter) {
        this.clientFilter = clientFilter;
    }
    
    void setServerFilter(Filter serverFilter) {
        this.serverFilter = serverFilter;
    }

    public Filter getClientFilter() {
        return clientFilter;
    }
    public Filter getServerFilter() {
        return serverFilter;
    }

}

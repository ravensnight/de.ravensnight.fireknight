package de.ravensnight.fireknight.v1.model;

public class NatRule extends ProtocolSet {

    private Address addrOrigin = null;
    private Address addrOverride = null;

    private Filter srcFilter = null;

    private boolean preroute = false;
    
    
    public Address getAddrOrigin() {
        return addrOrigin;
    }

    public void setAddrOrigin(Address addrOrigin) {
        this.addrOrigin = addrOrigin;
    }

    public Address getAddrOverride() {
        return addrOverride;
    }
    
    public void setAddrOverride(Address addrOverride) {
        this.addrOverride = addrOverride;
    }

    public Filter getSrcFilter() {
        return srcFilter;
    }

    public void setSrcFilter(Filter srcFilter) {
        this.srcFilter = srcFilter;
    }

    public boolean isPreroute() {
        return preroute;
    }

    public void setPreroute(boolean preroute) {
        this.preroute = preroute;
    }

}

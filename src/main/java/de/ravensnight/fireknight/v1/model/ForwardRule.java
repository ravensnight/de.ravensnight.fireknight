package de.ravensnight.fireknight.v1.model;

public class ForwardRule extends ProtocolSet {

    private Filter srcFilter = null;
    private String outInterface = null;

    public Filter getSrcFilter() {
        return srcFilter;
    }
    public void setSrcFilter(Filter srcFilter) {
        this.srcFilter = srcFilter;
    }
    public String getOutInterface() {
        return outInterface;
    }
    public void setOutInterface(String outInterface) {
        this.outInterface = outInterface;
    }
}

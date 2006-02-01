package biochemie.util.edges;


import biochemie.util.Helper;

public class GCDiffEdge extends MyUndirectedEdge {
    private final double gcdiff;
    
    public GCDiffEdge(Object sourceVertex, Object targetVertex, double gcdiff) {
        super(sourceVertex, targetVertex);
        this.gcdiff=gcdiff;
    }
    public String toString() {
        return super.toString()+": GCdiff too high: "+Helper.format(gcdiff);
    }
    public boolean equals(Object other) {
        if(other instanceof GCDiffEdge) {
            GCDiffEdge o=(GCDiffEdge) other;
            return o.getSource().equals(getSource())
                && o.getTarget().equals(getTarget())
                && o.gcdiff==gcdiff;
        }
        return false;
    }
    public String matchString() {
        return getSource()+" gcdiff "+getTarget();
    }
}
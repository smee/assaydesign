package biochemie.util.edges;

import java.text.DecimalFormat;

import org._3pq.jgrapht.edge.UndirectedEdge;

public class GCDiffEdge extends UndirectedEdge {
    private final double gcdiff;
    private static final DecimalFormat df=new DecimalFormat("0.00");
    
    public GCDiffEdge(Object sourceVertex, Object targetVertex, double gcdiff) {
        super(sourceVertex, targetVertex);
        this.gcdiff=gcdiff;
    }
    public String toString() {
        return "GCdiff too high: "+df.format(gcdiff);
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
}
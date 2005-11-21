package biochemie.util.edges;

import java.text.DecimalFormat;

import org._3pq.jgrapht.edge.UndirectedEdge;

public class TMDiffEdge extends MyUndirectedEdge {

    private final double tmdiff;
    private static final DecimalFormat df=new DecimalFormat("0.00");
    public TMDiffEdge(Object sourceVertex, Object targetVertex, double tmdiff) {
        super(sourceVertex, targetVertex);
        this.tmdiff=tmdiff;
    }
    public String toString() {
        return "TMdiff too high: "+df.format(tmdiff);
    }
    public boolean equals(Object other) {
        if(other instanceof TMDiffEdge) {
            TMDiffEdge o=(TMDiffEdge) other;
            return o.getSource().equals(getSource())
                && o.getTarget().equals(getTarget())
                && o.tmdiff==tmdiff;
        }
        return false;
    }
    public String matchString() {
        return getSource()+" tmdiff "+getTarget();
    }
}

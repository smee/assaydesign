package biochemie.util.edges;

import org._3pq.jgrapht.edge.UndirectedEdge;
import org._3pq.jgrapht.edge.UndirectedEdge;

public class CalcDaltonEdge extends MyUndirectedEdge {

    public CalcDaltonEdge(Object sourceVertex, Object targetVertex) {
        super(sourceVertex, targetVertex);
    }
    public String toString() {
        return "Calcdalton";
    }
    public boolean equals(Object other) {
        if(other instanceof CalcDaltonEdge) {
            CalcDaltonEdge o=(CalcDaltonEdge) other;
            return o.getSource().equals(getSource())
                && o.getTarget().equals(getTarget());
        }
        return false;
    }
    public String matchString() {
        return getSource()+" calcdalton "+getTarget();
    }
}

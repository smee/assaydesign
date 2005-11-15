package biochemie.util.edges;

import org._3pq.jgrapht.edge.UndirectedEdge;

public class IdendityEdge extends UndirectedEdge {

    public IdendityEdge(Object sourceVertex, Object targetVertex) {
        super(sourceVertex, targetVertex);
    }
    public String toString() {
        return "same ID";
    }
    public boolean equals(Object other) {
        if(other instanceof IdendityEdge) {
            IdendityEdge o=(IdendityEdge) other;
            return o.getSource().equals(getSource())
                && o.getTarget().equals(getTarget());
        }
        return false;
    }
}

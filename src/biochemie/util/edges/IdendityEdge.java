package biochemie.util.edges;


public class IdendityEdge extends MyUndirectedEdge {

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
    public String matchString() {
        return getSource()+" sameID "+getTarget();
    }
}

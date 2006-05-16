package biochemie.util.edges;


public class SameTTailEdge extends MyUndirectedEdge{

    public SameTTailEdge(Object sourceVertex, Object targetVertex) {
        super(sourceVertex, targetVertex);
    }
    public String toString() {
        return "TTail";
    }
    public boolean equals(Object other) {
        if(other instanceof SameTTailEdge) {
            SameTTailEdge o=(SameTTailEdge) other;
            return o.getSource().equals(getSource())
                && o.getTarget().equals(getTarget());
        }
        return false;
    }
    public String matchString() {
        return getSource()+" same ttail "+getTarget();
    }
}

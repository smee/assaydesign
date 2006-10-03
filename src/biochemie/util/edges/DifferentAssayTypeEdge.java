package biochemie.util.edges;


public class DifferentAssayTypeEdge extends MyUndirectedEdge {

    public DifferentAssayTypeEdge(Object sourceVertex, Object targetVertex) {
        super(sourceVertex, targetVertex);
    }

    public String matchString() {
        return getSource()+"-different assay-"+getTarget();
    }
    public String toString(){
    	return "diff. Assaytype";
    }
}

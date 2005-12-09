package biochemie.pcr.matcher;

import biochemie.util.edges.MyUndirectedEdge;

public class MyDefaultEdge extends MyUndirectedEdge {

    public MyDefaultEdge(Object sourceVertex, Object targetVertex) {
        super(sourceVertex, targetVertex);
    }

    public String matchString() {
        return getSource()+"-default-"+getTarget();
    }

}

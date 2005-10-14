package biochemie.sbe.graph;

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.edge.UndirectedEdge;
import org._3pq.jgrapht.edge.EdgeFactories.UndirectedEdgeFactory;

public class AttributedEdgeFactory extends UndirectedEdgeFactory{
    static final class CalcDaltonEdge extends UndirectedEdge{        
        public CalcDaltonEdge(Object sourceVertex, Object targetVertex) {
            super(sourceVertex, targetVertex);
        }        
    }
    public Edge createEdge(Object source, Object target) {
        
        return super.createEdge(source, target);
    }

}

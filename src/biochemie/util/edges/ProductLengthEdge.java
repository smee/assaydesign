package biochemie.util.edges;

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.edge.UndirectedEdge;

public class ProductLengthEdge extends UndirectedEdge {
    private final int prdiff;
    
    public ProductLengthEdge(Object sourceVertex, Object targetVertex,int prdiff) {
        super(sourceVertex, targetVertex);
        this.prdiff=prdiff;
    }
    public int getProductLengthDiff() {return prdiff;};
    public String toString() {
        return "productlendiff="+prdiff;
    }
    public boolean equals(Object other) {
        if(other instanceof ProductLengthEdge) {
            ProductLengthEdge o=(ProductLengthEdge) other;
            return o.getSource().equals(getSource())
                && o.getTarget().equals(getTarget())
                && o.prdiff==prdiff;
        }
        return false;
    }
}

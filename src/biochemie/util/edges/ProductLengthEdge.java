package biochemie.util.edges;


public class ProductLengthEdge extends MyUndirectedEdge {
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
    public String matchString() {
        return getSource()+" Productlen "+getTarget();
    }
}

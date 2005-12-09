package biochemie.util.edges;

import org._3pq.jgrapht.edge.UndirectedEdge;
import org.apache.commons.lang.builder.HashCodeBuilder;

public abstract class MyUndirectedEdge extends UndirectedEdge {

    public MyUndirectedEdge(Object sourceVertex, Object targetVertex) {
        super(sourceVertex, targetVertex);
    }
    /**
     * Liefert String, der die Kante beschreibt. Wird benoetigt, um z.B. Kanten auszuschliessen.
     * @return
     */
    public abstract String matchString();
    
    public boolean equals(Object other) {
        if(other instanceof UndirectedEdge) {
            UndirectedEdge e=(UndirectedEdge) other;
            return e.getSource().equals(getSource()) && e.getTarget().equals(getTarget());
        }
        return false;
    }
    public int hashCode() {
//        System.out.println("src: "+getSource());
//        System.out.println("src hash="+getSource().hashCode());
//        System.out.println("trg: "+getTarget());
//        System.out.println("trg hash="+getTarget().hashCode());
        return new HashCodeBuilder(1303,1559).append(getSource()).append(getTarget()).toHashCode(); 
    }
}

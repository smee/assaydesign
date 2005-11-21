package biochemie.util.edges;

import org._3pq.jgrapht.edge.UndirectedEdge;

public abstract class MyUndirectedEdge extends UndirectedEdge {

    public MyUndirectedEdge(Object sourceVertex, Object targetVertex) {
        super(sourceVertex, targetVertex);
    }
    /**
     * Liefert String, der die Kante beschreibt. Wird benoetigt, um z.B. Kanten auszuschliessen.
     * @return
     */
    public abstract String matchString();
}

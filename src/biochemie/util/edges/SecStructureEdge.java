package biochemie.util.edges;

import org._3pq.jgrapht.edge.UndirectedEdge;

import biochemie.domspec.SBESekStruktur;

public class SecStructureEdge extends UndirectedEdge {

    private final SBESekStruktur s;

    public SecStructureEdge(Object sourceVertex, Object targetVertex, SBESekStruktur s) {
        super(sourceVertex, targetVertex);
        this.s=s;
    }
    public SBESekStruktur getSecStruc() {
        return s;
    }
    public String toString() {
        return (s.isIncompatible()?"incomp. ":"")+"Sekstructure: "+s.toString();
    }
    public boolean equals(Object other) {
        if(!(other instanceof SecStructureEdge))
            return false;
        return s.equals(((SecStructureEdge)other).s);
    }
    public int hashCode() {
        return s.hashCode();
    }
}

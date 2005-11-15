package biochemie.util.edges;

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.edge.UndirectedEdge;

import biochemie.domspec.Primer;
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
        if(other instanceof SecStructureEdge) {
            SecStructureEdge o=(SecStructureEdge) other;
            return ((Primer)o.getSource()).getId().equals(((Primer)getSource()).getId())
                && o.s.equals(s);
        }
        return false;
    }
}

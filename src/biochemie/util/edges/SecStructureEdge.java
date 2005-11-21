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
        SBESekStruktur o=((SecStructureEdge)other).s;
        if((s.getType()==SBESekStruktur.CROSSDIMER && o.getType()!=SBESekStruktur.CROSSDIMER)
                || (s.getType()!=SBESekStruktur.CROSSDIMER && o.getType()==SBESekStruktur.CROSSDIMER))
            return false;
        boolean ret=s.getPrimer().getId().equals(o.getPrimer().getId())
            && s.bautEin()==o.bautEin()
            && s.getPosFrom3()==o.getPosFrom3();
        if(s.getType()==SBESekStruktur.CROSSDIMER && o.getType()==SBESekStruktur.CROSSDIMER) {
            return ret && (s.getCrossDimerPrimer().getId().equals(o.getCrossDimerPrimer().getId())); 
        }
        return ret;
    }
    public int hashCode() {
        return s.hashCode();
    }
}

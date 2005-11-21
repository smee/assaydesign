package biochemie.util.edges;

import org._3pq.jgrapht.edge.UndirectedEdge;
import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.domspec.SBEPrimer;
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
            && s.getPosFrom3()==o.getPosFrom3()
            && ((SBEPrimer)s.getPrimer()).getType().equals(((SBEPrimer)o.getPrimer()).getType());
        if(s.getType()==SBESekStruktur.CROSSDIMER && o.getType()==SBESekStruktur.CROSSDIMER) {
            return ret && (s.getCrossDimerPrimer().getId().equals(o.getCrossDimerPrimer().getId())
                       && ((SBEPrimer)s.getCrossDimerPrimer()).getType().equals(((SBEPrimer)o.getCrossDimerPrimer()).getType())); 
        }
        return ret;
    }
    public int hashCode() {
        return new HashCodeBuilder(77,33).
            append(s.getPrimer().getId()).
            append(s.getPosFrom3()).
            append(s.bautEin()).
            append(s.getType()==SBESekStruktur.CROSSDIMER).
            append(((SBEPrimer)s.getPrimer()).getType()).toHashCode();
    }
}

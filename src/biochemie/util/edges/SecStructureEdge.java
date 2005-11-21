package biochemie.util.edges;

import org._3pq.jgrapht.edge.UndirectedEdge;
import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.domspec.SBEPrimer;
import biochemie.domspec.SBESekStruktur;

public class SecStructureEdge extends MyUndirectedEdge {

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
            SecStructureEdge o=(SecStructureEdge)other;
            return s.equals(o.s) 
            && (getSource().equals(o.getSource()) || getSource().equals(o.getTarget()))
            && (getTarget().equals(o.getSource()) || getTarget().equals(o.getTarget()));
        }else
            return false;
    }
    public int hashCode() {
        return new HashCodeBuilder(77,33).
            append(s.getPrimer().getId()).
            append(s.getPosFrom3()).
            append(s.bautEin()).
            append(s.getType()==SBESekStruktur.CROSSDIMER).
            append(((SBEPrimer)s.getPrimer()).getType()).toHashCode();
    }
    public String matchString() {
        StringBuffer sb=new StringBuffer("SBESecEdge ");
        sb.append(s.getPrimer().getId()).append(" ").
        append(s.getPosFrom3()).append(" ").
        append(((SBEPrimer)s.getPrimer()).getType()).append(" ");
        if(s.getType()==SBESekStruktur.CROSSDIMER) {
            sb.append(s.getCrossDimerPrimer().getId()).append(" ");
        }
        return new String(sb);
    }
    
}

package biochemie.util.edges;

import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.domspec.Primer;
import biochemie.domspec.SekStruktur;

public class SecStructureEdge extends MyUndirectedEdge {

    private final SekStruktur s;

    public SecStructureEdge(Object sourceVertex, Object targetVertex, SekStruktur s) {
        super(sourceVertex, targetVertex);
        this.s=s;
    }
    public SekStruktur getSecStruc() {
        return s;
    }
    public String toString() {
        if(s instanceof SekStruktur)            
            return (((SekStruktur)s).isIncompatible()?"incomp. ":"")+"Sekstructure: "+s.toString();
        else 
            return "Sekstructure: "+s.toString();
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
        HashCodeBuilder hcb= new HashCodeBuilder(77,33).
            append(s.getPrimer().getId()).
            append(s.getPosFrom3()).
            append(s.bautEin()).
            append(s.getType()==SekStruktur.CROSSDIMER);
        if(s.getPrimer() instanceof Primer)
            hcb.append(((Primer)s.getPrimer()).getType());
        return hcb.toHashCode();
    }
    public String matchString() {
        StringBuffer sb=new StringBuffer("SecEdge ");
        sb.append(s.getPrimer().getId()).append(" ").
        append(s.getPosFrom3()).append(" ").
        append(((Primer)s.getPrimer()).getType()).append(" ");
        if(s.getType()==SekStruktur.CROSSDIMER) {
            sb.append(s.getCrossDimerPrimer().getId()).append(" ");
        }
        return new String(sb);
    }
    
}

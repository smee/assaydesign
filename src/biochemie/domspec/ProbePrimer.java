package biochemie.domspec;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.sbe.ProbePrimerFactory;
import biochemie.sbe.SecStrucOptions;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.Helper;
import biochemie.util.edges.DifferentAssayTypeEdge;

public class ProbePrimer extends Primer {
    private final Primer p;
    private final int assayType;
    private final String addon;
    
    public ProbePrimer(String id, String seq, String type, String snp, int assayType, String addon, int productlen, SecStrucOptions cfg, int mindiff) {
        super(id, seq, type, snp, productlen, cfg, mindiff);
        this.assayType=assayType;
        p=null;
        this.addon=addon;
    }
    public ProbePrimer(Primer p, int assayType, String addon){
        super(p.id,p.getCompletePrimerSeq(),p.getType(),p.getSNP(), p.getProductLength(), p.cfg, p.mindiff);
        this.assayType=assayType;
        this.p=p;
        this.addon=addon;
    }
    public boolean passtMit(Multiplexable o) {
        edgecol.clear();
        if(o instanceof ProbePrimer) {
            ProbePrimer other=(ProbePrimer) o;
            boolean flag=super.passtMit(o);
            boolean temp=this.passtMitEingeschlossenemPrimer(other);
            flag=flag&&temp;
            temp=this.passtMitAssayType(other);
            flag=flag&&temp;
            return flag;
        }else{
            boolean ret= o.passtMit(this);
            edgecol.addAll(o.getLastEdges());
            return ret;
        }
    }
    private boolean passtMitEingeschlossenemPrimer(ProbePrimer o) {
        if(p==null || o.p==null)
            return true;
        boolean ret=p.passtMit(o);
        edgecol.addAll(p.getLastEdges());
        return ret;
    }
    private boolean passtMitAssayType(ProbePrimer other) {
        if(other.assayType != this.assayType){
            edgecol.add(new DifferentAssayTypeEdge(this,other));
            return false;
        }
        return true;
    }
    public String getCompletePrimerSeq() {
        String seq=getPrimerSeq();
        if(p!=null)
            seq=p.getCompletePrimerSeq();
        return seq+addon;
    }
    public int getAssayType(){
        return assayType;
    }
    public String getFilter() {
        return getType()+"_"+getPrimerSeq().length()+"_"+getAssayType();
    }
/*    public String[] getCDParamLine() {
        String seq=getCompletePrimerSeq();
        //das letzte nucl. ist ein ddX, alle anderen dX
        return new String[]{seq.substring(0,seq.length()-1),seq.substring(seq.length()-1)};//TODO probeprimer nicht aufspalten fuer die anhaenge, sondern....?
    }*/
    public String toString() {
        StringBuffer sb=new StringBuffer();
        if(p!=null){
            sb.append(p.toString()).append(", assayType=");
        }else{
            sb.append(super.toString());
        }
        sb.append(", assayType=").append(getAssayType());
        return sb.toString();
    }
    public boolean equals(Object o){
        if ( !(o instanceof ProbePrimer) ) {
            return false;
        }else {
            ProbePrimer other = (ProbePrimer)o;
            return getAssayType()==other.getAssayType() && super.equals(other);
        }
    }
    public int hashCode() {
        return new HashCodeBuilder(5347, 6043).
           append(getId()).
           append(getCompletePrimerSeq()).
           append(getAssayType()).
           toHashCode();
}
    public Primer getIncludedPrimer() {
        return p;
    }
}

package biochemie.domspec;

import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.calcdalton.CalcDalton;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.Helper;
import biochemie.util.edges.CalcDaltonEdge;
import biochemie.util.edges.SameTTailEdge;

public class PinpointPrimer extends Primer{

    private final String tTail;

    public PinpointPrimer(String id, String seq, String type, String snp, String tTail, SBEOptions cfg) {
        super(id, seq, type, snp, cfg.getSecStrucOptions());
        this.tTail=tTail;
        
    }

    public boolean passtMit(Multiplexable other) {
        edgecol.clear();
        if (other instanceof PinpointPrimer) {
            PinpointPrimer o = (PinpointPrimer) other;
            boolean flag=super.passtMit(o);
            boolean temp=passtMitTTail(o);
            flag= flag && temp;
            temp=passtMitCalcDalton(o);
            return flag && temp;
        }
        return super.passtMit(other);
    }


    private boolean passtMitTTail(PinpointPrimer o) {
        if( o.tTail.length()!= tTail.length()){
            edgecol.add(new SameTTailEdge(this,o));
            return false;
        }
        return true;
    }
    private boolean passtMitCalcDalton(PinpointPrimer other) {
        CalcDalton cd=Helper.getCalcDalton();
        String[][] sbedata= createCDParameters(this, other);
        if(0 == cd.calc(sbedata).length) {
            edgecol.add(new CalcDaltonEdge(this,other));
            return false;
        }
        return true;
    }
    public String getCompletePrimerSeq() {
        return tTail+super.getPrimerSeq();
    }

    public String getTTail() {
        return tTail;
    }

    public String getFilter() {
        return getType()+"_"+getPrimerSeq().length()+"_"+tTail.length();
    }
    public String toString() {
        return new StringBuffer(super.toString()).append(", T lenght=").append(getTTail().length()).toString();
    }
    public boolean equals(Object o){
        if ( !(o instanceof PinpointPrimer) ) {
            return false;
        }else {
            PinpointPrimer other = (PinpointPrimer)o;
            return getTTail().equals(other.getTTail()) && super.equals(other);
        }
    }
    public int hashCode() {
        return new HashCodeBuilder(14107, 27431).
           append(getId()).
           append(getCompletePrimerSeq()).
           append(getTTail()).
           toHashCode();
    }

}

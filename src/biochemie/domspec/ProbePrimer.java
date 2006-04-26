package biochemie.domspec;

import biochemie.sbe.SecStrucOptions;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.edges.DifferentAssayTypeEdge;

public class ProbePrimer extends Primer {
    private final Primer p;
    private int assayType;
    private final String addon;
    
    public ProbePrimer(String id, String seq, String type, String snp, int assayType, String addon, SecStrucOptions cfg) {
        super(id, seq, type, snp, cfg);
        this.assayType=assayType;
        p=null;
        this.addon=addon;
        this.assayType=assayType;
    }
    public ProbePrimer(Primer p, int assayType, String addon){//TODO
        super(p.id,p.getPrimerSeq(),p.getType(),p.getSNP(),p.cfg);
        this.assayType=assayType;
        this.p=p;
        this.addon=addon;
    }
    public boolean passtMit(Multiplexable o) {
        edgecol.clear();
        if(o instanceof ProbePrimer) {
            ProbePrimer other=(ProbePrimer) o;
            boolean temp=true, flag=true;
            flag= this.passtMitAssayType(other);
            temp=super.passtMit(o);
            flag=flag&&temp;
            return flag;
        }else{
            boolean ret= o.passtMit(this);
            edgecol.addAll(o.getLastEdges());
            return ret;
        }
    }
    private boolean passtMitAssayType(ProbePrimer other) {
        if(other.assayType != this.assayType){
            edgecol.add(new DifferentAssayTypeEdge(this,other));
            return false;
        }
        return true;
    }
    public String getCompletePrimerSeq() {
        return super.getPrimerSeq()+addon;
    }
}

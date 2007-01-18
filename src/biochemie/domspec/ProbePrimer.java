package biochemie.domspec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.sbe.SecStrucOptions;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.Helper;
import biochemie.util.edges.DifferentAssayTypeEdge;
import biochemie.util.edges.MyUndirectedEdge;

public class ProbePrimer extends Primer {
    private final Primer p;
    private final int assayType;
    private final List addonList;
    
    public ProbePrimer(String id, String seq, String type, String snp, int assayType, List addons, int productlen, SecStrucOptions cfg, int mindiff) {
        super(id, seq, type, snp, productlen, cfg, mindiff);
        this.assayType=assayType;
        p=null;
        this.addonList=addons;
    }
    public ProbePrimer(Primer p, int assayType, List addon){
        super(p.id,p.getPrimerSeq(),p.getType(),p.getSNP(), p.getProductLength(), p.cfg, p.mindiff);
        this.assayType=assayType;
        this.p=p;
        this.addonList=addon;
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
        edgecol.addAll(modifyEdges(p.getLastEdges()));
        return ret;
    }
    private Collection modifyEdges(Collection lastEdges) {
        for (Iterator it = lastEdges.iterator(); it.hasNext();) {
            MyUndirectedEdge edge = (MyUndirectedEdge) it.next();
            if(edge.getSource().equals(this.p))
                Helper.setField(edge,"m_source",this);//XXX dirty hack, setting private fields
            else
                if(edge.getTarget().equals(this.p))
                    Helper.setField(edge,"m_target",this);
        }
        return lastEdges;
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
//        return seq+addonList;
        return seq;
    }
    /* (non-Javadoc)
     * @see biochemie.domspec.IProbePrimer#getAssayType()
     */
    public int getAssayType(){
        return assayType;
    }
    /* (non-Javadoc)
     * @see biochemie.domspec.IProbePrimer#getAddonList()
     */
    public List getAddonList(){
        return Collections.unmodifiableList(addonList);
    }
    public String getFilter() {
        return getType()+"_"+getPrimerSeq().length()+"_"+getAssayType();
    }

    public String toString() {
        StringBuffer sb=new StringBuffer();
        if(p!=null){
            sb.append(p.toString());
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
            return getAssayType()==other.getAssayType()  
                    && this.addonList.equals(other.addonList)
                    && super.equals(other);
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
    public String[] getCDParamLine(boolean includeHairpins) {
        String[] res=new String[addonList.size()+1];
        res[0]=getPrimerSeq();
        for (int i = 0; i < addonList.size(); i++) {
            res[i+1]=(String)addonList.get(i);;
        }
        return res;
    }
    public String getName(){
        return id+"_"+getType()+"_"+getAssayType()+"_"+getPrimerSeq().length();
    }
    public boolean passtMitKompCD(Primer other) {
        boolean flag= super.passtMitKompCD(other);
        if(other instanceof ProbePrimer)
            flag=flag && passtMitAssayType((ProbePrimer) other);
        return flag;
    }
}

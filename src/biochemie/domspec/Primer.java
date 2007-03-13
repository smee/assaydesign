/*
 * Created on 05.11.2004 by Steffen Dienst
 *
 */
package biochemie.domspec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.calcdalton.CalcDalton;
import biochemie.pcr.modules.CrossDimerAnalysis;
import biochemie.sbe.SecStrucOptions;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.Helper;
import biochemie.util.edges.CalcDaltonEdge;
import biochemie.util.edges.IdendityEdge;
import biochemie.util.edges.ProductLengthEdge;
import biochemie.util.edges.SecStructureEdge;

/**
 * Immutable. Repraesentiert einen generischen Primer.
 * @author Steffen Dienst
 * 05.11.2004
 */
public abstract class Primer extends Observable implements Multiplexable, Cloneable {
    public final static String SEKSTRUK_CHANGED="sec.structures changed";
    public final static String PLEXID_CHANGED="multiplexid changed";
    public static final String _5_ = "5";
    public static final String _3_ = "3";
    
    private final String seq;
    protected String plexid;
    protected final String id;
    protected double temp;
    protected double gcgehalt;
    
    protected Set sekstruc;
    protected final Collection edgecol=new HashSet();
    private final String type;
    protected final String snp;
    protected final SecStrucOptions cfg;
    private final int productlen;
    protected final int mindiff;
    

    public Primer(String id,String seq, String type, String snp, SecStrucOptions cfg) {
        this(id,seq,type,snp,0,cfg,0);
    }
    public Primer(String id,String seq, String type, String snp, int prodlen, SecStrucOptions cfg, int mindiff) {
        this.id=id;
        this.seq=seq;
        this.type=type;
        this.snp=snp;
        this.productlen=prodlen;
        this.mindiff=mindiff;
        this.cfg=cfg;
        temp=biochemie.util.Helper.calcTM(seq);
        gcgehalt=biochemie.util.Helper.getXGehalt(seq,"GgCc");
        plexid=null;
    }
    
    
    
    public String getName() {
        return id+": "+seq;
    }
    
    /* (non-Javadoc)
     * @see biochemie.sbe.calculators.Multiplexable#passtMit(biochemie.sbe.calculators.Multiplexable)
     */
    public boolean passtMit(Multiplexable o){
        edgecol.clear();
        if(o instanceof Primer) {
            Primer other=(Primer) o;
            boolean temp=true, flag=true;
            flag= this.passtMitID(other);
            temp=this.passtMitSekStrucs(other);
            flag=flag&&temp;
            temp= this.passtMitProductLength(other);
            flag=flag&&temp;
            temp=this.passtMitIncompCrossdimern(other);
            flag=flag&&temp;
            temp=passtMitCalcDalton(other);
            flag=flag&&temp;
            return flag;
        }
        boolean ret= o.passtMit(this);
        edgecol.addAll(o.getLastEdges());
        return ret;
    }
    
    protected boolean passtMitProductLength(Primer other) {
        //Produktlänge
        int prdiff=productlen-other.productlen;
        if(Math.abs(prdiff)<mindiff) {
            edgecol.add(new ProductLengthEdge(this,other,prdiff));
            return false;    //Produktlängenunterschied zu gering
        }
        return true;
    }
    
    protected boolean passtMitCalcDalton(Primer other) {
        CalcDalton cd=Helper.getCalcDalton();
        String[][] sbedata= createCDParameters(this, other);
        if(0 == cd.calc(sbedata,false).length) {
            edgecol.add(new CalcDaltonEdge(this,other));
            return false;
        }
        return true;
    }
    /**
     * @return
     */
    public int numOfHHSekStruks() {
        int count=0;
        for (Iterator iter = getSecStrucs().iterator(); iter.hasNext();) {
            SekStruktur s = (SekStruktur) iter.next();
            if(SekStruktur.CROSSDIMER != s.getType())
                count++;
        }
        return count;
    }
    /**
     * Returns the sequence of the original primer only.
     * @return
     */
    public String getPrimerSeq() {
        return seq;
    }
    /**
     * Returns the primerseq that is actually used, including all addons etc.
     * @return
     */
    public abstract String getCompletePrimerSeq();
    
    public double getTemperature() {
        return temp;
    }
    public double getGCGehalt() {
        return gcgehalt;
    }
    
    /**
     * @return
     */
    public String getId() {
        return id;
    }
    public int getProductLength(){
        return productlen;
    }
    /**
     * Benachrichtigt Observer mit <code>PLEXID_CHANGED </code>
     * @param s
     */
    public void setPlexID(String s) {
        if(plexid != null && plexid.length() != 0)
            throw new IllegalStateException(getName()+": attempted to set plexid twice! Has already PlexID:"+plexid);
        this.plexid=s;
        setChanged();
        notifyObservers(PLEXID_CHANGED);
    }
    public String getPlexID() {
        return plexid;
    }
    /**
     * Liefert jeweils den Grund für das letzte von <code>passtMit(...) </code> gelieferte Ergebnis.
     * Für Debugzwecke
     * @return
     */
    public Collection getLastEdges() {
        return new HashSet(edgecol);
    }
    
    /**
     * @return
     */
    public Set getSecStrucs(){
        if(this.sekstruc==null)
            sekstruc=SekStrukturFactory.getSecStruks(this,cfg);
        return sekstruc;
    }
    
    
    /**
     * @return
     */
    protected List getHomodimerPositions() {
        return (List)Algorithms.collect(Algorithms.apply(Algorithms.select(getSecStrucs().iterator(),new UnaryPredicate() {
            public boolean test(Object obj) {
                return SekStruktur.HOMODIMER == ((SekStruktur) obj).getType();
            }
        }),new UnaryFunction() {
            public Object evaluate(Object obj) {
                SekStruktur s=(SekStruktur)obj;
                return new Integer(s.getPosFrom3());
            }
        }),new ArrayList());
    }
    
    
    /**
     * @return
     */
    protected List getHairpinPositions() {
        return (List)Algorithms.collect(Algorithms.apply(Algorithms.select(getSecStrucs().iterator(),new UnaryPredicate() {
            public boolean test(Object obj) {
                return SekStruktur.HAIRPIN == ((SekStruktur) obj).getType();
            }
        }),new UnaryFunction() {
            public Object evaluate(Object obj) {
                SekStruktur s=(SekStruktur)obj;
                return new Integer(s.getPosFrom3());
            }
        }),new ArrayList());
    }
    
    public int realSize() {
        return 1;
    }
    public List getIncludedElements() {
        List result = new ArrayList(1);
        result.add(this);
        return result;
    }
    public boolean equals(Object other) {
        if(other instanceof Primer) {
            Primer o=(Primer)other;
            return id.equals(o.id) && seq.equals(o.seq);
        }
        return false;
    }
    public int hashCode() {
        HashCodeBuilder b=new HashCodeBuilder(911,257).append(id).append(seq);
        return b.toHashCode();
    }
    
    
    
    public String getType() {
        return type;
    }
    
    
    
    public String getSNP() {
        return snp;
    }
    
    
    
    /**
     * Adds crossdimers with the primers in the given set to the internal memory
     * of secondary structures.
     */
    public void normalizeCrossdimers(Collection primers,CrossDimerAnalysis cda ) {
        
        for (Iterator it = primers.iterator(); it.hasNext();) {
            Primer p = (Primer) it.next();
            if(p!=this)
                getSecStrucs().addAll(SekStrukturFactory.getCrossdimer(this,p,cda));
        }
    }
    
    
    
    protected boolean passtMitID(Primer other) {
        if(other.getId().equals(this.getId())) {
            edgecol.add(new IdendityEdge(this,other));
            return false;   //derselbe SBECandidate
        }
        return true;
    }
    
    
    
    protected boolean passtMitSekStrucs(Primer other) {
        //Inkompatible Sekundärstrukturen?
        String snp1=getSNP();
        String snp2=other.getSNP();
        for (Iterator it = getSecStrucs().iterator(); it.hasNext();) {
            SekStruktur s = (SekStruktur) it.next();
            if(-1 != snp2.indexOf(s.bautEin())){
                edgecol.add(new SecStructureEdge(this,other, s));
                return false;
            }
        }
        for (Iterator it = other.getSecStrucs().iterator(); it.hasNext();) {
            SekStruktur s = (SekStruktur) it.next();
            if(snp1.indexOf(s.bautEin()) != -1){
                edgecol.add(new SecStructureEdge(other,this, s));
                return false;
            }
        }
        return true;
    }
    
    
    
    /**
     * Testet, ob Crossdimer entstehen und speichert diese in den jeweiligen Primer zur weiteren Verwendung.
     * @param other
     * @param evilcd true, jeder CD ist Ausschlusskriterium, false heisst, nur inkompatible.
     * @return
     */
    protected boolean passtMitIncompCrossdimern(Primer other) {
        return passtMitCDRec(this,other) && passtMitCDRec(other,this);//damit der crossdimer auch dem richtigen primer zugeordnet werden kann
    }
    
    
    
    protected boolean passtMitCDRec(Primer me, Primer other) {
        Set cross=SekStrukturFactory.getCrossdimer(me,other,cfg);
        for (Iterator it = cross.iterator(); it.hasNext();) {
            SekStruktur s = (SekStruktur) it.next();
            if(cfg.isAllCrossdimersAreEvil() || s.isIncompatible()) {
                edgecol.add(new SecStructureEdge(me,other,s));
                return false;
            }
        }
        return true;
    }
    public static String[][] createCDParameters(Primer p1, Primer p2) {
        return new String[][] {p1.getCDParamLine(), p2.getCDParamLine()};
        
}
    public String[] getCDParamLine(){
        return this.getCDParamLine(true);
    }
    /**
     * Creates the input for CalcDalton.
     * TODO invalid. product peak diffs. sollten ohne die sec.strucs berechnet werden. 
     * TODO Grund: da sich zwei primer mit incomp. sec.strucs. eh ausschliessen, werden diese einbauten auch nie auftauchen
     * TODO sec.strucs, die in den verbotenen massebreich fallen wüerden mussu auch ausschliessen, weil gibts ja nu nich 
     * @return
     */
    public String[] getCDParamLine(boolean includeHairpins) {
        Set chars=new TreeSet();
        String snp=this.getSNP();
        for(int i=0;i<snp.length();i++)
            chars.add(new Character(snp.charAt(i)));
        if(includeHairpins){
            Set sekstrucs1=this.getSecStrucs();
            for (Iterator it = sekstrucs1.iterator(); it.hasNext();) {
                SekStruktur s = (SekStruktur) it.next();
                if(s.getType()==SekStruktur.HAIRPIN || s.getType()==SekStruktur.HOMODIMER)
                    chars.add(new Character(s.bautEin().toUpperCase().charAt(0)));
            }
        }
        char[] attachments=new char[] {'A','C','G','T'};
        String[] arr=new String[attachments.length+1];
        arr[0]=this.getCompletePrimerSeq();
        for (int i = 0; i < attachments.length; i++) {
            arr[i+1]="";
            if(!chars.contains(new Character(attachments[i])))
                arr[i+1]=">";
            arr[i+1]+=attachments[i];
        }
        return arr;
    }
    /**
     * returns the primer without the dd-Nucleotide at the end. 
     * @return
     */
    protected String getDPrimerSeq(){
        return getPrimerSeq();
    }
    
    public abstract String getFilter();
    public String getCSVSekStructuresSeparatedBy(String sep) {
        List l=new ArrayList(getSecStrucs());
        Collections.sort(l,SekStruktur.getSeverityComparator());
        String positions="";
        String nucl="";
        String clazz="";
        for (Iterator it = l.iterator(); it.hasNext();) {
            SekStruktur s = (SekStruktur) it.next();
            int pos=s.getPosFrom3();
            if(-1 == pos)
                positions+="unknown";
            else
                positions+=pos;
            if(s.bautEin().toUpperCase().charAt(0)=='K')//anti pl
                nucl+=" - ";
            else
                nucl+="dd"+s.bautEin().toUpperCase().charAt(0);
            switch (s.getType()) {
            case SekStruktur.HAIRPIN:
                clazz+="hairpin, ";
                break;
            case SekStruktur.HOMODIMER:
                clazz+="homodimer, ";
                break;
            case SekStruktur.CROSSDIMER:
                clazz+="crossdimer with ID ";
                clazz+=s.getCrossDimerPrimer().getId()+", ";
                break;
            default:
                break;
            }
            clazz+=s.isIncompatible()?"incompatible":"compatible";
            if(it.hasNext()){
                positions+=", ";
                nucl+=", ";
                clazz+=", ";
            }
        }
        return positions+sep+nucl+sep+clazz;
    }
    
    public String toString() {
        StringBuffer sb=new StringBuffer();
        sb.append(getId()).append(":").append(getCompletePrimerSeq()).append(", ").append(getType());
        sb.append(", GC=").append(Helper.format(getGCGehalt())).append("%, Tm=").append(Helper.format(getTemperature())).append("°, hairpins=");
        sb.append(getHairpinPositions()).append(", homodimer=").append(getHomodimerPositions());
        return sb.toString();
    }
}

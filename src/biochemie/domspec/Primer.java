/*
 * Created on 05.11.2004 by Steffen Dienst
 *
 */
package biochemie.domspec;

import java.util.ArrayList;
import java.util.Collection;
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

import biochemie.calcdalton.CalcDaltonOptions;
import biochemie.pcr.modules.CrossDimerAnalysis;
import biochemie.sbe.SecStrucOptions;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.edges.IdendityEdge;
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
    
    /**
     * @param seq
     */
    public Primer(String id,String seq, String type, String snp,SecStrucOptions cfg) {
        this.id=id;
        this.seq=seq;
        this.type=type;
        this.snp=snp;
        this.cfg=cfg;
        temp=biochemie.util.Helper.calcTM(seq);
        gcgehalt=biochemie.util.Helper.getXGehalt(seq,"GgCc");
        plexid="";
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
            temp=this.passtMitCrossdimern(other,true);
            flag=flag&&temp;
            return flag;
        }
        boolean ret= o.passtMit(this);
        edgecol.addAll(o.getLastEdges());
        return ret;
    }

    /**
     * @return
     */
    public int numOfHHSekStruks() {
        int count=0;
        for (Iterator iter = getSecStrucs().iterator(); iter.hasNext();) {
            SBESekStruktur s = (SBESekStruktur) iter.next();
            if(SBESekStruktur.CROSSDIMER != s.getType())
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
                SBESekStruktur s=(SBESekStruktur)obj;
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
                SBESekStruktur s=(SBESekStruktur)obj;
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
            SBEPrimer p = (SBEPrimer) it.next();
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
            SBESekStruktur s = (SBESekStruktur) it.next();
            if(s.isVerhindert())
                continue;
            if(-1 != snp2.indexOf(s.bautEin())){
                edgecol.add(new SecStructureEdge(this,other, s));
                return false;
            }
        }
        for (Iterator it = other.getSecStrucs().iterator(); it.hasNext();) {
            SBESekStruktur s = (SBESekStruktur) it.next();
            if(s.isVerhindert())
                continue;
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
    protected boolean passtMitCrossdimern(Primer other, boolean evilcd) {
        return passtMitCDRec(this,other,evilcd) && passtMitCDRec(other,this,evilcd);//damit der crossdimer auch dem richtigen primer zugeordnet werden kann
    }



    private boolean passtMitCDRec(Primer me, Primer other, boolean evilcd) {
        Set cross=SekStrukturFactory.getCrossdimer(me,other,cfg);
        for (Iterator it = cross.iterator(); it.hasNext();) {
            SBESekStruktur s = (SBESekStruktur) it.next();
            if(s.isVerhindert())
                continue;
            if(!evilcd) {
                if(s.isIncompatible()) {
                    edgecol.add(new SecStructureEdge(me,other,s));
                    return false;
                }
            }else {
                edgecol.add(new SecStructureEdge(me,other,s));
                return false;
            }
        }
        return true;
    }



    public static String[] getCDParamLine(Primer p) {//TODO anpassen fuer andere anhaenge
        Set chars=new TreeSet();
        String snp=p.getSNP();
        for(int i=0;i<snp.length();i++)
            chars.add(new Character(snp.charAt(i)));
        Set sekstrucs1=p.getSecStrucs();
        for (Iterator it = sekstrucs1.iterator(); it.hasNext();) {
            SBESekStruktur s = (SBESekStruktur) it.next();
            if(s.getType()==SBESekStruktur.HAIRPIN || s.getType()==SBESekStruktur.HOMODIMER)
                chars.add(new Character(Character.toUpperCase(s.bautEin())));
        }
        char[] attachments=new char[] {'A','C','G','T'};
        String[] arr=new String[attachments.length+1];
        arr[0]=p.getPrimerSeq();
        for (int i = 0; i < attachments.length; i++) {
            arr[i+1]="";
            if(!chars.contains(new Character(attachments[i])))
                arr[i+1]=">";
            arr[i+1]+=attachments[i];
        }
        return arr;
    }


    public static String[][] createCDParameters(Primer p1, Primer p2) {
                return new String[][] {Primer.getCDParamLine(p1), Primer.getCDParamLine(p2)};
                
        }
}

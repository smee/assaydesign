/*
 * Created on 05.11.2004 by Steffen Dienst
 *
 */
package biochemie.domspec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;

import biochemie.sbe.calculators.Multiplexable;

/**
 * @author Steffen Dienst
 * 05.11.2004
 */
public abstract class Primer extends Observable implements Multiplexable, Cloneable {
	public final static String TEMP_CHANGED="temperature changed";
	public final static String GC_CHANGED="gc changed";
	public final static String SEKSTRUK_CHANGED="sec.structures changed";
	public final static String PLEXID_CHANGED="multiplexid changed";
	
    protected String seq;
    protected String plexid;
    protected String id;
    protected double temp;
    protected double gcgehalt;
    
    protected Set sekstruc;
    protected String edgereason;
    /**
     * @param seq
     */
    public Primer(String id,String seq) {
        this.id=id;
        this.seq=seq;
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
    abstract public boolean passtMit(Multiplexable other);

    /* (non-Javadoc)
     * @see biochemie.sbe.calculators.Multiplexable#maxPlexSize()
     */
    abstract public int maxPlexSize();

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

    public String getSeq() {
        return seq;
    }

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
            throw new IllegalStateException(getName()+": attempted to set plexid twice! Give PlexID:"+plexid);
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
    public String getEdgeReason() {
        return edgereason;
    }

    /**
     * @return
     */
    public abstract Set getSecStrucs();


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



}

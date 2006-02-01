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

import org._3pq.jgrapht.Edge;
import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.sbe.multiplex.Multiplexable;

/**
 * Immutable. Repraesentiert einen generischen Primer.
 * @author Steffen Dienst
 * 05.11.2004
 */
public abstract class Primer extends Observable implements Multiplexable, Cloneable {
	public final static String SEKSTRUK_CHANGED="sec.structures changed";
	public final static String PLEXID_CHANGED="multiplexid changed";

    private final String seq;
    protected String plexid;
    protected final String id;
    protected double temp;
    protected double gcgehalt;

    protected Set sekstruc;
    protected final Collection edgecol=new HashSet();
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
}

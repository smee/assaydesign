/*
 * Created on 05.11.2004 by Steffen Dienst
 *
 */
package biochemie.domspec;

import java.util.Comparator;

import biochemie.util.Helper;

/**
 * @author Steffen Dienst
 * 05.11.2004
 */
public class SekStruktur  implements Cloneable{

    public static final int HAIRPIN = 0;
    public static final int HOMODIMER = 1;
    public static final int CROSSDIMER = 2;
    protected final int type;
    protected final Primer p;
    protected final Primer other;
    protected final int pos;
    protected char bautAn = 0;

    /**
     * Constructor for Crossdimers.
     * @param p2
     * @param other2
     * @param pos2
     */
    public SekStruktur(Primer p, Primer other, int pos) {
        if(null == p || null == other)
            throw new IllegalArgumentException("no primer given for crossdimer!");
        this.p=p;
        this.other=other;
        this.type=CROSSDIMER;
        this.pos=pos;

    }

    /**
     * Constructor for given builtin nucleotides with unknown position.
     * @param p2
     * @param t
     * @param einbau2
     */
    public SekStruktur(Primer p, int t, char einbau) {
        this.p=p;
        this.other=null;
        this.pos=-1;
        this.bautAn=Character.toUpperCase(einbau);
        switch (t) {
            case HAIRPIN :
            case HOMODIMER:
                this.type=t;
                break;
            default :
                throw new IllegalArgumentException("invalid secondary strucure type given!");
        }
    }

    /**
     * Constructor for Hairpin/Homodimer.
     * @param p2
     * @param t
     * @param pos2
     */
    public SekStruktur(Primer p, int t, int pos) {
        if(t!= HAIRPIN && t != HOMODIMER)
            throw new IllegalArgumentException("invalid type given for sec.structure!");
        this.p=p;
        this.type=t;
        this.pos=pos;
        this.other=null;
    }





    /**
     * @return
     */
    public int getPosFrom3() {
        return pos;
    }

    /**
     * @return
     */
    public int getType() {
        return type;
    }

    public char bautEin() {
        if(0 == this.bautAn) {
            String seq=p.getSeq();
            if(getType()==CROSSDIMER)
                seq=other.getSeq();
            bautAn = Helper.sekundaerStrukturBautEin(seq,getPosFrom3());
        }
        return bautAn;
    }

    /**
     * Liefert Comparator zum sortieren einer Liste von SekStruk mit HAIRPINS vor HOMODIMER vor CROSSDIMER
     * @return
     */
    public static Comparator getTypeComparator() {
        return new Comparator() {
            public int compare(Object arg0, Object arg1) {
                SekStruktur s1=(SekStruktur)arg0;
                SekStruktur s2=(SekStruktur)arg1;
                return s2.getType()-s1.getType();
            }
        };
    }



    /**
     * @return
     */
    public Primer getCrossDimerPrimer() {
        if(CROSSDIMER != getType())
            throw new IllegalArgumentException("trying to get the other involved primer, althoug this is no crossdimer!");
        return p;
    }

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

    public String getAsciiArt() {
        switch (type) {
        case HAIRPIN:
            return Helper.outputHairpin(p.getSeq(),pos-1,p.getSeq().length());
        case HOMODIMER:
            return Helper.outputXDimer(p.getSeq(),p.getSeq(),p.getSeq().length() - pos,p.getSeq().length());
        case CROSSDIMER:
            return Helper.outputXDimer(p.getSeq(),other.getSeq(),p.getSeq().length() - pos,Math.min(p.getSeq().length(),other.getSeq().length()));

        default:
            return "unknown type of sec.struk encountered.";
        }
    }


}

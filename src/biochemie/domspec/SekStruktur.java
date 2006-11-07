/*
 * Created on 05.11.2004 by Steffen Dienst
 *
 */
package biochemie.domspec;

import java.util.Comparator;

import org.apache.commons.lang.builder.HashCodeBuilder;

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
    protected boolean incomp;
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
        init();
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
        this.type=t;
        init();

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
        init();
    }

    private void init() {
        switch (type) {
        case HAIRPIN :
        case HOMODIMER:
            incomp=isInkompatibleSekStruktur(p.getCompletePrimerSeq(),getPosFrom3(),p.getSNP(),bautAn);
            break;
        case CROSSDIMER:
            //es sind zwei SNPs beteiligt, gegen die getestet werden muss.
            incomp=isInkompatibleSekStruktur(other.getCompletePrimerSeq(),getPosFrom3(),p.getSNP(),bautAn)
                          || isInkompatibleSekStruktur(other.getCompletePrimerSeq(),getPosFrom3(),other.getSNP(),bautAn);
            break;
        default :
            throw new IllegalArgumentException("invalid secondary strucure type given!");
    }        
    }

    public boolean isIncompatible() {
        return incomp;
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

    public String bautEin() {
        if(0 == this.bautAn) {
            String seq=p.getCompletePrimerSeq();
            if(getType()==CROSSDIMER)
                seq=other.getCompletePrimerSeq();
            bautAn = Helper.sekundaerStrukturBautEin(seq,getPosFrom3());
        }
        return Character.toString(bautAn);
    }

    public Primer getPrimer() {
        return p;
    }
    public Primer getCDPrimer() {
        return other;
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

    public double getEnthalpy() {
        String match=null;
        switch (getType()) {
        case HAIRPIN:
            match=getBindingSeq(getPrimer().getCompletePrimerSeq(),Helper.revcomplPrimer(getPrimer().getCompletePrimerSeq()),getPosFrom3());
            return Helper.cal_dG_secondaryStruct(match)+Helper.LoopEnergy(getLoopLength(getPosFrom3(),match.length()))-1;
        case HOMODIMER:
            match=getBindingSeq(getPrimer().getCompletePrimerSeq(),Helper.revcomplPrimer(getPrimer().getCompletePrimerSeq()),getPosFrom3());
            return Helper.cal_dG_secondaryStruct(match)-1;
        case CROSSDIMER:
            match=getBindingSeq(getPrimer().getCompletePrimerSeq(), Helper.revcomplPrimer(getCDPrimer().getCompletePrimerSeq()),getPosFrom3());
            return Helper.cal_dG_secondaryStruct(Helper.complPrimer(match))-1;
        default:
            return 0;
        }
    }

    /**
     * @return
     */
    public Primer getCrossDimerPrimer() {
        if(CROSSDIMER != getType())
            throw new IllegalArgumentException("trying to get the other involved primer, althoug this is no crossdimer!");
        return other;
    }

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

    public String getAsciiArt() {
        switch (type) {
        case HAIRPIN:
            return Helper.outputHairpin(p.getCompletePrimerSeq(),pos-1,p.getCompletePrimerSeq().length(), getEnthalpy());
        case HOMODIMER:
            return Helper.outputXDimer(p.getCompletePrimerSeq(),p.getCompletePrimerSeq(),p.getCompletePrimerSeq().length() - pos,p.getCompletePrimerSeq().length(), getEnthalpy());
        case CROSSDIMER:
            return Helper.outputXDimer(p.getCompletePrimerSeq(),other.getCompletePrimerSeq(),p.getCompletePrimerSeq().length() - pos,Math.min(p.getCompletePrimerSeq().length(),other.getCompletePrimerSeq().length()), getEnthalpy());

        default:
            return "unknown type of sec.struk encountered.";
        }
    }
    protected static String getBindingSeq(String primer, String rcPrimer,int pos) {
        int tempindex=primer.length()-1;
        StringBuffer binding=new StringBuffer(Math.max(primer.length(),rcPrimer.length()));
        for(int j=pos-1;0 <= j;j--,tempindex--) {  //maximal bis maxmatchlength suchen
            if(primer.charAt(tempindex)==rcPrimer.charAt(j) && Helper.isNukleotid(rcPrimer.charAt(j))) {
                binding.append(primer.charAt(tempindex));
            }else
                break;
        }
        return binding.toString();
    }
    protected static int getLoopLength(int matchstart, int matchlen) {
        int looplen=matchstart-2*matchlen;
        if(looplen<0)
            throw new IllegalArgumentException("hairpin looplen <0!");
        return looplen;
    }
    /**
     * Liefert Comaprator, der Sekundaerstrukturen nach Ernsthaftigkeit sortiert, also
     * inkompatible Sekstruks vor kompatiblen.
     * @return
     */
    public static Comparator getSeverityComparator() {
        return new Comparator() {
            public int compare(Object arg0, Object arg1) {
                SekStruktur s1=(SekStruktur)arg0;
                SekStruktur s2=(SekStruktur)arg1;
                if(s1.isIncompatible())
                    return -1;
                if(s2.isIncompatible())
                    return 1;
                return 0;
            }
        };
    }

    public int hashCode() {
        int hash= new HashCodeBuilder(17, 37).
           append("SBESekstruktur").
           append(p.getId()).
           append(pos).
           append(type).
           toHashCode();
        return hash;
    }
    public boolean equals(Object obj) {
        if ( !(obj instanceof SekStruktur) ) {
            return false;
        }
        if(this==obj)
            return true;
        SekStruktur rhs = (SekStruktur) obj;
        boolean eq=true;
        eq=eq && (this.type==rhs.type)
              && (this.incomp==rhs.incomp)
              && type==rhs.type
              && p.equals(rhs.p);
        if(eq==true && CROSSDIMER == type) {  //betrachte Crossdimer mit anderen Primern als gleich, auch wenn die Bruchstelle nicht stimmt.
            eq=eq && other.equals(rhs.other);
        }
        return eq;
    }
    public String toString() {
        if( getType() != CROSSDIMER )
            return p.getId()+": "+p.getCompletePrimerSeq()+", "+(isIncompatible()?"in":"")+"compatible "+(HAIRPIN == getType()?"hairpin":"homodimer")+", pos="+getPosFrom3()+", bautein="+bautEin();
        else
            return p.getId()+" with "+other.getId()+": "
                     +(isIncompatible()?"in":"")+"compatible crossdimer";
    }

    /**
     * Test, ob das Nukleotid, welches vor dem Hairpin eingebaut wird, in SNP liegt oder nicht
     * @param primer
     * @param bautAn 
     * @param bruchstelle
     * @return true, wenn das einzubauende Nukleotid im SNP liegt
     */
    private boolean isInkompatibleSekStruktur(String primer, int pos, String snp, char bautAn) {
        char einbau=bautAn;
        if(bautAn==0 && pos!=-1){
            einbau= Helper.sekundaerStrukturBautEin(primer, pos);//nimm das Nukleotid VOR der Position!
        }
        return -1 != snp.indexOf(einbau);
    }
}

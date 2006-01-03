/*
 * Created on 15.10.2004 by Steffen Dienst
 *
 */
package biochemie.domspec;

import java.util.Comparator;

import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.util.Helper;

public class SBESekStruktur extends SekStruktur{
    private boolean incomp=false;
    private boolean verh=false;

    protected  SBESekStruktur(SBEPrimer p,SBEPrimer other,int pos) {
        super(p,other,pos);
        init();
    }
    public SBESekStruktur(SBEPrimer p,int t, char einbau) {
        super(p,t,einbau);
        init();
    }
    protected SBESekStruktur(SBEPrimer p,int t, int pos) {
        super(p,t,pos);
        init();
    }

      /**
     *
     */
    private void init() {
        if( bautAn != 0){//vorgegeben, also hairpin, gibt keinen other primer
            incomp= (((SBEPrimer)p).getSNP().indexOf(bautAn) != -1);
            verh=false;
        }else{
            switch (type) {
            case HAIRPIN :
            case HOMODIMER :
                incomp=Helper.isInkompatibleSekStruktur(p.getSeq(),getPosFrom3(),((SBEPrimer)p).getSNP());
                int pl=((SBEPrimer)p).getBruchstelle();
                verh=getPosFrom3() == pl || getPosFrom3() == pl-1;
                break;
            case CROSSDIMER:
                //es sind zwei SNPs beteiligt, gegen die getestet werden muss.
                incomp=Helper.isInkompatibleSekStruktur(other.getSeq(),getPosFrom3(),((SBEPrimer)p).getSNP())
                              || Helper.isInkompatibleSekStruktur(other.getSeq(),getPosFrom3(),((SBEPrimer)other).getSNP());
                //bei crossdimern verhindert ja der PL des anderen Primers
                int plo=((SBEPrimer)other).getBruchstelle();
                verh=getPosFrom3() == plo || getPosFrom3() == plo-1;
                break;

            default :
                break;
            }
        }
    }

    public boolean isIncompatible() {
        return incomp;
    }
    public boolean isVerhindert() {
        return verh;
    }
    public boolean equals(Object obj) {
        if ( !(obj instanceof SBESekStruktur) ) {
            return false;
        }
        if(this==obj)
            return true;
        SBESekStruktur rhs = (SBESekStruktur) obj;
        boolean eq=true;
        eq=eq && (this.type==rhs.type)
              && (this.verh==rhs.verh)
              && (this.incomp==rhs.incomp)
              && type==rhs.type
              && p.equals(rhs.p);
        if(eq==true && CROSSDIMER == type) {  //betrachte Crossdimer mit anderen Primern als gleich, auch wenn die Bruchstelle nicht stimmt.
            eq=eq && other.equals(rhs.other);
        }
        return eq;
    }
    public int hashCode() {
            int hash= new HashCodeBuilder(17, 37).
               append("SBESekstruktur").
               append(p.getId()).
               append(((SBEPrimer)p).getBruchstelle()).
               append(pos).
               append(verh).
               append(type).
               toHashCode();
            return hash;
    }
    public String toString() {
        if( getType() != CROSSDIMER )
            return p.getId()+": "+p.getSeq()+",PL="+((SBEPrimer)p).getBruchstelle()+", "+(isIncompatible()?"in":"")+"compatible "+(HAIRPIN == getType()?"hairpin":"homodimer")+", pos="+getPosFrom3()+", bautein="+bautEin()+", "+(verh?"ir":"")+"relevant";
        else
            return p.getId()+"(PL="+((SBEPrimer)p).getBruchstelle()+") with "+other.getId()+"(PL="+((SBEPrimer)other).getBruchstelle()+"): "
                     +(isIncompatible()?"in":"")+"compatible crossdimer, "+(verh?"ir":"")+"relevant";
            //return other.getId()+": "+other.getSeq()+",PL= "+((SBEPrimer)other).getBruchstelle()+", "+(isIncompatible()?"in":"")+"compatible crossdimer with ID "+p.getId()+": "+p.getSeq()+",PL= "+((SBEPrimer)p).getBruchstelle();
    }
    /**
     * Liefert Comaprator, der Sekundaerstrukturen nach Ernsthaftigkeit sortiert, also
     * inkompatible Sekstruks vor kompatiblen.
     * @return
     */
    public static Comparator getSeverityComparator() {
        return new Comparator() {
            public int compare(Object arg0, Object arg1) {
                SBESekStruktur s1=(SBESekStruktur)arg0;
                SBESekStruktur s2=(SBESekStruktur)arg1;
                if(s1.isIncompatible())
                    return -1;
                if(s2.isIncompatible())
                    return 1;
                return 0;
            }
        };
    }


    /* (non-Javadoc)
     * @see biochemie.domspec.SekStruktur#getAsciiArt()
     */
    public String getAsciiArt() {
        String seq = Helper.replaceWithPL(p.getSeq(),((SBEPrimer)p).getBruchstelle());
        switch (type) {
        case HAIRPIN:
            return Helper.outputHairpin(seq,pos-1,seq.length());
        case HOMODIMER:
            return Helper.outputXDimer(seq,seq,seq.length() - pos,p.seq.length());
        case CROSSDIMER:
            String otherseq = Helper.replaceWithPL(other.getSeq(), ((SBEPrimer)other).getBruchstelle());
            return Helper.outputXDimer(seq,otherseq,seq.length()-pos,Math.min(seq.length(),otherseq.length()));

        default:
            return "unknown type of sec.struk encountered.";
        }
    }
}
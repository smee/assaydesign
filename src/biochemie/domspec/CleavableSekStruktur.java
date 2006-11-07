/*
 * Created on 15.10.2004 by Steffen Dienst
 *
 */
package biochemie.domspec;


import org.apache.commons.lang.builder.HashCodeBuilder;

public class CleavableSekStruktur extends SekStruktur{

    private boolean verh=false;

    protected  CleavableSekStruktur(CleavablePrimer p,CleavablePrimer other,int pos) {
        super(p,other,pos);
        init();
    }
    public CleavableSekStruktur(CleavablePrimer p,int t, char einbau) {
        super(p,t,einbau);
        init();
    }
    protected CleavableSekStruktur(CleavablePrimer p,int t, int pos) {
        super(p,t,pos);
        init();
    }

      /**
     *
     */
    private void init() {
        if( bautAn != 0){//vorgegeben, also hairpin, gibt keinen other primer
            incomp= (((CleavablePrimer)p).getSNP().indexOf(bautAn) != -1);
            verh=false;
        }else{
            switch (type) {
            case HAIRPIN :
            case HOMODIMER :
                int pl=((CleavablePrimer)p).getBruchstelle();
                verh=getPosFrom3() == pl || getPosFrom3() == pl-1;
                break;
            case CROSSDIMER:
                //bei crossdimern verhindert ja der PL des anderen Primers
                int plo=((CleavablePrimer)other).getBruchstelle();
                verh=getPosFrom3() == plo || getPosFrom3() == plo-1;
                break;

            default :
                break;
            }
        }
    }


    public boolean isVerhindert() {
        return verh;
    }
    public boolean equals(Object obj) {
        if ( !(obj instanceof CleavableSekStruktur) ) {
            return false;
        }
        if(this==obj)
            return true;
        CleavableSekStruktur rhs = (CleavableSekStruktur) obj;
        return this.verh==rhs.verh && super.equals(obj);
    }
    public int hashCode() {
            int hash= new HashCodeBuilder(977, 1523).
               appendSuper(super.hashCode()).
               append(((CleavablePrimer)p).getBruchstelle()).
               append(verh).
               toHashCode();
            return hash;
    }
    public String toString() {
        if( getType() != CROSSDIMER )
            return p.getId()+": "+p.getCompletePrimerSeq()+",PL="+((CleavablePrimer)p).getBruchstelle()+", "+(isIncompatible()?"in":"")+"compatible "+(HAIRPIN == getType()?"hairpin":"homodimer")+", pos="+getPosFrom3()+", bautein="+bautEin()+", "+(verh?"ir":"")+"relevant";
        else
            return p.getId()+"(PL="+((CleavablePrimer)p).getBruchstelle()+") with "+other.getId()+"(PL="+((CleavablePrimer)other).getBruchstelle()+"): "
                     +(isIncompatible()?"in":"")+"compatible crossdimer, "+(verh?"ir":"")+"relevant";
    }

}
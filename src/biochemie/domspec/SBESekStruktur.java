/*
 * Created on 15.10.2004 by Steffen Dienst
 *
 */
package biochemie.domspec;

import java.util.Comparator;
import java.util.Observable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.util.Helper;


public class SBESekStruktur extends SekStruktur{
    private boolean incomp=false;
    private boolean verh=false;
	private static final String VERH_CHANGED = "verhindert changed";
	private static final String INCOMP_CHANGED = "(in)comp. changed";
    
    protected  SBESekStruktur(SBEPrimer p,SBEPrimer other,int pos) {
        super(p,other,pos);
        init();
    }
    public SBESekStruktur(SBEPrimer p,int t, char einbau) {
        super(p,t,einbau);
    }
    protected SBESekStruktur(SBEPrimer p,int t, int pos) {
        super(p,t,pos);
        init();
    }
    
      /**
     * 
     */
    private void init() {
        if(0 != einbau){
            incomp= (-1 != ((SBEPrimer)other).getSNP().indexOf(einbau));
            verh=false;
        }else{
            switch (type) {
            case HAIRPIN :
            case HOMODIMER :
                incomp=Helper.isInkompatibleSekStruktur(p.getSeq(),getPosFrom3(),((SBEPrimer)p).getSNP());
                break;
            case CROSSDIMER:
                incomp=Helper.isInkompatibleSekStruktur(other.getSeq(),getPosFrom3(),((SBEPrimer)p).getSNP());
                break;
                
            default :
                break;
            }
            revalidate();
        }
    }
    private void revalidate() {
        SBEPrimer totest= (SBEPrimer) (type==CROSSDIMER?other:p);
        if(-1 != pos){
            boolean v= (pos == totest.getBruchstelle() 
                || pos == totest.getBruchstelle() - 1);
            if(verh != v){
            	setChanged();
            	notifyObservers(VERH_CHANGED);
            }
        }
       
        boolean inc= totest.getSNP().indexOf(einbau) != -1;
        if( incomp != inc){
        	incomp = inc;
        	setChanged();
        	notifyObservers(INCOMP_CHANGED);
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
              && (this.p.getId()==rhs.p.getId());
        if(CROSSDIMER == type) {  //betrachte Crossdimer mit anderen Primern als gleich, auch wenn die Bruchstelle nicht stimmt.
            eq=eq && (other.getId()==rhs.other.getId());
        }else {
            eq=eq && (((SBEPrimer)p).getBruchstelle()==((SBEPrimer)rhs.p).getBruchstelle());
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
//            System.out.println("{"+this+"} has hashcode "+hash);
            return hash;
    }
    public String toString() {
        if( getType() == CROSSDIMER )
            return other.getId()+": "+other.getSeq()+",PL= "+((SBEPrimer)other).getBruchstelle()+", "+(isIncompatible()?"in":"")+"compatible crossdimer with ID "+p.getId()+": "+p.getSeq()+",PL= "+((SBEPrimer)p).getBruchstelle();
        return p.getId()+": "+p.getSeq()+",PL="+((SBEPrimer)p).getBruchstelle()+", "+(isIncompatible()?"in":"")+"compatible "+(HAIRPIN == getType()?"hairpin":"homodimer")+", pos="+getPosFrom3()+", bautein="+bautEin()+", "+(verh?"ir":"")+"relevant";
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
    
    public void update(Observable o, Object arg) {
        super.update(o, arg);
        revalidate();
    }
    /* (non-Javadoc)
     * @see biochemie.domspec.SekStruktur#getAsciiArt()
     */
    public String getAsciiArt() {
        String seq = Helper.replacePL(p.getSeq(),((SBEPrimer)p).getBruchstelle());
        switch (type) {
        case HAIRPIN:
            return Helper.outputHairpin(seq,pos-1,seq.length());
        case HOMODIMER:
            return Helper.outputXDimer(seq,seq,seq.length() - pos,p.seq.length());
        case CROSSDIMER:
            String otherseq = Helper.replacePL(other.getSeq(), ((SBEPrimer)other).getBruchstelle());
            return Helper.outputXDimer(seq,otherseq,seq.length() - pos,Math.min(seq.length(),otherseq.length()));

        default:
            return "unknown type of sec.struk encountered.";
        }
    }
}
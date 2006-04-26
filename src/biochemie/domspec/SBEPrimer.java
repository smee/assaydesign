/*
 * Created on 30.11.2004
 *
 */
package biochemie.domspec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.calcdalton.CalcDalton;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.Helper;
import biochemie.util.edges.CalcDaltonEdge;
import biochemie.util.edges.ProductLengthEdge;


/**
 * Diese Klasse enthält alle Infos zu einem möglichen Primer, wie Typ, Sek.strukturen etc.
 *
 * @author Steffen
 *
 */
public class SBEPrimer extends Primer{

    private final int pl;
    final SBEOptions cfg;
    private final int productlen;


    public SBEPrimer(SBEOptions cfg,String id,String seq,String snp, String type, String bautein, int prodlen,boolean usergiven) {
        this(cfg,id,seq,Helper.getPosOfPl(seq),snp,type,bautein,prodlen,usergiven);
    }
    /**
     * This constructor needs a L within the sequence to determine the position of the photolinker
     * @param cfg SBEOptionsProvider
     * @param id unique id
     * @param seq String consisting of ACGT and max. one L which specifies the pl
     * @param repl nucleotide that was replaced by the photolinker
     * @param snp String of ACGT
     * @param type SBEPrimer._5_ or SBEPrimer._3_
     * @param bautein String of ACGT
     * @param prodlen Length of the sbeproduct
     * @param usergiven true: don't probe for secstructures.
     */
    public SBEPrimer(SBEOptions cfg,String id,String seq, int pl, String snp, String type, String bautein, int prodlen,boolean usergiven) {
        super(id,seq, type,snp,cfg.getSecStrucOptions());
        this.cfg = cfg;
        this.pl=pl;
        if(pl == -1)
            throw new IllegalArgumentException("Sequence of primer "+id+" has no L within sequence!");
        this.productlen= prodlen;
        init(bautein);
	}

    /**
     * @return
     */
    public boolean hasInkompatibleHomodimer() {
        boolean ret=((Boolean)Algorithms.inject(getSecStrucs().iterator(),Boolean.FALSE,new BinaryFunction() {
            public Object evaluate(Object seed, Object sek) {
                SBESekStruktur s=(SBESekStruktur)sek;
                return Boolean.valueOf(((Boolean)seed).booleanValue() ||  (SBESekStruktur.HOMODIMER == s.getType() && s.isIncompatible()));
            }
        })).booleanValue();
        return ret;
    }
    /**
     * @return
     */
    public boolean hasInkompatibleHairpins() {
        boolean ret=((Boolean)Algorithms.inject(sekstruc.iterator(),Boolean.FALSE,new BinaryFunction() {
            public Object evaluate(Object seed, Object sek) {
                SBESekStruktur s=(SBESekStruktur)sek;
                return Boolean.valueOf(((Boolean) seed).booleanValue() || SBESekStruktur.HAIRPIN == s.getType() && true == s.isIncompatible());
            }
        })).booleanValue();
        return ret;
    }

    /**
     * @param primer
     * @param type
     * @param bautein
     */
    private void init(String bautein) {
        if(bautein.length()!=0){
            sekstruc=new HashSet();
            if(!bautein.equalsIgnoreCase("none")){
                for (int i = 0; i < bautein.length(); i++) {
                    sekstruc.add(new SBESekStruktur(this,SBESekStruktur.HAIRPIN,bautein.charAt(i)));
                }
            }
        }
        temp=Helper.calcTM(getCompletePrimerSeq());//temperatur mit pl ist anders als ohne
        gcgehalt=Helper.getXGehalt(getCompletePrimerSeq(),"CcGg");//wegen pl
    }


    public int getBruchstelle() {
        return pl;
    }

    public boolean equals(Object o){
        if ( !(o instanceof SBEPrimer) ) {
            return false;
        }else {
            SBEPrimer other = (SBEPrimer)o;
            return getId().equals(other.getId())
                    && getBruchstelle()==other.getBruchstelle()
                    && getType().equals(other.getType());
        }
    }
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
           append(getId()).
           append(getCompletePrimerSeq()).
           append(getBruchstelle()).
           toHashCode();
}

    public boolean passtMit(Multiplexable o) {
        edgecol.clear();
        if(o instanceof SBEPrimer) {
            SBEPrimer other=(SBEPrimer) o;
            //dumm, aber nur so umgehe ich den kurzschlussoperator &&....
            boolean flag= super.passtMit(other);
            boolean temp=true;
            temp=passtMitProductLength(other) && flag;
            flag=flag&&temp;
            temp=passtMitCalcDalton(other);
            flag=flag&&temp;
            return flag;
        }else {//keine Ahnung, wie ich mich mit dem vergleichen soll, is ja kein Primer...
            return super.passtMit(o);
        }
    }
    /**
     * Testet, ob dieser Primer mit other passt, wobei nur inkompatible Crossdimer beruecksichtigt werden.
     * @param other
     * @return
     */
    public boolean passtMitKompCD(SBEPrimer other) {
        edgecol.clear();
        boolean flag=true, temp=true;
        flag= passtMitID(other);
        temp= passtMitProductLength(other);
        flag=flag&&temp;
        temp= passtMitSekStrucs(other) ;
        flag=flag&&temp;
        temp= passtMitCrossdimern(other,false) ;
        flag=flag&&temp;
        temp= passtMitCalcDalton(other);
        flag=flag&&temp;
        return flag;
    }
    private boolean passtMitProductLength(SBEPrimer other) {
        //Produktlänge
        int prdiff=productlen-other.productlen;
        if(Math.abs(prdiff)<cfg.getMinProductLenDiff()) {
            edgecol.add(new ProductLengthEdge(this,other,prdiff));
            return false;    //Produktlängenunterschied zu gering
        }
        return true;
    }
    /**
     * @param struct
     * @param other
     * @return
     */
    protected boolean passtMitCalcDalton(SBEPrimer other) {
        CalcDalton cd=Helper.getCalcDalton();
        String[][] sbedata= createCDParameters(this, other);
        int[] br = cfg.getPhotolinkerPositions();
		int[] fest=new int[] {ArrayUtils.indexOf(br,this.getBruchstelle())
                			 ,ArrayUtils.indexOf(br,other.getBruchstelle())};
        if(0 == cd.calc(sbedata, fest).length) {
            edgecol.add(new CalcDaltonEdge(this,other));
            return false;
        }
        return true;
    }

    public String getName() {
        return getId()+'_'+getBruchstelle()+'_'+getType();
    }
    public String toString() {
        
        return getId()+":"+getCompletePrimerSeq()+", "+getType()+", PL="+getBruchstelle()+
        ", GC="+Helper.format(getGCGehalt())+"%"+
        ", Tm="+Helper.format(getTemperature())+"°, hairpins="
        +getHairpinPositions()+", homodimer="+getHomodimerPositions();
    }
    public String getCSVSekStructuresSeparatedBy(String sep) {
        List l=new ArrayList(getSecStrucs());
        Collections.sort(l,SBESekStruktur.getSeverityComparator());
        String positions="";
        String nucl="";
        String clazz="";
        String irrel="";
        for (Iterator it = l.iterator(); it.hasNext();) {
            SBESekStruktur s = (SBESekStruktur) it.next();
            int pos=s.getPosFrom3();
            if(-1 == pos)
                positions+="unknown";
            else
                positions+=pos;
            if(Character.toUpperCase(s.bautEin())=='K')//anti pl
                nucl+=" - ";
            else
                nucl+="dd"+Character.toUpperCase(s.bautEin());
            switch (s.getType()) {
            case SBESekStruktur.HAIRPIN:
                clazz+="hairpin, ";
                break;
            case SBESekStruktur.HOMODIMER:
                clazz+="homodimer, ";
                break;
            case SBESekStruktur.CROSSDIMER:
                clazz+="crossdimer with ID ";
            	clazz+=s.getCrossDimerPrimer().getId()+", ";
                break;
            default:
                break;
            }
            clazz+=s.isIncompatible()?"incompatible":"compatible";
            //TODO in der Ausgabe: bei irrel.: no bleibt, bei yes: PL oder Multiplex
            if(s.isVerhindert()){
                irrel+="PL=3'";
                if(1 == getBruchstelle() - s.getPosFrom3())
                    irrel+="+1";
            }else{
                irrel+="no";
            }
            if(it.hasNext()){
                positions+=", ";
                nucl+=", ";
                clazz+=", ";
                irrel+=", ";
            }
        }
        return positions+sep+nucl+sep+clazz+sep+irrel;
    }


    public String getCompletePrimerSeq() {
        String seq=super.getPrimerSeq();
        return Helper.replaceWithPL(seq,pl);
    }
}
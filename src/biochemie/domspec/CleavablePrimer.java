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
import java.util.Set;

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
import biochemie.util.edges.SecStructureEdge;


/**
 * Diese Klasse enthält alle Infos zu einem möglichen Primer, wie Typ, Sek.strukturen etc.
 *
 * @author Steffen
 *
 */
public class CleavablePrimer extends Primer{

    private final int pl;
    final SBEOptions cfg;


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
    public CleavablePrimer(SBEOptions cfg,String id,String seq,String snp, String type, String bautein, int prodlen,boolean usergiven) {
        this(cfg,id,seq,Helper.getPosOfPl(seq),snp,type,bautein,prodlen,usergiven);
    }
    /**
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
    public CleavablePrimer(SBEOptions cfg,String id,String seq, int pl, String snp, String type, String bautein, int prodlen,boolean usergiven) {
        super(id,seq, type,snp,prodlen, cfg.getSecStrucOptions(),cfg.getMinProductLenDiff());
        this.cfg = cfg;
        this.pl=pl;
        assert ArrayUtils.indexOf(cfg.getPhotolinkerPositions(),pl)!=-1;
        if(pl == -1)
            throw new IllegalArgumentException("Sequence of primer "+id+" has no L within sequence!");
        init(bautein);
	}

    /**
     * @return
     */
    public boolean hasInkompatibleHomodimer() {
        boolean ret=((Boolean)Algorithms.inject(getSecStrucs().iterator(),Boolean.FALSE,new BinaryFunction() {
            public Object evaluate(Object seed, Object sek) {
                CleavableSekStruktur s=(CleavableSekStruktur)sek;
                return Boolean.valueOf(((Boolean)seed).booleanValue() ||  (CleavableSekStruktur.HOMODIMER == s.getType() && s.isIncompatible()));
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
                CleavableSekStruktur s=(CleavableSekStruktur)sek;
                return Boolean.valueOf(((Boolean) seed).booleanValue() || CleavableSekStruktur.HAIRPIN == s.getType() && true == s.isIncompatible());
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
                    sekstruc.add(new CleavableSekStruktur(this,CleavableSekStruktur.HAIRPIN,bautein.charAt(i)));
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
        if ( !(o instanceof CleavablePrimer) ) {
            return false;
        }else {
            CleavablePrimer other = (CleavablePrimer)o;
            return getBruchstelle()==other.getBruchstelle()
                    && super.equals(other);
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
        if(o instanceof CleavablePrimer) {
            CleavablePrimer other=(CleavablePrimer) o;
            //dumm, aber nur so umgehe ich den kurzschlussoperator &&....
            boolean flag= super.passtMit(other);
            boolean temp=true;
            temp=passtMitProductLength(other) && flag;
            flag=flag&&temp;
            return flag;
        }else {//keine Ahnung, wie ich mich mit dem vergleichen soll, is ja kein Primer...
            return super.passtMit(o);
        }
    }
    
    /**
     * @param struct
     * @param other
     * @return
     */
    protected boolean passtMitCalcDalton(Primer other) {
        if(other instanceof CleavablePrimer){
            CalcDalton cd=Helper.getCalcDalton();
            String[][] sbedata= createCDParameters(this, other);
            int[] br = cfg.getPhotolinkerPositions();
            int[] fest=new int[] {ArrayUtils.indexOf(br,this.getBruchstelle())
                    ,ArrayUtils.indexOf(br,((CleavablePrimer)other).getBruchstelle())};
            if(0 == cd.calc(sbedata, fest).length) {
                edgecol.add(new CalcDaltonEdge(this,other));
                return false;
            }
            return true;
        }else
            return super.passtMitCalcDalton(other);
    }
    
    protected boolean passtMitCDRec(Primer me, Primer other, boolean evilcd) {
        Set cross=SekStrukturFactory.getCrossdimer(me,other,cfg.getSecStrucOptions());
        for (Iterator it = cross.iterator(); it.hasNext();) {
            SekStruktur s = (SekStruktur) it.next();
            if(s instanceof CleavableSekStruktur && ((CleavableSekStruktur)s).isVerhindert())
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

    public String getName() {
        return getId()+'_'+getBruchstelle()+'_'+getType();
    }
    public String toString() {
        return new StringBuffer(super.toString()).append(", PL=").append(getBruchstelle()).toString();
    }
    public String getCSVSekStructuresSeparatedBy(String sep) {
        List l=new ArrayList(getSecStrucs());
        Collections.sort(l,CleavableSekStruktur.getSeverityComparator());
        String positions="";
        String nucl="";
        String clazz="";
        String irrel="";
        for (Iterator it = l.iterator(); it.hasNext();) {
            CleavableSekStruktur s = (CleavableSekStruktur) it.next();
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
            case CleavableSekStruktur.HAIRPIN:
                clazz+="hairpin, ";
                break;
            case CleavableSekStruktur.HOMODIMER:
                clazz+="homodimer, ";
                break;
            case CleavableSekStruktur.CROSSDIMER:
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

    protected boolean passtMitSekStrucs(Primer other) {
        if(!cfg.isSecStrucEdgeCreating())
            return true;
        //Inkompatible Sekundärstrukturen?
        String snp1=getSNP();
        String snp2=other.getSNP();
        for (Iterator it = getSecStrucs().iterator(); it.hasNext();) {
            SekStruktur s = (SekStruktur) it.next();
            if(s instanceof CleavableSekStruktur && ((CleavableSekStruktur)s).isVerhindert())
                continue;
            if(-1 != snp2.indexOf(s.bautEin())){
                edgecol.add(new SecStructureEdge(this,other, s));
                return false;
            }
        }
        for (Iterator it = other.getSecStrucs().iterator(); it.hasNext();) {
            SekStruktur s = (SekStruktur) it.next();
            if(s instanceof CleavableSekStruktur && ((CleavableSekStruktur)s).isVerhindert())
                continue;
            if(snp1.indexOf(s.bautEin()) != -1){
                edgecol.add(new SecStructureEdge(other,this, s));
                return false;
            }
        }
        return true;
    }
    
    public String getCompletePrimerSeq() {
        String seq=super.getPrimerSeq();
        return Helper.replaceWithPL(seq,pl);
    }
    public String getFilter() {
        return getType()+"_"+getPrimerSeq().length()+"_"+getBruchstelle();
    }
}
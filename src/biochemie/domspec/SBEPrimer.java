/*
 * Created on 30.11.2004
 *
 */
package biochemie.domspec;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.calcdalton.CalcDalton;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.io.SBEConfig;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.Helper;


/**
 * Diese Klasse enthält alle Infos zu einem möglichen Primer, wie Typ, Sek.strukturen etc.
 *
 * @author Steffen
 *
 */
public class SBEPrimer extends Primer{

    public static final String _5_ = "5";
    public static final String _3_ = "3";
    private final int pl;
    private final String type;
    private final String snp;
    private final SBEOptions cfg;
    private final int productlen;


    public void setPlexID(String s) {
        super.setPlexID(s);
    }
    /**
     * This constructor needs a L within the sequence to determine the position of the photolinker
     * @param cfg SBEOptionsProvider
     * @param id unique id
     * @param seq String consisting of ACGT and max. one L which specifies the pl
     * @param snp String of ACGT
     * @param type SBEPrimer._5_ or SBEPrimer._3_
     * @param bautein String of ACGT
     * @param prodlen Length of the sbeproduct
     * @param usergiven tru: don't probe for secstructures.
     */
    public SBEPrimer(SBEOptions cfg,String id,String seq, String snp, String type, String bautein, int prodlen,boolean usergiven) {
        super(id,seq);
        this.cfg = cfg;
        this.pl=Helper.getPosOfPl(seq);
        if(pl == -1)
            throw new IllegalArgumentException("Sequence of primer "+id+" has no L within sequence!");

        this.productlen= prodlen;
        this.type=type;
        if(type.indexOf(_3_) != -1)
            this.snp=Helper.complPrimer(snp);
        else
            this.snp=snp;
        init(bautein, usergiven);
	}
	public Set getSecStrucs() {
		if(sekstruc == null)
			sekstruc = SekStrukturFactory.getSecStruks(this,cfg);
		return Collections.unmodifiableSet(sekstruc);
	}

    /**
     * @return
     */
    public boolean hasInkompatibleHomodimer() {
        boolean ret=((Boolean)Algorithms.inject(sekstruc.iterator(),Boolean.FALSE,new BinaryFunction() {
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
    private void init(String bautein,boolean usergiven) {
        if(usergiven==false && bautein.length()==0){
            int incompHairpin=0,incompHomodimer=0;
            HashSet positions=new HashSet();
            for (Iterator it = getSecStrucs().iterator(); it.hasNext();) {
            	SBESekStruktur s = (SBESekStruktur) it.next();
                if(Helper.isInkompatibleSekStruktur(seq,s.getPosFrom3(),snp)) {
                    switch (s.getType()) {
                        case SBESekStruktur.HAIRPIN :
                            incompHairpin++;
                            positions.add(new Integer(s.getPosFrom3()));
                            break;
                        case SBESekStruktur.HOMODIMER :
                            incompHomodimer++;
                            positions.add(new Integer(s.getPosFrom3()));
                            break;
                        default :
                            break;
                    }
                }
            }
        }else{//es wurde schon was vorgegeben
            sekstruc=new HashSet();
            if(!bautein.equalsIgnoreCase("none")){
                for (int i = 0; i < bautein.length(); i++) {
                    sekstruc.add(new SBESekStruktur(this,SBESekStruktur.HAIRPIN,bautein.charAt(i)));
                }
            }
        }
    }


    public String getType() {
        return type;
    }

    public int getBruchstelle() {
        return pl;
    }
    public String getSNP() {
        return this.snp;
    }

    public boolean equals(Object o){
        if ( !(o instanceof Primer) ) {
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
           append(getSeq()).
           append(getBruchstelle()).
           toHashCode();
}

    public boolean passtMit(Multiplexable o) {
        if(o instanceof SBEPrimer) {
            SBEPrimer other=(SBEPrimer) o;
            return passtMitID(other)
                && passtMitProductLength(other)
                && passtMitSekStrucs(other) 
                && passtMitCrossdimern(other,true) 
                && passtMitCalcDalton(other);
        }else {//keine Ahnung, wie ich mich mit dem vergleichen soll, is ja kein Primer...
            boolean ret= o.passtMit(this);
            edgereason=o.getEdgeReason();
            return ret;
        }
    }
    /**
     * Testet, ob dieser Primer mit other passt, wobei nur inkompatible Crossdimer beruecksichtigt werden.
     * @param other
     * @return
     */
    public boolean passtMitKompCD(SBEPrimer other) {
        return passtMitID(other)
        && passtMitProductLength(other)
        && passtMitSekStrucs(other) 
        && passtMitCrossdimern(other,false) 
        && passtMitCalcDalton(other);
    }
    public boolean passtMitID(SBEPrimer other) {
        if(other.getId().equals(this.getId())) {
            edgereason="same";
            return false;   //derselbe SBECandidate
        }
        return true;
    }
    public boolean passtMitProductLength(SBEPrimer other) {
        //Produktlänge
        int prdiff=productlen-other.productlen;
        if(Math.abs(prdiff)<cfg.getMinProductLenDiff()) {
            edgereason="productlendiff="+prdiff;
            return false;    //Produktlängenunterschied zu gering
        }
        return true;
    }
    public boolean passtMitSekStrucs(SBEPrimer other) {
        //Inkompatible Sekundärstrukturen?
        String snp1=getSNP();
        String snp2=other.getSNP();
        for (Iterator it = sekstruc.iterator(); it.hasNext();) {
            SBESekStruktur s = (SBESekStruktur) it.next();
            if(s.isVerhindert())
                continue;
            if(-1 != snp2.indexOf(s.bautEin())){
                edgereason="incomp. Sekstructure";
                return false;
            }
        }
        for (Iterator it = other.sekstruc.iterator(); it.hasNext();) {
            SBESekStruktur s = (SBESekStruktur) it.next();
            if(s.isVerhindert())
                continue;
            if(snp1.indexOf(s.bautEin()) != -1){
                edgereason="incomp. Sekstructure";
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
    public boolean passtMitCrossdimern(SBEPrimer other, boolean evilcd) {
        //Crossdimer?
        Set cross1=SekStrukturFactory.getCrossdimer(this,other,cfg);
        //this.sekstruc.addAll(cross1);            				//soll bei jeweils dem Primer verzeichnet werden, der einbaut
        Set cross2=SekStrukturFactory.getCrossdimer(other,this,cfg);
        //other.sekstruc.addAll(cross2);
        cross1.addAll(cross2);//reusing vars
        for (Iterator it = cross1.iterator(); it.hasNext();) {
            SBESekStruktur s = (SBESekStruktur) it.next();
            if(s.isVerhindert())
                continue;
            if(!evilcd) {
                if(s.isIncompatible()) {
                    edgereason="incomp. Crossdimer";
                    return false;
                }
            }else {
                edgereason="Crossdimer";
                return false;
            }
        }
        return true;
    }
    /**
     * @param struct
     * @param other
     * @return
     */
    public boolean passtMitCalcDalton(SBEPrimer other) {
        CalcDalton cd=Helper.getCalcDalton(cfg);
        String[][] sbedata= {{this.getSeq(),"A","C","G","T"}
        					,{other.getSeq(),"A","C","G","T"}};
        int[] br = cfg.getPhotolinkerPositions();
		int[] fest=new int[] {ArrayUtils.indexOf(br,this.getBruchstelle())
                			 ,ArrayUtils.indexOf(br,other.getBruchstelle())};
        if(0 == cd.calc(sbedata, fest).length) {
            edgereason="CalcDalton";
            return false;
        }
        return true;
    }

    public String getName() {
        return getId()+'_'+getBruchstelle()+'_'+getType();
    }
    public String toString() {
        DecimalFormat nf=new DecimalFormat("#.##");
        return getSeq()+", "+getType()+", PL="+getBruchstelle()+", temperature="+nf.format(getTemperature())+"°, hairpins="+getHairpinPositions()+", homodimer="+getHomodimerPositions();
    }
    public String getCSVSekStructuresSeparatedBy(String sep) {
        List l=new ArrayList(sekstruc);
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


    public int maxPlexSize() {
        return cfg.getMaxPlex();
    }
    /**
     * Adds crossdimers with the primers in the given set to the internal memory
     * of secondary structures.
     */
    public void normalizeCrossdimers(Collection primers) {
        if(sekstruc == null)
            return; //nothing to normalize
        getSecStrucs();
        
        for (Iterator it = primers.iterator(); it.hasNext();) {
            SBEPrimer p = (SBEPrimer) it.next();
            if(p!=this)
            	sekstruc.addAll(SekStrukturFactory.getCrossdimer(this,p,cfg));
        }
    }
}
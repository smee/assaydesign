/*
 * Created on 30.11.2004
 *
 */
package biochemie.domspec;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
import biochemie.sbe.MiniSBE;
import biochemie.sbe.SBEOptionsProvider;
import biochemie.sbe.calculators.Multiplexable;
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
    private int pl;
    private final String type;
    private final String snp;
    private final SBEOptionsProvider cfg;
    private final int productlen;
    
	public static final String PL_CHANGED = "photolinker changed";
    
    public void setPlexID(String s) {
        super.setPlexID(s);
    }
    public SBEPrimer(SBEOptionsProvider cfg,String id,String seq, String snp, String type, String bautein, int prodlen,boolean usergiven) {
        super(id,seq);
        this.cfg = cfg;
        this.pl=-1;
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
        boolean hh= bautein.length() == 0 && !bautein.equalsIgnoreCase("none");
        if(hh && !usergiven){
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
            int[] br =cfg.getPhotolinkerPositions();
            if(0 < positions.size()) {
                int pos=((Integer)positions.iterator().next()).intValue();
                int bruchmax=Helper.findMaxIn(br);
                int bruchmin=Helper.findMinIn(br);
                if (pos > bruchmax)
                    pos -= 1; //darf max. 1 Nukleotid Richtung 3' gehen
                if (pos >=bruchmin && pos <= bruchmax) {//ich kann diese Sek,struktur verhindern!
                    setBruchstelle(pos);
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
    public void setBruchstelle(int i) {
        if(pl != -1)
            throw new IllegalStateException("photolinker already set! Attempted to set it twice. Error in Program!");
        
        pl=i;
        revalidate();
        setChanged();
        notifyObservers(PL_CHANGED);
    }
    public String getSNP() {
        return this.snp;
    }
    /**
     * Testet, inwiefern sich ein Photolinker auf die Sekundärstrukturen dieses Kandidaten auswirken.
     */
    private void revalidate() {
        StringBuffer sb= new StringBuffer(getSeq());
        sb.deleteCharAt(sb.length() - getBruchstelle());
        
        temp=Helper.calcTM(sb.toString());
        setChanged();
        notifyObservers(TEMP_CHANGED);
        
        gcgehalt=Helper.getXGehalt(sb.toString(),"GgCc");
        setChanged();
        notifyObservers(GC_CHANGED);
    }

    public boolean equals(Object o){
        if ( !(o instanceof Primer) ) {
            return false;
        }
        SBEPrimer rhs = (SBEPrimer) o;
        return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(this.getId(), rhs.getId())
        .append(getBruchstelle(), rhs.getBruchstelle())
        .isEquals();    
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
            if(other.getId().equals(this.getId())) {
                edgereason="same";
                return false;	//derselbe SBECandidate
            }
            //Produktlänge
            int prdiff=productlen-other.productlen;
            if(Math.abs(prdiff)<cfg.getMinProductLenDiff()) {
                edgereason="productlendiff="+prdiff;
                return false;    //Produktlängenunterschied zu gering
            }
            
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
            return passtMitCrossdimern(other) && passtMitCalcDalton(other);
        }else {
            boolean ret= o.passtMit(this);//keine Ahnung, wie ich mich mit dem vergleichen soll, is ja kein Primer...
            edgereason=o.getEdgeReason();
            return ret;
        }
    }
    /**
     * @param other
     * @return
     */
    protected boolean passtMitCrossdimern(SBEPrimer other) {
        //Crossdimer?
        Set cross1=SekStrukturFactory.getCrossdimer(this,other,cfg);
        other.sekstruc.addAll(cross1);            				//soll bei jeweils dem Primer verzeichnet werden, der einbaut
        Set cross2=SekStrukturFactory.getCrossdimer(other,this,cfg);
        this.sekstruc.addAll(cross2);
        cross1.addAll(cross2);
        for (Iterator it = cross1.iterator(); it.hasNext();) {
            SBESekStruktur s = (SBESekStruktur) it.next();
            if(s.isVerhindert())
                continue;
            if(!cfg.getAllCrossdimersAreEvil()) {
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
    protected boolean passtMitCalcDalton(SBEPrimer other) {
        CalcDalton cd=MiniSBE.getCalcDalton(cfg);
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

    /**
     * testet, ob einer der Nukleotide in <code>nukl </code> in <code>snp</code> enthalten ist.
     * @param nukl
     * @param snp
     * @return
     */
    boolean isIncompatible(String nukl, String snp) {
        for (int i = 0; i < nukl.length(); i++) {
            if(-1 != snp.indexOf(nukl.charAt(i)))
                return true;
        }
        return false;
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
}
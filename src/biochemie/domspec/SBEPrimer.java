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
import java.util.TreeSet;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.calcdalton.CalcDalton;
import biochemie.calcdalton.CalcDaltonOptions;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.Helper;
import biochemie.util.edges.CalcDaltonEdge;
import biochemie.util.edges.IdendityEdge;
import biochemie.util.edges.ProductLengthEdge;
import biochemie.util.edges.SecStructureEdge;


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
    private char repl;


    public SBEPrimer(SBEOptions cfg,String id,String seq,String snp, String type, String bautein, int prodlen,boolean usergiven) {
        this(cfg,id,seq,'0',snp,type,bautein,prodlen,usergiven);
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
     * @param usergiven tru: don't probe for secstructures.
     */
    public SBEPrimer(SBEOptions cfg,String id,String seq, char repl, String snp, String type, String bautein, int prodlen,boolean usergiven) {
        super(id,seq);
        this.cfg = cfg;
        this.repl=repl;
        this.pl=Helper.getPosOfPl(seq);
        if(pl == -1)
            throw new IllegalArgumentException("Sequence of primer "+id+" has no L within sequence!");

        this.productlen= prodlen;
        this.type=type;
        if(type.equals(_3_)) {
            this.snp=Helper.complPrimer(snp);
        }else {
            this.snp=snp;
        }
        init(bautein, usergiven);
	}
	public Set getSecStrucs() {
		if(sekstruc == null)
			sekstruc = SekStrukturFactory.getSecStruks(this,cfg);
		return Collections.unmodifiableSet(sekstruc);
	}
    public void setPlexID(String s) {
        super.setPlexID(s);
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
            sekstruc = null;
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
           append(getSeq()).
           append(getBruchstelle()).
           toHashCode();
}

    public boolean passtMit(Multiplexable o) {
        edgecol=new HashSet();
        if(o instanceof SBEPrimer) {
            SBEPrimer other=(SBEPrimer) o;
            //dumm, aber nur so umgehe ich den kurzschlussoperator &&....
            boolean flag= true;
            boolean temp=true;
            temp=passtMitID(other);
            flag=flag&&temp;
            temp=passtMitProductLength(other) && flag;
            flag=flag&&temp;
            temp=passtMitSekStrucs(other)  && flag;
            flag=flag&&temp;
            temp=passtMitCrossdimern(other,true); 
            flag=flag&&temp;
            temp=passtMitCalcDalton(other);
            flag=flag&&temp;
            return flag;
        }else {//keine Ahnung, wie ich mich mit dem vergleichen soll, is ja kein Primer...
            boolean ret= o.passtMit(this);
            edgecol=o.getLastEdges();
            return ret;
        }
    }
    /**
     * Testet, ob dieser Primer mit other passt, wobei nur inkompatible Crossdimer beruecksichtigt werden.
     * @param other
     * @return
     */
    public boolean passtMitKompCD(SBEPrimer other) {
        edgecol=new HashSet();
        return passtMitID(other)
        && passtMitProductLength(other)
        && passtMitSekStrucs(other) 
        && passtMitCrossdimern(other,false) 
        && passtMitCalcDalton(other);
    }
    private boolean passtMitID(SBEPrimer other) {
        if(other.getId().equals(this.getId())) {
            edgecol.add(new IdendityEdge(this,other));
            return false;   //derselbe SBECandidate
        }
        return true;
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
    private boolean passtMitSekStrucs(SBEPrimer other) {
        //Inkompatible Sekundärstrukturen?
        String snp1=getSNP();
        String snp2=other.getSNP();
        for (Iterator it = sekstruc.iterator(); it.hasNext();) {
            SBESekStruktur s = (SBESekStruktur) it.next();
            if(s.isVerhindert())
                continue;
            if(-1 != snp2.indexOf(s.bautEin())){
                edgecol.add(new SecStructureEdge(this,other, s));
                return false;
            }
        }
        for (Iterator it = other.sekstruc.iterator(); it.hasNext();) {
            SBESekStruktur s = (SBESekStruktur) it.next();
            if(s.isVerhindert())
                continue;
            if(snp1.indexOf(s.bautEin()) != -1){
                edgecol.add(new SecStructureEdge(other,this, s));
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
    protected boolean passtMitCrossdimern(SBEPrimer other, boolean evilcd) {
        return passtMitCDRec(this,other,evilcd) && passtMitCDRec(other,this,evilcd);//damit der crossdimer auch dem richtigen primer zugeordnet werden kann
    }
    private boolean passtMitCDRec(SBEPrimer me,SBEPrimer other, boolean evilcd) {
        boolean retval=true;
        Set cross=SekStrukturFactory.getCrossdimer(me,other,cfg);
        for (Iterator it = cross.iterator(); it.hasNext();) {
            SBESekStruktur s = (SBESekStruktur) it.next();
            if(s.isVerhindert())
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
    /**
     * @param struct
     * @param other
     * @return
     */
    protected boolean passtMitCalcDalton(SBEPrimer other) {
        CalcDalton cd=Helper.getCalcDalton(cfg);
        String[][] sbedata= createCDParameters(this, other, cfg);
        int[] br = cfg.getPhotolinkerPositions();
		int[] fest=new int[] {ArrayUtils.indexOf(br,this.getBruchstelle())
                			 ,ArrayUtils.indexOf(br,other.getBruchstelle())};
        if(0 == cd.calc(sbedata, fest).length) {
            edgecol.add(new CalcDaltonEdge(this,other));
            return false;
        }
        return true;
    }

    public static String[][] createCDParameters(SBEPrimer p1, SBEPrimer p2, CalcDaltonOptions cfg) {
//        if(cfg.getCalcDaltonAllExtensions())
//            return new String[][]{
//                {p1.getSeq(),"A","C","G","T"}
//               ,{p2.getSeq(),"A","C","G","T"}};
//        else
            return new String[][] {getCDParamLine(p1), getCDParamLine(p2)};
            
    }
    
    public static String[] getCDParamLine(SBEPrimer p) {
        Set chars=new TreeSet();
        String snp=p.getSNP();
        for(int i=0;i<snp.length();i++)
            chars.add(new Character(snp.charAt(i)));
        Set sekstrucs1=p.getSecStrucs();
        for (Iterator it = sekstrucs1.iterator(); it.hasNext();) {
            SBESekStruktur s = (SBESekStruktur) it.next();
            if(s.getType()==SBESekStruktur.HAIRPIN || s.getType()==SBESekStruktur.HOMODIMER)
                chars.add(new Character(Character.toUpperCase(s.bautEin())));
        }
        char[] attachments=new char[] {'A','C','G','T'};
        String[] arr=new String[attachments.length+1];
        arr[0]=p.getSeq();
        for (int i = 0; i < attachments.length; i++) {
            arr[i+1]="";
            if(!chars.contains(new Character(attachments[i])))
                arr[i+1]=">";
            arr[i+1]+=attachments[i];
        }
        return arr;
    }
    public String getName() {
        return getId()+'_'+getBruchstelle()+'_'+getType();
    }
    public String toString() {
        DecimalFormat nf=new DecimalFormat("#.##");
        return getId()+":"+getSeq()+", "+getType()+", PL="+getBruchstelle()+
        ", GC="+nf.format(getGCGehalt())+"%"+
        ", Tm="+nf.format(getTemperature())+"°, hairpins="
        +getHairpinPositions()+", homodimer="+getHomodimerPositions();
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
    /**
     * Returns the sequence without the PL if possible.
     * @return
     */
    public String getSeqWOPl() {
        if(repl!='0')
            return Helper.replacePL(getSeq(),repl);
        return getSeq();
    }
}
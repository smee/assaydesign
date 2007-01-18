package biochemie.sbe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.core.IsNull;

import biochemie.domspec.CleavablePrimer;
import biochemie.domspec.CleavableSekStruktur;
import biochemie.domspec.Primer;
import biochemie.util.Helper;

/*
 * Created on 20.11.2003
 *
 */
/**
 *
 * @author Steffen
 *
 */
/*
 *
 * @author Steffen
 */
public class CleavablePrimerFactory extends PrimerFactory {

    public static final String CSV_OUTPUT_HEADER = 
                    " Multiplex ID;"
                    +"SBE-Primer ID;"
                    +"Sequence incl. L;"
                    +"SNP allele;"
                    +"Linker (=L): position;"
                    +"Primerlength;"
                    +"GC contents incl L;"
                    +"Tm incl L;"
                    +"Excluded 5\' Primers;"
                    +"Excluded 3\' Primers;"
                    +"Primer from 3' or 5';"
                    +"PCR-Product-length;"
                    +"Actual sequence;"
                    +"Fragment: T-Content;"
                    +"Fragment: G-content;"
                    +"Sec.struc.: position (3\');"
                    +"Sec.struc.: incorporated nucleotide;"
                    +"Sec.struc.: class;"
                    +"Sec.struc.: irrelevant due to L;"
                    +"Comment";

    static protected class TemperatureDistanceAndHairpinComparator implements Comparator {

        private final double opt;
        public TemperatureDistanceAndHairpinComparator(double opt) {
            this.opt= opt;
        }
        public int compare(Object o1, Object o2) {
            CleavablePrimer p1= (CleavablePrimer)o1;
            CleavablePrimer p2= (CleavablePrimer)o2;

            int numinc1=0, numinc2=0;       //Anzahl der incimp. SekStruks, ohne die, deren pos==pl ist
            int numhh1=0, numhh2=0;         //Anzahl der SekStruks, ohne die, deren pos==pl ist

            for (Iterator it = p1.getSecStrucs().iterator(); it.hasNext();) {
                CleavableSekStruktur s = (CleavableSekStruktur) it.next();
                if(p1.getBruchstelle() - s.getPosFrom3() != 1) {//wenn kein gegenpl eingebaut wuerde
                    numhh1++;
                    if(s.isIncompatible())
                        numinc1++;
                }
            }
            for (Iterator it = p2.getSecStrucs().iterator(); it.hasNext();) {
                CleavableSekStruktur s = (CleavableSekStruktur) it.next();
                if(p2.getBruchstelle() - s.getPosFrom3() != 1) {
                    numhh2++;
                    if(s.isIncompatible())
                        numinc2++;
                }
            }
            // Sortieren nach kompatiblen vor inkomp. SekStrukturen
            if(numinc2 > numinc1)
                return -1;
            if(numinc2 < numinc1)
                return 1;
            //wenn gleich: Sortieren nach der Anzahl von Sekstruk
            if(numhh2 > numhh1)
                return -1;
            if(numhh2 < numhh1)
                return 1;
            //ansonsten zortieren nach Abstand von der optimalen Temperatur
            double t1= Math.abs(opt - p1.getTemperature());
            double t2= Math.abs(opt - p2.getTemperature());

            return (t1 < t2 ? -1 : (t1 == t2 ? 0 : 1));
        }
    }

    private static final String PL = "pl key";
    private final int pl5;
    private final int pl3;


    /**
     * Konstruktor fuer nur einen Primer.
     * @param cfg
     * @param id
     * @param seq
     * @param snp
     * @param productlen
     * @param bautein5
     * @param givenmplex
     * @param unwanted
     */
    public CleavablePrimerFactory(SBEOptions cfg, String id, String seq, int pl5, String snp, int productlen, String bautein5, String givenmplex, String unwanted, boolean userGiven) {
        this(cfg,id,seq,pl5,"",'0',snp,productlen,bautein5,"",givenmplex,unwanted, userGiven,false);
    }

    /**
     * @param id
     * @param snp
     * @param productlen
     * @param givenMultiplexid
     * @param unwanted Ausdruck fuer Primer, die der User nicht will (List a la "3'_length_PL ...", z.B.: "3'_25_11 5'_19_8")
      */
    public CleavablePrimerFactory(SBEOptions cfg,String id, String l, int pl5,String r, int pl3, 
            String snp, int productlen, String bautEin5, String bautEin3, 
            String givenMultiplexid, String unwanted, boolean userGiven, boolean rememberOutput) {
        super(cfg,id,l,snp,r,bautEin5, bautEin3,productlen, givenMultiplexid.trim(), userGiven, unwanted,rememberOutput);

        this.pl5=pl5;
        this.pl3=pl3;
     }
    public String toString() {
        return "Sbec. \""+id;
    }
    

    public boolean hasPL(){
        return -1 != getBruchstelle();
    }
    /**
     * Filtert eine Liste von PTTStructs, so dass am Ende höchstens zwei Primer übrigbleiben:
     * Einer aus der 5' und einer aus der 3'Sequenz.
     * @param liste
     */
    protected List findBestPrimers(List liste) {

        int[] br = cfg.getPhotolinkerPositions();
        List l=new ArrayList();
        for (int i = 0; i < br.length; i++) {
            final int b=br[i];
           l.add(Algorithms.detect(liste.iterator(),new UnaryPredicate() {
                    public boolean test(Object obj) {
                        CleavablePrimer p=((CleavablePrimer)obj);
                        return p.getBruchstelle()== b && p.getType().equals(Primer._5_);
                    }
                },null));
           l.add(Algorithms.detect(liste.iterator(),new UnaryPredicate() {
                    public boolean test(Object obj) {
                        CleavablePrimer p=((CleavablePrimer)obj);
                        return p.getBruchstelle()== b && p.getType().equals(Primer._3_);
                    }
                },null));
        }
        Algorithms.remove(l.iterator(),IsNull.instance());
        chosen=null;
        return l;

    }
    public int getBruchstelle() {
        assertPrimerChosen();
        return ((CleavablePrimer)chosen).getBruchstelle();
    }


    /**
     * Sequenz im Format "bioc gta cc(L) gta cga ccg"
     * @return
     */
    private String getFavSeqMitPhotolinker() {
        assertPrimerChosen();

        if (-1 == getBruchstelle())
            return "";
        String seq= chosen.getCompletePrimerSeq();
        StringBuffer sb= new StringBuffer();
        int j= 0;
        for (int i= seq.length() - 1; 0 <= i; i--, j++) {
            if (seq.charAt(i)=='L') {
                sb.append(")L(");
            } else {
                sb.append(seq.charAt(i));
            }
            if (0 == (j + 1) % 3)
                sb.append(' ');
        }
        sb.reverse();
        sb.insert(0,cfg.getBiotinString());
        return sb.toString();
    }
    public boolean hasValidPrimer() {
        return super.hasValidPrimer() && -1 != ((CleavablePrimer)chosen).getBruchstelle();
   }
    /**
     * Der Photolinker wird rausgeschnitten, d.h. es rücken zwei Basen zusammen,
     * die sonst nicht zusammen stünden. Damit kann sich die Temperatur unvorhersehbar ändern!.
     * @return
     */
    double getTMMitPhotolinker() {
        assertPrimerChosen();

        if (-1 == ((CleavablePrimer)chosen).getBruchstelle()) {
            return 0;
        }
        StringBuffer sb= new StringBuffer(chosen.getCompletePrimerSeq());
        sb.deleteCharAt(sb.length() - ((CleavablePrimer)chosen).getBruchstelle());
        return Helper.calcTM(sb.toString());
    }

    private double getGCGehaltMitPhotolinker() {
        assertPrimerChosen();

        StringBuffer sb= new StringBuffer(chosen.getCompletePrimerSeq());
        if (-1 == ((CleavablePrimer)chosen).getBruchstelle()) {
            return 0;
        }
        int pos= sb.length() - ((CleavablePrimer)chosen).getBruchstelle();
        sb.replace(pos, pos + 1, "X");
        //System.out.println("replacing "+chosen.getPrimer()+" at pos="+pos+", pl="+chosen.getBruchstelle()+" ==> "+sb.toString());
        return Helper.getXGehalt(sb.toString(), "cCgG");
    }
    private double getXGehaltBruchStueck(String nukl) {
        assertPrimerChosen();
        int bruch= ((CleavablePrimer)chosen).getBruchstelle();
        if (-1 == bruch)
            return 0;

        String primer= chosen.getCompletePrimerSeq();
        return Helper.getXGehalt(primer.substring(primer.length() - bruch+1), nukl);//zum bruchstueck zaehlt der pl nicht dazu
    }
    /**
     * @param seq
     * @return
     */
    private boolean isInGCIntervall(String seq) {
        double gcvalue= Helper.getXGehalt(seq, "cCgG");
        return gcvalue >= cfg.getMinGC() && gcvalue <= cfg.getMaxGC();
    }

    /**
     * Ergebniszeile.
     * @return
     */
    public String getCSVRow() {
        if(chosen == null && primercandidates.size()==0)
            return ";"+getId()+";;;;;;;" +invalidreason5
                    + ";" + invalidreason3
                    + ";;;;;;;;;;"+getReason();

        assertPrimerChosen();
        StringBuffer sb=new StringBuffer();
        DecimalFormat df= new DecimalFormat("0.00");
        sb.append(chosen.getPlexID());
        sb.append(';');
        sb.append(getId());
        sb.append(';');
        sb.append(getFavSeqMitPhotolinker());
        sb.append(';');
        sb.append(chosen.getSNP());
        sb.append(';');
        sb.append(getBruchstelle());
        sb.append(';');
        sb.append(getFavSeq().length());
        sb.append(';');
        sb.append(df.format(chosen.getGCGehalt()));
        sb.append(';');
        sb.append(df.format(getTMMitPhotolinker()));
        sb.append(';');
        sb.append(invalidreason5);
        sb.append(';');
        sb.append(invalidreason3);
        sb.append(';');
        sb.append(getType());
        sb.append(';');
        sb.append(getProductLength());
        sb.append(';');
        sb.append(chosen.getPrimerSeq());
        sb.append(';');
        sb.append(df.format(getXGehaltBruchStueck("tT")));
        sb.append(';');
        sb.append(df.format(getXGehaltBruchStueck("gG")));
        sb.append(';');
        sb.append(((CleavablePrimer)chosen).getCSVSekStructuresSeparatedBy(";"));
        sb.append(';');
        sb.append(getReason());
        return sb.toString();
    }
    private String getReason() {
        if(!isFoundValidSeq()) {
            return "no valid primer found!";
        }
        String ret="ok, "+(getType().equals(CleavablePrimer._5_)?"5'":"3'")+" Primer used, ";
        //ret+=(getType().equals(Primer._5_)?optimaltempreason5:optimaltempreason3)+", ";
        Set s=chosen.getSecStrucs();
        int chp=0,ichp=0,chd=0,ichd=0,ccd=0,iccd=0;
        for (Iterator it = s.iterator(); it.hasNext();) {
            CleavableSekStruktur sek = (CleavableSekStruktur) it.next();
            switch (sek.getType()) {
                case CleavableSekStruktur.HAIRPIN :
                    if(sek.isIncompatible())
                        ichp++;
                    else
                        chp++;
                    break;
                case CleavableSekStruktur.HOMODIMER :
                    if(sek.isIncompatible())
                        ichd++;
                    else
                        chd++;
                    break;
                case CleavableSekStruktur.CROSSDIMER :
                    if(sek.isIncompatible())
                        iccd++;
                    else
                        ccd++;
                    break;
                default :
                    break;
            }
        }
        if(0 == ichp + chp)
            ret+="no ";
        else {
            if(0 < ichp) {
                if(0 == chp)
                    ret+="in";
                else
                    ret+="(in)";
            }
            ret+="comp. ";
        }
        ret+="hairpins, ";
        if(0 == ichd + chd)
            ret+="no ";
        else {
            if(0 < ichd) {
                if(0 == chd)
                    ret+="in";
                else
                    ret+="(in)";
            }
            ret+="comp. ";
        }
        ret+="homodimer, ";
        if(0 == iccd + ccd)
            ret+="no ";
        else {
            if(0 < iccd) {
                if(0 == ccd)
                    ret+="in";
                else
                    ret+="(in)";
            }
            ret+="comp. ";
        }
        ret+="crossdimer";

        return ret;
    }

    /**
     * Liest alle Werte aus dem CSV-String und liefert Instanz von <code>SBECandidate </code>.
     * @param line
     * @return
     * @throws WrongValueException
     */
    public static CleavablePrimerFactory getSBECandidateFromInputline(SBEOptions cfg, String line) throws WrongValueException {
        StringTokenizer st=new StringTokenizer(line,";\"");
        String[] seq=new String[2];
        String hair5="",hair3="";
        int productlen;
        st=new StringTokenizer(line,";\"");
        String id=st.nextToken() ;  //id
        seq[0]=Helper.getNuklFromString(st.nextToken()).toUpperCase() ;//left seq.
        hair5=Helper.getNuklFromString(st.nextToken()).toUpperCase();//Definitiver Hairpin 5'
        String snp=Helper.getNuklFromString(st.nextToken()).toUpperCase() ;  //SNP
        seq[1]=Helper.getNuklFromString(st.nextToken()).toUpperCase(); //right seq.
        hair3=Helper.getNuklFromString(st.nextToken()).toUpperCase() ;//Definitiver Hairpin 3'
        String temp=st.nextToken();
        try{
    		productlen=Integer.parseInt(temp);
        }catch (NumberFormatException e) {
    		productlen=temp.length() ;//PCR-Produktlaenge
    	}
        int festeBruchstelle=-1;
        if(st.hasMoreTokens()){
            String tmp=st.nextToken().trim();
            if(null != tmp && 0 != tmp.length())
                try {
                    festeBruchstelle= Integer.parseInt(tmp);
                } catch (NumberFormatException e1) {
                    throw new WrongValueException("Falscher Parameter: \""+tmp+"\"; sollte z.B. sein \"9\"");
                }
        }
        String givenMultiplexid="";
        if(st.hasMoreTokens())
            givenMultiplexid=st.nextToken().trim();
        String unwanted = "";
        if(st.hasMoreTokens())
            unwanted = st.nextToken().trim();

        boolean userGiven=false;
        if(st.hasMoreTokens())
        	userGiven=Boolean.valueOf(st.nextToken()).booleanValue();

        if(festeBruchstelle != -1) {//wenn ein PL vorgegeben ist, wird nur der linke Primer betrachtet
            int posOfL=Helper.getPosOfPl(seq[0]);
            if(posOfL != -1 && posOfL != festeBruchstelle)
                throw new IllegalArgumentException("In primer ID="+id+": Left primer has a L in position="+posOfL+" != field PL="+festeBruchstelle+" !");
            char repl=Helper.getNuklAtPos(seq[0],festeBruchstelle);
            String primer = Helper.replaceWithPL(seq[0],festeBruchstelle);
            return new CleavablePrimerFactory(cfg, id, primer,repl,snp, productlen, hair5, givenMultiplexid, unwanted, userGiven);
        }else
            return new CleavablePrimerFactory(cfg,id,seq[0],'0',seq[1],'0',snp,productlen,hair5,hair3,givenMultiplexid,unwanted, userGiven, false);
    }


    protected void assertPrimerChosen() {
        if(null == chosen || -1 == ((CleavablePrimer)chosen).getBruchstelle())
            throw new IllegalStateException("no Primer chosen yet!");
    }


    protected void createGivenPrimers() {
        if(pl5 > 0) {
            System.out.println("Using given 5' primer.");
            CleavablePrimer primer = new CleavablePrimer(cfg, id, seq5, pl5,snp, Primer._5_,bautEin5, productlen, true);
            primer.addObserver(this);
            primercandidates.add(primer);
        }
        if(pl3 > 0) {
            System.out.println("Using given 3' primer.");
            String rstring=Helper.revcomplPrimer(seq3);
            String rsnp=Helper.revcomplPrimer(snp);
            CleavablePrimer primer = new CleavablePrimer(cfg, id, rstring, pl3,
                    rsnp, Primer._3_,bautEin3, productlen, true);
            primer.addObserver(this);
            primercandidates.add(primer);
        }
            
    }

    public Collection createPossiblePrimers(String seq, String type) {
        int[] br=cfg.getPhotolinkerPositions();
        int pl=-1;
        String bautEin=null;
        if(type.equals(Primer._5_)){
            pl=pl5;
            bautEin=bautEin5;
        }else{
            pl=pl3;
            bautEin=bautEin3;
        }
        if(pl !=  -1) {
            br=new int[] {pl};//vorgegebener pl
        }
        Collection result=new ArrayList(br.length);
        for (int j = 0; j < br.length; j++) {
            if(seq.length() > br[j]) {        //wenn die Sequenz kuerzer ist als die Pos. des PL kann mans gleich lassen
                /*
                 * Ich kann den Primer nicht einfach clonen, weil sonst die Sekundaerstrukturen immer noch auf den originalen Primer verweisen,
                 * so dass eine gesetzte Bruchstelle keine Wirkung haette.
                 */
                CleavablePrimer p=new CleavablePrimer(cfg,id,seq,br[j],snp,type,bautEin,getProductLength(),false);
                result.add(p);
            }
        }
        return result;
    }
        
    public String getFilter() {
        assertPrimerChosen();
        return chosen.getType()+"_*_"+((CleavablePrimer)chosen).getBruchstelle();
    }

    public String getCsvheader() {
        return this.getAssayTypeName()+CSV_OUTPUT_HEADER;
    }
    public String getAssayTypeName(){
        return "Cleavable";
    }
}
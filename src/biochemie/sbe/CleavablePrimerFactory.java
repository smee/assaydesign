package biochemie.sbe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.core.IsNull;
import org.apache.commons.lang.ArrayUtils;

import biochemie.domspec.Primer;
import biochemie.domspec.SBEPrimer;
import biochemie.domspec.SBESekStruktur;
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
public class CleavablePrimerFactory extends PrimerFactory implements Observer {



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
    
    protected void createValidPrimerCandidates() {
        //Erzeuge Array mit Structs sortiert nach Abstand von optimaler Temperatur, alle nicht m�glichen Kandidaten sind schon entfernt
        primercandidates.addAll(findBestPrimers(createSortedCandidateList(seq5, bautEin5, pl5,seq3, bautEin3,pl3)));
        System.out.println("\nPrimer chosen for multiplexing for "+id+":\n" +
                               "------------------------------------------------\n"
                    + Helper.toStringln(primercandidates.toArray(new Object[primercandidates.size()])));
       
        if (0 == primercandidates.size()) {
            System.out.println("==> No Primer found for " + seq5 + " and " + seq3);
            return;
        }
    }

    public boolean hasPL(){
        return -1 != getBruchstelle();
    }
    /**
     * Filtert eine Liste von PTTStructs, so dass am Ende h�chstens zwei Primer �brigbleiben:
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
                        SBEPrimer p=((SBEPrimer)obj);
                        return p.getBruchstelle()== b && p.getType().equals(SBEPrimer._5_);
                    }
                },null));
           l.add(Algorithms.detect(liste.iterator(),new UnaryPredicate() {
                    public boolean test(Object obj) {
                        SBEPrimer p=((SBEPrimer)obj);
                        return p.getBruchstelle()== b && p.getType().equals(SBEPrimer._3_);
                    }
                },null));
        }
        Algorithms.remove(l.iterator(),IsNull.instance());
        chosen=null;
        return l;

    }
    public int getBruchstelle() {
        assertPrimerChosen();
        return ((SBEPrimer)chosen).getBruchstelle();
    }
    /**
     * Liefert Liste zurueck mit PrimerTypeTemperatureStructs im Temperaturbereich, absteigend
     * sortiert nach Abstand zur optimalen Temperatur. Alle Primer ausserhalb des GCGehaltes werden
     * nicht beruecksichtigt. Ausserdem werden alle Primer mit einer Laenge von <18 geloescht.
     * Die Liste besteht aus: Primer ohne Hairpin, nach Abstand von optimaler Temperatur ansteigend geordnet
     * gefolgt von Primern mit genau einem Hairpin, auch geordnet nach Abstand von opt. Temp.
     */
    protected List createSortedCandidateList(String left, String bautEin5, int pl5, String right, String bautEin3, int pl3) {
        System.out.println("\nDetailed report for choice of possible 5' primer for " + id +
		 "\n-----------------------------------------------------------------");
    	List liste= generateFilteredPrimerList(left, SBEPrimer._5_,bautEin5,pl5);
        System.out.println("\nDetailed report for choice of possible 3' primer for " + id +
		 "\n-----------------------------------------------------------------");
    	liste.addAll(generateFilteredPrimerList(right, SBEPrimer._3_,bautEin3,pl3));
        Collections.sort(liste, new TemperatureDistanceAndHairpinComparator(cfg.getOptTemperature()));

        System.out.println("\nOrdered list of possible primer according to your preferences for "+id+":\n" +
        					 "--------------------------------------------------------------------------------\n"
                + Helper.toStringln(liste.toArray(new Object[liste.size()])));
        return liste;
    }
/**
 * Erzeugt eine Liste von SBEPrimern, die geordnet Kandidaten enth�lt, die die Filter �berlebt haben.
 * @param primer
 * @param type
 * hh Schalter f�r Hairpin/Homodimer, bei true werden sie verwendet
 * @param repl 
 * @return
 */
    private List generateFilteredPrimerList(String primer, String type,String bautein, int pl) {
        String snp=this.snp;
        if(type.equals(Primer._3_)) {
            primer=Helper.revcomplPrimer(primer);
            snp=Helper.complPrimer(snp);
        }
        ArrayList liste= new ArrayList();
		boolean hh=!bautein.equalsIgnoreCase("none") && 0 == bautein.length(); //in diesen beiden F�llen werden die H-Filter nicht verwendet
        /*
         * lege Liste an mit allen Sequenzen, die aus Primer entstehen, indem Basen am 5'-Ende abgeschnitten werden.
         */
        int[] br=cfg.getPhotolinkerPositions();
        if(pl !=  -1) {
            br=new int[] {pl};//vorgegebener pl
        }
        for (int startidx= 0; startidx < primer.length(); startidx++) {
            for (int j = 0; j < br.length; j++) {
                if(primer.length() - startidx > br[j]) {        //wenn die Sequenz kuerzer ist als die Pos. des PL kann mans gleich lassen
                    /*
                     * Ich kann den Primer nicht einfach clonen, weil sonst die Sekundaerstrukturen immer noch auf den originalen Primer verweisen,
                     * so dass eine gesetzte Bruchstelle keine Wirkung haette.
                     */
                    SBEPrimer p=new SBEPrimer(cfg,id,primer.substring(startidx),br[j],snp,type,bautein,getProductLength(),false);
                    p.addObserver(this);
                    liste.add(p);
                }
            }
        }
        return filterPrimerList(liste, hh, type);

    }



    public String getFavSeqWOPl() {
        return ((SBEPrimer)chosen).getSeqWOPl();
    }
    /**
     * Sequenz im Format "bioc gta cc(L) gta cga ccg"
     * @return
     */
    private String getFavSeqMitPhotolinker() {
        assertPrimerChosen();

        if (-1 == getBruchstelle())
            return "";
        String seq= chosen.getSeq();
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
        return super.hasValidPrimer() && -1 != ((SBEPrimer)chosen).getBruchstelle();
   }
    /**
     * Der Photolinker wird rausgeschnitten, d.h. es r�cken zwei Basen zusammen,
     * die sonst nicht zusammen st�nden. Damit kann sich die Temperatur unvorhersehbar �ndern!.
     * @return
     */
    double getTMMitPhotolinker() {
        assertPrimerChosen();

        if (-1 == ((SBEPrimer)chosen).getBruchstelle()) {
            return 0;
        }
        StringBuffer sb= new StringBuffer(chosen.getSeq());
        sb.deleteCharAt(sb.length() - ((SBEPrimer)chosen).getBruchstelle());
        return Helper.calcTM(sb.toString());
    }

    private double getGCGehaltMitPhotolinker() {
        assertPrimerChosen();

        StringBuffer sb= new StringBuffer(chosen.getSeq());
        if (-1 == ((SBEPrimer)chosen).getBruchstelle()) {
            return 0;
        }
        int pos= sb.length() - ((SBEPrimer)chosen).getBruchstelle();
        sb.replace(pos, pos + 1, "X");
        //System.out.println("replacing "+chosen.getPrimer()+" at pos="+pos+", pl="+chosen.getBruchstelle()+" ==> "+sb.toString());
        return Helper.getXGehalt(sb.toString(), "cCgG");
    }
    private double getXGehaltBruchStueck(String nukl) {
        assertPrimerChosen();
        int bruch= ((SBEPrimer)chosen).getBruchstelle();
        if (-1 == bruch)
            return 0;

        String primer= chosen.getSeq();
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
        /*
            "Multiplex ID"
            ,"SBE-ID"
            ,"Sequence incl. PL"
            ,"SNP allele"
            ,"Photolinker (=PL): position"
            ,"Primerlength"
            ,"GC contents incl PL"
            ,"Tm incl PL"
            ,"Excluded 5\' Primers"
            ,"Excluded 3\' Primers"
            ,"Sec.struc.: position (3\')"
            ,"Sec.struc.: incorporated nucleotide"
            ,"Sec.struc.: class"
            ,"Sec.struc.: irrelevant due to PL"
            ,"Primer from 3' or 5'"
            ,"Fragment: T-Content"
            ,"Fragment: G-content"
            ,"PCR-Product-length"
            ,"Sequence excl.PL"
            ,"Comment"};
         */
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
        sb.append(getSNP());
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
        sb.append(((SBEPrimer)chosen).getCSVSekStructuresSeparatedBy(";"));
        sb.append(';');
        sb.append(getType());
        sb.append(';');
        sb.append(df.format(getXGehaltBruchStueck("tT")));
        sb.append(';');
        sb.append(df.format(getXGehaltBruchStueck("gG")));
        sb.append(';');
        sb.append(getProductLength());
        sb.append(';');
        sb.append(getFavSeqWOPl());
        sb.append(';');
        sb.append(getReason());
        return sb.toString();
    }
    private String getReason() {
        if(!isFoundValidSeq()) {
            return "no valid primer found!";
        }
        if(usedreason.length() != 0)
            return usedreason;
        String ret="ok, "+(getType().equals(SBEPrimer._5_)?"5'":"3'")+" Primer used, ";
        //ret+=(getType().equals(Primer._5_)?optimaltempreason5:optimaltempreason3)+", ";
        Set s=chosen.getSecStrucs();
        int chp=0,ichp=0,chd=0,ichd=0,ccd=0,iccd=0;
        for (Iterator it = s.iterator(); it.hasNext();) {
            SBESekStruktur sek = (SBESekStruktur) it.next();
            switch (sek.getType()) {
                case SBESekStruktur.HAIRPIN :
                    if(sek.isIncompatible())
                        ichp++;
                    else
                        chp++;
                    break;
                case SBESekStruktur.HOMODIMER :
                    if(sek.isIncompatible())
                        ichd++;
                    else
                        chd++;
                    break;
                case SBESekStruktur.CROSSDIMER :
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
    public void update(Observable o, Object arg) {
		if(arg.equals(SBEPrimer.PLEXID_CHANGED))
			choose((SBEPrimer) o);
	}
    /**
     * @return Returns the csvheader.
     */
    public static String[] getCsvheader() {
        return (String[]) ArrayUtils.clone(csvheader);
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
    private final static String[] csvheader =
        new String[] {
            "Multiplex ID"
            ,"SBE-Primer ID"
            ,"Sequence incl. L"
            ,"SNP allele"
            ,"Linker (=L): position"
            ,"Primerlength"
            ,"GC contents incl L"
            ,"Tm incl L"
            ,"Excluded 5\' Primers"
            ,"Excluded 3\' Primers"
            ,"Sec.struc.: position (3\')"
            ,"Sec.struc.: incorporated nucleotide"
            ,"Sec.struc.: class"
            ,"Sec.struc.: irrelevant due to PL"
            ,"Primer from 3' or 5'"
            ,"Fragment: T-Content"
            ,"Fragment: G-content"
            ,"PCR-Product-length"
            ,"Actual sequence"
            ,"Comment"};


    protected void assertPrimerChosen() {
        if(null == chosen || -1 == ((SBEPrimer)chosen).getBruchstelle())
            throw new IllegalStateException("no Primer chosen yet!");
    }


    protected void createGivenPrimers() {
        if(pl5 > 0) {
            System.out.println("Using given 5' primer.");
            SBEPrimer primer = new SBEPrimer(cfg, id, seq5, pl5,snp, Primer._5_,bautEin5, productlen, true);
            primer.addObserver(this);
            primercandidates.add(primer);
        }
        if(pl3 > 0) {
            System.out.println("Using given 3' primer.");
            String rstring=Helper.revcomplPrimer(seq3);
            SBEPrimer primer = new SBEPrimer(cfg, id, rstring, pl3,
                    snp, Primer._3_,bautEin3, productlen, true);
            primer.addObserver(this);
            primercandidates.add(primer);
        }
            
    }
}
package biochemie.sbe;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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

import biochemie.domspec.SBEPrimer;
import biochemie.domspec.SBESekStruktur;
import biochemie.sbe.filter.GCFilter;
import biochemie.sbe.filter.KandidatenFilter;
import biochemie.sbe.filter.LaengenFilter;
import biochemie.sbe.filter.PolyXFilter;
import biochemie.sbe.filter.SekStructureFilter;
import biochemie.sbe.filter.TemperaturFilter;
import biochemie.sbe.filter.UnwantedPrimerFilter;
import biochemie.sbe.multiplex.MultiplexableFactory;
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
public class SBECandidate implements MultiplexableFactory, Observer {

    private static final class TemperatureDistanceAndHairpinComparator implements Comparator {
        private final double opt;
        private TemperatureDistanceAndHairpinComparator(double opt) {
            this.opt= opt;
        }
        public int compare(Object o1, Object o2) {
            SBEPrimer p1= (SBEPrimer)o1;
            SBEPrimer p2= (SBEPrimer)o2;

            int numinc1=0, numinc2=0;       //Anzahl der incimp. SekStruks, ohne die, deren pos==pl ist
            int numhh1=0, numhh2=0;         //Anzahl der SekStruks, ohne die, deren pos==pl ist

            for (Iterator it = p1.getSecStrucs().iterator(); it.hasNext();) {
                SBESekStruktur s = (SBESekStruktur) it.next();
                if(p1.getBruchstelle() - s.getPosFrom3() != 1) {//wenn kein gegenpl eingebaut wuerde
                    numhh1++;
                    if(s.isIncompatible())
                        numinc1++;
                }
            }
            for (Iterator it = p2.getSecStrucs().iterator(); it.hasNext();) {
                SBESekStruktur s = (SBESekStruktur) it.next();
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

    SBEPrimer chosen;
    List primercandidates;
    private String invalidreason3="",invalidreason5="",usedreason="", optimaltempreason5="",optimaltempreason3="";
    private final String id;
    private final String leftstring, rightstring;
    private final String snp;
    private final int productlen;
    private String givenMultiplexID;
	private final SBEOptionsProvider cfg;
    private String unwanted;

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
    public SBECandidate(SBEOptionsProvider cfg, String id, String seq, String snp, int productlen, String bautein5, String givenmplex, String unwanted, boolean userGiven) {
        this(cfg,id,seq,"",snp,productlen,bautein5,"",givenmplex,unwanted, userGiven);
    }

    /**
     * @param id
     * @param snp
     * @param productlen
     * @param givenMultiplexid
     * @param unwanted Ausdruck fuer Primer, die der User nicht will (List a la "3'_length_PL ...", z.B.: "3'_25_11 5'_19_8")
      */
    public SBECandidate(SBEOptionsProvider cfg,String id, String l, String r, String snp, int productlen, String bautEin5, String bautEin3, String givenMultiplexid, String unwanted, boolean userGiven) {
        leftstring=l;
        rightstring= Helper.revcomplPrimer(r);
        this.id=id;
        this.productlen=productlen;
        this.snp=snp;
        this.givenMultiplexID=givenMultiplexid.trim();
        this.primercandidates=new ArrayList();
        this.cfg = cfg;
        this.unwanted=unwanted;
        System.out.println("\nAnalyzing Seq.ID: " + id + "\n------------------------");

        if(userGiven){
        	SBEPrimer primer = new SBEPrimer(cfg, id, l, snp, SBEPrimer._5_,bautEin5, productlen, true);
            primer.addObserver(this);
        	primercandidates.add(primer);
        }else
        	createValidCandidate(l, bautEin5,r,bautEin3);
     }

    /**
     * Erzeuge eine Liste von Sequenzen, die allen Kriterien gerecht werden und setzt alle entsprechenden
     * Instanzfelder.
     *
     */
    private void createValidCandidate(String l, String b5, String r, String b3) {
        //Erzeuge Array mit Structs sortiert nach Abstand von optimaler Temperatur, alle nicht möglichen Kandidaten sind schon entfernt
        primercandidates=findBestPrimers(createSortedCandidateList(l, b5, r, b3));
        if (cfg.isDebug()) {
            System.out.println("Using the following primer as candidates:\n" +
                               "-----------------------------------------\n"
                    + Helper.toStringln(primercandidates.toArray(new Object[primercandidates.size()])));
        }
        if (0 == primercandidates.size()) {
            System.out.println("==> No Primer found for " + leftstring + " and " + rightstring);
            return;
        }

    }
    public boolean hasPL(){
        return -1 != getBruchstelle();
    }
    /**
     * Filtert eine Liste von PTTStructs, so dass am Ende höchstens zwei Primer übrigbleiben:
     * Einer aus der 5' und einer aus der 3'Sequenz.
     * @param liste
     */
    private List findBestPrimers(List liste) {

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
    /**
     * Prüft, ob ein Primer ausgewählt worden ist oder nicht.
     * @throws IllegalStateException wenn noch nicht feststeht, welcher Primer ausgewählt wurde.
     */
    private void assertPrimerChosen() {
        if(null == chosen || -1 == chosen.getBruchstelle())
            throw new IllegalStateException("no Primer chosen yet!");
    }
    public int getBruchstelle() {
        assertPrimerChosen();
        return chosen.getBruchstelle();
    }
    public String getGivenMultiplexID() {
        return givenMultiplexID;
    }
    /**
     * Liefert Liste zurueck mit PrimerTypeTemperatureStructs im Temperaturbereich, absteigend
     * sortiert nach Abstand zur optimalen Temperatur. Alle Primer ausserhalb des GCGehaltes werden
     * nicht beruecksichtigt. Ausserdem werden alle Primer mit einer Laenge von <18 geloescht.
     * Die Liste besteht aus: Primer ohne Hairpin, nach Abstand von optimaler Temperatur ansteigend geordnet
     * gefolgt von Primern mit genau einem Hairpin, auch geordnet nach Abstand von opt. Temp.
     * @return
     */
    private List createSortedCandidateList(String left, String bautEin5, String right, String bautEin3) {
        List liste= getFilteredPTTStructList(left, SBEPrimer._5_,bautEin5);
        liste.addAll(getFilteredPTTStructList(right, SBEPrimer._3_,bautEin3));
        Collections.sort(liste, new TemperatureDistanceAndHairpinComparator(cfg.getOptTemperature()));

        System.out.println("List of possible primer:\n------------------------\n"
                + Helper.toStringln(liste.toArray(new Object[liste.size()])));
        return liste;
    }
/**
 * Erzeugt eine Liste von PTTStructs, die geordnet Kandidaten enthält, die die Filter überlebt haben.
 * @param primer
 * @param type
 * hh Schalter für Hairpin/Homodimer, bei true werden sie verwendet
 * @return
 */
    private List getFilteredPTTStructList(String primer, String type,String bautein) {
        ArrayList liste= new ArrayList();
		boolean hh=!bautein.equalsIgnoreCase("none") && 0 == bautein.length(); //in diesen beiden Fällen werden die H-Filter nicht verwendet
        /*
         * lege Liste an mit allen Sequenzen, die aus Primer entstehen, indem Basen am 5'-Ende abgeschnitten werden.
         */
        int[] br=cfg.getPhotolinkerPositions();
        int plpos=primer.indexOf('L');
        if(plpos !=  -1)
            br=new int[] {plpos};//vorgegebener pl

        for (int startidx= 0; startidx < primer.length(); startidx++) {
            for (int j = 0; j < br.length; j++) {
                if(primer.length() - startidx > br[j]) {        //wenn die Sequenz kuerzer ist als die Pos. des PL kann mans gleich lassen
                    /*
                     * Ich kann den Primer nicht einfach clonen, weil sonst die Sekundaerstrukturen immer noch auf den originalen Primer verweisen,
                     * so dass eine gesetzte Bruchstelle keine Wirkung haette.
                     */
                    String seq = Helper.replacePL(primer.substring(startidx),br[j]);
                    SBEPrimer p=new SBEPrimer(cfg,id,seq,snp,type,bautein,productlen,false);
                    p.addObserver(this);
                    liste.add(p);
                }
            }
        }
        final int allcount = liste.size();

        //lege Liste mit Filtern an, die verwendet werden sollen
        List kf=new ArrayList();

        kf.add(new LaengenFilter(cfg));
		kf.add(new TemperaturFilter(cfg));
		kf.add(new PolyXFilter(cfg));
		kf.add(new GCFilter(cfg));
		kf.add(new UnwantedPrimerFilter(cfg, unwanted));
		if(hh){
			kf.add(new SekStructureFilter(cfg));
		}
        for (int i= 0; i < kf.size(); i++) {
            ((KandidatenFilter)kf.get(i)).filter(liste);
        }
        List erg= new ArrayList(liste.size());
        for (Iterator it= liste.iterator(); it.hasNext();) {
            erg.add(it.next());
        }
        int filtcount = 0;
        for (Iterator it = kf.iterator(); it.hasNext();) {
            KandidatenFilter filt = (KandidatenFilter) it.next();
            String r=filt.rejectReason()+filt.rejectedCount()+"/"+allcount+", ";
            filtcount += filt.rejectedCount();
            if(type.equals(SBEPrimer._5_))
                invalidreason5+=r;
            else
                invalidreason3+=r;
        }
        String prefix = "All: "+filtcount+"/"+allcount+", ";
        if(type.equals(SBEPrimer._5_)) {
            invalidreason5=prefix+invalidreason5.substring(0,invalidreason5.length()-2);
        }else {
            invalidreason3=prefix+invalidreason3.substring(0,invalidreason3.length()-2);
        }
        return erg;
    }

    /**
     * Feld ID
     * @return
     */
    public String getId() {
        return id;
    }
    /**
     * Verwendeter Primer.
     * @return
     */
    public String getFavSeq() {
        assertPrimerChosen();
        return chosen.getSeq();
    }
    public String getMultiplexId() {
        assertPrimerChosen();
        return chosen.getPlexID();
    }

    /**
     * Sequenz im Format "bioc gta cc(L) gta cga ccg"
     * @return
     */
    private String getFavSeqMitPhotolinker() {
        assertPrimerChosen();

        int bruchstelle= chosen.getBruchstelle();
        if (-1 == bruchstelle)
            return "";
        String seq= chosen.getSeq();
        StringBuffer sb= new StringBuffer();
        int j= 0;
        for (int i= seq.length() - 1; 0 <= i; i--, j++) {
            if ((seq.length() - i) == bruchstelle) {
                sb.append(")L(");
            } else {
                sb.append(seq.charAt(i));
            }
            if (0 == (j + 1) % 3)
                sb.append(' ');
        }
        sb.append("oib");
        return sb.reverse().toString();
    }
    /**
     * Entweder 5 oder 3
     * @return
     */
    public String getType() {
        assertPrimerChosen();
        return chosen.getType();
    }
    public String getSNP() {
        assertPrimerChosen();
        return chosen.getSNP();
    }
    /**
     * Der Photolinker wird rausgeschnitten, d.h. es rücken zwei Basen zusammen,
     * die sonst nicht zusammen stünden. Damit kann sich die Temperatur unvorhersehbar ändern!.
     * @return
     */
    double getTMMitPhotolinker() {
        assertPrimerChosen();

        if (-1 == chosen.getBruchstelle()) {
            return 0;
        }
        StringBuffer sb= new StringBuffer(chosen.getSeq());
        sb.deleteCharAt(sb.length() - chosen.getBruchstelle());
        return Helper.calcTM(sb.toString());
    }

    private double getGCGehaltMitPhotolinker() {
        assertPrimerChosen();

        StringBuffer sb= new StringBuffer(chosen.getSeq());
        if (-1 == chosen.getBruchstelle()) {
            return 0;
        }
        int pos= sb.length() - chosen.getBruchstelle();
        sb.replace(pos, pos + 1, "X");
        //System.out.println("replacing "+chosen.getPrimer()+" at pos="+pos+", pl="+chosen.getBruchstelle()+" ==> "+sb.toString());
        return Helper.getXGehalt(sb.toString(), "cCgG");
    }
    private double getXGehaltBruchStueck(String nukl) {
        assertPrimerChosen();
        int bruch= chosen.getBruchstelle();
        if (-1 == bruch)
            return 0;

        String primer= chosen.getSeq();
        return Helper.getXGehalt(primer.substring(primer.length() - bruch+1), nukl);//zum bruchstueck zaehlt der pl nicht dazu
    }
    /**
     * Gibt an, ob ein gueltiger Primer gefunden wurde.
     */
    public boolean isFoundValidSeq() {
        return null != chosen && 0 != chosen.getSeq().length();
    }
    public int getProduktLaenge() {
        return productlen;
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
        sb.append(chosen.getCSVSekStructuresSeparatedBy(";"));
        sb.append(';');
        sb.append(getType());
        sb.append(';');
        sb.append(df.format(getXGehaltBruchStueck("tT")));
        sb.append(';');
        sb.append(df.format(getXGehaltBruchStueck("gG")));
        sb.append(';');
        sb.append(getProduktLaenge());
        sb.append(';');
        sb.append(getFavSeq());
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
    /**
     * Entscheidet, welche Ausprägung (Primer+Bruchstelle) verwendet wird.
     * @param struct
     */
    protected void choose(SBEPrimer struct) {
        if(null != chosen && !chosen.equals(struct))
            throw new IllegalStateException("ERROR: another primer was already chosen for this Id!");

        chosen=struct;
        primercandidates.clear();
    }

    /**
     * Liefert Liste von Multiplexable zurueck, die alle möglichen Primerkandidaten enthalten.
     * @return
     */
    public List getMultiplexables() {
        int[] br=cfg.getPhotolinkerPositions();
        List l=new ArrayList();
        if(chosen != null) {//es gibt schon etwas fertiges
            assert(chosen.getBruchstelle() != -1);
            if(chosen.getPlexID().length() == 0)//noch nicht in einem Multiplex
                l.add(chosen);
            return l;
        }
        l.addAll(primercandidates);
        return l;
    }
    /**
     * @return
     */
    public boolean hasValidPrimer() {
         return null != chosen && -1 != chosen.getBruchstelle();
    }

	public void update(Observable o, Object arg) {
		if(arg.equals(SBEPrimer.PLEXID_CHANGED))
			choose((SBEPrimer) o);
	}
    /**
     * @return
     */
    public Set getSekStrucs() {
        assertPrimerChosen();
        return chosen.getSecStrucs();
    }
    public void normalizeCrossdimers(Collection sbec) {
        assertPrimerChosen();
        Set primers = new HashSet();
        for (Iterator it = sbec.iterator(); it.hasNext();) {
            SBECandidate sc = (SBECandidate) it.next();
            if(sc.chosen != null)
                primers.add(sc.chosen);
        }
        chosen.normalizeCrossdimers(primers);
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
    public static SBECandidate getSBECandidateFrom(SBEOptionsProvider cfg, String line) throws WrongValueException {
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

            String primer = Helper.replacePL(seq[0],festeBruchstelle);
            return new SBECandidate(cfg, id, primer,snp, productlen, hair5, givenMultiplexid, unwanted, userGiven);
        }else
            return new SBECandidate(cfg,id,seq[0],seq[1],snp,productlen,hair5,hair3,givenMultiplexid,unwanted, userGiven);
    }
    private final static String[] csvheader =
        new String[] {
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
}
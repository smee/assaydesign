package biochemie.sbe;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import biochemie.domspec.Primer;
import biochemie.domspec.SBEPrimer;
import biochemie.domspec.SBESekStruktur;
import biochemie.domspec.SekStrukturFactory;
import biochemie.sbe.filter.ForbiddenCDMassFilter;
import biochemie.sbe.filter.GCFilter;
import biochemie.sbe.filter.KandidatenFilter;
import biochemie.sbe.filter.LaengenFilter;
import biochemie.sbe.filter.PolyXFilter;
import biochemie.sbe.filter.SekStructureFilter;
import biochemie.sbe.filter.TemperaturFilter;
import biochemie.sbe.filter.UnwantedPrimerFilter;
import biochemie.sbe.multiplex.MultiplexableFactory;
import biochemie.util.Helper;

public abstract class PrimerFactory  implements  MultiplexableFactory,Observer, PrimerCreatorCallback{
    static protected final class TemperatureDistanceAndHairpinComparator implements Comparator {

            private final double opt;
            public TemperatureDistanceAndHairpinComparator(double opt) {
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

    private String writtenoutput=null;
    protected final SBEOptions cfg;
    protected final String id;
    protected final String seq5;
    protected final String seq3;
    protected final String snp;
    protected final int productlen;
    protected Primer chosen;
    protected String givenMultiplexID;
    private final String unwanted;
    protected String invalidreason3="",invalidreason5="",usedreason="";
    protected final List primercandidates;
    private boolean rememberOutput;
    private boolean userGiven;
    protected final String bautEin5;
    protected final String bautEin3;
    
    
    public PrimerFactory(SBEOptions cfg, String id, String seq5, String snp, String seq3, String bautEin5, String bautEin3, int productlen, String givenMultiplexid, boolean userGiven, String unwanted, boolean rememberOutput){
        this.cfg=cfg;
        this.id=id;
        this.seq5=seq5;
        this.seq3=seq3;
        this.snp=snp;
        this.productlen=productlen;
        this.primercandidates=new ArrayList();
        this.givenMultiplexID=givenMultiplexid;
        this.unwanted=unwanted;
        this.rememberOutput=rememberOutput;
        this.userGiven=userGiven;
        this.bautEin5=bautEin5;
        this.bautEin3=bautEin3;
    }
    public void createPrimers(){
        createPrimers(this);
    }
    protected void createPrimers(PrimerCreatorCallback cb){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream orgout=System.out;
        System.setOut(new PrintStream(bos));
        if(userGiven)
            createGivenPrimers();
        else
            createValidPrimerCandidates(cb);
        System.setOut(orgout);
        if(rememberOutput)
            this.writtenoutput=bos.toString();
        else
            System.out.println(bos.toString());
    }
    
    protected void createValidPrimerCandidates(PrimerCreatorCallback cb){
        //Erzeuge Array mit Structs sortiert nach Abstand von optimaler Temperatur, alle nicht m�glichen Kandidaten sind schon entfernt
        primercandidates.addAll(findBestPrimers(createSortedCandidateList(seq5, bautEin5, seq3, bautEin3, cb)));
        System.out.println("\nPrimer chosen for multiplexing for "+id+":\n" +
                               "------------------------------------------------\n"
                    + Helper.toStringln(primercandidates.toArray(new Object[primercandidates.size()])));
       
        if (0 == primercandidates.size()) {
            System.out.println("==> No Primer found for " + seq5 + " and " + seq3);
            return;
        }
    }
    /**
     * Liefert Liste zurueck mit PrimerTypeTemperatureStructs im Temperaturbereich, absteigend
     * sortiert nach Abstand zur optimalen Temperatur. Alle Primer ausserhalb des GCGehaltes werden
     * nicht beruecksichtigt. Ausserdem werden alle Primer mit einer Laenge von <18 geloescht.
     * Die Liste besteht aus: Primer ohne Hairpin, nach Abstand von optimaler Temperatur ansteigend geordnet
     * gefolgt von Primern mit genau einem Hairpin, auch geordnet nach Abstand von opt. Temp.
     */
    protected List createSortedCandidateList(String left, String bautEin5, String right, String bautEin3, PrimerCreatorCallback cb) {
        System.out.println("\nDetailed report for choice of possible 5' primer for " + id +
         "\n-----------------------------------------------------------------");
        List liste= generateFilteredPrimerList(left, Primer._5_,bautEin5,cb);
        System.out.println("\nDetailed report for choice of possible 3' primer for " + id +
         "\n-----------------------------------------------------------------");
        liste.addAll(generateFilteredPrimerList(right, Primer._3_,bautEin3,cb));
        Collections.sort(liste, new TemperatureDistanceAndHairpinComparator(cfg.getOptTemperature()));

        System.out.println("\nOrdered list of possible primer according to your preferences for "+id+":\n" +
                             "--------------------------------------------------------------------------------\n"
                + Helper.toStringln(liste.toArray(new Object[liste.size()])));
        return liste;
    }
    /**
     * Erzeugt eine Liste von Primern, die geordnet Kandidaten enth�lt, die die Filter �berlebt haben.
     * @param primer
     * @param type
     * hh Schalter f�r Hairpin/Homodimer, bei true werden sie verwendet
     * @return
     */
    protected List generateFilteredPrimerList(String primer, String type,String bautein, PrimerCreatorCallback cb) {
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
        for (int startidx= 0; startidx < primer.length(); startidx++) {
            String seq=primer.substring(startidx);
            Collection col=cb.createPossiblePrimers(seq,type);
            for (Iterator it = col.iterator(); it.hasNext();) {
                Primer p = (Primer) it.next();
                p.addObserver(this);
                liste.add(p);
                
            }
        }
        return filterPrimerList(liste, hh, type);

    }

    protected abstract void createGivenPrimers();

    public String getOutput() {
        return writtenoutput==null?"":writtenoutput;
    }
    /**
     * Pr�ft, ob ein Primer ausgew�hlt worden ist oder nicht.
     * @throws IllegalStateException wenn noch nicht feststeht, welcher Primer ausgew�hlt wurde.
     */
    protected void assertPrimerChosen(){
        if(null == chosen)
            throw new IllegalStateException("no Primer chosen yet!");
}

    public String getGivenMultiplexID() {
        return givenMultiplexID;
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
        return chosen.getCompletePrimerSeq();
    }

    public String getMultiplexId() {
        assertPrimerChosen();
        return chosen.getPlexID();
    }

    public String getSNP() {
        return snp;
    }

    /**
     * @return
     */
    public boolean hasValidPrimer() {
         return null != chosen;
    }

    public Primer getFavPrimer() {
        assertPrimerChosen();
        return chosen;
    }
    public int getProductLength() {
        return productlen;
    }
    /**
     * Filters a list of primers. 
     * @see biochemie.sbe.filter filter.
     * @param liste
     * @param sec
     * @param userFilter
     * @param type
     * @return
     */
    protected List filterPrimerList(ArrayList liste, boolean sec, String type) {
        int allcount = liste.size();
        
        List kf=new ArrayList();
        
        kf.add(new LaengenFilter(cfg));
        kf.add(new TemperaturFilter(cfg));
        kf.add(new PolyXFilter(cfg));
        kf.add(new GCFilter(cfg));
        kf.add(new UnwantedPrimerFilter(cfg, unwanted));
        kf.add(new ForbiddenCDMassFilter(cfg));
        if(sec){
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
            int actcount=filt.rejectedCount();
            if(actcount >0){
                String r=filt.rejectReason()+actcount+"/"+(allcount-filtcount)+", ";
                filtcount += actcount;
                if(type.equals(Primer._5_))
                    invalidreason5+=r;
                else
                    invalidreason3+=r;
            }
        }
        String prefix = "Excluded primers: "+filtcount+"/"+allcount+", ";
        if(type.equals(Primer._5_)) {
            invalidreason5=prefix+invalidreason5.substring(0,invalidreason5.length());
            invalidreason5=invalidreason5.substring(0,invalidreason5.length()-2);
        }else {
            invalidreason3=prefix+invalidreason3.substring(0,invalidreason3.length());
            invalidreason3=invalidreason3.substring(0,invalidreason3.length()-2);
        }
        return erg;
    }

    /**
     * Filtert eine Liste von Primern, so dass am Ende h�chstens zwei Primer �brigbleiben:
     * Einer aus der 5' und einer aus der 3'Sequenz.
     * @param liste
     */
    abstract protected List findBestPrimers(List primers);

    /**
     * Entscheidet, welche Auspr�gung (Primer+Bruchstelle) verwendet wird.
     * @param struct
     */
    protected void choose(Primer struct) {
        if(null != chosen && !chosen.equals(struct))
            throw new IllegalStateException("ERROR: another primer was already chosen for this Id!");
    
        chosen=struct;
        primercandidates.clear();
    }

    /**
     * Liefert Liste von Multiplexable zurueck, die alle m�glichen Primerkandidaten enthalten.
     * @return
     */
    public List getMultiplexables() {
        List l=new ArrayList();
        if(chosen != null) {//es gibt schon etwas fertiges
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
    public Set getSekStrucs() {
        assertPrimerChosen();
        return chosen.getSecStrucs();
    }

    public void normalizeCrossdimers(Collection sbec) {
        assertPrimerChosen();
        Set primers = new HashSet();
        for (Iterator it = sbec.iterator(); it.hasNext();) {
            CleavablePrimerFactory sc = (CleavablePrimerFactory) it.next();
            if(sc.chosen != null)
                primers.add(sc.chosen);
        }
        chosen.normalizeCrossdimers(primers, SekStrukturFactory.getCrossDimerAnalysisInstance(cfg.getSecStrucOptions()));
    }

    /**
     * Entweder 5 oder 3
     * @return
     */
    public String getType() {
        assertPrimerChosen();
        return chosen.getType();
    }

    /**
     * Gibt an, ob ein gueltiger Primer gefunden wurde.
     */
    public boolean isFoundValidSeq() {
        return null != chosen && 0 != chosen.getCompletePrimerSeq().length();
    }
    public void update(Observable o, Object arg) {
        if(arg.equals(Primer.PLEXID_CHANGED))
            choose((SBEPrimer) o);
    }
}

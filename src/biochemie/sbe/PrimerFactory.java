package biochemie.sbe;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.UnaryPredicate;

import biochemie.domspec.Primer;
import biochemie.domspec.SekStruktur;
import biochemie.domspec.SekStrukturFactory;
import biochemie.sbe.filter.ForbiddenCDMassFilter;
import biochemie.sbe.filter.ForbiddenPeakDistanceFilter;
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
    static protected class TemperatureDistanceAndHairpinComparator implements Comparator {

        private final double opt;
        public TemperatureDistanceAndHairpinComparator(double opt) {
            this.opt= opt;
        }
        public int compare(Object o1, Object o2) {
            Primer p1= (Primer)o1;
            Primer p2= (Primer)o2;

            int numinc1=0, numinc2=0;       //Anzahl der incimp. SekStruks, ohne die, deren pos==pl ist
            int numhh1=0, numhh2=0;         //Anzahl der SekStruks, ohne die, deren pos==pl ist

            for (Iterator it = p1.getSecStrucs().iterator(); it.hasNext();) {
                SekStruktur s = (SekStruktur) it.next();
                numhh1++;
                if(s.isIncompatible())
                    numinc1++;
            }
            for (Iterator it = p2.getSecStrucs().iterator(); it.hasNext();) {
                SekStruktur s = (SekStruktur) it.next();
                numhh2++;
                if(s.isIncompatible())
                    numinc2++;
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
    protected StringBuffer invalidreason3,invalidreason5;
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
        //Erzeuge Array mit Structs sortiert nach Abstand von optimaler Temperatur, alle nicht möglichen Kandidaten sind schon entfernt
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
        List liste= filterPrimerList(generatePrimerList(left, Primer._5_,bautEin5,cb),
                                    !bautEin5.equalsIgnoreCase("none") && 0 == bautEin5.length(),
                                    Primer._5_);
        System.out.println("\nDetailed report for choice of possible 3' primer for " + id +
         "\n-----------------------------------------------------------------");
        liste.addAll(filterPrimerList(generatePrimerList(right, Primer._3_,bautEin3,cb),
                                      !bautEin3.equalsIgnoreCase("none") && 0 == bautEin3.length(),
                                      Primer._3_));
        Collections.sort(liste, new TemperatureDistanceAndHairpinComparator(cfg.getOptTemperature()));

        System.out.println("\nOrdered list of possible primer according to your preferences for "+id+":\n" +
                             "--------------------------------------------------------------------------------\n"
                + Helper.toStringln(liste.toArray(new Object[liste.size()])));
        return liste;
    }
    /**
     * Erzeugt eine Liste von Primern, die geordnet Kandidaten enthält, die die Filter überlebt haben.
     * @param primer
     * @param type
     * hh Schalter für Hairpin/Homodimer, bei true werden sie verwendet
     * @return
     */
    protected List generatePrimerList(String primer, String type,String bautein, PrimerCreatorCallback cb) {
        String snp=this.snp;
        if(type.equals(Primer._3_)) {
            primer=Helper.revcomplPrimer(primer);
            snp=Helper.complPrimer(snp);
        }
        ArrayList liste= new ArrayList();
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
        return liste;

    }

    protected abstract void createGivenPrimers();

    public String getOutput() {
        return writtenoutput==null?"":writtenoutput;
    }
    /**
     * Prüft, ob ein Primer ausgewählt worden ist oder nicht.
     * @throws IllegalStateException wenn noch nicht feststeht, welcher Primer ausgewählt wurde.
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
    protected List filterPrimerList(List liste, boolean sec, String type) {
        int allcount = liste.size();
        if(type.equals(Primer._5_))
            invalidreason5=new StringBuffer();
        else
            invalidreason3=new StringBuffer();
        List kf=new ArrayList();
        
        kf.add(new LaengenFilter(cfg));
        kf.add(new TemperaturFilter(cfg));
        kf.add(new PolyXFilter(cfg));
        kf.add(new GCFilter(cfg));
        kf.add(new UnwantedPrimerFilter(cfg, unwanted));
        kf.add(new ForbiddenCDMassFilter(cfg));
        kf.add(new ForbiddenPeakDistanceFilter(cfg));
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
                    invalidreason5.append(r);
                else
                    invalidreason3.append(r);
            }
        }
        String prefix = "Excluded primers: "+filtcount+"/"+allcount+", ";
        if(type.equals(Primer._5_)) {
            invalidreason5.insert(0,prefix);
            invalidreason5.delete(invalidreason5.length()-2,invalidreason5.length());
        }else {
            invalidreason3.insert(0,prefix);
            invalidreason3.delete(invalidreason3.length()-2,invalidreason3.length());
        }
        return erg;
    }

    /**
     * Filtert eine Liste von Primern, so dass am Ende höchstens zwei Primer übrigbleiben:
     * Einer aus der 5' und einer aus der 3'Sequenz. Standardmaessig sind das der jeweils erste Primer in der Liste für jede Seite.
     * @param posSet
     */
    protected List findBestPrimers(List primers){
        List result=new LinkedList();
        if(primers.size()==0)
            return result;
        Collections.sort(primers,new TemperatureDistanceAndHairpinComparator(cfg.getOptTemperature()));
        result.add(Algorithms.detect(primers.iterator(),new UnaryPredicate() {
            public boolean test(Object obj) {
                Primer p=((Primer)obj);
                return p.getType().equals(Primer._5_);
            }
        },null));
        result.add(Algorithms.detect(primers.iterator(),new UnaryPredicate() {
            public boolean test(Object obj) {
                Primer p=((Primer)obj);
                return p.getType().equals(Primer._3_);
            }
        },null));
        return result;
    }

    /**
     * Entscheidet, welche Ausprägung (Primer+Bruchstelle) verwendet wird.
     * @param struct
     */
    protected void choose(Primer struct) {
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
        List l=new ArrayList();
        if(chosen != null) {//es gibt schon etwas fertiges
            if(chosen.getPlexID().length() == 0)//noch nicht in einem Multiplex
                l.add(chosen);
            return l;
        }
        if(primercandidates.size()==0)
            createPrimers();
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
            PrimerFactory sc = (PrimerFactory) it.next();
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
            choose((Primer) o);
    }
    public abstract String getFilter();

}

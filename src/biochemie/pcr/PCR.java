package biochemie.pcr;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import biochemie.pcr.io.PCRConfig;
import biochemie.pcr.io.Primer3Manager;
import biochemie.pcr.io.UI;
import biochemie.pcr.modules.BLAT;
import biochemie.pcr.modules.BlatException;
import biochemie.pcr.modules.CrossDimerAnalysis;
import biochemie.pcr.modules.ExonIntron;
import biochemie.pcr.modules.GCDiff;
import biochemie.pcr.modules.HairpinAnalysis;
import biochemie.pcr.modules.HomoDimerAnalysis;
import biochemie.pcr.modules.RepetetiveSeq;
import biochemie.pcr.modules.SNP;
import biochemie.sbe.WrongValueException;

/**
 * @author Steffen Dienst
 *
 */
/*
 *
 * @author Steffen
 *
 */
public class PCR {
	public static boolean verbose=true;
    public static boolean debug=false;

	PCRConfig config=null;
	GCDiff gcdiffObj=null;
	RepetetiveSeq repseqObj=null;
	HairpinAnalysis hairanalysisObj=null;
    CrossDimerAnalysis crossanalysisObj=null;
    HomoDimerAnalysis homoanalysisObj=null;
	BLAT blatObj=null;
	SNP snpObj=null;
	ExonIntron exonObj=null;

	boolean gcdiffOn=true;
	boolean snpOn=true;
	boolean hairOn=true;
    boolean crossOn=true;
    boolean homoOn=true;
	boolean blatOn=true;
	boolean repOn=true;
	boolean exonOn=false;

	public static int maxscore = 100;
    private final int MAXNUM=1000;//anzahl der primerpaare, die jeweils eingelesen werden sollen

	public PCR(PCRConfig cfg) {
		this.config=cfg;
        PCR.debug = config.getProperty("DEBUG").equals("true");
		try {
			maxscore=config.getInteger("SCORE_MAXSCORE");
		} catch (WrongValueException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Liest Parameter aus Config aus und setzt, was es kann.
	 * Also die Flags, welche Analyse-Module an sind, ein GCDiff-Objekt
	 * mit Grenzen,...
	 */
	private void setFlagsAndParamsFromConfig() {

		if(config.getProperty("GCDIFF").equalsIgnoreCase("true")) {
			this.gcdiffOn=true;
			this.gcdiffObj=new GCDiff(config, PCR.debug);
		}else
			this.gcdiffOn=false;
		if(config.getProperty("HAIR").equalsIgnoreCase("true")) {
			this.hairOn=true;
			this.hairanalysisObj=new HairpinAnalysis(config, PCR.debug);
		}else
			this.hairOn=false;
        if(config.getProperty("HOMO").equalsIgnoreCase("true")) {
            this.homoOn=true;
            this.homoanalysisObj=new HomoDimerAnalysis(config, PCR.debug);
        }else
            this.homoOn=false;
        if(config.getProperty("CROSS").equalsIgnoreCase("true")) {
            this.crossOn=true;
            this.crossanalysisObj=new CrossDimerAnalysis(config, PCR.debug);
        }else
            this.crossOn=false;
		if(config.getProperty("SNP").equalsIgnoreCase("true")) {
			snpObj=new SNP(config, PCR.debug);
			this.snpOn=true;
		}else
			this.snpOn=false;
		if(config.getProperty("PCR_PRODUCT_INCLUDES_EXON_INTRON_BORDER").equalsIgnoreCase("true")) {
			exonObj=new ExonIntron(config, PCR.debug);
			this.exonOn=true;
		}else
			this.exonOn=false;
		if(config.getProperty("BLAT").equalsIgnoreCase("true")) {
			this.blatOn=true;
			try {
				this.blatObj=new BLAT(config,PCR.debug);
			} catch (BlatException e) {
				System.err.println("Fehler bei Zugriff auf Blat. Das BLAT-Analysemodul wurde DEAKTIVIERT!");
				System.err.println("Fehlermeldung: "+e.getMessage());
				blatOn=false;
			}
		}else
			this.blatOn=false;
		if(config.getProperty("REP").equalsIgnoreCase("true")) {
			this.repOn=true;
			this.repseqObj=new RepetetiveSeq(config, PCR.debug);
		}
		else
			this.repOn=false;
	}

	/**
	 * Hauptfunktion.
	 * @param args
	 */
	public static void main(String[] args) {
		PCRConfig config=new UI().parseCmdLineParameter(args);
		if(config == null)
			return;
		
        PCR pcr=new PCR(config);
        if(null == pcr.config) {
            System.exit(1);
        }
    	if(PCR.verbose)
			System.out.println("Lese Konfiguration ein...");
		pcr.setFlagsAndParamsFromConfig();
		if(PCR.verbose)
			System.out.println("Starte Analyse...");
		try {
            pcr.startAnalysis();
        } catch (IOException e) {
            UI.errorDisplay("Fehler beim Dateizugriff!\nFehlermeldung: "+e.getMessage());
        }

	}

	public void startAnalysis() throws IOException{
	    if(PCR.debug) {
	        System.out.println("Verwende folgende Parameter: "+this);
	    }
	    
	    StringTokenizer st = new StringTokenizer(config.getProperty("INFILES"));
	    final int primernum=config.getInteger("NUM_OF_SUCCESSFUL_PAIRS",30);
	    String outfilename=config.getProperty("OUTFILE");
	    if(null == outfilename || 0 == outfilename.length()) {
	        outfilename=biochemie.util.Helper.dateFunc()+".txt";
	    }
	    int cyclescount=0, solutionsFound=0;
	    Set ppset = new HashSet();
	    List pps = new ArrayList();
	    while (st.hasMoreTokens()) {
	        String file = st.nextToken();
	        if(PCR.debug)
	            System.out.println("Using primer3file: "+file+"\n" +
	            "------------------------------");
	        Collection c=runAnalysis(file,++cyclescount);
            int oldcount=solutionsFound;
	        for (Iterator it = c.iterator(); it.hasNext();) {
	            PrimerPair p = (PrimerPair) it.next();
	            if(!ppset.contains(p)) {
	                if(p.okay())
	                    solutionsFound++;
	                ppset.add(p);
	                pps.add(p);
	            }
	        }
	        if(PCR.debug)
                System.out.println("New solutions found with inputfile \""+file+"\": "+(solutionsFound-oldcount));
	        if(solutionsFound >= primernum) {
	            System.out.println("Got "+solutionsFound+" valid solutions while considering "+pps.size()+" possible primerpairs, stopping calculations.");                
	            break;   //genug loesungen gefunden, also ferdsch :)
	        }
	    }
	    writeFiles(outfilename, pps);
	}
    private void writeFiles(String outfilename, List pps) throws IOException {
        //Init files
        boolean outputcsv=config.getBoolean("OUTPUT_CSV",true);     
        if(outputcsv && outfilename.endsWith(".csv")==false)
            outfilename += ".csv";
        FileWriter notokayout=new FileWriter("not_ok_"+outfilename);
        FileWriter combined=new FileWriter("combined_"+outfilename);
        FileWriter out=new FileWriter(outfilename);
        if(outputcsv){
            notokayout.write(PrimerPair.getCSVHeaderLine());
            combined.write(PrimerPair.getCSVHeaderLine());
            out.write(PrimerPair.getCSVHeaderLine());
            out.write("\n");
            notokayout.write("\n");
            combined.write("\n");
        }
        int i=1,count=1;
        String line=null;
        for (Iterator it = pps.iterator(); it.hasNext();i++) {
            PrimerPair pair = (PrimerPair) it.next();
            if(!outputcsv)
                line=(count++)+". Paar mit erf�llten Kriterien :\n"+pair.toString()+'\n';
            else
                line=pair.toCSVString(i);
            if(pair.okay()) {
                out.write(line);
                out.write("\n");
                combined.write(line);
                combined.write("\n");
            }else{//zu viele strafpunkte
                notokayout.write(line);
                notokayout.write("\n");
                combined.write(line);
                combined.write("\n");
            }
        }
        out.close();
        notokayout.close();
        combined.close();
    }
	/**
	 * Starte die eigentliche Arbeit. Liefert die Anzahl gefundener Loesungen.
	 * @param i 
	 */
    private Collection runAnalysis(String filename, int cyclescount) throws IOException {
        //run primer3
        Primer3Manager primer3=new Primer3Manager(config, PCR.debug);
        primer3.runPrimer3(filename);
        if(0 == primer3.getNumberOfResults()){
            return new LinkedList();
        }
        //setze nachtraeglich Parameter fuer BLAT-Modul
        if(blatOn) {
            blatObj.setSequence(config.getProperty("SEQUENCE"));
        }
        
        //Starte Module
        List pps=doTheFilterBoogie(primer3);
        for (Iterator it = pps.iterator(); it.hasNext();) {
            PrimerPair p = (PrimerPair) it.next();
            p.setCycleNum(cyclescount);
        }
        if(PCR.verbose) {
            System.out.println("Fertig mit "+filename);
            System.out.println("");
        }
        return pps;
    }

	/**
     * Liest nacheinander Paare aus dem Ergebnisfile von primer3 ein und schickt sie durch die Filter.
	 * @param primer3
	 * @return
	 */
        private List doTheFilterBoogie(Primer3Manager primer3) {
            PrimerPair[] pps=null;
            java.util.List allPairs= new ArrayList();
   
            int index=-1;
            while(null != (pps = primer3.getNextResults(MAXNUM))){
                index++;
                if(gcdiffOn) {
                    if(PCR.verbose)
                        System.out.println("Analysiere GC-Differenz...");
                    gcdiffObj.calcScores(pps);
                }
                if(snpOn) {
                    if(PCR.verbose)
                        System.out.println("Analysiere SNPs...");
                    snpObj.calcScores(pps);
                }
                if(hairOn) {
                    if(PCR.verbose)
                        System.out.println("Analysiere Sekundaerstruktur auf Hairpins...");
                    hairanalysisObj.calcScores(pps);
                }
                if(homoOn) {
                    if(PCR.verbose)
                        System.out.println("Analysiere Sekundaerstruktur auf Homodimer...");
                    homoanalysisObj.calcScores(pps);
                }
                if(crossOn) {
                    if(PCR.verbose)
                        System.out.println("Analysiere Sekundaerstruktur auf Crossdimer...");
                    crossanalysisObj.calcScores(pps);
                }
                if(repOn) {
                    if(PCR.verbose)
                        System.out.println("Analysiere repetetive Sequenzen...");
                    repseqObj.calcScores(pps);
                }
                if(exonOn) {
                    if(PCR.verbose)
                        System.out.println("Teste, ob PCR-Produkte Exon/Intron-Grenze enthalten...");
                    exonObj.calcScores(pps);
                }
                for (int i = 0; i < pps.length; i++) {
                    allPairs.add(pps[i]);
                }
            }
            
                final int solutionscount=countSuccessfulPairs(allPairs);
                
                PrimerPair[] forBlat=new PrimerPair[solutionscount];
                int idx=0;
                for (Iterator it = allPairs.iterator(); it.hasNext();) {
                    PrimerPair pair = (PrimerPair) it.next();
                    if(pair.okay())
                        forBlat[idx++]=pair;
                }
            if (blatOn) {
                try {
                    blatObj.calcScores(forBlat);//alle paare gleichzeitig, weil ja eh die gesamte Seq. an BLAT geschickt wird.
                } catch (BlatException e) {
                    System.err.println("Probleme beim Zugriff auf BLAT-Site");
                    System.err.println("Fehlermeldung: " + e.getMessage());
                    System.err.println("BLAT-Modul DEAKTIVIERT!");
                    blatOn= false;
                }
            }
            return allPairs;
        }

        private int countSuccessfulPairs(List pps) {
        	int count=pps.size();
        	for (Iterator it = pps.iterator(); it.hasNext();) {
        		PrimerPair pair = (PrimerPair) it.next();
        		if(!pair.okay()) {
        			count--;
        		}            
        	}
        	return count;
        }

	public String toString() {
		return config.toString();
	}

}

package biochemie.pcr;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
 * TODO verstecken des primer3-parameterfiles, alles in pcrconfig.default
 * TODO verbindliches Ausgabeformat (XML?)
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
	
	private int maxscore = 100;
	
	public PCR(String[] args) {
		//Einlesen der Konfiguration
		config=new UI().parseCmdLineParameter(args);
		//ist was schiefgegangen?
		if(null == config)
			return;
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
			this.gcdiffObj=new GCDiff(config);
		}else
			this.gcdiffOn=false;
		if(config.getProperty("HAIR").equalsIgnoreCase("true")) {
			this.hairOn=true;
			this.hairanalysisObj=new HairpinAnalysis(config);
		}else
			this.hairOn=false;
        if(config.getProperty("HOMO").equalsIgnoreCase("true")) {
            this.homoOn=true;
            this.homoanalysisObj=new HomoDimerAnalysis(config);
        }else
            this.homoOn=false;
        if(config.getProperty("CROSS").equalsIgnoreCase("true")) {
            this.crossOn=true;
            this.crossanalysisObj=new CrossDimerAnalysis(config);
        }else
            this.crossOn=false;
		if(config.getProperty("SNP").equalsIgnoreCase("true")) {
			snpObj=new SNP(config);
			this.snpOn=true;
		}else
			this.snpOn=false;
		if(config.getProperty("PCR_PRODUCT_INCLUDES_EXON_INTRON_BORDER").equalsIgnoreCase("true")) {
			exonObj=new ExonIntron(config);
			this.exonOn=true;
		}else
			this.exonOn=false;
		if(config.getProperty("BLAT").equalsIgnoreCase("true")) {
			this.blatOn=true;
			try {
				this.blatObj=new BLAT(config);
			} catch (BlatException e) {
				System.err.println("Fehler bei Zugriff auf Blat. Das BLAT-Analysemodul wurde DEAKTIVIERT!");
				System.err.println("Fehlermeldung: "+e.getMessage());
				blatOn=false;
			}
		}else
			this.blatOn=false;
		if(config.getProperty("REP").equalsIgnoreCase("true")) {
			this.repOn=true;
			this.repseqObj=new RepetetiveSeq(config);
		}
		else
			this.repOn=false;
	}

	/**
	 * Hauptfunktion.
	 * @param args
	 */
	public static void main(String[] args) {
        PCR pcr=new PCR(args);
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
		StringTokenizer st = new StringTokenizer(config.getProperty("INFILES"));
		while (st.hasMoreTokens()) {
			String file = st.nextToken();
			if(PCR.debug)
				System.out.println("Using primer3file: "+file+"\n" +
								   "------------------------------");
			int solutions=runAnalysis(file);
			if(solutions > 0) 
				break;   //loesungen gefunden, also ferdsch :)
		}
	}
	/**
	 * Starte die eigentliche Arbeit. Liefert die Anzahl gefundener Loesungen.
	 */
	private int runAnalysis(String filename) throws IOException {
		if(PCR.debug) {
			System.out.println("Verwende folgende Parameter: "+this);
        }         
        Primer3Manager primer3=new Primer3Manager(config);
        primer3.runPrimer3(filename);
                
		if(0 == primer3.getNumberOfResults()){
			//UI.errorDisplay("Keine Ergebnisse von Primer3 --> nix zu filtern.");
			return 0;
		}
        //setze nachtraeglich Parameter fuer BLAT-Modul
//		if(blatOn) {
//			blatObj.setSequence(config.getProperty("SEQUENCE"));
//		}
        //Starte Module
        PrimerPair[] pps=null;
        java.util.List survivorsList= null; 
        FileWriter debugout=null;
        String outfilename=config.getProperty("OUTFILE");
        boolean outputcsv=config.getBoolean("OUTPUT_CSV",true);
        if(null == outfilename || 0 == outfilename.length()) {
            outfilename=biochemie.util.Helper.dateFunc()+".txt";
        }
        if(PCR.debug) {
	            if(outputcsv)
	            	outfilename += ".csv";
                debugout=new FileWriter("not_ok_"+outfilename);
                if(outputcsv)
                	debugout.write(PrimerPair.getCSVHeaderLine());
                debugout.write("\n");
        }
        int maxNum=1000;
        int index=-1;
    	String line=null;
        while(null != (pps = primer3.getNextResults(maxNum))){
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
            countAndMarkSuccessfulPairs(pps);
            if(null == survivorsList)
                survivorsList=new ArrayList();
    
            for(int i=0;i<pps.length;i++) {
                if(pps[i].okay){
                    survivorsList.add(pps[i]);
                }else if(PCR.debug) {
                		if(!outputcsv)
                			line="\nNr. "+(maxNum*index+i+1)+'\n'+pps[i].toString();
                		else
                			line=pps[i].toCSVString(maxNum*index+i+1);
                        debugout.write(line);
                        debugout.write("\n");
                    }
            }
        }
        final int solutionsCont=survivorsList.size();
        
        pps=(PrimerPair[])survivorsList.toArray(new PrimerPair[0]);
		if (blatOn) {
			try {
				blatObj.calcScores(pps);
			} catch (BlatException e) {
				System.err.println("Probleme beim Zugriff auf BLAT-Site");
				System.err.println("Fehlermeldung: " + e.getMessage());
				System.err.println("BLAT-Modul DEAKTIVIERT!");
				blatOn= false;
			}
		}
		countAndMarkSuccessfulPairs(pps);

		File outfile=new File(outfilename);
		FileWriter out=new FileWriter(outfile);
		if(outputcsv){
			out.write(PrimerPair.getCSVHeaderLine());
			out.write("\n");
		}
        //TODO in csv-ausgabe sowohl durchnummerieren als auch ursprüngliche Pos. in prinmer3-liste angeben (zwei Spalten)
    	int count=1;
		for(int i=0;i<pps.length;i++) {
			if(pps[i].okay) {
        		if(!outputcsv)
        			line=(count++)+". Paar mit erfüllten Kriterien :\n"+pps[i].toString()+'\n';
        		else
        			line=pps[i].toCSVString(maxNum*index+i+1);
				out.write(line);
				out.write("\n");
			}else{
        		if(!outputcsv)
        			line="\nNr. "+(-1)+'\n'+pps[i].toString();
        		else
        			line=pps[i].toCSVString(-1);
                debugout.write(line);
                debugout.write("\n");
			}
		}
        out.close();
        if(PCR.debug) {
            debugout.close();
        }
        if(PCR.verbose) {
            System.out.println("Fertig mit "+filename);
        }
        return solutionsCont;
	}
	
	private int countAndMarkSuccessfulPairs(PrimerPair[] pps) {
		int count=pps.length;
		for(int i=0;i<pps.length;i++) {
			if(pps[i].getOverallScore()>=maxscore) {
				pps[i].okay=false;
				count--;
			}
		}
		return count;
	}


	public String toString() {
		return config.toString();
	}

}

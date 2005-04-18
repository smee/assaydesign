/*
 * Created on 16.09.2003
 *
*/
package biochemie.pcr.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.StringTokenizer;

import biochemie.pcr.PCR;
import biochemie.util.Helper;

/**
 * Alles was mit dem Einlesen von Parametern etc. zu tun hat.
 * @author Steffen
 *
 */
public class UI {
	Primer3Config primer3config=null;
	private static final int MAINMENU=0;
	private static final int PRIMER3=1;
	private static final int BLAT=2;
	private static final int GCDIFF=3;
	private static final int SEK=4;
	private static final int REPSEQ=5;
	private static final int SNP=6;
	private static final int GENERAL=7;
	private static final int SAVE=8;
	private static final int pr1=11;
	private static final int pr2=12;
	private static final int pr3=13;
	private static final int pr4=14;
	private static final int pr5=15;
	private static final int pr6=16;
	private static final int gc1=31;
	private static final int gc2=32;
	private static final int gc3=33;
	private static final int sek1=41;
	private static final int sek2=42;
	private static final int sek3=43;
	private static final int rep1=51;
	private static final int rep2=52;
	private static final int rep3=53;
	private static final int snp1=61;
	private static final int snp2=62;
	private static final int snp3=63;
	private static final int snp4=64;
	private static final int gen1=71;
	private static final int save1=81;
	private static final int save2=82;
	private static final int SNPTARGET=pr6;
	private static final int target1=161;
	private static final int target2=162;
	private static final int target3=163;

	PCRConfig config;
    private String configfilename;
	/**
	 * Einlesen der Kommandozeilenparameter.
	 * @param args
	 * @return biochemie.pcr-Objekt
	 */
	public PCRConfig parseCmdLineParameter(String[] args) {
        boolean interactive=false;
		if(1 <= args.length) {//es muessen mindestens 1x pcrconfig angegeben werden
				if(!new File(args[args.length-1]).exists()) {
					System.err.println("Configfile "+args[args.length-1]+" not found!");
					return null;
				}
			config=new PCRConfig();
            try {
                configfilename=args[args.length-1];
                config.readConfigFile(configfilename);System.out.println("Using configfile: "+configfilename);
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            }
			for(int i=0;i<args.length-1;i++) {
				//Einlesen aller Kommandozeilenparams
				if(args[i].startsWith("-help")) {
					helpDisplay();
					return null;
				}else
					if(args[i].startsWith("-"))  {
						if(args[i].substring(1).equalsIgnoreCase("hair")) {
							config.setProperty("HAIR","false");
						}else
                        if(args[i].substring(1).equalsIgnoreCase("cross")) {
                            config.setProperty("CROSS","false");
                        }else
                        if(args[i].substring(1).equalsIgnoreCase("homo")) {
                            config.setProperty("HOMO","false");
                        }else
						if(args[i].substring(1).equalsIgnoreCase("gcdiff")) {
							config.setProperty("GCDIFF","false");
						}else
						if(args[i].substring(1).equalsIgnoreCase("debug")) {
							config.setProperty("DEBUG","false");
						}else
						if(args[i].substring(1).equalsIgnoreCase("blat")) {
							config.setProperty("BLAT","false");
                        }else
                        if(args[i].substring(1).equalsIgnoreCase("exon")) {
                            config.setProperty("PCR_PRODUCT_INCLUDES_EXON_INTRON_BORDER","false");
						}else
						if(args[i].substring(1).equalsIgnoreCase("snp")) {
							config.setProperty("SNP","false");
						}else
						if(args[i].substring(1).equalsIgnoreCase("rep")) {
							config.setProperty("REP","false");
						}else {
							UI.errorDisplay("Parameter "+args[i]+" unbekannt!");
						}
					}else if(args[i].startsWith("+")) {
						if(args[i].substring(1).equalsIgnoreCase("interactive")) {
							interactive=true;
                        }else
                        if(args[i].substring(1).equalsIgnoreCase("exon")) {
                            config.setProperty("PCR_PRODUCT_INCLUDES_EXON_INTRON_BORDER","true");
						}else
                        if(args[i].substring(1).equalsIgnoreCase("hair")) {
                            config.setProperty("HAIR","true");
                        }else
                        if(args[i].substring(1).equalsIgnoreCase("cross")) {
                            config.setProperty("CROSS","true");
                        }else
                        if(args[i].substring(1).equalsIgnoreCase("homo")) {
                            config.setProperty("HOMO","true");
                        }else
                        if(args[i].substring(1).startsWith("embl")) {
                            String emblfilename=args[i].substring(args[i].indexOf('=')+1);
                            try {
                                if(PCR.debug)
                                    System.out.println("Werte Embl-File aus...");
                                EMBLParser embl=new EMBLParser(emblfilename);
                                //Primer3Config p3c=new Primer3Config(config.getProperty("INFILE"));
                                config.setProperty("SEQUENCE",embl.getSequence());
                                //p3c.writeConfigFile(config.getProperty("INFILE"));

                                config.setProperty("REP_SEQ_LIST",embl.getRepetetiveSeqsAsString());
                                config.setProperty("SNP_LIST",embl.getAllSNPsAsString());
                                config.setProperty("EXONS",embl.getAllExonsAsString());
                                try {
                                    config.updateConfigFile(args[args.length-1]);
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                    return  null;
                                }

                            } catch (FileNotFoundException e) {
                                System.err.println("Fehler beim Einlesen von "+emblfilename);
                                System.err.println("Grund: "+e.getMessage());
                            }
                        }else if(args[i].substring(1).startsWith("uscd")) {
                            if(PCR.debug)
                                System.out.println("Werte USCD-File aus (repetetive Sequenzen)...");
                            String uscdfilename=args[i].substring(args[i].indexOf('=')+1);
                            try {
                                USCDParser uscdp=new USCDParser(uscdfilename);
                                config.setProperty("REP_SEQ_LIST",uscdp.getRepetetiveSeqsAsString());
                                config.updateConfigFile(args[args.length-1]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else
						if(args[i].substring(1).startsWith("out")) {
							config.setProperty("OUTFILE",args[i].substring(args[i].indexOf('=')+1));
						}else
						if(args[i].substring(1).equalsIgnoreCase("gcdiff")) {
							config.setProperty("GCDIFF","true");
						}else
						if(args[i].substring(1).equalsIgnoreCase("debug")) {
							config.setProperty("DEBUG","true");
                            if(!Boolean.getBoolean("DEBUG") && !Boolean.getBoolean("DEBUG"))
                                biochemie.util.LogStdStreams.initializeErrorLogging("pcr.log", "---------Program started: " + new Date()+" -----------", true,true);
						}else
						if(args[i].substring(1).equalsIgnoreCase("blat")) {
							config.setProperty("BLAT","true");
						}else
						if(args[i].substring(1).equalsIgnoreCase("rep")) {
							config.setProperty("REP","true");
						}else
						if(args[i].substring(1).equalsIgnoreCase("snp")) {
							config.setProperty("SNP","true");
						}else {
							UI.errorDisplay("Parameter "+args[i]+" unbekannt!");
						}
					}else {
						UI.errorDisplay("Parameter "+args[i]+" unbekannt!");
						return null;
					}
			}
			String infiles = config.getProperty("INFILES");
			StringTokenizer st = new StringTokenizer(infiles);
			try {
                while(st.hasMoreTokens()){
                	String tok = st.nextToken();
                	Primer3Config p3c=new Primer3Config(tok);
                	p3c.setProperty("SEQUENCE",config.getProperty("SEQUENCE"));
                    int pos=config.getInteger("PARAM_SNP_OF_INTEREST", -1);
                    int len=config.getInteger("PARAM_LENTH_OF_5'/3'_SNP_FLANKING_SEQUENCES_TO_BE_AMPLIFIED", -1);
                	p3c.setProperty("TARGET",(pos-len)+","+(len*2));
                	p3c.updateConfigFile(tok);				
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } 
			if(interactive) {
				return getParameterInteractive(config);
			}else return config;
			}else {
				helpDisplay();
				return null;
			 }
	}

	/**
	 * Liest von der Kommandozeile interaktiv alle benötigten Parameter ein.
	 * @return configobjekt
	 */
	private PCRConfig getParameterInteractive(PCRConfig config) {
		int in=UI.MAINMENU;

		do {
		switch (in) {
			case UI.MAINMENU :
				showMenu(in);
				in= getSelection(UI.MAINMENU, UI.SAVE);
				if (UI.MAINMENU == in) {
					return config;
				}
				break;
			case UI.BLAT :
				showMenu(in);
				in= getSelection(0, 0);
				in= (UI.MAINMENU == in)?UI.MAINMENU:processBLATMenu(in);
				break;
			case UI.GCDIFF :
				showMenu(in);
				in= getSelection(UI.gc1, UI.gc3);
				in= (UI.MAINMENU == in)?UI.MAINMENU:processGCDiffMenu(in);
				break;
			case UI.GENERAL :
				showMenu(in);
				in= getSelection(UI.gen1, UI.gen1);
				in= (UI.MAINMENU == in)?UI.MAINMENU:processGeneralMenu(in);
				break;
			case UI.PRIMER3 :
				showMenu(in);
				in= getSelection(UI.pr1, UI.pr6);
				in= (UI.MAINMENU == in)?UI.MAINMENU:processPrimer3Menu(in);
				break;
			case UI.SNPTARGET:
				showMenu(in);
				in= getSelection(UI.target1, UI.target3);
				in= (UI.MAINMENU == in)?UI.MAINMENU:processSNPTargetMenu(in);
				break;
			case UI.REPSEQ :
				showMenu(in);
				in= getSelection(UI.rep1, UI.rep3);
				in= (UI.MAINMENU == in)?UI.MAINMENU:processRepSeqMenu(in);
				break;
			case UI.SAVE :
				showMenu(in);
				in= getSelection(UI.save1, UI.save2);
				in= (UI.MAINMENU == in)?UI.MAINMENU:processSaveMenu(in);
				break;
			case UI.SEK :
				showMenu(in);
				in= getSelection(UI.sek1, UI.sek3);
				in= (UI.MAINMENU == in)?UI.MAINMENU:processSekMenu(in);
				break;
			case UI.SNP :
				showMenu(in);
				in= getSelection(UI.snp1, UI.snp4);
				in= (UI.MAINMENU == in)?UI.MAINMENU:processSNPMenu(in);
				break;
			default :
				UI.errorDisplay("Fehler bei der Bearbeitung des Menus.");
				break;
		}
		}while(true);
	}

	private int processSNPTargetMenu(int in) {
		String input;
		String temp=config.getProperty("PARAM_SNP_OF_INTEREST");
		String sequence=primer3config.getProperty("SEQUENCE");
		String flankingLenString=config.getProperty("PARAM_LENTH_OF_5'/3'_SNP_FLANKING_SEQUENCES_TO_BE_AMPLIFIED");
		int flankingInt=Integer.parseInt(flankingLenString);
		String basindex=primer3config.getProperty("PRIMER_FIRST_BASE_INDEX");
		int indexstart=Integer.parseInt(basindex);
		int intPos=0;

		switch (in) {
			case UI.target1 :
				input=getInput("Position des Ziel-SNPs in der Sequenz: ["+temp+"] : ");
				if("" != input) {
					try {
						intPos=Integer.parseInt(input);
					} catch (NumberFormatException e) {
						return UI.SNPTARGET;
					}
					if(0 > intPos || intPos >=sequence.length()) {
						return UI.SNPTARGET;
					}
				}
				break;
			case UI.target2:
				input=getInput("SNP 5' FLANKING SEQUENCE : ");
				//--------------------------------------------------------------------------
				//los gehts...
				int counterOfOccurences=0;
				int start=0;
				boolean forward=true;
				int tempInt=0;
				while(-1 != (tempInt = sequence.indexOf(input, start))) {
					start=tempInt+1;
					counterOfOccurences++;
				}
				start--;
				if(0 == counterOfOccurences) {//nichts gefunden -> suche revers-compl.
					String revcomplinput=Helper.revcomplPrimer(input);
					System.out.println("Flanking sequence not found. Searching for reverses complement "+revcomplinput);
					counterOfOccurences=0;
					start=0;
					while(-1 != (start = sequence.indexOf(input, start++))) {
						counterOfOccurences++;
					}
					if(0 == counterOfOccurences) {//auch revers compl. nichts gefunden
						System.out.println("Flanking sequence not found, " +
							"please search again with another 5' Sequence or specify position.");
						return UI.SNPTARGET;
					}else forward=false;
				}
				if(forward) {
					//bei normaler Suche ist ZielSNP rechts der gesuchten Sequenz
					start += input.length();
				}else {
					//ansonsten die Base links der gefunden Sequenz
					start--;
				}
				if(1 == counterOfOccurences) {//gefunden
					if(0 <= (start - flankingInt) && (start+flankingInt)<sequence.length()) {
						intPos=start+indexstart;	//wenn in primer3config der Index bei 1 anfaengt -> +1
					}else {//flankingSequence passt nicht beidseitig in die Sequence
						System.out.println("Flanking sequence is included but TargetSNP+flanking sequences don't fit into SEQUENCE.");
						System.out.println("Adjust SEQUENCE or choose another flanking sequence.");
						return UI.SNPTARGET;
					}
				}else if(1 < counterOfOccurences) {
					System.out.println("More then one sequences found, please search again with a longer 5' Sequence or specify position.");
					return UI.SNPTARGET;
				}
				//--------------------------------------------------------------------------
				break;
			case UI.target3:
				input=getInput("PARAM_LENTH_OF_5'/3'_SNP_FLANKING_SEQUENCES_TO_BE_AMPLIFIED ["+flankingLenString+"] : ");
				if("" != input) {
					config.setProperty("PARAM_LENTH_OF_5'/3'_SNP_FLANKING_SEQUENCES_TO_BE_AMPLIFIED",input);
				}
				break;
			default :
				break;
		}
		String prop=""+(intPos-flankingInt)+','+(flankingInt*2+1);
		primer3config.setProperty("TARGET",prop);
		return UI.SNPTARGET;
	}

	/**
	 * @param in
	 */
	private int processGeneralMenu(int in) {
		switch (in) {
			case UI.gen1 :
				String input=getInput("Max. Score ["+config.getProperty("SCORE_MAXSCORE")+"] : ");
				if("" != input){
					config.setProperty("SCORE_MAXSCORE",input);
				}
				break;
			default :
				break;
		}
		return UI.GENERAL;
	}

	/**
	 * @param in
	 */
	private int processGCDiffMenu(int in) {
		String input;
		switch (in) {
			case UI.gc1 :
				input=getInput("GCDiff - Modul ["+config.getProperty("GCDIFF")+"] : ");
				if(input.equalsIgnoreCase("true")) {
					config.setProperty("GCDIFF","true");
				}else {
					config.setProperty("GCDIFF","false");
				}
				break;
			case UI.gc2 :
				input=getInput("PARAM_GCDIFF_LOWER_BOUND ["+config.getProperty("PARAM_GCDIFF_LOWER_BOUND")+"] : ");
				if("" != input){
					config.setProperty("PARAM_GCDIFF_LOWER_BOUND",input);
				}
				break;
			case UI.gc3 :
				input=getInput("PARAM_GCDIFF_UPPER_BOUND ["+config.getProperty("PARAM_GCDIFF_UPPER_BOUND")+"] : ");
				if("" != input){
					config.setProperty("PARAM_GCDIFF_UPPER_BOUND",input);
				}
				break;
			default :
				break;
		}
		return UI.GCDIFF;
	}

	/**
	 * @param in
	 */
	private int processRepSeqMenu(int in) {
		String input;
		switch (in) {
			case UI.rep1 :
				input=getInput("Repetetive Sequenzen - Modul ["+config.getProperty("REP")+"] : ");
				if(input.equalsIgnoreCase("true")) {
					config.setProperty("REP","true");
				}else {
					config.setProperty("REP","false");
				}
				break;
			case UI.rep2 :
				input=getInput("PARAM_REPETETIVE_SEQ (max 100!)["+config.getProperty("PARAM_REPETETIVE_SEQ")+"]% : ");
				if("" != input){
					config.setProperty("PARAM_REPETETIVE_SEQ",input);
				}
				break;
			case UI.rep3 :
				input=getInput("Liste repetetiver Sequenzen ["+config.getProperty("REP_SEQ_LIST")+"] : ");
				if("" != input){
					config.setProperty("REP_SEQ_LIST",input);
				}
				break;
			default :
				break;
		}
		return UI.REPSEQ;
	}

	/**
	 * @param in
	 */
	private int processSaveMenu(int in) {
		switch (in) {
			case UI.save1 :
				if (null != primer3config) {
					try {
                        primer3config.updateConfigFile(config.getProperty("INFILE"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
				}
				break;
			case UI.save2 :
				try {System.out.println("updating configfile: "+configfilename);
                    config.updateConfigFile(configfilename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
				break;
			default :
				break;
		}
		return UI.SAVE;
	}

	/**
	 * @param in
	 */
	private int processSekMenu(int in) {
		String input;
		switch (in) {
			case UI.sek1 :
				input= getInput("SekundaereAnalyse - Modul [" + config.getProperty("SEK") + "] : ");
				if (input.equalsIgnoreCase("true")) {
					config.setProperty("SEK", "true");
				} else {
					config.setProperty("SEK", "false");
				}
				break;
			case UI.sek2 :
				input= getInput("PARAM_WINDOW_SIZE am 3'-Ende ["+config.getProperty("PARAM_WINDOW_SIZE") + "] : ");
				if("" != input){
					config.setProperty("PARAM_WINDOW_SIZE",input);
				}
				break;
			case UI.sek3 :
				input= getInput("PARAM_MIN_BINDING innerhalb des Window ["+config.getProperty("PARAM_MIN_BINDING") + "] : ");
				if("" != input){
					config.setProperty("PARAM_MIN_BINDING",input);
				}
				break;
			default :
				break;
		}
		return UI.SEK;
	}

	/**
	 * @param in
	 */
	private int processSNPMenu(int in) {
		String input;
		switch (in) {
			case UI.snp1 :
			input= getInput("unberwuenschte SNP - Modul [" + config.getProperty("SNP") + "] : ");
			if (input.equalsIgnoreCase("true")) {
				config.setProperty("SNP", "true");
			} else {
				config.setProperty("SNP", "false");
			}
				break;
			case UI.snp2 :
				input= getInput("Punktzahl ["+config.getProperty("SCORE_SNP_END") + "] : ");
				if("" != input){
					config.setProperty("SCORE_SNP_END",input);
				}
				break;
			case UI.snp3 :
			input= getInput("Punktzahlen fuer Abstand 3,4,... mit ' ' getrennt ["+config.getProperty("SCORE_SNP_DISTANCES") + "] : ");
			if("" != input){
				config.setProperty("SCORE_SNP_DISTANCES",input);
			}
			break;
			case UI.snp4 :
				input= getInput("SNP-Positionen mit ' ' getrennt [" + config.getProperty("SNP_LIST") + "] : ");
				if ("" != input) {
					config.setProperty("SNP_LIST", input);
				}
				break;
			default :
				break;
		}
		return UI.SNP;
	}

	/**
	 * @param in
	 */
	private int processBLATMenu(int in) {
			return UI.BLAT;
	}

	private int getSelection(int min,int max) {
		int i=-1;
		String temp;
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		do {
			System.out.print("Auswahl : ");
			try {
				temp=in.readLine();
				i=Integer.parseInt(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
			catch (NumberFormatException e) {}
		}while ((i<min || i>max) && UI.MAINMENU != i);
		return i;
	}

	private String getInput(String frage) {
		String temp="";
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		System.out.print(frage);
		try {
			temp=in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}

	/**
	 * @param in
	 */
	private int processPrimer3Menu(int in) {
		if(null == primer3config)
            try {
                primer3config=new Primer3Config(config.getProperty("INFILE"));
            } catch (IOException e) {
                e.printStackTrace();
                return UI.PRIMER3;
            }

		String input;
		switch (in) {
			case UI.pr1 :
				input=getInput("SEQUENCE [" + config.getProperty("SEQUENCE") + "] : ");
				if ("" != input) {
                    config.setProperty("SEQUENCE", input);
				}
				break;
			case UI.pr2 :
				input=getInput("PRIMER_MISPRIMING_LIBRARY [" + primer3config.getProperty("PRIMER_MISPRIMING_LIBRARY") + "] : ");
				if ("" != input) {
					primer3config.setProperty("PRIMER_MISPRIMING_LIBRARY", input);
				}
				break;
			case UI.pr3 :
				input=getInput("PRIMER_PRODUCT_SIZE_RANGE [" + primer3config.getProperty("PRIMER_PRODUCT_SIZE_RANGE") + "] : ");
				if ("" != input) {
					primer3config.setProperty("PRIMER_PRODUCT_SIZE_RANGE", input);
				}
				break;
			case UI.pr4 :
				input=getInput("PRIMER_NUM_RETURN [" + primer3config.getProperty("PRIMER_NUM_RETURN") + "] : ");
				if ("" != input) {
                    config.setProperty("PRIMER_NUM_RETURN", input);
				}
				break;
			case UI.pr5 :
				input=getInput("PRIMER_MAX_POLY_X [" + primer3config.getProperty("PRIMER_MAX_POLY_X") + "] : ");
				if ("" != input) {
					primer3config.setProperty("PRIMER_MAX_POLY_X", input);
				}
				break;
			case UI.pr6 :
				return UI.SNPTARGET;
			default :
				break;
		}
		return UI.PRIMER3;
	}
	/**
	 * Zeige ConsolenMenu an.
	 * @param i gibt Menupunkt an, 0 ist Hauptmenu...
	 */
	private void showMenu(int i) {

		switch (i) {
			case UI.MAINMENU :
				System.out.println("\nKonfigurationsmenu");
				System.out.println("------------------\n");
				System.out.println("1. Primer3-Parameter");
				System.out.println("2. BLAT");
				System.out.println("3. GCDiff");
				System.out.println("4. SekundaerAnalyse");
				System.out.println("5. Repetetive Sequenzen");
				System.out.println("6. Unerwuenschte SNPs");
				System.out.println("7. Generelles");
				System.out.println("8. Speichern");
				System.out.println("\n0. Start des Programms");
				System.out.println();
				return;
			case UI.PRIMER3 :
				System.out.println("\nPrimer3-Parameter");
				System.out.println("-----------------\n");
				System.out.println("11. SEQUENCE ");
				System.out.println("12. PRIMER_MISPRIMING_LIBRARY");
				System.out.println("13. PRIMER_PRODUCT_SIZE_RANGE");
				System.out.println("14. PRIMER_NUM_RETURN");
				System.out.println("15. PRIMER_MAX_POLY_X");
				System.out.println("16. Ziel-SNP eingeben");
				System.out.println();
				break;
			case UI.BLAT :
				System.out.println("\nBLAT-Parameter");
				System.out.println("--------------\n");
				System.out.println("nicht implementiert!");
				break;
			case UI.GCDIFF:
				System.out.println("\nGCDiff-Parameter");
				System.out.println("--------------\n");
				System.out.println("31. GCDiff an/aus");
				System.out.println("32. PARAM_GCDIFF_LOWER_BOUND");
				System.out.println("33. PARAM_GCDIFF_UPPER_BOUND");
				System.out.println();
				break;
			case UI.SEK:
				System.out.println("\nSekundaerAnalyse");
				System.out.println("----------------\n");
				System.out.println("41. SekundaerAnalyse an/aus");
				System.out.println("42. PARAM_WINDOW_SIZE am 3'-Ende");
				System.out.println("43. PARAM_MIN_BINDING");
				System.out.println();
				break;
			case UI.REPSEQ:
				System.out.println("\nRepetetive Sequenzen");
				System.out.println("--------------------\n");
				System.out.println("51. Repetetive Sequenzen an/aus");
				System.out.println("52. PARAM_REPETETIVE_SEQ in Prozent");
				System.out.println("53. Liste von repetetiven Sequenzen (z.B. \"10,10 35,120 300,15\"");
				System.out.println();
				break;
			case UI.SNP:
				System.out.println("\nUnerwuenschte SNPs");
				System.out.println("------------------\n");
				System.out.println("61. SNP an/aus");
				System.out.println("62. Punktzahl fuer SNP am 3'-Ende bzw. bis zu 2 Basen davor");
				System.out.println("63. Punkte fuer weitere SNP-Positionen");
				System.out.println("64. SNP-Positionen eingeben");
				System.out.println();
				break;
			case UI.GENERAL:
				System.out.println("\nGenerelles");
				System.out.println("----------\n");
				System.out.println("71. MAXSCORE - CutOff-Grenze");
				System.out.println("");
				System.out.println("");
				break;
			case UI.SAVE:
				System.out.println("\nSpeichern");
				System.out.println("---------\n");
				System.out.println("81. Speichere Primer3-Parameter");
				System.out.println("82. Speichere PCR-Parameter");
				System.out.println();
				break;
			case UI.SNPTARGET:
				System.out.println("\nZiel-SNP eingeben");
				System.out.println("-----------------\n");
				System.out.println("161. SNP-Position direkt eingeben");
				System.out.println("162. SNP-5' flankierende Sequenz");
				System.out.println("163. PARAM_LENTH_OF_5'/3'_SNP_FLANKING_SEQUENCES_TO_BE_AMPLIFIED");
				System.out.println();
			default :
				break;
		}
		System.out.println("0. zurueck\n");
	}

	private void helpDisplay() {
		System.out.println("Syntax:\t" +
							"PCR [options] PCRconfigfile\n");
		System.out.println("Options: (statt den Optionen im Configfile!)\n--------");
		System.out.println("\t-help\t\tAnzeige dieser Parameterliste");
		System.out.println("\t+out=outfile\tDateiname fuer Ausgabe (Achtung: Wird ueberschrieben!)");
        System.out.println("\t+embl=emblfile\tDateiname des EMBL-Files, fakultativ");
        System.out.println("\t+uscd=uscdfile\tLies rep.Seq. aus USCDFile (nicht die Sequenz!)");
		System.out.println("\t+hair\t\tSekundaeranalyse (Hairpins)an (default)");
		System.out.println("\t-hair\t\tSekundaeranalyse (Hairpins) aus");
        System.out.println("\t+cross\t\tSekundaeranalyse (Crossdimer)an (default)");
        System.out.println("\t-cross\t\tSekundaeranalyse (Crossdimer) aus");
        System.out.println("\t+homo\t\tSekundaeranalyse (Homodimer)an (default)");
        System.out.println("\t-homo\t\tSekundaeranalyse (Homodimer) aus");
		System.out.println("\t+gcdiff\t\tGC-Differenz beruecksichtigen (default)");
		System.out.println("\t-gcdiff\t\tGC-Differenz nicht beruecksichtigen");
		System.out.println("\t+blat\t\tBLAT-Analyse an (default)");
		System.out.println("\t-blat\t\tBLAT-Analyse aus");
		System.out.println("\t+snp\t\tSNP-Suche an (default) ");
		System.out.println("\t-snp\t\tSNP-Suche aus");
		System.out.println("\t+rep\t\tBeruecksichtigung repetetiver Sequenzen an");
		System.out.println("\t-rep\t\tBeruecksichtigung repetetiver Sequenzen aus");
        System.out.println("\t+exon\t\tExon/Introngrenze muss zwischen Primern liegen");
        System.out.println("\t-exon\t\tExon/Introngrenze ignorieren");
		System.out.println("\t+interactive\tFragt interaktiv nach Parametern");
		System.out.println("\n\t+debug\t\tZeige interne Berechnungen an");
	}

	/**
	 * Gibt Fehlermeldung aus, wenn ein unbekannter Parameter
	 * eingegeben wurde.
	 * @param param Eingegebener Parameter
	 */
	public static void errorDisplay(String message) {
		System.err.println(message);
		System.exit(1);
	}

}
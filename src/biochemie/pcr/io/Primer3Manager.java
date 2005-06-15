/*
 * Created on 27.01.2004
 *
 */
package biochemie.pcr.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import biochemie.pcr.PCR;
import biochemie.pcr.PrimerPair;
import biochemie.util.Helper;

/**
 * Klasse zum Kapseln aller Primer3 relevanten Sachen. Funktion: Der Paramter INFILE wird aus der PCRConfig
 * ausgelesen und primer3 mit dieser Eingabe gestartet. Danach wird die Anzahl gefundener Paare ermittelt,
 * die SEQUENCE im PCRConfigobjekt gespeichert. Die Datei wird offen gehalten, so dass die Ergebnisse
 * stueckweise ausgelesen werden koennen. Die Datei wird geschlossen, wenn das letzte Ergebnis ausgelesen wurde.
 * @author Steffen
 *
 */
public class Primer3Manager {
    /**
     * aktuelle Position im ErgebnisFile (Nr. des aktuellen Paares)
     */
    private int aktPos=0;
    private int indexbase=-1;
    private PCRConfig config;
    private String primer3output;
    private String primer3exe;
    private int numberOfResults=0;
    private BufferedReader input=null;
	private String feste5seq;
    private final boolean debug;
    private final int offset;
    private int blocksize;

    public Primer3Manager(PCRConfig config, boolean debug){
        this.config=config;
        this.offset=config.getInteger("INTERN_SEQ_OFFSET",0);
        this.feste5seq=Helper.getNuklFromString(config.getProperty("FESTE5SEQ"));
        this.primer3exe=config.getString("PRIMER3COMMAND");
        this.debug=debug;
    }


    class StreamGobbler extends Thread
	{
	    InputStream is;
	    String type;
	    OutputStream os;

	    StreamGobbler(InputStream is, String type)
	    {
	        this(is, type, null);
	    }

	    StreamGobbler(InputStream is, String type, OutputStream redirect)
	    {
	        this.is = is;
	        this.type = type;
	        this.os = redirect;
	    }

	    public void run()
	    {
	        try
	        {
	            PrintWriter pw = null;
	            if (null != os)
	                pw = new PrintWriter(os);

	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String line=null;
	            while ( null != (line = br.readLine()))
	            {
	                if (null != pw)
	                    pw.println(line);
	                //System.out.println(type + ">" + line);
	            }
	            if (null != pw)
	                pw.flush();
	        } catch (IOException ioe)
	            {
	            ioe.printStackTrace();
	            }
	    }
	}
    /**
     * Start von primer3. Auslesen relevanter Parameter, wie z.B. Anzahl gefundener Paare und Index_Base.
     */
    public void runPrimer3(String filename) {

        this.primer3output=filename+".tmp";
        try {
            String osName = System.getProperty("os.name" );
            String[] aufruf=new String[3];
            if( osName.equals( "Windows NT" )
                || osName.equals( "Windows XP" )
                || osName.equals( "Windows 2000"))
            {
                aufruf[0] = "cmd.exe";
                aufruf[1] = "/C";
            }
            else if( osName.equals( "Windows 95") || osName.equals( "Windows 98") )
            {
                aufruf[0] = "command.com";
                aufruf[1] = "/C";
            }
            else if( osName.toLowerCase().indexOf("linux") != -1) {
                aufruf[0]="/bin/sh";
                aufruf[1] = "-c";
            }
              else {
                  //aufruf = "/bin/sh "+ primer3output;
                  UI.errorDisplay("OS "+osName+" not supported, yet. Sorry.");
              }
            aufruf[2]=primer3exe+" < "+filename+" > "+primer3output;
            //aufruf[2]="/home/sdienst/bin/primer3.exe <test.in > test.in.tmp";
            if(debug){
            	System.out.println("calling primer3 via: "+Helper.toString(aufruf));
            }
            if(PCR.verbose) {
                System.out.println("Starte PRIMER3 (kann je nach Parametern dauern!)...");
            }
            /*
             * Das Problem ist folgendes: Es gibt scheinbar Deadlocks beim lesen/schreiben der Ein-/Ausgabe des Prozesses.
             * Laut den Infos im Inet reichen irgedwelche Puffer nicht aus, so dass ein Thread, der lesen/schreiben sollte
             * blockiert. Also verwenden wir die Umleitungsfertigkeiten der Shell
             */
            Process primerprocess=Runtime.getRuntime().exec(aufruf);
/*            BufferedReader input=new BufferedReader(new FileReader(this.inputfile));//ausgaben von primer3
            Process primerprocess = Runtime.getRuntime().exec(aufruf);
        StreamGobbler inputGobbler=new StreamGobbler(new FileInputStream(inputfile),"INPUT",primerprocess.getOutputStream());
        StreamGobbler outputGobbler = new StreamGobbler(primerprocess.getInputStream(), "OUTPUT", new FileOutputStream(primer3output));

        // kick them off
        inputGobbler.start();
        outputGobbler.start();
*/
            if(0 != primerprocess.waitFor()) {
                switch (primerprocess.exitValue()) {
                    case 0:
                        break;//alles i.O.
                    case -1 :
                        UI.errorDisplay("Kritischer Fehler beim Aufruf von Primer3.");
                        break;
                    case -2 :
                        UI.errorDisplay("Zu wenig Speicher für Aufruf von Primer3.");
                        break;
                    case -3 :
                        UI.errorDisplay("Keine Eingabe für Primer3.");
                        break;
                    case -4 :
                        UI.errorDisplay("Fehler in der Eingabe von Primer3. Fehler steht in der Ausgabedatei unter \"PRIMER_ERROR\"");
                        break;
                    default :
                        System.err.println("Got return code "+primerprocess.exitValue()+" from primer3!");
                        break;
                }
            }
        } catch (IOException e) {
            UI.errorDisplay("Kritischer Fehler beim Aufruf von Primer3 : "+e.getMessage());

        } catch (InterruptedException e) {
            UI.errorDisplay("Primer3 wurde abgebrochen!");
        }
        readNumberOfResults();
    }

    /**
     * Liest Anzahl der von Primer3 errechneten Ergebnisse aus und speichert sie zur späteren Verwendung.
     */
    private void readNumberOfResults() {
        try {
            input=new BufferedReader(new FileReader(this.primer3output));
        } catch (FileNotFoundException e) {
            UI.errorDisplay("Datei "+primer3output+" wurde geloescht, kann Ergebnisse nicht lesen.");
        }
        if(null != input){
            boolean found1,found2,found3,found4,found5;
            found1=found2=found3=found4=found5=false;
            int numreturn=-1, pairexplain=-1;
            String line;
            /* Es muessen drei Werte gefunden werden:
             * - PRIMER_FIRST_BASE_INDEX --> 0 oder 1 basierter Index
             * - PRIMER_PAIR_EXPLAIN     --> enthaelt Anzahl gefundener Paare
             * - PRIMER_NUM_RETURN       --> max. gewuenschte Paare
             * Effektiv vorhanden sind Math.min(pair_explain,num_return)
             */
             try {
                while(null != (line = input.readLine()) && (!found1 || !found2 || !found3 || !found4|| !found5)){
                    //System.out.println(line);
                     if(line.startsWith("PRIMER_NUM_RETURN")){
                         String temp=line.substring(line.indexOf('=')+1).trim();
                         numreturn=Integer.parseInt(temp);
                         found1=true;
                     }else if(line.startsWith("PRIMER_PAIR_EXPLAIN")){
                         String temp=line.substring(line.indexOf("ok ")+3);
                         pairexplain=Integer.parseInt(temp);
                         found2=true;
                     }else if(line.startsWith("PRIMER_FIRST_BASE_INDEX")){
                         String temp=line.substring(line.indexOf('=')+1);
                         indexbase=Integer.parseInt(temp);
                         found3=true;
                     }else if(line.startsWith("SEQUENCE")){
                         String temp=line.substring(line.indexOf('=')+1);
                         found4=true;
                     }else if(line.startsWith("PRIMER_MISPRIMING_LIBRARY=")){
                         if(line.substring(line.indexOf('=')+1).trim().length()>0) {//wenn eine mispriming library ex., gibts jeweils 23 zeilen
                             blocksize=23;
                             found5=true;
                         }
                     }
                 }
                 input.close();
                 input=null;
                 if(-1 == numreturn){
                     UI.errorDisplay("Parameter \"PRIMER_NUM_RETURN\" im Inputfile von Primer3 nicht gesetzt!");
                 }
                 if(-1 == pairexplain){
                     UI.errorDisplay("Parameter \"PRIMER_EXPLAIN_FLAG\" im Inputfile von Primer3 nicht gesetzt!\n" +
                                  "Bitte \"PRIMER_EXPLAIN_FLAG=1\" setzen.");
                 }
                 if(-1 == indexbase){
                     UI.errorDisplay("Parameter \"PRIMER_FIRST_BASE_INDEX\" im Inputfile von Primer3 nicht gesetzt!");
                 }
                 this.numberOfResults=Math.min(numreturn,pairexplain);
            } catch (IOException e1) {
                UI.errorDisplay("Fehler beim Lesen der Primer3-Ergebnisse: "+e1.getMessage());
            }
        }

    }

    public PrimerPair[] getNextResults(int maxNum) throws NumberFormatException {
            PrimerPair[] pps=null;
            if(null == input){
                try {
                    input=new BufferedReader(new FileReader(this.primer3output));
                } catch (FileNotFoundException e) {
                    UI.errorDisplay("Datei "+primer3output+" wurde geloescht, kann Ergebnisse nicht lesen.");
                }
            }
            if(aktPos+maxNum>=numberOfResults)
                maxNum=numberOfResults-aktPos-1;
            if( maxNum <= 0 ){
                try {
                    input.close();
                    input = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            if(PCR.verbose) {
                System.out.println("Lese PrimerPaare aus "+primer3output+"...["+aktPos+'-'+(aktPos+maxNum)+']');
            }
            pps=new PrimerPair[maxNum];

            /**
              Ein typischer Datensatz sieht so aus:
               PRIMER_PAIR_PENALTY_1=37.9560
               PRIMER_LEFT_1_PENALTY=16.798300
               PRIMER_RIGHT_1_PENALTY=19.157707
               PRIMER_LEFT_1_SEQUENCE=gtgtgtgtgtgtgtgtgtgt
               PRIMER_RIGHT_1_SEQUENCE=tctgggttttgttgttttct
               PRIMER_LEFT_1=3296,20
               PRIMER_RIGHT_1=3566,20
               PRIMER_LEFT_1_TM=54.002
               PRIMER_RIGHT_1_TM=54.842
               PRIMER_LEFT_1_GC_PERCENT=50.000
               PRIMER_RIGHT_1_GC_PERCENT=35.000
               PRIMER_LEFT_1_SELF_ANY=0.00
               PRIMER_RIGHT_1_SELF_ANY=1.00
               PRIMER_LEFT_1_SELF_END=0.00
               PRIMER_RIGHT_1_SELF_END=0.00
               PRIMER_LEFT_1_END_STABILITY=6.4000
               PRIMER_RIGHT_1_END_STABILITY=7.0000
               PRIMER_PAIR_1_COMPL_ANY=1.00
               PRIMER_PAIR_1_COMPL_END=0.00
               PRIMER_PRODUCT_SIZE_1=271
               In dieser Reihenfolge wird also gelesen.
             */
            String line=null;
            float gc1=0,gc2=0,gc=0;
            String l,r,temp;
            String[] block;
            int i=0;
            if(0 == aktPos)//sonderfall fuers erste Paar
            try {
                while(null != (line = input.readLine())){
                    if(line.startsWith("PRIMER_PAIR_EXPLAIN=")){
                        block=Primer3Manager.readLines(input,blocksize);
                        //Helper.outputObjectArray(block);
                        //System.out.println("-------------------");
                        l=feste5seq + block[3].substring(block[3].indexOf('=')+1);
                        r=feste5seq + block[4].substring(block[4].indexOf('=')+1);
                        temp=block[5].substring(block[5].indexOf('=')+1);
                        int leftpos=Integer.parseInt(temp.substring(0,temp.indexOf(',')))-this.indexbase + offset;
                        temp=block[6].substring(block[6].indexOf('=')+1);
                        int rightpos=Integer.parseInt(temp.substring(0,temp.indexOf(',')))-this.indexbase + offset;
                        temp=block[9].substring(block[9].indexOf('=')+1);
                        gc1=Float.parseFloat(temp);
                        temp=block[10].substring(block[10].indexOf('=')+1);
                        gc2=Float.parseFloat(temp);
                        gc=Math.abs(gc1-gc2);
                        pps[0]=new PrimerPair(l,r,leftpos,rightpos,gc,aktPos+1, feste5seq.length());
                        i++;
                        aktPos++;
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            while(i<maxNum){
                block=Primer3Manager.readLines(input,blocksize);
//                System.out.println(Helper.toString(block));
//                System.out.println("-------------------");
                l=feste5seq + block[3].substring(block[3].indexOf('=')+1);
                r=feste5seq + block[4].substring(block[4].indexOf('=')+1);
                temp=block[5].substring(block[5].indexOf('=')+1);
                int leftpos=Integer.parseInt(temp.substring(0,temp.indexOf(',')))-this.indexbase + offset;
                temp=block[6].substring(block[6].indexOf('=')+1);
                int rightpos=Integer.parseInt(temp.substring(0,temp.indexOf(',')))-this.indexbase + offset;
                temp=block[9].substring(block[9].indexOf('=')+1);
                gc1=Float.parseFloat(temp);
                temp=block[10].substring(block[10].indexOf('=')+1);
                gc2=Float.parseFloat(temp);
                gc=Math.abs(gc1-gc2);
                pps[i]=new PrimerPair(l,r,leftpos,rightpos,gc,aktPos+1, feste5seq.length());
                i++;
                aktPos++;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pps;
    }

    /**
     * @return
     */
    public int getNumberOfResults() {
        return this.numberOfResults;
    }

	/**
	 * Reads i Lines from input and returns them in a String[]
	 * @param input
	 * @param i
	 * @return
	 */
	private static String[] readLines(BufferedReader input, int i) throws IOException {
	    String[] array= new String[i];
	    for (int j= 0; j < i; j++) {
	        array[j]= input.readLine();
	    }
	    return array;
	}
}
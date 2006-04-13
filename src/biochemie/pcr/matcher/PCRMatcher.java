/*
 * Created on 05.11.2004 by Steffen Dienst
 *
 */
package biochemie.pcr.matcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import com.Ostermiller.util.BadDelimiterException;

import biochemie.domspec.Primer;
import biochemie.pcr.PrimerPair;
import biochemie.pcr.modules.CrossDimerAnalysis;
import biochemie.sbe.WrongValueException;
import biochemie.sbe.io.MultiKnoten;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.Helper;
import biochemie.util.config.GeneralConfig;

/**
 * @author Steffen Dienst
 * 05.11.2004
 */
public class PCRMatcher {
    public static final int MAXCLIQUESTRATEGY = 0;
    public static final int COLORINGSTRATEGY = 1;
    public static final int CDLIKESTRATEGY = 2;
    List pcrpairs;
    private int maxplex;
    private MultiKnoten complex;

    public PCRMatcher(List files) throws IOException {
        this.maxplex=files.size();

        pcrpairs=new ArrayList();
        for (Iterator it = files.iterator(); it.hasNext();) {
            String filename = (String) it.next();
            try {
                pcrpairs.addAll(readPCRPrimersFrom(filename));
            } catch (IOException e) {
                System.out.println("Fehler beim Lesen von Datei \""+filename+"\", skipping...");
            }
        }
        System.out.println("Found "+pcrpairs.size()+" primers in "+maxplex + " files");
    }



    /**
     * @param filename
     * @return
     * @throws IOException
     */
    private Collection readPCRPrimersFrom(String filename) throws IOException {
        List primers=new LinkedList();
        BufferedReader br=new BufferedReader(new FileReader(filename));
        String line=br.readLine();//skip header
        int pos=0;
        while((line=br.readLine()) != null) {
            primers.addAll(parsePrimerLine(line.trim(), filename,++pos));
        }
        return primers;
    }

    /**
     * @param line
     * @return
     */
    private Collection parsePrimerLine(String line, String filename,int pos) {
        List primers=new LinkedList();
        if(line.length() == 0)
            return primers;
        boolean hasPrimer=false;
        for(int i=0;i<line.length();i++) {
            if(line.charAt(i)!=';') {
                hasPrimer=true;
                break;
            }
        }
        if(!hasPrimer)
            return primers;
        try {
            String left;
            String right;
            StringTokenizer st=new StringTokenizer(line,"\";");

            st.nextToken().trim();//ignore pos
            st.nextToken();//ignore org.pos
            left=st.nextToken().trim().toUpperCase();
            st.nextToken();//start
            st.nextToken();//length
            st.nextToken();//gc%1
            st.nextToken();//gc%2
            right=st.nextToken().trim().toUpperCase();
            PCRPrimer p1=new PCRPrimer(filename,pos,line,left,Primer._5_);
            PCRPrimer p2=new PCRPrimer(filename,pos,line,right,Primer._3_);
            primers.add(new PCRPair(p1,p2,maxplex));
        } catch (NoSuchElementException e) {
            System.err.println("Fehler beim Lesen der Zeile :\""+line+"\" in Datei \""+filename+"\"!");
            System.exit(1);
        }
        return primers;
    }

public static void main(String[] args) {
    if(args.length <2) {
        showUsage();
        return;
    }
    ConfigMaker cm=null;
    try {
        cm=new ConfigMaker(args[0]);
    } catch (BadDelimiterException e) {
        System.err.println("Invalid csv, wrong delimiters used!");
        e.printStackTrace();
    } catch (IOException e) {
        System.err.println("Error trying to read the configfile!");
        e.printStackTrace();
    }
    if(cm==null)
        System.exit(1);
    
    List filesToProcess=new ArrayList(args.length-1);
    for (int i = 1; i < args.length; i++) {
        filesToProcess.add(args[i]);
    }    
    System.out.println("Using the following inputfiles:");
    System.out.println(Helper.toStringln(filesToProcess));
    try {
        PCRMatcher pm=new PCRMatcher(filesToProcess);
        pm.doTheCalcBoogie(cm);
        String outname="matcher_"+Helper.dateFunc()+".csv";
        System.out.println("\nWriting to "+outname);
        pm.outputToFile(outname);
    } catch (IOException e) {
        System.err.println("Error while accessing files!");
        e.printStackTrace();
    }
    
}

    public void outputToFile(String outname) throws IOException {
        List pairs=complex.getIncludedElements();
        BufferedWriter bw=new BufferedWriter(new FileWriter(outname));
        bw.write(PrimerPair.getCSVHeaderLine());
        bw.write("\n");
        for (Iterator iter = pairs.iterator(); iter.hasNext();) {
            PCRPair pair = (PCRPair) iter.next();
            bw.write(pair.getCSVLine());
            bw.write("\n");
        }
        bw.close();
    }



    private void doTheCalcBoogie(ConfigMaker cm) {
        int count=0;
        complex=new MultiKnoten(Collections.EMPTY_LIST);
        while(cm.getNumOfConfigsLeft() > 0 && complex.realSize()<maxplex) {
            System.out.println("\nStarte Durchlauf "+count+"...");
            GeneralConfig cfg=cm.getNextConfig();
            MatcherStrategy ms=getMatcherStrategy(cfg);
            updateConfigs(cfg);
            Collection max=ms.getBestPCRPrimerSet(pcrpairs,complex);
            createNewComplex(max);
            System.out.println("Durchlauf "+count+": "+complex.realSize()+" Primer gluecklich vereint... "+complex.toString());
            count++;
        }
    }


    /**
     * Creates a new complex node for the graph and removes all primers with the same ID
     * from primers;
     * @param max
     */
    private void createNewComplex(Collection max) {
        Collection pairs=new HashSet();
        for (Iterator it = max.iterator(); it.hasNext();) {
            Multiplexable m = (Multiplexable) it.next();
            pairs.addAll(m.getIncludedElements());
        }
        complex=new MultiKnoten(pairs);
        pcrpairs.removeAll(pairs);
    }



    private void updateConfigs(GeneralConfig cfg) {
        for (Iterator it = pcrpairs.iterator(); it.hasNext();) {
            PCRPair pair = (PCRPair) it.next();
            pair.setNewConfig(cfg);
        }
        if(complex==null)
            return;
        List inc=complex.getIncludedElements();
        for (Iterator it = inc.iterator(); it.hasNext();) {
            PCRPair pair = (PCRPair) it.next();
            pair.setNewConfig(cfg);
        }
    }



    private MatcherStrategy getMatcherStrategy(GeneralConfig cfg) {
        int sec=cfg.getInteger("TIME_TO_COLOR",0);
        switch (cfg.getInteger("STRATEGY",CDLIKESTRATEGY)) {
        case MAXCLIQUESTRATEGY:
            return new MaxCliqueStrategy(sec,maxplex);
        case COLORINGSTRATEGY:
            return new ColorerStrategy(sec,maxplex);
        case CDLIKESTRATEGY:
            return new CDLikeStrategy(maxplex);
        default:
            return null;
        }
    }




    /**
     *
     */
    private static void showUsage() {
        System.out.println("PCRMatcher.exe configfile PCR-File1 PCR-File2...");
    }
}

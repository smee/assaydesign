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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import biochemie.pcr.modules.CrossDimerAnalysis;
import biochemie.sbe.WrongValueException;
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
    private static double maxtmdiff;
    private static double maxgcdiff;
    List primers;
    private CrossDimerAnalysis cda;
    private int maxplex;

    public PCRMatcher(List files, double tm, double gc, CrossDimerAnalysis cda) throws IOException {
        maxtmdiff=tm;
        maxgcdiff=gc;
        this.cda=cda;
        this.maxplex=files.size();

        primers=new ArrayList();
        for (Iterator it = files.iterator(); it.hasNext();) {
            String filename = (String) it.next();
            primers.addAll(readPCRPrimersFrom(filename));
        }
        System.out.println("Found "+primers.size()+" primers in "+maxplex + " files");
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
            primers.addAll(parsePrimerLine(line, filename,++pos));
        }
        return primers;
    }

    /**
     * @param line
     * @return
     */
    private Collection parsePrimerLine(String line, String idprefix,int pos) {
        List primers=new LinkedList();
        String left;
        String right;
        StringTokenizer st=new StringTokenizer(line,"\";");

        st.nextToken().trim();//ignore pos
        st.nextToken();//ignore org.pos
        left=st.nextToken();
        st.nextToken();//start
        st.nextToken();//length
        right=st.nextToken().trim();
        PCRPrimer p1=new PCRPrimer(idprefix,pos,line,left,PCRPrimer.LEFT,maxtmdiff,maxgcdiff,maxplex,cda);
        PCRPrimer p2=new PCRPrimer(idprefix,pos,line,right,PCRPrimer.RIGHT,maxtmdiff,maxgcdiff,maxplex,cda);
        primers.add(new PCRPair(p1,p2,maxplex));
        return primers;
    }



    public static void main(String[] args) {
        if(args.length <1) {
            showUsage();
            return;
        }
        List filesToProcess=new LinkedList();
        GeneralConfig conf=new GeneralConfig() {
            protected String[][] getInitializedProperties() {
                return new String[][]{
                        {"FILES",""}
                       ,{"TIME_TO_COLOR","30"}
                       ,{"PARAM_CROSS_WINDOW_SIZE","0"}
                       ,{"PARAM_CROSS_MIN_BINDING","0"}
                       ,{"MAX_TM_DIFF","10"}
                       ,{"MAX_GC_DIFF","30"}
                       ,{"STRATEGY","2"}};
            }};
        try {
            conf.readConfigFile(args[0]);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.err.println("Error on reading file \""+args[0]+"\"");
            return;
        }
        if(args.length>1) {//overriding parameter FILES in configfile
            for (int i = 1; i < args.length; i++) {
                filesToProcess.add(args[i]);
            }
        }else {
            StringTokenizer st=new StringTokenizer(conf.getString("FILES")," \t,;:\"");
            while(st.hasMoreTokens())
                filesToProcess.add(st.nextToken());
        }
        try {
        	System.out.println("Using the following inputfiles:");
        	System.out.println(Helper.toStringln(filesToProcess));


        	int seconds=conf.getInteger("TIME_TO_COLOR");
        	CrossDimerAnalysis cda=new CrossDimerAnalysis(conf.getString("PARAM_CROSS_WINDOW_SIZE")
        			,conf.getString("PARAM_CROSS_MIN_BINDING")
					,Boolean.toString(false));
        	PCRMatcher pm=new PCRMatcher(filesToProcess, conf.getDouble("MAX_TM_DIFF"), conf.getDouble("MAX_GC_DIFF"),cda);

        	int maxplex=filesToProcess.size();

        	MatcherStrategy ms=null;

        	int strategyToUse=conf.getInteger("STRATEGY");

        	switch (strategyToUse) {
        	case MAXCLIQUESTRATEGY:
        		ms=new MaxCliqueStrategy(seconds,maxplex);
        		break;
        	case COLORINGSTRATEGY:
        		ms=new ColorerStrategy(seconds,maxplex);
        		break;
        	case CDLIKESTRATEGY:
        		ms=new CDLikeStrategy(maxplex);
        		break;
        	default:
        		break;
        	}
        	//TODO zeilen in n csvfile augeben statt auf stdo
        	Collection pairsToUse=ms.getBestPCRPrimerSet(pm.primers);
        	System.out.println("Using:");
        	System.out.println(Helper.toStringln(pairsToUse));
            String outname="matcher_"+Helper.dateFunc()+".csv";
            System.out.println("\nWriting to "+outname);
            outputFile(pairsToUse, outname);
        } catch (WrongValueException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }




    private static void outputFile(Collection pairsToUse, String outname) throws IOException {
        BufferedWriter bw=new BufferedWriter(new FileWriter(outname));
        for (Iterator iter = pairsToUse.iterator(); iter.hasNext();) {
            PCRPair pair = (PCRPair) iter.next();
            bw.write(pair.getCSVLine());
            bw.write("\n");
        }
        bw.close();
    }



    /**
     *
     */
    private static void showUsage() {
        System.out.println("PCRMatcher.exe configfile [pcrprimerfile1 pcrprimerfile2...]");
    }
}

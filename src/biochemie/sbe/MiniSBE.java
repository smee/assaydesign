package biochemie.sbe;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org._3pq.jgrapht.UndirectedGraph;

import biochemie.sbe.io.SBEConfig;
import biochemie.sbe.io.SBEPrimerReader;
import biochemie.sbe.multiplex.BestellMultiplexer;
import biochemie.sbe.multiplex.ExperimentMultiplexer;
import biochemie.sbe.multiplex.MultiplexableFactory;
import biochemie.sbe.multiplex.Multiplexer;
import biochemie.util.GraphHelper;
import biochemie.util.Helper;
import biochemie.util.LogStdStreams;
/*
 * Created on 18.11.2003
 *
 */
/**
 *
 * @author Steffen
 *
 */
public class MiniSBE {
    SBEPrimerReader sbpr = null;
    public static final int UNKNOWN=-1;
    public static final int CLEAVABLE=0;
    public static final int PINPOINT=1;
    public static final int PROBE=2;
    public static final int PROBE_CLEAVABLE=3;
    public static final int PROBE_PINPOINT=4;
    
    public static String[] assayTypes=new String[]{"Cleavable linker","Pinpoint","Probe","Probe+Cleavable","Probe+Pinpoint"};

    public MiniSBE(String optname, String primername) {
        List sbec=null;
        SBEOptions cfg = null;
        try {
            cfg = new SBEConfig();
            ((SBEConfig)cfg).readConfigFile(optname);
        }catch (FileNotFoundException e) {
        	System.out.println(optname+" nicht gefunden!");
        	return;
    	} catch (IOException e) {
            e.printStackTrace();
            System.out.println("Fehler beim Lesen von \""+optname+"\"");
        }
        try {
        	sbpr = new SBEPrimerReader();
        	sbec = sbpr.getSBECandidates(primername,cfg);
        }catch(IOException ioe){
        	ioe.printStackTrace();
        	System.exit(1);
        } catch (WrongValueException e) {
        	e.printStackTrace();
        	System.exit(1);
        }

        doCalculation(sbec,cfg,Collections.EMPTY_SET);
        primername=new File(primername).getName();
        String outname = "out_" + primername;
        sbpr.writeSBEResults(outname);

    }
    public MiniSBE(List sbec, SBEOptions cfg,Set filter){
    	doCalculation(sbec, cfg,filter);
    }
    protected void doCalculation(List sbec, SBEOptions cfg,Set filter){
        UndirectedGraph oldGraph=null;
        
        Helper.createAndRememberCalcDaltonFrom(cfg);//XXX unsauber, muss anders gehen. 
        Multiplexer m=null;
        while(true) {
            Set structs=new HashSet();
            boolean allgiven=true;
            for (Iterator it = sbec.iterator(); it.hasNext();) {
                MultiplexableFactory mf = (MultiplexableFactory) it.next();
                List l=mf.getMultiplexables();
                allgiven=allgiven && 1 >= l.size();
                structs.addAll(l);
            }
            if(0 == structs.size()) break; //s gibbet nix zu multiplexen
            
            if(Thread.currentThread().isInterrupted())
                return;
            if(allgiven){//wenn alle Primer vorgegeben sind, muss ich Experimente finden
                m=new ExperimentMultiplexer(cfg);
            }else
                m=new BestellMultiplexer(cfg);
            if(!cfg.getSecStrucOptions().isAllCrossdimersAreEvil() 
                    && !cfg.getSecStrucOptions().isIgnoreCompCrossdimers()
                    && cfg.getSecStrucOptions().isSecStrucEdgeCreating())
                structs=Multiplexer.getEnhancedPrimerList(structs,cfg);
            oldGraph=createGraphAndMultiplex(structs,oldGraph,cfg,m,filter);
            
        }
    }


    private UndirectedGraph createGraphAndMultiplex(Set structs, UndirectedGraph oldGraph, SBEOptions cfg, Multiplexer m,Set filter) {
        long t=System.currentTimeMillis();
        boolean drawGraph=cfg.isDrawGraphes();
        UndirectedGraph g;
        if(oldGraph!=null) {
            System.out.println("Recycling old graph....");
            Set vert=new HashSet(oldGraph.vertexSet());
            for (Iterator it = vert.iterator(); it.hasNext();) {
                Object v = (Object) it.next();
                if(!structs.contains(v))
                    oldGraph.removeVertex(v);
            }
            g=oldGraph;
        }else {
            System.out.println("Creating graph with "+structs.size()+" vertices...");
            g=GraphHelper.createIncompGraph(structs,drawGraph, 0,filter);
        }
        System.out.println("Graph has "+g.edgeSet().size()+" edges.");
        System.out.println("Graph creation took "+(System.currentTimeMillis()-t)+"ms");
        m.findMultiplexes(g);
        return g;
    }
    public static void main(String[] args) {
        if ( args.length < 2) {
            System.out.println("Usage:\n------\n\n MiniSBE.exe optionsfile primerfile.csv");
            return;
        }
        String optname= "", primername="";

        primername= args[args.length-1];
        optname= args[args.length-2];
        initLogfile(".");
        try {
            MiniSBE m=new MiniSBE(optname,primername);
        }catch(RuntimeException rte) {
            rte.printStackTrace();
        }
    }


    /**
     *
     */
    public static void initLogfile(String path) {
        try {
            String startstring="---------Program started: " + new Date()+" -----------\n";
            startstring+="\tVersion: $$$DATE$$$";
            if(!Boolean.getBoolean("DEBUG"))
                LogStdStreams.initializeErrorLogging(path+File.separatorChar+"minisbe.log",startstring, true, false);
        } catch (RuntimeException e) {
            e.printStackTrace();//XXX
        }
    }
    public static String getDatum() {
        Calendar cal=Calendar.getInstance();
        int day= cal.get(Calendar.DAY_OF_MONTH);
        int month= cal.get(Calendar.MONTH);
        int year= cal.get(Calendar.YEAR);
        StringBuffer sb= new StringBuffer();
        if (10 > day)
            sb.append('0');
        sb.append(day);
        if (10 > month)
            sb.append('0');
        sb.append(month);
        if (99 < year) {
            year -= (year / 100) * 100;
        }
        if (10 > year)
            sb.append('0');
        sb.append(year);
        return sb.toString();
    }

}
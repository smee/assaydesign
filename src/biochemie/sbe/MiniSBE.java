package biochemie.sbe;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import biochemie.calcdalton.CalcDalton;
import biochemie.sbe.io.SBEConfig;
import biochemie.sbe.io.SBEPrimerReader;
import biochemie.sbe.multiplex.BestellMultiplexer;
import biochemie.sbe.multiplex.ExperimentMultiplexer;
import biochemie.sbe.multiplex.Multiplexer;
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
	
    public MiniSBE(String optname, String primername) {
        List sbec=null;
        SBEOptionsProvider cfg = null;
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

        doCalculation(sbec,cfg);
        primername=new File(primername).getName();
        String outname = "out_" + primername;
        sbpr.writeSBEResults(outname);
        
    }
    public MiniSBE(List sbec, SBEOptionsProvider cfg){
    	doCalculation(sbec, cfg);
    }
    protected void doCalculation(List sbec, SBEOptionsProvider cfg){
        Multiplexer m1=new ExperimentMultiplexer(cfg);
        Multiplexer m2=new BestellMultiplexer(cfg);
        while(true) {
            List structs=new ArrayList();
            boolean allgiven=true;
            for (Iterator it = sbec.iterator(); it.hasNext();) {
                MultiplexableFactory mf = (MultiplexableFactory) it.next();
                List l=mf.getMultiplexables();
                allgiven=allgiven && 1 >= l.size();
                structs.addAll(l);
            }
            if(0 == structs.size()) break; //s gibbet nix zu multiplexen
            
            if(allgiven)//wenn alle Primer vorgegeben sind, muss ich Experimente finden
                m1.findMultiplexes(structs);
            else
                m2.findMultiplexes(structs);
                
        }
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
            startstring+="\t11.01.05 mit diversen Guitweaks";
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
    
    
	/**
	 * Helpermethode.
	 * @param cfg
	 * @return
	 */
	public static CalcDalton getCalcDalton(SBEOptionsProvider cfg) {
		return new CalcDalton(cfg.getPhotolinkerPositions()
				             ,cfg.getCalcDaltonFrom()
							 ,cfg.getCalcDaltonTo()
							 ,cfg.getCalcDaltonPeaks()
				             ,cfg.getCalcDaltonVerbFrom()
							 ,cfg.getCalcDaltonVerbTo()
							 ,cfg.getCalcDaltonAllowOverlap());
	}

}
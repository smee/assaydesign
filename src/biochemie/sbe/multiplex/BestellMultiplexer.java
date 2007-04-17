/*
 * Created on 02.08.2004 by Steffen
 *
 */
package biochemie.sbe.multiplex;

import java.util.Set;

import org._3pq.jgrapht.UndirectedGraph;

import biochemie.sbe.SBEOptions;
import biochemie.sbe.calculators.MaximumCliqueFinder;
import biochemie.sbe.calculators.ReusableThread;
import biochemie.util.GraphHelper;
import biochemie.util.GraphWriter;

/**
 * Bestimmt per Maxcliquensuche die jeweils groesstmoegliche Menge von Primer.
 * Es wird also auf jeder Stufe die Groesse des Multiplexes dessen Groesse maximiert.
 * @author Steffen
 * 02.08.2004
 */
public class BestellMultiplexer extends Multiplexer {
    ReusableThread rt;

    /**
     * @param csvr
     * @param debug
     */
    public BestellMultiplexer(SBEOptions cfg) {
        super(cfg);
	    rt=new ReusableThread(cfg.getCalcTime() * 1000);
    }

    
	public void findMultiplexes(UndirectedGraph g) {
        System.out.println("Using BestellMultiplexer... (maximizes the size of multiplexes)");
        if(Thread.currentThread().isInterrupted())
            return;
	    UndirectedGraph grev=GraphHelper.getKomplementaerGraph(g,cfg.isDrawGraphes(),GraphWriter.TGF);
        System.out.println("RevGraph consists of "+grev.vertexSet().size()+" vertices, "+grev.edgeSet().size()+" edges.");
	    
	    int maxplexnr= cfg.getMaxPlex();
	    if(maxplexnr>g.vertexSet().size())
	        maxplexnr=g.vertexSet().size();
	    rt.setInterruptableJob(new MaximumCliqueFinder(grev,maxplexnr,debug));
	    Set maxclique=(Set)rt.getResult();	    
	    
        if(Thread.currentThread().isInterrupted())
            return;
        
	    if(debug) {
	        System.out.println("Maxclique found: ");
	        System.out.println("Size: "+maxclique.size());
	    }


        giveMultiplexIDTo(maxclique);
    }

} 
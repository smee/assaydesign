/*
 * Created on 02.08.2004 by Steffen
 *
 */
package biochemie.sbe.multiplex;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org._3pq.jgrapht.UndirectedGraph;

import biochemie.sbe.SBEOptionsProvider;
import biochemie.sbe.calculators.MaximumCliqueFinder;
import biochemie.sbe.calculators.Multiplexable;
import biochemie.sbe.calculators.ResusableThread;
import biochemie.util.GraphHelper;

/**
 * Bestimmt per Maxcliquensuche die jeweils groesstmoegliche Menge von Primer.
 * Es wird also auf jeder Stufe die Groesse des Multiplexes dessen Groesse maximiert.
 * @author Steffen
 * 02.08.2004
 */
public class BestellMultiplexer extends Multiplexer {
    ResusableThread rt;

    /**
     * @param csvr
     * @param debug
     */
    public BestellMultiplexer(SBEOptionsProvider cfg) {
        super(cfg);
	    rt=new ResusableThread(cfg.getCalcTime() * 1000);
    }

    
	public void findMultiplexes(List mult) {
        System.out.println("Using BestellMultiplexer... (maximizes the size of multiplexes)");
        boolean drawGraph=cfg.isDrawGraphes();

	    UndirectedGraph g=GraphHelper.createIncompGraph(mult,drawGraph,0);
	    UndirectedGraph grev=GraphHelper.getKomplementaerGraph(g);
	    if(debug)
	        System.out.println("RevGraph consists of "+grev.vertexSet().size()+" vertices, "+grev.edgeSet().size()+" edges.");
	    
	    int maxplexnr= cfg.getMaxPlex();
	    if(maxplexnr>mult.size())
	        maxplexnr=mult.size();
	    rt.setInterruptableJob(new MaximumCliqueFinder(grev,maxplexnr,debug));
	    Set maxclique=(Set)rt.getResult();	    
	    
	    if(debug) {
	        System.out.println("Maxclique found: ");
	        System.out.println("Size: "+maxclique.size());
	    }


        String plexid=getNextMultiplexID();
        for (Iterator iter = maxclique.iterator(); iter.hasNext();) {
            Multiplexable struc = (Multiplexable) iter.next();
            System.out.println(struc.getName());
            struc.setPlexID(plexid);
        }
    }

} 
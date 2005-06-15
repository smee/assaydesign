/*
 * Created on 02.08.2004 by Steffen
 *
 */
package biochemie.sbe.multiplex;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org._3pq.jgrapht.UndirectedGraph;

import biochemie.sbe.SBEOptions;
import biochemie.sbe.calculators.MaximumCliqueFinder;
import biochemie.sbe.calculators.ReusableThread;
import biochemie.util.GraphHelper;

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

    
	public void findMultiplexes(List mult) {
        System.out.println("Using BestellMultiplexer... (maximizes the size of multiplexes)");
//        Collections.sort(mult, new Comparator() {
//            public int compare(Object o1, Object o2) {
//                return ((Multiplexable)o1).getName().compareTo(((Multiplexable)o2).getName());
//            }            
//        });
//        for (Iterator it = mult.iterator(); it.hasNext();) {
//            Multiplexable m = (Multiplexable) it.next();
//            System.out.println(m.getName());
//        }
//        System.exit(0);
        boolean drawGraph=cfg.isDrawGraphes();
        System.out.println("Creating graph with "+mult.size()+" vertices...");
	    UndirectedGraph g=GraphHelper.createIncompGraph(mult,drawGraph,0);
        if(Thread.currentThread().isInterrupted())
            return;
	    UndirectedGraph grev=GraphHelper.getKomplementaerGraph(g);
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


        giveMultiplexIDTo(maxclique);
    }

} 
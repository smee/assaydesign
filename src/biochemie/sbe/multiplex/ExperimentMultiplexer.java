/*
 * Created on 24.10.2004 by Steffen Dienst
 *
 */
package biochemie.sbe.multiplex;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org._3pq.jgrapht.UndirectedGraph;

import biochemie.sbe.SBEOptionsProvider;
import biochemie.sbe.calculators.ResusableThread;
import biochemie.sbe.calculators.SBEColorerProxy;
import biochemie.util.GraphHelper;

/**
 * Geht davon aus, dass jeder SBECandidate genau einen Primer hat, so
 * dass nur die Aufteilung der Primer auf Multiplexe gesucht werden muss.
 * Die Anzahl der benoetigten Multiplexe wird minimiert.
 * @author Steffen Dienst
 * 24.10.2004
 */
public class ExperimentMultiplexer extends Multiplexer {
    ResusableThread rt;
    /**
     * @param csvr
     * @param debug
     */
    public ExperimentMultiplexer(SBEOptionsProvider cfg) {
        super(cfg);
        rt=new ResusableThread(cfg.getCalcTime() * 1000);
    }

    public void findMultiplexes(List multip) {
        System.out.println("Using graph coloring to find multiplexes for given primers...");

        boolean drawGraph=cfg.isDrawGraphes();

        UndirectedGraph g=GraphHelper.createIncompGraph(multip,drawGraph, 0);

        SBEColorerProxy itrp=new SBEColorerProxy(g,new HashSet(),debug);
        rt.setInterruptableJob(itrp);
        List colors=(List) rt.getResult();
        Collections.sort(colors,new Comparator() {//sortieren nach Groesse
            public int compare(Object arg0, Object arg1) {
                return ((Collection)arg1).size()-((Collection)arg0).size();
            }
        });
        if (debug) {
            System.out.println("Found multiplexes:\n------------------");
            for (Iterator it = colors.iterator(); it.hasNext();) {
                Set set = (Set) it.next();
                System.out.print("[");
                for (Iterator iter = set.iterator(); iter.hasNext();) {
                    Multiplexable struc = (Multiplexable) iter.next();
                    System.out.print(struc.getName()+' ');
                }
                System.out.println("]");
            }
            System.out.println();
        }
        for (Iterator it = colors.iterator(); it.hasNext();) {
            Set mult = (Set) it.next();
            giveMultiplexIDTo(mult);
        }
    }

}

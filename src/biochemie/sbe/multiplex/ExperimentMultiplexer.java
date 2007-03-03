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

import biochemie.sbe.SBEOptions;
import biochemie.sbe.calculators.ReusableThread;
import biochemie.sbe.calculators.SBEColorerProxy;

/**
 * Geht davon aus, dass jeder SBECandidate genau einen Primer hat, so
 * dass nur die Aufteilung der Primer auf Multiplexe gesucht werden muss.
 * Die Anzahl der benoetigten Multiplexe wird minimiert.
 * @author Steffen Dienst
 * 24.10.2004
 */
public class ExperimentMultiplexer extends Multiplexer {
    ReusableThread rt;
    /**
     * @param csvr
     * @param debug
     */
    public ExperimentMultiplexer(SBEOptions cfg) {
        super(cfg);
        rt=new ReusableThread(cfg.getCalcTime() * 1000);
    }

    public void findMultiplexes(UndirectedGraph g) {
        System.out.println("Using graph coloring to find multiplexes for given primers...");

        System.out.println("RevGraph consists of "+g.vertexSet().size()+" vertices, "+g.edgeSet().size()+" edges.");
        
        if(Thread.currentThread().isInterrupted())
            return;
        SBEColorerProxy itrp=new SBEColorerProxy(g,new HashSet(), cfg.getMaxPlex(),debug);
        rt.setInterruptableJob(itrp);
        List colors=(List) rt.getResult();
        if(colors==null){
            if(debug)
                System.out.println("Interrupted....");
            return;
        }
        Collections.sort(colors,new Comparator() {//sortieren nach Groesse
            public int compare(Object arg0, Object arg1) {
                return countMultiplexables(((Collection)arg1).iterator())-countMultiplexables(((Collection)arg0).iterator());
            }

            private int countMultiplexables(Iterator it) {
                int sum=0;
                while(it.hasNext())
                    sum+=((Multiplexable)it.next()).realSize();
                return sum;
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

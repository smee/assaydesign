/*
 * Created on 06.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.pcr.matcher;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org._3pq.jgrapht.UndirectedGraph;

import biochemie.sbe.calculators.MaximumCliqueFinder;
import biochemie.sbe.calculators.ResusableThread;
import biochemie.util.GraphHelper;
import biochemie.util.GraphWriter;

/**
 * Implementiert die Suche nach passenden PCRPrimerpaaren per Maxcliquensuche.
 * @author Steffen Dienst
 *
 */
public class MaxCliqueStrategy implements MatcherStrategy {

    private int seconds;
    private int maxplex;
    
    public MaxCliqueStrategy(int sec, int maxplex){
        this.seconds=sec;
        this.maxplex=maxplex;
    }
    public Collection getBestPCRPrimerSet(List pcrpairs) {
        System.out.println("Creating graph... (Might take a while!)");
        UndirectedGraph g=GraphHelper.getKomplementaerGraph(GraphHelper.createIncompGraph(pcrpairs,true,GraphWriter.TGF));
        
        System.out.println("Reverse graph has "+g.edgeSet().size()+" edges.");
        
        MaximumCliqueFinder mcf=new MaximumCliqueFinder(g,maxplex,true);
        
        ResusableThread rt=new ResusableThread(seconds*1000);
        
        System.out.println("Time for clique finding: "+seconds+" s");
        System.out.println("Starting clique search...");
        rt.setInterruptableJob(mcf);
        
        Set maxclique=(Set) rt.getResult();

        return maxclique;
    }

}

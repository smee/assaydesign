/*
 * Created on 06.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.pcr.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org._3pq.jgrapht.UndirectedGraph;

import biochemie.sbe.calculators.ResusableThread;
import biochemie.sbe.calculators.SBEColorerProxy;
import biochemie.util.GraphHelper;
import biochemie.util.GraphWriter;

/**
 * @author Steffen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ColorerStrategy implements MatcherStrategy {

    private int seconds;
    private int maxplex;
    
    public ColorerStrategy(int sec, int maxplex){
        this.seconds=sec;
        this.maxplex=maxplex;
    }
    
    public Collection getBestPCRPrimerSet(List pcrpairs) {
        System.out.println("Creating graph... (Might take a while!)");
        UndirectedGraph g=GraphHelper.createIncompGraph(pcrpairs,true,GraphWriter.TGF);
        
        System.out.println("Graph has "+g.edgeSet().size()+" edges.");
        
       
        ResusableThread rt=new ResusableThread(seconds*1000);
        
        System.out.println("Time for Coloring: "+seconds+" s");
        System.out.println("Starting coloring search...");
        
        SBEColorerProxy scp=new SBEColorerProxy(g,new HashSet(),true);
        rt.setInterruptableJob(scp);
        List result=new ArrayList((Collection)rt.getResult());
        
        Comparator resultcomp= new Comparator(){
            public int compare(Object arg0, Object arg1) {
                Set s1=(Set) arg0;
                Set s2=(Set) arg1;
                if(s1.size() != s2.size())
                    return s2.size()-s1.size();
                
                double avg1=getAvg(s1);
                double avg2=getAvg(s2);
                return Double.compare(avg1,avg2);
            }                
        };
        
        Collections.sort(result,resultcomp);
        System.out.println("Found "+result.size()+" working multiplexes:\n" +
        		                          "------------------------------------------------------------");
        for (Iterator it = result.iterator(); it.hasNext();) {
            Set s = (Set) it.next();
            System.out.println("Size: " + s.size()+", avg. pos = " + getAvg(s));
        }
        
        return (Collection) result.get(0);
        
    }
    private double getAvg(Set s){
        double avg1=0;
        for (Iterator it = s.iterator(); it.hasNext();) {
            PCRPair p = (PCRPair) it.next();
            avg1+=p.leftp.getPos();
        }
        avg1/=s.size();
        return avg1;
    }
}

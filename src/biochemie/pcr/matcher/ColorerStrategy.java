/*
 * Created on 06.11.2004
 *
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

import biochemie.domspec.Primer;
import biochemie.sbe.calculators.ReusableThread;
import biochemie.sbe.calculators.SBEColorerProxy;
import biochemie.sbe.io.MultiKnoten;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.GraphHelper;
import biochemie.util.GraphWriter;

/**
 * @author Steffen
 *
 */
public class ColorerStrategy implements MatcherStrategy {

    private int seconds;
    private int maxplex;

    public ColorerStrategy(int sec, int maxplex){
        this.seconds=sec;
        this.maxplex=maxplex;
    }

    public Collection getBestPCRPrimerSet(List pcrpairs, final Multiplexable needed) {
        System.out.println("Creating graph... (Might take a while!)");
        UndirectedGraph g=GraphHelper.createIncompGraph(pcrpairs,true,GraphWriter.TGF, Collections.EMPTY_SET);

        System.out.println("Graph has "+g.edgeSet().size()+" edges.");


        ReusableThread rt=new ReusableThread(seconds*1000);

        System.out.println("Time for Coloring: "+seconds+" s");
        System.out.println("Starting coloring search...");
        
        Set init=new HashSet();
        init.add(needed);
        SBEColorerProxy scp=new SBEColorerProxy(g,init,maxplex,true);
        rt.setInterruptableJob(scp);
        List result=new ArrayList((Collection)rt.getResult());

        Comparator resultcomp= new Comparator(){
            public int compare(Object arg0, Object arg1) {                
                Set s1=(Set) arg0;
                Set s2=(Set) arg1;
                if(s1.contains(needed))
                    return -1;
                if(s2.contains(needed))
                    return +1;
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
            System.out.println("Size: " + getNumOfPrimers(s)+", avg. pos = " + getAvg(s));
        }
        Collection col=(Collection) result.get(0);
        if(col.size()==1)
            return Collections.EMPTY_LIST;
        return col;

    }
    private int getNumOfPrimers(Set s) {
        int count=0;
        for (Iterator it = s.iterator(); it.hasNext();) {
            count+=((Multiplexable)it.next()).realSize();
        }
        return count;
    }

    private double getAvg(Set s){
        double avg1=0;
        for (Iterator it = s.iterator(); it.hasNext();) {
            Object o=it.next();
            if (o instanceof PCRPair) {
                PCRPair p = (PCRPair) o;
                avg1+=p.leftp.getPos();                
            }else if (o instanceof MultiKnoten) {
                MultiKnoten mk = (MultiKnoten) o;
                Set ss=new HashSet(mk.getIncludedElements());
                //unsauber, ist kein richtiger durchschnitt!
                avg1+=getAvg(ss);
            }
        }
        avg1/=s.size();
        return avg1;
    }
}

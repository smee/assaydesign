/*
 * Created on 06.10.2004 by Steffen Dienst
 *
 */
package biochemie.sbe.calculators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org._3pq.jgrapht.UndirectedGraph;

import biochemie.sbe.multiplex.Multiplexable;

/**
 * @author Steffen Dienst
 * 06.10.2004
 */
public class SBEColorerProxy implements Interruptible {

    InterruptableGraphColorer col;
    private UndirectedGraph graph;
    private Set maxclique;
    private List vertices;
    
    int[] maxplexnr;
    /**
     * @param g
     * @param maxPlexNr
     * @param i
     * @param debug
     */
    public SBEColorerProxy(UndirectedGraph g,Set maxclique, boolean debug) {
        this.maxclique=maxclique;
        boolean[][] admatrix=createAdmatrixFrom(g,maxclique);
        col=new InterruptableGraphColorer(admatrix,maxplexnr,maxclique.size(),debug);
        this.graph=g;
    }


    /**
     * @param g
     * @param maxclique2
     * @return
     */
    private boolean[][] createAdmatrixFrom(UndirectedGraph g, Set maxclique) {
        vertices = new ArrayList(maxclique);
        Set s=g.vertexSet();
        for (Iterator it = s.iterator(); it.hasNext();) {
            Object node = it.next();
            if(!maxclique.contains(node))
                vertices.add(node);
        }
        assert vertices.size()==s.size();
        boolean[][] admatrix=new boolean[vertices.size()][];
        for (int i = 0; i < admatrix.length; i++) {
            admatrix[i]=new boolean[vertices.size()];
            Arrays.fill(admatrix[i],false);
        }
        for (int i = 0; i < vertices.size()-1; i++) {
            for (int j = i+1; j < vertices.size(); j++) {
                if(g.containsEdge(vertices.get(i),vertices.get(j)))
                    admatrix[i][j]=admatrix[j][i]=true;
            }
        }
        maxplexnr=new int[vertices.size()];
        for (int i=0;i < vertices.size();i++) {
            Multiplexable m= (Multiplexable) vertices.get(i);
            maxplexnr[i]=m.maxPlexSize();
        }
        return admatrix;
    }


    public void start() {
        col.start();
    }

    /* (non-Javadoc)
     * @see biochemie.sbe.calculators.Interruptible#stop()
     */
    public void stop() {
        col.stop();
    }

    /**
     * Liefert Collection von Sets zurueck, die jeweils den versch. Farben entsprechen 
     */
    public Object getResult() {
        int[] arr=(int[]) col.getResult();
        int max=biochemie.util.Helper.findMaxIn(arr);
        List sets=new ArrayList(max+1);        
        for (int i = 0; i <= max; i++) {
            sets.add(new HashSet());
        }
        for (int i = 0; i < arr.length; i++) {
            Set s=(Set)sets.get(arr[i]);
            s.add(vertices.get(i));
        }
        return sets;
    }

}

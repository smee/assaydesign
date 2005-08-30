/*
 * Created on 10.08.2004 by Steffen
 *
 */
package biochemie.sbe.calculators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.Graph;
import org._3pq.jgrapht.UndirectedGraph;
import org._3pq.jgrapht.alg.ConnectivityInspector;
import org._3pq.jgrapht.alg.util.VertexDegreeComparator;
import org._3pq.jgrapht.graph.SimpleGraph;
import org._3pq.jgrapht.graph.UndirectedSubgraph;
import org._3pq.jgrapht.traverse.BreadthFirstIterator;
import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.UnaryProcedure;

import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.GraphWriter;

/**
 * Findet zu einem gegebenen Graphen die größtmögliche maximale Clique.
 * @author Steffen
 * 10.08.2004
 * TODO beruecksichtigt keine pseudoprimer! (geweichtete knoten oder sowas)
 */
public class MaximumCliqueFinder implements Interruptible{
    List graphes;
    UndirectedGraph graph;
    private final boolean debug;
    private final int maxplex;
    private Set maxclique=null;
    private int k;
    GraphWriter gw;
    private volatile boolean interrupted=false;
    private Thread calcThread;
    private int oldbest;
    
    public MaximumCliqueFinder(boolean[][] admatrix,int maxplexnr,boolean debug) {
        this.debug=debug;
        this.maxplex= Math.min(maxplexnr,admatrix.length);
        if(debug) {
            System.out.println("creating graph of size "+admatrix.length);
            List l=new ArrayList();
            for (int i = 0; i < admatrix.length; i++) {
                l.add(new Integer(i));
            }
            gw=new GraphWriter(l,"clique.tgf",GraphWriter.TGF);
        }
        SimpleGraph graph=createGraphFrom(admatrix);   
        if(debug)
            gw.close();
        graphes=createSortedIndependentSubgraphList(graph);
        
    }
    public MaximumCliqueFinder(UndirectedGraph g,int maxplexnr, boolean debug) {
        this.debug=debug;
        this.maxplex= Math.min(maxplexnr, g.vertexSet().size());
        graph=g;
        graphes=createSortedIndependentSubgraphList(graph);
    }

    /**
     * @param graph2
     * @return
     */
    private List createSortedIndependentSubgraphList(UndirectedGraph graph) {
        List graphes=new ArrayList();
        ConnectivityInspector ci=new ConnectivityInspector(graph);
        List l=ci.connectedSets();
        if(debug) System.out.println("Found "+l.size()+" connected sets.");
        for (Iterator it = l.iterator(); it.hasNext();) {
            Set vertices = (Set) it.next();
            graphes.add(new UndirectedSubgraph(graph,vertices,null));
        }
        /*
         * sortiere nach maximaler Cliquengröße
         */
        if(debug) System.out.println("Finding colorings...");
        final Map m=new HashMap();
        for (Iterator it = graphes.iterator(); it.hasNext();) {
            UndirectedGraph g = (UndirectedGraph) it.next();
            m.put(g,new Integer(findFastColoring(g)));
        }
        Collections.sort(graphes,new Comparator() {
            public int compare(Object o1, Object o2) {
                Integer i1=(Integer)m.get(o1);
                Integer i2=(Integer)m.get(o2);
                return i2.compareTo(i1);
            }
        });
        if(debug) {
            System.out.println("Graph consists of "+graphes.size()+" subgraphes:");
            for (Iterator it = m.keySet().iterator(); it.hasNext();) {
                Graph g=(Graph)it.next();
                int colors=((Integer)m.get(g)).intValue();
                if(1 < g.vertexSet().size())
                    System.out.println(g.vertexSet().size()+" vertices ==> "+colors+" colors max.");
            }
        }
        return graphes;
    }

    /**
     * @param admatrix
     * @return
     */
    private SimpleGraph createGraphFrom(boolean[][] admatrix) {
        SimpleGraph graph=new SimpleGraph();
        for (int i = 0; i < admatrix.length; i++) {
            graph.addVertex(new Integer(i));
        }
        for (int i = 0; i < admatrix.length; i++) {
            boolean[] row=admatrix[i];
            for (int j = i+1; j < row.length; j++) {
                if(row[j]) {
                    graph.addEdge(new Integer(i), new Integer(j));
                    if(debug)
                        gw.addArc(i,j,"");
                }
            }
        }
        return graph;
    }

    public Set maxClique() {
        if(null == maxclique) {
            startSearch();
/*            if(1 == maxclique.size())
                maxclique.clear();*/
        }
        return maxclique;        
    }

    /**
     * Sucht die Teilgraphen nach maximalen Cliquen ab. Dabei nimmt er zuerst
     * die Teilgraphen, die nach erster Schätzung die größte Clique beinhalten könnten.
     */
    private void startSearch() {
        calcThread=Thread.currentThread();
        maxclique=new HashSet();
        int ub=Integer.MAX_VALUE;
        oldbest = 0;
        for (Iterator it = graphes.iterator(); it.hasNext();) {
            UndirectedSubgraph sg = (UndirectedSubgraph) it.next();
            if(sg.edgeSet().size()<maxclique.size()) continue;   //kann keine groessere Clique enthalten
            Set s=findMaxClique(sg,new HashSet(),maxclique,ub, 0);
            if(s.size()>maxclique.size()) {
                maxclique=s;
            }
        }
    }

    public int maxCliqueSize() {
        if(null == maxclique) {
            startSearch();
        }
        return maxclique.size();
    }
    /**
     * Findet die größtmögliche maximale Clique. Das ganze basiert auf dem Paper 
     * "Exact Coloring of Real-Life Graphs is Easy" von Olivier Coudert
     * @param graph ungerichteter Graph, der die CLique enthält
     * @param cons die Clique, die gerade untersucht wird
     * @param best die größte bisher gefundene Clique
     * @param ub obere Grenze für noch mögliche Cliquen
     * @return
     */
    private Set findMaxClique(final UndirectedGraph g, Set c, Set best, int ub, int plexsize) {
        if(calcThread.isInterrupted()) {//muss leider aufhören :(
            if(best.size()>0)
                return best;
            else return c;
        }
        if(0 == g.vertexSet().size())
            return c;
        if(plexsize>=maxplex)
            return best;
        /* 
         * Finde Näherungslösung für die Färbung um obere Grenze zu haben für die mögliche Größe
         * einer Clique im aktuellen Graphen:
         *
         * Die eigentliche neue Pruningtechnik, Paper S.3, Theorem 1.
         */
        List r=null;
        do {
            r=findRemovableVertices(g,c.size()-best.size());
            for (Iterator iter = r.iterator(); iter.hasNext();) {
               g.removeVertex(iter.next());                
            }
        }while(!r.isEmpty());//wenn Knoten entfernt werden, werden Farben frei, also verändert sich das q anderer Knoten, so daß vielleicht noch mehr entfernt werden können
        /* Es kann keine größere Clique als best mehr gefunden werden,
         * Regel A im Paper:
         */
        if(c.size()+g.vertexSet().size()<=best.size())
            return best;
        ub=Math.min(ub,k+c.size());
        //System.out.println(ub);
        if(ub<=best.size()) {		//prune
            return best;
        }
        /*
         * Wenn ein "Knotengrad < |best|-|C|" ist, kann er nicht an einer größeren Clique beteiligt sein,
         * also kann er entfernt werden. Regel B
         */
        //Algorithm.remove geht leider nicht, weil g.vertexSet() ein UnmodifieableSet liefert
        RuleB rb=new RuleB(best.size()-c.size(),g);
        Algorithms.foreach(g.vertexSet().iterator(),rb);
        rb.doIt();
        if(g.vertexSet().isEmpty()) {//sonst geht Collections.max kaputt
            return best;
        }
        Object v=Collections.max(g.vertexSet(),new VertexDegreeComparator(g));//finde einen Knoten mit maximalem Knotengrad
        
        List l=g.edgesOf(v);
        Set vertices=new HashSet();
        for (Iterator it = l.iterator(); it.hasNext();) {
            Edge e = (Edge) it.next();
            vertices.add(e.getSource());
            vertices.add(e.getTarget());            
        }
        vertices.remove(v);
        UndirectedSubgraph g1=new UndirectedSubgraph(g,vertices,null);//Knoten: Nachbarn von v + zugehörige Kanten
        assert g1.vertexSet().size()==vertices.size();//sicherstellen, dass nur die Nachbarn von v im Graphen enthalten sind
        Set newc=new HashSet(c);
        newc.add(v);
        int newplexsize=plexsize;
        if(v instanceof Multiplexable)
            newplexsize+=((Multiplexable)v).realSize();
        else
            newplexsize++;
        best=findMaxClique(g1,newc,best,ub, newplexsize);
        if(debug)            
            if(best.size()>oldbest) {
                System.out.println(v+" is in clique of size "+best.size());
                oldbest=best.size();
            }
        if(ub==best.size()) {
            return best;
        }
        //Regel c: v darf nicht entfernt werden, wenn...
//        if(g.degreeOf(v)>=g.vertexSet().size()-2) {
//            return best;
//        }
        vertices=new HashSet(g.vertexSet());
        vertices.remove(v);
        g1=new UndirectedSubgraph(g,vertices,null);
        assert g.edgeSet().size() > g1.edgeSet().size();
        return findMaxClique(g1,c,best,ub, plexsize);
    }
    
    private class RuleB implements UnaryProcedure{
        private int neededdegree;
        private UndirectedGraph g;
        private List toRemove;
        RuleB(int n, UndirectedGraph g){
            this.neededdegree=n;
            this.g=g;
            toRemove=new ArrayList();
        }
        /**
         * Ist notwendig, um eine Concurrent#ModificationException zu verhindern
         */
        public void doIt() {
            for (Iterator iter = toRemove.iterator(); iter.hasNext();) {
                g.removeVertex(iter.next());                
            }
            
        }
        public void run(Object obj) {
            assert g.degreeOf(obj)<g.vertexSet().size();//sicherstellen, das im aktuellen subgraph keine überflüssigen Kanten vorkommen
            if(g.degreeOf(obj)<neededdegree)
                toRemove.add(obj);
        }
    }
    
    /**
     * Findet eine beliebige Färbung des Graphen. 
     * @param g
     * @return
     */
    private int findFastColoring(UndirectedGraph g) {
        Object[] vert=g.vertexSet().toArray();
        int[][] blocked=new int[vert.length][vert.length];
        int usedcolors=0;
        
        outer:
        for (int u = 0; u < vert.length; u++) {
            for (int color=0; color <= usedcolors+1; color++) {
                if (0 == blocked[u][color]) {
                    for(int v=u+1;v<vert.length;v++)
                        if(g.containsEdge(vert[u],vert[v]))
                            blocked[v][color]++;
                    usedcolors=Math.max(usedcolors, color+1);
                    continue outer;
                }
            }
        }
        return usedcolors;
    }
    
    private List findRemovableVertices(UndirectedGraph g,int diff) {
        Object[] vert= g.vertexSet().toArray(new Object[g.vertexSet().size()]);
        int[][] blocked=new int[vert.length][vert.length];
        k=0;
        
        outer:
        for (int u = 0; u < vert.length; u++) {
            for (int color=0; color <= k+1; color++) {
                if (0 == blocked[u][color]) {
                    for(int v=0;v<vert.length;v++)
                        if(g.containsEdge(vert[u],vert[v])) {
                            blocked[v][color]++;	//v kann nicht diesselbe farbe haben wie u
                        }
                    k=Math.max(k, color+1);
                    continue outer;
                }
            }
        }
//        if(debug)
//            System.out.println("|c|-|best|+k="+(diff+k));
        /*
         * Laut dem Paper kann jeder Vertex, der mit q versch. Farben gefärbt werden könnte,
         * so dass q >|C| -|best| + k gilt, entfernt werden, weil diese Knoten keine größeren Cliquen erzeugen. 
         */
        List r=new ArrayList();
        for (int i = 0; i < vert.length; i++) {
            int satur=0;
            for (int j = 0; j <k; j++) {
                if(0 != blocked[i][j])
                    satur++;
            }
            //satur enthält jetzt die Anzahl Farben, die der Knoten i annehmen kann
            int q=k-satur;
            if(q>diff+k) {
                r.add(vert[i]);
//                if(debug)
//                    System.out.println("\trem. "+vert[i]+", q="+q);
            }
        }
        return r;
    }
    private Set getInducedVertexSet(Object v,Graph g) {
        Set result=new HashSet();
        BreadthFirstIterator it=new BreadthFirstIterator(g,v);
        it.setCrossComponentTraversal(false);
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see biochemie.sbe.calculators.Interruptible#start()
     */
    public void start() {
        interrupted=false;
        startSearch();        
    }
    /* (non-Javadoc)
     * @see biochemie.sbe.calculators.Interruptible#stop()
     */
    public void stop() {
        if(calcThread != null)
            calcThread.interrupt();
    }
    /* (non-Javadoc)
     * @see biochemie.sbe.calculators.Interruptible#getResult()
     */
    public Object getResult() {
        return maxclique;        
    }
}
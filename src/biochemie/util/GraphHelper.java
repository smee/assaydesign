/*
 * Created on 03.12.2004
 *
 */
package biochemie.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org._3pq.jgrapht.UndirectedGraph;
import org._3pq.jgrapht.graph.SimpleGraph;

import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.edges.MyUndirectedEdge;

/**
 * @author sdienst
 *
 */
public class GraphHelper {
    
    /**
     * Erstellt komplementaeren Graphen.
     * @param g
     * @return
     */
    public static UndirectedGraph getKomplementaerGraph(UndirectedGraph g, boolean writeGraph, int outputtype) {
        if(g == null)
            return null;
        GraphWriter gw=null;
        if(writeGraph) {
            gw=new GraphWriter(createNamesList(g.vertexSet()),"revgraph",outputtype);
        }
        UndirectedGraph result=new SimpleGraph();
        List vert=new ArrayList(g.vertexSet());
        result.addAllVertices(vert);
        for (int i = 0; i < vert.size(); i++) {
            Multiplexable v1=(Multiplexable) vert.get(i);
//          System.out.println("v1="+v1);
//          System.out.println("v2="+v1);
//          System.out.println("edges for v1: "+g.edgesOf(v1));
            List included1=v1.getIncludedElements();
            for (Iterator it = included1.iterator(); it.hasNext();) {
                Multiplexable m1 = (Multiplexable) it.next();
                
                for (int j = i+1; j < vert.size(); j++) {
                    Multiplexable v2=(Multiplexable) vert.get(j);
                    List included2=v2.getIncludedElements();
                    for (Iterator it2 = included2.iterator(); it2.hasNext();) {
                        Multiplexable m2 = (Multiplexable) it2.next();
                        //TODO SBEPrimerProxy kommt nicht als source/target vor, sondern nur enthaltene primer...
                        
                        if(!m1.equals(m2) && !g.containsEdge(m1,m2)) {
                            result.addEdge(m1,m2);
                            if(writeGraph)
                                gw.addArc(i,j,"");
                        }
                    }
                }
            }
        }
        if(gw != null)
            gw.close();
        return result;
    }
    
    /**
     * Erstellt den Unvertraeglichkkeitsgraphen von einer List von Multiplexables.
     * @param multiplexables
     * @param writegraph true, graph wird in eine Datei geschrieben
     * @param outputtype kann sein: GraphWriter.TGF, GraphWriter.GML oder XWG, 0 default
     * @param filteredEdges Kanten, die ignoriert werden sollen
     * @return Undirectedgraph
     */
    public static UndirectedGraph createIncompGraph(Collection multiplexables, final boolean writegraph, int outputtype, Set filteredEdges) {
        List names = createNamesList(multiplexables);
        GraphWriter gw=null;
        if(writegraph) {
            gw=new GraphWriter(names,"graph",outputtype);
        }
        Multiplexable[] mult=(Multiplexable[]) multiplexables.toArray(new Multiplexable[multiplexables.size()]);
        UndirectedGraph g=new SimpleGraph();
        g.addAllVertices(multiplexables);
        for (int i = 0; i < mult.length; i++) {
            Multiplexable s1=(Multiplexable) mult[i];
            if(Thread.currentThread().isInterrupted())
                return null;
            for (int j = i+1; j < mult.length; j++) {
                Multiplexable s2=(Multiplexable) mult[j];
                if(!s1.passtMit(s2)) {
                    Collection edges=s1.getLastEdges();
                    for (Iterator it = edges.iterator(); it.hasNext();) {
                        MyUndirectedEdge edge = (MyUndirectedEdge) it.next();
                        
                        if(!filteredEdges.contains(edge.matchString())) {
                            //loops lassen wir weg, entstehen z.b. wenn man wegen kompatiblen crossdimern multiknoten bildet
                            if(!edge.getSource().equals(edge.getTarget()))
                                g.addEdge(edge);
                            if(writegraph)
                                gw.addArc(i,j,edge.toString());
                        }else
                            System.out.println("skipping edge, filtered... ("+edge+")");
                    }
                }
            }
        }
        if(gw != null)
            gw.close();
        return g;
    }
    
    /**
     * @param multiplexables
     * @return
     */
    private static List createNamesList(Collection multiplexables) {
        List names=new ArrayList(multiplexables.size());
        for (Iterator it = multiplexables.iterator(); it.hasNext();) {
            Multiplexable p = (Multiplexable) it.next();
            names.add(p.getName());
        }
        return names;
    }
}

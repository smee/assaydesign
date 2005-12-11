/*
 * Created on 03.12.2004
 *
 */
package biochemie.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
            Object v1=vert.get(i);
//            System.out.println("v1="+v1);
//            System.out.println("edges for v1: "+g.edgesOf(v1));
            for (int j = i+1; j < vert.size(); j++) {
                Object v2=vert.get(j);
                if(!g.containsEdge(v1,v2)) {
                    result.addEdge(v1,v2);
                    if(writeGraph)
                        gw.addArc(i,j,"");
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
    public static UndirectedGraph createIncompGraph(List multiplexables, final boolean writegraph, int outputtype, Set filteredEdges) {
        List names = createNamesList(multiplexables);
        GraphWriter gw=null;
        if(writegraph) {
            gw=new GraphWriter(names,"graph",outputtype);
        }
        UndirectedGraph g=new SimpleGraph();
        g.addAllVertices(multiplexables);
        for (int i = 0; i < multiplexables.size(); i++) {
            Multiplexable s1=(Multiplexable) multiplexables.get(i);
            if(Thread.currentThread().isInterrupted())
                return null;
            for (int j = i+1; j < multiplexables.size(); j++) {
                Multiplexable s2=(Multiplexable) multiplexables.get(j);
                if(!s1.passtMit(s2)) {//TODO was ist mit weiteren kanten? wenn z.b. sekstruk eine calcdalton-Kante ueberdeckt?
                    if(!filteredEdges.contains(((MyUndirectedEdge)s1.getLastEdge()).matchString())) {
                        g.addEdge(s1.getLastEdge());
                        if(writegraph)
                            gw.addArc(i,j,s1.getLastEdge().toString());
                    }else
                        System.out.println("skipping edge, filtered... ("+s1.getLastEdge()+")");
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

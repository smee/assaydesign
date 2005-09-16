/*
 * Created on 03.12.2004
 *
 */
package biochemie.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org._3pq.jgrapht.UndirectedGraph;
import org._3pq.jgrapht.graph.SimpleGraph;

import biochemie.sbe.multiplex.Multiplexable;

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
    public static UndirectedGraph getKomplementaerGraph(UndirectedGraph g) {
        if(g == null)
            return null;
        UndirectedGraph result=new SimpleGraph();
        List vert=new ArrayList(g.vertexSet());
        result.addAllVertices(vert);
        for (int i = 0; i < vert.size(); i++) {
            Object v1=vert.get(i);
            for (int j = i+1; j < vert.size(); j++) {
                Object v2=vert.get(j);
                if(!g.containsEdge(v1,v2))
                    result.addEdge(v1,v2);
            }
        }
        return result;
    }

    /**
     * Erstellt den Unvertraeglichkkeitsgraphen von einer List von Multiplexables.
     * @param multiplexables
     * @param writegraph true, graph wird in eine Datei geschrieben
     * @param outputtype kann sein: GraphWriter.TGF, GraphWriter.GML oder XWG, 0 default
     * @return Undirectedgraph
     */
    public static UndirectedGraph createIncompGraph(List multiplexables, boolean writegraph, int outputtype) {
        List names=new ArrayList(multiplexables.size());
        for (Iterator it = multiplexables.iterator(); it.hasNext();) {
            Multiplexable p = (Multiplexable) it.next();
            names.add(p.getName());
        }
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
                if(!s1.passtMit(s2)) {
                    g.addEdge(s1,s2);
                    if(writegraph)
                        gw.addArc(i,j,s1.getEdgeReason());
                }
            }
        }
        if(gw != null)
            gw.close();
        return g;
    }

}

/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package biochemie.sbe.multiplex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import biochemie.sbe.MiniSBE;
import biochemie.sbe.SBEOptionsProvider;
import biochemie.sbe.calculators.Multiplexable;

/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class Multiplexer {
	private static int plexnr=1;
    protected final boolean debug;
    protected SBEOptionsProvider cfg;

	public Multiplexer( SBEOptionsProvider cfg){
		this.cfg = cfg;
        this.debug=cfg.isDebug();
	}
	/**
     * Methode, die einer Menge von Multiplexables Multiplex-IDs zuweist.
     * Erwartet eine Liste von @link SBECandidates
	 * @param sbec
	 */
	public abstract void findMultiplexes(List sbec);

    /**
     * Liefert einen String, der als ID eines einzelnen Multiplexes dient.
     * @return
     */
    protected String getNextMultiplexID() {
        return 'M' + MiniSBE.getDatum() + '-' + plexnr++;
    }
    /**
     * Creates graph, in which vertices are PTTStructs, An edge means the adjacent PTTStructs
     * mustn't be used within one multiplex.
     * @param sbec
     * @return
     */
/*    protected UndirectedGraph createIncompGraph(List structs) {
        GraphWriter gw=null;
        if(debug) {
            gw=new GraphWriter(getNames(structs),"graph",GraphWriter.TGF);
        }
        UndirectedGraph g=new SimpleGraph();
        g.addAllVertices(structs);
        for (int i = 0; i < structs.size(); i++) {
            Multiplexable s1=(Multiplexable) structs.get(i);
            for (int j = i+1; j < structs.size(); j++) {
                Multiplexable s2=(Multiplexable) structs.get(j);
                if(!s1.passtMit(s2)) {
                    g.addEdge(s1,s2);
                    if(debug && s1 instanceof biochemie.domspec.Primer)
                        gw.addArc(i,j,((biochemie.domspec.Primer)s1).getEdgeReason());
                }
            }
        }
        if(debug)
            gw.close();
        return g;
    }*/
    /**
     * @param sbec
     * @return
     */
    private List getNames(List structs) {
        List names=new ArrayList();
        for (int i = 0; i < structs.size(); i++) {
            Multiplexable s1=(Multiplexable) structs.get(i);
            names.add(s1.getName());
        }
        return names;
    }
    /**
     * Tags the multiplexables with an unique identifier.
     * @param maxclique
     */
    protected void giveMultiplexIDTo(Set maxclique) {
        String plexid=getNextMultiplexID();
        if(debug) System.out.println("New multiplex "+plexid+" for :");
        for (Iterator iter = maxclique.iterator(); iter.hasNext();) {
            Multiplexable struc = (Multiplexable) iter.next();
            if(debug) System.out.println(struc);
            struc.setPlexID(plexid);
        }
    }

}

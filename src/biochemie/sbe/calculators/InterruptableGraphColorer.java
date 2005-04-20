/*
 * Created on 11.05.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package biochemie.sbe.calculators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org._3pq.jgrapht.UndirectedGraph;



public class InterruptableGraphColorer implements IGraphColorer, Interruptible{
	private int best;
	private final int numnodes;
	private int expExSize;
	private final boolean[][] admatrix;
	private int[][] blocked;
	private Map ergmap;
	int[] coloring;
	int[] usedColorCount;
    /**
     * Maximale Anzahl der Knoten einer Farbe
     */
	int[] plexGroesse;
    int maxplexgroesse;
    private final boolean debug;
    private transient boolean isInterrupted;
    /**
     * Groesse der groessten maximalen Clique. Weniger Farben als diese Clique Knoten hat sind nicht moeglich.
     */
    private int maxcliquesize;
	/**
	 * Liest Matrix.
	 * @param admatrix
	 */
	public InterruptableGraphColorer(boolean[][] admatrix,int[] plexgr, int maxclique,boolean debug){
		this.admatrix=admatrix;
		this.debug=debug;
		this.numnodes=admatrix.length;
		init(plexgr, maxclique);
	}
	public InterruptableGraphColorer(UndirectedGraph g,int[] plexgr, int maxclique,boolean debug){
			this.admatrix=createAdmatrixFromGraph(g);
			this.debug=debug;
			this.numnodes=admatrix.length;
			init(plexgr, maxclique);
	}
/**
	 * @param plexgr
	 * @param maxclique
	 * @param debug
	 */
	private void init(int[] plexgr, int maxclique) {
		this.blocked=new int[numnodes][numnodes];
		this.ergmap=new HashMap();
		this.coloring=new int[numnodes];
		usedColorCount=new int[numnodes];
		this.plexGroesse=plexgr;
		this.maxplexgroesse=biochemie.util.Helper.findMaxIn(plexgr);
		this.maxcliquesize=maxclique;
		isInterrupted=false;
		expExSize=numnodes;
	}
private boolean[][] createAdmatrixFromGraph(UndirectedGraph g){
        List vertices=new ArrayList(g.vertexSet());
        boolean[][] admatrix = new boolean[vertices.size()][vertices.size()];
        for (int i = 0; i < vertices.size()-1; i++) {
                for (int j = i+1; j < vertices.size(); j++) {
                    if(g.containsEdge(vertices.get(i),vertices.get(j)))
                        admatrix[i][j]=admatrix[j][i]=true;
                }
            }
            return admatrix;
}
	/**
	 * Beginnt mit der Suche nach einer Färbung.
	 */
	public void start() {
        long starttime=System.currentTimeMillis();
        best = numnodes+1;
        int col=0;
        for (int i = 0; i < maxcliquesize; i++,col++) {
            for(int v=i+1;v<numnodes;v++) {
                if (admatrix[i][v])
                    blocked[v][col]++;
            }
            usedColorCount[col]++;
            coloring[i]=col;
        }
		visit(maxcliquesize,maxcliquesize);
        System.out.println("Coloring took "+(System.currentTimeMillis() - starttime)+"ms.");
	}
	void visit (int u, int usedcolors){
	    if (usedcolors>=best || (isInterrupted && best<=numnodes))
	        return;
	    if(expExSize<numnodes)//es gibt nichts besseres unter diesen Bedingungen
	        return;
	    if (u==numnodes){
	        best = usedcolors;
	        retrieveColoring(best);
            if(usedcolors==maxcliquesize)
                isInterrupted=true;//weniger farben gehen nicht!
	    }
	    else{
	        int color,v;
	        for (color=0; color <= usedcolors; color++)
	            if (0 == blocked[u][color]) {
	                if(usedColorCount[color]<plexGroesse[u]) {//hab diese Farbe noch nicht zu oft benutzt
	                    usedColorCount[color]++;
	                    for (v=u+1; v < numnodes; v++)
	                        if (admatrix[u][v])
	                            blocked[v][color]++;
	                    coloring[u]=color;
	                    visit(u+1,Math.max(usedcolors,color+1));
	                    for (v=u+1; v < numnodes; v++)
	                        if (admatrix[u][v]) 
	                            blocked[v][color]--;
	                    usedColorCount[color]--;
	                }
	            }
	    }
	}

    private void retrieveColoring(int best){
	    int[] erg=new int[numnodes];
	    System.arraycopy(coloring, 0, erg, 0, numnodes);
	    if(debug) {
	        System.out.println("Found coloring with "+best+" colors:");
		    System.out.println(biochemie.util.Helper.toString(coloring));
	    }
	    ergmap.put(new Integer(best),erg);
	    expExSize=(best-1)*maxplexgroesse;
	}
	public Map getErgMap(){
		return ergmap;
	}
	/**
	 * Liefert Array, in dem für jeden Knoten die gefundene Farbe steht, beginnend bei 0.
	 * @return
	 */
	public int[] getMinimalColoring(){
		return (int[]) ergmap.get(new Integer(best));
	}
	public Object getResult() {
	    return getMinimalColoring();
	}
	public int usedColors(){
		return best;
	}

    /**
     * 
     */
    public synchronized void stop() {
        isInterrupted=true;
    }
}
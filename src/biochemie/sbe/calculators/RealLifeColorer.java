/*
 * Created on 10.08.2004 by Steffen
 *
 */
package biochemie.sbe.calculators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Steffen
 * 10.08.2004
 */
public class RealLifeColorer implements Interruptible {
        private int best;
        private int numnodes;
        private boolean[][] admatrix;
        private int[][] blocked;
        private Map ergmap;
        int[] coloring;
        int[] usedColorCount;
        int plexGroesse;
        private boolean debug;
        private volatile boolean isInterrupted;
        private List name;
        /**
         * Liest Matrix.
         * @param admatrix
         */
        public RealLifeColorer(boolean[][] admatrix,List name, boolean debug){
            this.admatrix=admatrix;
            this.debug=debug;
            this.numnodes=admatrix.length;
            this.blocked=new int[numnodes][numnodes];
            best = numnodes+1;
            this.ergmap=new HashMap();
            this.coloring=new int[numnodes];
            usedColorCount=new int[numnodes];
            this.name=name;
        }
        
        
        /**
         * Beginnt mit der Suche nach einer Färbung.
         */
        public void start() {
            visit(0,0);
        }
        void visit (int u, int usedcolors){
            if (usedcolors>=best || isInterrupted)
                return;
            if (u==numnodes){
                best = usedcolors;
                retrieveColoring(best);
            }
            else{
                int color,v;
                for (color=0; color <= usedcolors; color++)
                    if (0 == blocked[u][color]) {
                        if(usedColorCount[color]<plexGroesse) {//hab diese Farbe noch nicht zu oft benutzt
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
            if(debug) {
                int count;
                for(int i=0;i<best;i++) {
                    count=0;
                    System.out.println("\nColor "+(i+1)+":\n---------");
                    for (int j = 0; j < erg.length; j++) {
                        if(erg[j]==i) {
                            System.out.print(name.get(j)+" ");
                            count++;
                        }
                    }
                    System.out.println();                   
                }
            }
            ergmap.put(new Integer(best),erg);
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

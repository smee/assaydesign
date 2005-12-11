/*
 * Created on 06.11.2004
 *
 */
package biochemie.pcr.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.Helper;

/**
 * @author Steffen
 *
 */
public class CDLikeStrategy implements MatcherStrategy {

    private int maxplex;
    private int solutionsize;

    public CDLikeStrategy(int maxplex){
        this.maxplex=maxplex;
    }
    public Collection getBestPCRPrimerSet(List pcrpairs, Multiplexable needed) {
        solutionsize=Integer.MAX_VALUE;
        List l=new ArrayList(pcrpairs.size()+1);
        if(needed!=null && needed.realSize()>0)
            l.add(needed);
        l.addAll(pcrpairs);
        Multiplexable[][] pairs=createPairMatrix(l);
//        System.out.print("got "+pairs.length+" files, with ");
//        for (int i = 0; i < pairs.length; i++) {
//            System.out.print(pairs[i].length+" ");
//        }
//        System.out.println();
        boolean[][][][] passtMatrix=createPasstMatrix(pairs);

        List erglist=new LinkedList();

        int ptr=0;
        int[] laufvar=new int[pairs.length];
        laufvar[0]=0;

        do{
            boolean okay=true;
            for(int i=0;i < ptr && okay;i++){
                //System.out.println(i+" "+laufvar[i]+" "+ptr+" "+laufvar[ptr]);
                okay = passtMatrix[i][laufvar[i]][ptr][laufvar[ptr]];
            }
            if(okay){
                if(ptr == laufvar.length -1){//haben eine Loesung
                    if(!noSmallerSolutionPossible(laufvar,ptr)){
                        int sum=sum(laufvar,ptr);
                        if(sum<=solutionsize){
                            if(sum<solutionsize){
                                erglist.clear();
                                solutionsize=sum;
                            }
                            erglist.add(Helper.clone(laufvar));
                        }
                    }
                }else{//noch nicht ganz unten
                    ++ptr;
                    laufvar[ptr]=0;
                    continue;
                }
            }
            laufvar[ptr]++;
            if(laufvar[ptr] == pairs[ptr].length) {//bin in dieser zeile fertig
                ptr--;          //und eins hoch
                while(-1 != ptr && laufvar[ptr] >= pairs[ptr].length-1)//ueber alle festen und fertigen
                    ptr--;
                if(-1 == ptr) //abbruch, falls ganz oben
                    break;
                laufvar[ptr]++;
            }
        }while(laufvar[0] < pairs.length);

        List result=new ArrayList();
        if(erglist.size()==0)
            return result;
        int[] e=(int[])erglist.get(0);
        for (int i = 0; i < e.length; i++) {
            result.add(pairs[i][e[i]]);
        }
        return result;
    }

    /**
     * @return
     */
    protected boolean noSmallerSolutionPossible(int[] laufvar,int ptr) {
        int sum=sum(laufvar,ptr);
        if(sum>solutionsize)
            return true;
        return false;
    }

    	protected int sum(int[] laufvar,int ptr){
            int sum=0;
            for (int i= 0; i <= ptr; i++) {
                sum+=laufvar[i];
            }
            return sum;
    	}
    /**
     * @param pairs
     * @return
     */
    private boolean[][][][] createPasstMatrix(Multiplexable[][] pairs) {
        boolean[][][][] m=new boolean[pairs.length][][][];
        for (int i = 0; i < pairs.length; i++) {
            m[i]=new boolean[pairs[i].length][][];
            for (int j = 0; j < pairs[i].length; j++) {
                m[i][j]=new boolean[pairs.length][];
                for (int k = 0; k < pairs.length; k++) {
                    m[i][j][k]=new boolean[pairs[k].length];
                }
            }
        }
        for (int i = 0; i < pairs.length; i++) {
            for (int j = 0; j < pairs[i].length; j++) {
                for (int k = i+1; k < pairs.length; k++) {
                    for (int l = 0; l < pairs[k].length; l++) {
                        m[i][j][k][l]=pairs[i][j].passtMit(pairs[k][l]);
                    }
                }
            }
        }
        return m;
    }
    /**
     * Erstellt Matrix: Anzahl versch. Dateien(IDs) als Zeilen und die jeweiligen PCRPairs sortiert nach Pos. in der Datei in den Spalten
     * @param pcrpairs
     * @return
     */
    private Multiplexable[][] createPairMatrix(List pcrpairs) {
        Map ids=new HashMap();
        for (Iterator it = pcrpairs.iterator(); it.hasNext();) {
            Multiplexable p = (Multiplexable) it.next();
            List inc=p.getIncludedElements();
            for (Iterator iter = inc.iterator(); iter.hasNext();) {
                PCRPair pair = (PCRPair) iter.next();
                String idx=pair.getName();
                List l=(List) ids.get(idx);
                if(l == null){
                    l = new ArrayList();
                    ids.put(idx,l);
                }
                l.add(pair);
            }
        }

        Multiplexable[][] matrix=new Multiplexable[ids.size()][];
        int i=0;

        for (Iterator iter = ids.keySet().iterator(); iter.hasNext();i++) {
            String idx=(String) iter.next();
            List l=(List) ids.get(idx);

//            Collections.sort(l, new Comparator(){
//                public int compare(Object arg0, Object arg1) {
//                    return ((PCRPair)arg0).leftp.getPos() -  ((Multiplexable)arg1).leftp.getPos() ;
//                }
//            });
            matrix[i]= (Multiplexable[]) l.toArray(new Multiplexable[l.size()]);
        }

        return matrix;
    }

}

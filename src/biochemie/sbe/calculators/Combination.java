/*
 * Created on 01.12.2003
 *
 */
package biochemie.sbe.calculators;
import biochemie.util.Helper;
/**
 *
 * @author Steffen
 *
 */
public class Combination {
    private int N;
    private int M;
    int[] aktcomb;
    
    public Combination(int N, int M){
        this.N=N;
        this.M=M;
        aktcomb=null;        
    }
    /**
     * init wird erst nach dem ersten Aufruf irgendeiner Methode gestartet.
     * Workaround für den FAll einer Kombination von k aus k. In diesem Fall gibt es nur
     * eine Kombination. Da hasNext() immer angibt, ob nach der aktuellen noch weitere
     * Kombinationen vorhanden sind, würde in diesem Fall keine Kombination verwendet werden.
     *
     */
    private void init() {
        aktcomb=new int[M];
        for (int i= 0; i < aktcomb.length; i++)
            aktcomb[i]=i;
        aktcomb[M - 1] -= 1;
    }
    public int[] getNextCombination(){
        if(null == aktcomb)
            init();
        for (int i= aktcomb.length-1; 0 <= i; i--) {
            aktcomb[i]++;
            if(aktcomb[i]<=N-M+i){
                return Helper.clone(aktcomb);
            }else{
                if(0 == i)
                    throw new IllegalStateException("Keine Kombination mehr!");
                else{
                    aktcomb[i]=aktcomb[i-1]+2;
                    for(int j=i+1;j<aktcomb.length;j++)
                        aktcomb[j]=aktcomb[j-1]+1;
                }
            }
        }
        return Helper.clone(aktcomb);
    }
    public boolean hasNext(){
        if(null == aktcomb){
            init();
            return true;
        }            
        return aktcomb[0]!=N-M;
    }
    /**
     * @param crossd
     * @return
     */
    public int[] getNextCombinationWithout(int num) {
        if(0 > num || num>=N)
            return getNextCombination();
        if(hasNext())
            getNextCombination();
        else return null;
        for(int i=0;i<aktcomb.length;i++){
            if(aktcomb[i]==num){
                aktcomb[i]++;
                for(int j=i+1;j<aktcomb.length;j++){
                    aktcomb[j]=aktcomb[j-1]+1;
                }
                aktcomb[aktcomb.length-1]--;
                if(hasNext())
                    return getNextCombination();
                else 
                    return null;
            }
        }
        return Helper.clone(aktcomb);
    }
    public String toString(){
        StringBuffer sb=new StringBuffer();
        sb.append(M).append(" aus ").append(N);
        return sb.toString();
    }
}

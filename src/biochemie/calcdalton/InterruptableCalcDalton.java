/*
 * Created on 15.06.2004 by Steffen
 *
 */
package biochemie.calcdalton;

import java.util.ArrayList;
import java.util.List;

import biochemie.sbe.calculators.Interruptible;
import biochemie.util.Helper;

/**
 * @author Steffen
 * 15.06.2004
 */
public class InterruptableCalcDalton extends CalcDalton implements Interruptible{
   private boolean interrupted=false;
private String[][] sbeData;
private int[] fest;
private int[][] erg;

    public InterruptableCalcDalton(int[] br, double[] abstaendeFrom, double[] abstaendeTo, double peaks, boolean overlap) {
		super(br,abstaendeFrom,abstaendeTo,peaks,new double[0],new double[0],overlap);
    }
	public InterruptableCalcDalton(int[] br, double[] abstaendeFrom, double[] abstaendeTo
					, double peaks
					, double[] verbMasseFrom, double[] verbMasseTo
					, boolean overlap){
	    super(br,abstaendeFrom,abstaendeTo,peaks,verbMasseFrom,verbMasseTo,overlap);
	}
	
	public int[][] calc(String[][] sbeData, int[] fest) {
        List erglist=new ArrayList();
        boolean[] brIstFest=new boolean[fest.length]; //Feld fuer feste Bruchstellen
        solutionSize=Integer.MAX_VALUE;
		anzahl_sbe=fest.length;        //wieviele SBE-Primer?
	    //anzahlVarsNeeded=(int) Math.ceil(Math.log(brlen)/Math.log(10000));
        anzahlVarsNeeded=6 > anzahl_sbe?anzahl_sbe:6;
		/*Workaround für feste Bruchstelle:
		 * ich muss sicherstellen, dass bei fester gewählter Bruchstelle die jeweilige
		 * Schleife jeweils nur genau einmal durchlaufen wird. Deshalb wird flags[i] auf false
		 * gesetzt, wenn die jeweilige Bruchstelle fest sein soll. Dann wird die zugehörige 
		 * Schleife jeweils nur genau einmal durchlaufen.
		 */
		for (int i= 0; i < brIstFest.length; i++) {
			if(-1 < fest[i]) {
				brIstFest[i]=true;
			}
			else {
				brIstFest[i]=false;
				fest[i]=0;//sonst ist die noch auf -1, also falscher Index
			}
						
		}
        /*-------------------------- hier gehts los------------------------------*/
        maxreacheddepth=0;
        initializeMassen(sbeData);
		if(1 == sbeData.length) {
			erglist.add(new int[]{fest[0]});
		}
		else {
            laufvar=new int[anzahl_sbe];//enthaelt k,l,m,...
            double[][] sbe_masse=new double[anzahl_sbe][];
            int ptr=0;//Laufvariable
            laufvar[0]=fest[0];
            int[][][][] checked=new int[anzahl_sbe][brlen][anzahl_sbe][brlen];
            do {
                sbe_masse[ptr]=getMassenArray(ptr,laufvar[ptr]);
                boolean okay=true;
                for(int i=0;i<ptr && okay;i++) {//vergleiche diese Masse mit allen drueber
                        if(0 == checked[ptr][laufvar[ptr]][i][laufvar[i]]){//wurde noch nicht getestet
                            boolean flag;
                            if(!overlap) //count++;
                                flag=isDiffOkay(sbe_masse[i],sbe_masse[ptr]);
                            else
                                flag=isDiffOverlapOkay(sbe_masse[i],sbe_masse[ptr]);
                            if(!flag){
                                okay=false;
                                checked[ptr][laufvar[ptr]][i][laufvar[i]]=-1;
                            }else{
                                checked[ptr][laufvar[ptr]][i][laufvar[i]]=1;
                            }
                        }else{//schon verglichen!
                            if(0 > checked[ptr][laufvar[ptr]][i][laufvar[i]]) {
                                okay=false;
                            }
                        }
                    
                }
                if(okay) {//passt alles bisher
                    maxreacheddepth=(ptr>maxreacheddepth)?ptr:maxreacheddepth;
                    if(ptr==anzahl_sbe-1) {//sind wir ganz unten? wenn ja -->tabelle bauen
                        if(!noSmallerSolutionPossible(ptr)){
                            setSolutionSize();
                            if(CalcDalton.debug){ 
                                System.out.print("Lsg. new: "+biochemie.util.Helper.toString(laufvar));
                                System.out.println();
                            }
                            erglist.add(Helper.clone(laufvar));
                        }
                    }else {//noch nicht ganz unten
                        ptr++;//also eins runter
                        laufvar[ptr]=fest[ptr];
                        continue;
                    }
                }
                laufvar[ptr]++;
                if(laufvar[ptr]==brlen || brIstFest[ptr] || noSmallerSolutionPossible(ptr)) {//bin in dieser zeile fertig oder feste br
                    ptr--;          //und eins hoch
                    while(-1 != ptr && (brIstFest[ptr] || laufvar[ptr]==brlen-1))//ueber alle festen und fertigen
                        ptr--;
                    if(-1 == ptr) //abbruch, falls ganz oben
                        break;
                    laufvar[ptr]++;
                }
            }while(laufvar[0]!=brlen && !interrupted);//wenn die erste Laufvariable auf brlen steht, simmer ferdsch
	}
	int[][] erg=new int[erglist.size()][];
    for (int i= 0; i < erglist.size(); i++) {
        Object obj=erglist.get(i);
        erg[i]=(int[]) obj;
    }
    return erg;
    }
	
	public void setParameters(String[][] sbeData, int[] fest) {
	    this.sbeData=sbeData;
	    this.fest=fest;
	    interrupted=false;
	}
    public void start() {
        this.erg=calc(sbeData,fest);
    }

    public synchronized void stop() {
        interrupted=true;
    }
    public Object getResult() {
        return new Integer(maxreacheddepth);
    }
}

/*
 * Created on 14.12.2003
 *
 */
package biochemie.pcr.modules;

import java.util.ArrayList;
import java.util.List;

import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.PCRConfig;
import biochemie.util.Helper;

/**
 *
 * @author Steffen
 *
 */
public class CrossDimerAnalysis extends SekAnalysis{
     
    public CrossDimerAnalysis(PCRConfig cfg, boolean debug) {
        super(cfg,CROSS,debug);
    }
	public CrossDimerAnalysis(String windowsizes, String minbinds, String debug) {
		super(windowsizes, minbinds,debug, CROSS);
	}
    public CrossDimerAnalysis(int win, int min, boolean debug){
        super(null,CROSS,debug);
        this.windowsize=new int[]{win};
        this.minbinds=new int[]{min};
        validateParameter();
    }
    
    /**
     * Berechnung der Strafpunkte für Sekundärstruktur eines Primers.
     * Formel: Maxscore*(Anzahl Bindungen im Fenster wenn mehr als min_binding)/windowsize
     * Der rechte Primer ist in funktionell richtiger Richtung, kann also so wie er ist
     * analysiert werden.
     */
    public void calcScores(PrimerPair[] pps) {
        if(null == pps)
            return;
        float x;
        for(int j=0;j<windowsize.length;j++) {
	        for(int i=0;i<pps.length;i++) {
	            x=analyzeCrossDimer(pps[i].leftp,pps[i].rightp,j);
	            pps[i].addCrossScore(Math.round(maxscore*x/windowsize[j]));
				x=analyzeCrossDimer(pps[i].rightp,pps[i].leftp,j);
				pps[i].addCrossScore(Math.round(maxscore*x/windowsize[j]));
	        }
	    }
    }

   /**
    * Berechnet die Anzahl von binds nach bekannter Vorgehensweise. Beide Primer muessen
    * in 5'-3'-Richtung angegeben werden.
    * @param primer
    * @param primer2
    * @return
    */
    public int analyzeCrossDimer(String primer2, String primer,int idx){
        if(0 == windowsize[idx] || 0 == minbinds[idx])
            return 0;
        int binds= 0;               //temp. var für momentane anzahl binds  
        maxbind=Integer.MIN_VALUE;              //temp. Var. für momentane anzahl maximaler binds
        int pos=0;
        int rcIndex=0;
        int j;
        if(null == liste)
        	liste=new ArrayList();
        String rcPrimer=Helper.revcomplPrimer(primer2);
        for(int i=1;i<primer.length();i++) {
            if(primer.charAt(i)==rcPrimer.charAt(0) && Helper.isNukleotid(primer.charAt(i))) {
                binds=1;
                for(j=i+1,rcIndex=1;j<i+windowsize[idx] && j<primer.length() && rcIndex<rcPrimer.length();j++,rcIndex++) {
                    if(primer.charAt(j)==rcPrimer.charAt(rcIndex) && Helper.isNukleotid(primer.charAt(j))) {
                        binds++;
                    }
                }
                if(binds>maxbind) {
                    maxbind=binds;
                    pos=i;
                }
                if(binds>=minbinds[idx]){
                        liste.add(new Integer(primer.length()-i));
					if(debug)
						System.out.println(Helper.outputXDimer(primer,primer2,pos,windowsize[idx]));    
                }
            }
        }
        return (maxbind< minbinds[idx]) ? 0 : maxbind;
    }
	public int analyzeAllCrossDimer(String primer, String primer2){
		int sum=0;
		for(int j=0;j<windowsize.length;j++) {
			sum+=analyzeCrossDimer(primer,primer2,j);
		}
		return sum;
	}    
    /**
     * Gibt die Positionen der Hairpins als Array zurueck. Es werden die Parameter windowsize und
     * min_binding beachtet, d.h. wenn weniger als min_binding Basen binden wird 0 zurückgegeben!
     * Die Ergebnisse repraesentieren jeweils die Position im Primer <code>primer </code>, an denen das 3'-Ende
     * bindet (von 3' aus gerechnet), start bei 1. D.h. wenn das Ende genau auf den Anfang fällt, würde
     * primer.length() zurückgegeben werden. ACHTUNG: es wird nur die angegebene Mgl. getestet, d.h. wenn man wirklich
     * alle Crossdimer haben will muss noch getCrossDimerPositions(primer2, primer) aufgerufen werden!
     * @return int[]
     */
    public List getCrossDimerPositions(String primer, String primer2){
        analyzeAllCrossDimer(primer,primer2);
       	List l=liste;
       	liste=null;
        return l;
        
    }
}

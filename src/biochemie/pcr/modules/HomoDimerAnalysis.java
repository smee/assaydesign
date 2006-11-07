/*
 * Created on 14.12.2003
 *
 */
package biochemie.pcr.modules;

import java.util.HashSet;
import java.util.Set;

import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.PCRConfig;
import biochemie.util.Helper;
/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class HomoDimerAnalysis extends SekAnalysis{
    
    public HomoDimerAnalysis(PCRConfig cfg, boolean debug) {
        super(cfg,HOMO,debug);
    }
	public HomoDimerAnalysis(String w, String b, String d){
		super(w,b,d,HOMO);
	}
    public HomoDimerAnalysis(int windowSize, int minbinds, boolean debug){
        super(null,HOMO,debug);
        this.windowsize=new int[]{windowSize};
        this.minbinds=new int[]{minbinds};
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
        for(int j=0;j<windowsize.length;j++){
	        for(int i=0;i<pps.length;i++) {
	            x=analyzeHomoDimer(pps[i].leftp,j);
	            pps[i].addHomoScore(Math.round(maxscore*x/windowsize[j]));
	            x=analyzeHomoDimer(pps[i].rightp,j);
	            pps[i].addHomoScore(Math.round(maxscore*x/windowsize[j]));
	        }
        }
    }
    

    private int analyzeHomoDimer(String primer, int idx) {
        if(0 == windowsize[idx] || 0 == minbinds[idx])
            return 0;
        int binds= 0;               //temp. var für momentane anzahl binds  
        maxbind= Integer.MIN_VALUE;
        int pos=0;
        int rcIndex=0;
        int j;
        if(null == posSet)
        	posSet=new HashSet();
        String rcPrimer=Helper.revcomplPrimer(primer);
        for(int i=1;i<primer.length();i++) {
            if(primer.charAt(i)==rcPrimer.charAt(0)) {
                binds=1;
                for(j=i+1,rcIndex=1;j<i+windowsize[idx]&&j<primer.length();j++,rcIndex++) {
                    if(primer.charAt(j)==rcPrimer.charAt(rcIndex)) {
                        binds++;
                    }
                }
                if(binds>maxbind) {
                    maxbind=binds;
                    pos=i;
                }
                if(binds>=minbinds[idx]){
                    posSet.add(new Integer(primer.length()-i));
					if(debug)
						System.out.println(Helper.outputXDimer(primer,primer,pos,windowsize[idx],0));
                }
            }
        }
        return (maxbind< minbinds[idx]) ? 0 : maxbind;
    }
	private int analyzeAllHomoDimer(String primer) {
		int sum=0;
		for (int i = 0; i < windowsize.length; i++) {
			sum+=analyzeHomoDimer(primer,i);
		}
		return sum;
	}
    /**
     * Gibt die Positionen der Homodimer als Array zurueck. Es werden die Parameter windowsize und
     * min_binding beachtet, d.h. wenn weniger als min_binding Basen binden wird 0 zurückgegeben!
     * Die Ergebnisse repraesentieren jeweils die Position im Primer, an denen das 3'-Ende
     * bindet (von 3' aus gerechnet), start bei 1. D.h. wenn das Ende genau auf den Anfang fällt, würde
     * primer.length() zurückgegeben werden
     * @return int[]
     */
    public Set getHomoDimerPositions(String primer){
        analyzeAllHomoDimer(primer);
        Set l=posSet;
		posSet=null;
        return l;
    }
}

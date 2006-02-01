package biochemie.pcr.modules;

import java.util.ArrayList;
import java.util.List;

import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.PCRConfig;
import biochemie.util.Helper;


/*
 * 
 */
/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class HairpinAnalysis extends SekAnalysis{
     
	public HairpinAnalysis(PCRConfig cfg, boolean debug) {
		super(cfg,HAIR,debug);
	}
	public HairpinAnalysis(String w, String b, String d){
		super(w,b,d,HAIR);
	}
    public HairpinAnalysis(int windowSize, int minbinds, boolean debug){
        super(null,HAIR,debug);
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
        for(int j=0;j<windowsize.length;j++){
	    	float x;
	    	for(int i=0;i<pps.length;i++) {
	            x=analyzePrimerHairpin(pps[i].leftp,j);
	    		pps[i].addHairScore(Math.round(maxscore*x/windowsize[j]));
	    		x=analyzePrimerHairpin(pps[i].rightp,j);
	    		pps[i].addHairScore(Math.round(maxscore*x/windowsize[j]));
	    	}
        }
    }
	
    /**
     * Gibt die max. Anzahl bindender Basen am 3'-Ende zurück. Es werden die Parameter windowsize und
     * min_binding beachtet, d.h. wenn weniger als min_binding Basen binden wird 0 zurückgegeben!
     * @param primer Primer als String
     * @return Anzahl bindender Basen für Hairpinfall
     */
    public  int analyzePrimerHairpin(String primer, int idx) {
        if(0 >= windowsize[idx] || 0 >= minbinds[idx])
            return 0;
    	int plength= primer.length();
    	int binds= 0;				//temp. var für momentane anzahl binds	
    	int pos=0;
    	int maxmatchlength=minbinds[idx];	
        maxbind=Integer.MIN_VALUE;
        if(null == liste)
        	liste=new ArrayList();
    	String rcPrimer= Helper.revcomplPrimer(primer);
    	
    	for (int i= 2*minbinds[idx]+2; i < plength-1; i++) {
    		/* Im revcompl-Primer muss die Sequenz von minbinding+2 bis max. länge-1 liegen.
    		 * minbind+2 wegen min. 2 Basen Abstand zwischen bindenden 3'-Ende und 
    		 * Match und länge-1 wegen kein Hairpin der Enden (steht in Holgers Anforderung) 
    		 */
    		if (rcPrimer.charAt(i) == primer.charAt(plength-1) && Helper.isNukleotid(rcPrimer.charAt(i))) { //letzte Base passt schon mal
    			maxmatchlength=(i-1)/2;                             //soviele binds kann es hier max. geben
    			if(maxmatchlength>windowsize[idx]) {//mehr als windowsize binds interessieren uns nicht
    				maxmatchlength=windowsize[idx];
    			}else if(maxmatchlength<minbinds[idx]) {//keine aussicht auf minbinds mehr!
    				continue;
    			}
                binds= getMatchCount(primer, maxmatchlength, rcPrimer, i);
    			if(binds>maxbind) {
    				maxbind=binds;
                    pos=i;
    			}
                if(binds>=minbinds[idx]){
                    liste.add(new Integer(i+1));
					if(debug)
						System.out.println(Helper.outputHairpin(primer,pos,windowsize[idx],0));
                }				
    		}
    	}
   
    	return (maxbind< minbinds[idx]) ? 0 : maxbind;
    }
    /**
     * Gibt die Anzahl der an der Stelle i (von 3' aus) bindenden Basen.
     * @param primer
     * @param plength
     * @param binds
     * @param maxmatchlength
     * @param rcPrimer
     * @param i
     * @return
     */
    private int getMatchCount(String primer, int maxmatchlength, String rcPrimer, int i) {
        int tempindex;
        int binds=1;
        int plength=primer.length();
        tempindex=plength-1;
        for(int j=i-1;0 < j && i-j<maxmatchlength;j--) {  //maximal bis maxmatchlength suchen
        	tempindex--;
        	if(primer.charAt(tempindex)==rcPrimer.charAt(j) && Helper.isNukleotid(rcPrimer.charAt(j))) {
        		binds++;
        	}
        }
        return binds;
    }
    private int analyzeAllPrimerHairpin(String primer){
    	int sum=0;
    	for (int i = 0; i < windowsize.length; i++) {
			sum+=analyzePrimerHairpin(primer,i);
		}
		return sum;
    }
    /**
     * Gibt die Positionen der Hairpins als Array zurueck. Es werden die Parameter windowsize und
     * min_binding beachtet, d.h. wenn weniger als min_binding Basen binden wird 0 zurückgegeben!
     * Die Ergebnisse repraesentieren jeweils die Position im Primer, an denen das 3'-Ende
     * bindet (von 3' aus gerechnet), start bei 1. D.h. wenn das Ende genau auf den Anfang fällt, würde
     * primer.length() zurückgegeben werden
     * @return int[]
     */
    public List getHairpinPositions(String primer){
    	analyzeAllPrimerHairpin(primer);
		List l=liste;
		liste=null;
		return l;
    }
}

/*
 * Created on 05.09.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package biochemie.pcr.modules;

import java.util.StringTokenizer;

import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.PCRConfig;
import biochemie.pcr.io.UI;


/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SNP extends AnalyseModul{
	public int[] scores;
	public int[] snps;
	int endscore;
	
	public SNP(PCRConfig cfg, boolean debug) {
		super(cfg,debug);
		try {
			this.endscore=Integer.parseInt(config.getProperty("SCORE_SNP_END").trim());
		} catch (NumberFormatException e) {
			UI.errorDisplay("SCORE_SNP_END enthaelt keine gueltige Zahl!");
		}
		scores=parseScoreString(config.getProperty("SCORE_SNP_DISTANCES"));
		snps=parseSNPList(config.getProperty("SNP_LIST"));
	}
	/**
	 * Liest String der Form "1 2 3 4 500" ein und parst die Zahlenwerte.
	 * Da alle Benutzereingaben mit Index 1 beginnen wird jeweils eins abgezogen.
	 * @param snplist
	 * @return
	 */
	public int[] parseSNPList(String snplist) {
		if(null == snplist || 0 == snplist.length()) {
			System.err.println("SNP_LIST enthaelt keine Werte!");
            return new int[0];
		}
		StringTokenizer st=new StringTokenizer(snplist);
        int[] snps=new int[st.countTokens()];
        for(int i=0;st.hasMoreTokens();i++)	 {
            snps[i]=Integer.parseInt(st.nextToken());
        }		
		return snps;
	}
	/**
	 * @param scorestring
	 * @return
	 */
	public int[] parseScoreString(String scorestring) {
		if(null == scorestring || 0 == scorestring.length()) {
			UI.errorDisplay("SCORE_SNP_DISTANCES enthaelt keine Werte!");
		}
        StringTokenizer st=new StringTokenizer(scorestring);
        int[] scores=new int[st.countTokens()+3];
        scores[0]=scores[1]=scores[2]=endscore;
        for(int i=3;st.hasMoreTokens();i++)  {
            scores[i]=Integer.parseInt(st.nextToken());
        }       
		return scores;
	}
	
	
	public void calcScores(PrimerPair[] pps) {
        if(null == pps)
            return;
        if(0 == snps.length) {
            return ;     
        }
        for (int i= 0; i < pps.length; i++) {
			pps[i].addSNPScore(calcSNPScoresForward(pps[i].leftpos,pps[i].leftlen));
			pps[i].addSNPScore(calcSNPScoresRev(pps[i].rightpos,pps[i].rightlen));
		}
	}

	private int calcSNPScoresForward(int primerstart,int primerlength) {
		int tempscore=0;
		int x=0;
		int primerend=primerstart+primerlength-1;
		for(int i=0;i<snps.length;i++) {
			x=primerend-snps[i];
			if(0 <= x && x<scores.length) {
				tempscore+=scores[x];
			}
		}
		return tempscore;
	}
    private int calcSNPScoresRev(int primerstart,int primerlength) {
        int tempscore=0;
        int x=0;
/*drehe angaben herum, da die reverse primer von PRIMER 3 revers angegeben werden!*/
        primerstart=primerstart-primerlength+1;
        for(int i=0;i<snps.length;i++) {
            x=snps[i]-primerstart;
            if(0 <= x && x<scores.length) {
                tempscore+=scores[x];
            }
        }
        return tempscore;
    }

}

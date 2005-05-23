/*
 * Created on 10.09.2003
 *
*/
package biochemie.pcr.modules;

import junit.framework.TestCase;
import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.PCRConfig;
import biochemie.pcr.modules.SNP;

/**
 * @author Steffen
 *
 */
public class TestSNP extends TestCase {
	SNP snp;
	PCRConfig cfg;

	protected void setUp() throws Exception {
		cfg=new PCRConfig();
	}
	
	public void testLeftPrimerOnly() {
		cfg.setProperty("SCORE_SNP_END","100");
		cfg.setProperty("SCORE_SNP_DISTANCES","50 40 30 20 10");
		cfg.setProperty("SNP_LIST","15 55 123 300 366 700");
		cfg.setProperty("SCORE_MAXSCORE","100");
		snp=new SNP(cfg,false);
		int[] exp= {0,0,0,0,100,100,50};
		PrimerPair[] pps=new PrimerPair[7];
		pps[0]=new PrimerPair("aaaaaaaaaa","",0,0,0,0,0);
		pps[1]=new PrimerPair("aaaaaaaaaa","",54,0,0,0,0);
		pps[2]=new PrimerPair("aaaaa","",49,0,0,0,0);
		pps[3]=new PrimerPair("aaaaaa","",49,0,0,0,0);
		pps[4]=new PrimerPair("aaaaaaa","",49,0,0,0,0);
		pps[5]=new PrimerPair("aaaaaaaa","",49,0,0,0,0);
		pps[6]=new PrimerPair("aaaaaaaaaa","",49,0,0,0,0);

		snp.calcScores(pps);
		for (int i= 0; i < pps.length; i++) {
            //System.out.println("l_exp: "+exp[i]+" got: "+pps[i].scores[PrimerPair.SNP]);
			assertEquals(exp[i],pps[i].scores[PrimerPair.SNP]);
		}
	}
    public void testRightPrimerOnly() {
        cfg.setProperty("SCORE_SNP_END","100");
        cfg.setProperty("SCORE_SNP_DISTANCES","50 40 30 20 10");
        cfg.setProperty("SNP_LIST","15 55 123 300 366 700");
        cfg.setProperty("SCORE_MAXSCORE","100");
        snp=new SNP(cfg,false);
        int[] exp= {0,0,0,100,100,100,50};
        PrimerPair[] pps=new PrimerPair[7];
        pps[0]=new PrimerPair("","aaaaaaaaaa",0,25,0,0,0);
        pps[1]=new PrimerPair("","aaaaaaaaaa",0,16,0,0,0);
        pps[2]=new PrimerPair("","aaaaa",0,9,0,0,0);
        pps[3]=new PrimerPair("","aaaaaaa",0,20,0,0,0);
        pps[4]=new PrimerPair("","aaaaaaa",0,21,0,0,0);
        pps[5]=new PrimerPair("","aaaaaaa",0,19,0,0,0);
        pps[6]=new PrimerPair("","aaaaaaaaa",0,20,0,0,0);

        snp.calcScores(pps);
        for (int i= 0; i < pps.length; i++) {
            //System.out.println("r_exp: "+exp[i]+" got: "+pps[i].scores[PrimerPair.SNP]);
            assertEquals(exp[i],pps[i].scores[PrimerPair.SNP]);
        }
    }
	
}

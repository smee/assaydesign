/*
 * Created on 02.09.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package biochemie.pcr.modules;

import junit.framework.TestCase;
import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.EMBLParser;
import biochemie.pcr.io.PCRConfig;
import biochemie.pcr.modules.RepetetiveSeq;

/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestRepetetiveSeq extends TestCase {
	RepetetiveSeq repseq;
	PrimerPair[] pps;
	EMBLParser emblp;
	int[] exp= {0,0,100,100};
	
	protected void setUp() throws Exception {
		PCRConfig p=new PCRConfig();
		p.setProperty("PARAM_REPETETIVE_SEQ","40");
		emblp=new EMBLParser("embl.txt");
		//liefert folgende Rep. Seqs:
		/*4998,48 6108,27 4998,23 2473,16 4023,28 3140,84 2130,310 
		 * 1522,158 2412,40 3252,66 3007,21 2914,168 2473,171 6108,33 
		 * 4020,41 668,10 3290,28 863,167 3528,7 3637,17 3290,28 5551,52 
		 * 2411,48 5402,157 4222,7*/ 
		p.setProperty("REP_SEQ_LIST",emblp.getRepetetiveSeqsAsString());
        repseq=new RepetetiveSeq(p);
		pps=new PrimerPair[4];
		pps[0]=new PrimerPair(2100,20,0,0);	
		pps[1]=new PrimerPair(2100,40,0,0);
		pps[2]=new PrimerPair(2100,360,0,0);
		pps[3]=new PrimerPair(2100,60,0,0);
	}
	
	public void testDefaultSeq() {
		repseq.calcScores(pps);
		for (int i= 0; i < pps.length; i++) {
            //System.out.println("exp: "+exp[i]+" got: "+pps[i].scores[PrimerPair.REPSEQ]);
			assertEquals(exp[i],pps[i].scores[PrimerPair.REPSEQ]);
			
		}
	}
}

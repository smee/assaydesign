/*
 * Created on 28.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package biochemie.pcr.modules;

import junit.framework.TestCase;
import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.PCRConfig;
import biochemie.pcr.modules.GCDiff;

/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestGCDiff extends TestCase {
	GCDiff gcd;
	PrimerPair[] pps;
	int[] exp= {0,100,100,50,25};
	
	protected void setUp() throws Exception {
		PCRConfig p=new PCRConfig();
		gcd=new GCDiff(p,false);
		pps=new PrimerPair[5];
		pps[0]=new PrimerPair("","",0,1000,1,0);
		pps[1]=new PrimerPair("","",0,1000,10,0);
		pps[2]=new PrimerPair("","",0,1000,-10,0);
		pps[3]=new PrimerPair("","",0,1000,5,0);
		pps[4]=new PrimerPair("","",0,500,7,0);
	}
	
	public void testGCDiff() {
		gcd.calcScores(pps);
		for(int i=0;i<pps.length;i++)
			assertEquals(exp[i],pps[i].scores[PrimerPair.GCDIFF]);
	}
}

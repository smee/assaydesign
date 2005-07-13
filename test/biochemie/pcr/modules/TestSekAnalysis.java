/*
 * Created on 26.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package biochemie.pcr.modules;

import java.util.List;

import junit.framework.TestCase;
import biochemie.pcr.PCR;
import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.PCRConfig;
import biochemie.pcr.modules.CrossDimerAnalysis;
import biochemie.pcr.modules.HairpinAnalysis;
import biochemie.pcr.modules.HomoDimerAnalysis;

/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestSekAnalysis extends TestCase {
	HairpinAnalysis sek;
    HomoDimerAnalysis hom;
    CrossDimerAnalysis cross;
	PrimerPair[] pps;
	PCRConfig cfg;

	protected void setUp() throws Exception {
		cfg=new PCRConfig();
        cfg.setProperty("PARAM_HAIRPIN_WINDOW_SIZE","6");
        cfg.setProperty("PARAM_HAIRPIN_MIN_BINDING","4");
        cfg.setProperty("PARAM_HOMO_WINDOW_SIZE","6");
        cfg.setProperty("PARAM_HOMO_MIN_BINDING","4");
        cfg.setProperty("PARAM_MAXSCORE","100");
        sek=new HairpinAnalysis(cfg,false);
        hom = new HomoDimerAnalysis(cfg,false);
		pps=new PrimerPair[9];		
		pps[0]=new PrimerPair("","A",0,0,0,0,0,0);
		pps[1]=new PrimerPair("AA","AAA",0,0,0,0,0,0);
		pps[2]=new PrimerPair("AAAA","AAAAAA",0,0,0,0,0,0);
		pps[3]=new PrimerPair("AAAAAAA","AAAAAAAA",0,0,0,0,0,0);
		pps[4]=new PrimerPair("AGGT","AAAAATT",0,0,0,0,0,0);
		pps[5]=new PrimerPair("AAAAAAATTTT","AAATGATCCCATCAT",0,0,0,0,0,0);
		pps[6]=new PrimerPair("AAAACCTTTT","AAAAACCTTTT",0,0,0,0,0,0);
		pps[7]=new PrimerPair("AGTTCCTGATGGAAGGTTCC","AGTTCCTGATGGAACGAACGTTCC",0,0,0,0,0,0);
		pps[8]=new PrimerPair("ATTAGGAACATCAT","AATTAGGAACATCAT",0,0,0,0,0,0);
	}

	public void testDefaultHairpins() {

		int[] erg={0,0,0,0,0,150,67,167,67};
		
		sek.calcScores(pps);
		for (int i= 0; i < pps.length; i++) {
            //System.out.println("exp: "+erg[i]+", but was: "+pps[i].scores[PrimerPair.HAIR]);
			assertEquals(erg[i],pps[i].scores[PrimerPair.HAIR]);
		}
	}
	
	public void testNonDefaultBigHairpins() {
		cfg.setProperty("PARAM_HAIRPIN_WINDOW_SIZE","40");
		cfg.setProperty("PARAM_HAIRPIN_MIN_BINDING","2");
		sek=new HairpinAnalysis(cfg,false);
		int[] erg={0,0,0,0,5,23,18,25,18};
		
		sek.calcScores(pps);
		for (int i= 0; i < pps.length; i++) {
			assertEquals(erg[i],pps[i].scores[PrimerPair.HAIR]);
		}
	}
	public void testNonDefaultHairpins(){
		cfg.setProperty("PARAM_HAIRPIN_WINDOW_SIZE","4");
		cfg.setProperty("PARAM_HAIRPIN_MIN_BINDING","4");
		sek=new HairpinAnalysis(cfg,false);

		int[] erg2={0,0,0,0,0,200,100,200,0};
		
		sek.calcScores(pps);
		for (int i= 0; i < pps.length; i++) {
            //System.out.println("exp: "+erg2[i]+", but was: "+pps[i].scores[PrimerPair.HAIR]);
			assertEquals(erg2[i],pps[i].scores[PrimerPair.HAIR]);
		}
	}
	
	public void testBigGapsHairpins() {
		//testet, ob auch größere Knicke als 2 Basen erkannt werden
		pps=new PrimerPair[2];		
		pps[0]=new PrimerPair("ATGGAAGGGTTCC","ATGGAAGGGGGGTTCC",0,0,0,0,0,0);
		pps[1]=new PrimerPair("ATGGAAGGGGGCTTCC","ATGGAAGGGGCCTTCC",0,0,0,0,0,0);
		int[] erg={134,183};
		
		sek.calcScores(pps);
		for (int i= 0; i < pps.length; i++) {
			assertEquals(erg[i],pps[i].scores[PrimerPair.HAIR]);
		}
	}
    public void testDimer() {
        pps=new PrimerPair[6];
        pps[0]=new PrimerPair("AAATT","AATT",0,0,0,0,0,0);
        pps[1]=new PrimerPair("AAACCGGTT","AAAAAAA",0,0,0,0,0,0);
        pps[2]=new PrimerPair("ACGTACGTACGT","",0,0,0,0,0,0);
        pps[3]=new PrimerPair("ACGTACGTACGT","ACGTACGTACGT",0,0,0,0,0,0);
        pps[4]=new PrimerPair("GGGTGTTTTTGAAAAGA","",0,0,0,0,0,0);
        pps[5]=new PrimerPair("","",0,0,0,0,0,0);
        int[] exp= {67,100,100,200,83,0};
        hom.calcScores(pps);
        for (int i= 0; i < pps.length; i++) {
            assertEquals(exp[i],pps[i].scores[PrimerPair.HOMO]);
        }
    }
    public void testHomodimer(){
        hom=new HomoDimerAnalysis(4,2,false);
        List erg=hom.getHomoDimerPositions("AAATT");
        assertEquals(2,erg.size());
        assertEquals(4,((Integer)erg.get(0)).intValue());
        assertEquals(3,((Integer)erg.get(1)).intValue());
        hom=new HomoDimerAnalysis(6,4,true);
        //TODO mehr Tests!
    }
}

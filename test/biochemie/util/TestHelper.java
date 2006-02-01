/*
 * Created on 03.09.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package biochemie.util;

import junit.framework.TestCase;
import biochemie.util.Helper;

/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestHelper extends TestCase {

	public void testKomplementaerePrimer() {
		assertEquals("AAA",Helper.complPrimer("TTT"));
		assertEquals("ACGT",Helper.complPrimer("TGCA"));
		assertEquals("CATCAGCTTTTCAG",Helper.complPrimer("GTAGTCGAAAAGTC"));
	}
	public void testReversKomplementaerePrimer() {
		assertEquals("AAA",Helper.revcomplPrimer("TTT"));
		assertEquals("CAAGTCCCCTTG",Helper.revcomplPrimer("CAAGGGGACTTG"));
		assertEquals("CAAGTCCCCTTG",Helper.revcomplPrimer("CAAGGGGACTTG"));
	}

    public void testTMCalculation() {
        String[] primer= {"ATCGATACGTAG","GCACTATCACGACT","TCGACTGACGAGCACGTACGTCAGTCGTACGTA"
        ,"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",""};
        double[] exp= {16.53,29.24,77.34,68.32,0};
        for(int i=0;i<primer.length;i++)
            assertEquals(exp[i],Helper.calcTM(primer[i],50,1.5),0.01);
    }
    public void testTokenizerWithNullStrings(){
    	int[] i = Helper.tokenizeToInt(null);
    	assertNotNull(i);
    	assertEquals(0,i.length);
    	float[] f  = Helper.tokenizeToFloat(null);
    	assertNotNull(f);
    	assertEquals(0,f.length);
    	double[] d = Helper.tokenizeToDouble(null);
    	assertNotNull(d);
    	assertEquals(0,d.length);
    }
    public void testTokenizerWithEmptyStrings(){
    	int[] i = Helper.tokenizeToInt("");
    	assertNotNull(i);
    	assertEquals(0,i.length);
    	float[] f  = Helper.tokenizeToFloat("");
    	assertNotNull(f);
    	assertEquals(0,f.length);
    	double[] d = Helper.tokenizeToDouble("");
    	assertNotNull(d);
    	assertEquals(0,d.length);
    }

    public void testTokenizer(){
    	int[] erg = {9,8,10,11,12,13,14,15};

    	int[] okay = Helper.tokenizeToInt("9 8 10 11     12 \t13 14 15");
    	assertEquals(8,okay.length);
    	for (int i = 0; i < okay.length; i++) {
			assertEquals(erg[i],okay[i]);
		}
    }

    public void testTokenizeMalformedStrings(){
    	int[] arr = Helper.tokenizeToInt("9.9.9fdsf 8\t\t\t\t77,3");

    	assertEquals(1,arr.length);
    	assertEquals(8,arr[0]);
    }
    public void testGetPosOfPl() {
        assertEquals(-1, Helper.getPosOfPl("AAAAAAAA"));
        assertEquals(5, Helper.getPosOfPl("AAALAAAA"));
        assertEquals(10, Helper.getPosOfPl("LCGTACGAGG"));
        assertEquals(1, Helper.getPosOfPl("acgtcagactgcatL"));
    }
    public void testCalcSecEnthalpy() {
        assertEquals(-5.23,Helper.cal_dG_secondaryStruct("AAAC"),1e-2);
        assertEquals(-3.29,Helper.cal_dG_secondaryStruct("AAC"),1e-2);
    }
}

/*
 * Created on 03.09.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package biochemie.pcr.modules;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;

import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.PCRConfig;
import biochemie.pcr.modules.BLAT.BlatResultEntry;
import biochemie.util.Helper;

/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestBLAT extends TestCase {
	class FileSource implements BlatSource{
		String filelines;
		public FileSource(String filename){
			filelines = Helper.readAllLines(filename, true);
			if(filelines == null)
				throw new NullPointerException("Problems with file "+filename);
		}
		
		public Collection getBlatResults(String pcrproduct) {
			return InetSource.retrieveBlatergs(filelines);
		}		
	}
	
	BlatSource mock;
	PCRConfig cfg;
	String pathtotestfiles;
	
	MockControl control;
	PrimerPair[] pairs;
    String seq;
    
	public void setUp(){
		cfg = new PCRConfig();		
        cfg.setProperty("MIN_ACCEPTED_MISSPRIMING_PRIMER_DISTANCE","8000");
        cfg.setProperty("SCORE_MAXSCORE","100");
        char[] c = new char[80000];
        Arrays.fill(c,'A');
        seq = new String(c);
        cfg.setProperty("SEQUENCE",seq);
        
		pathtotestfiles = System.getProperty("testfiles.dir");
		control =MockControl.createControl(BlatSource.class);
		//will nich wissen, was da uebergeben wird
		mock = (BlatSource) control.getMock();
        pairs = new PrimerPair[4];
        pairs[0] = new PrimerPair(50000,20,50100,20);
        pairs[1] = new PrimerPair(50000,20,50105,20);
        pairs[2] = new PrimerPair(50020,20,50210,21);
        pairs[3] = new PrimerPair(49980,22,50105,20);
	}
	
	public void testBlatWithNullSource(){
		//soll npe werfen, wenn keine quelle angegeben wurde.
		try {
			BLAT blat =new BLAT(cfg, null,false);
			fail();
		} catch (NullPointerException e) {
		}		
	}
	/**
     * Test, ob Blat merkt, wenn die minimalen Matches nicht gefunden werden. 
     * i.e. Primer werden nicht im Genom gefunden.
     *
	 */
	public void testEmptyPair(){
        control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
		PrimerPair testpair = new PrimerPair(0,8,19,8);
		
		//bla ist nur ein platzhalter, da ich nich weiss, was da uebergeben wird
		mock.getBlatResults("bla");		
        List erg = new LinkedList();
        erg.add(new BlatResultEntry(5,10,6));
        erg.add(new BlatResultEntry(10,13,6));
        
		control.setReturnValue(erg);
		control.replay();
		
		BLAT blat =new BLAT(cfg, mock,false);
		try {
			blat.calcScores(new PrimerPair[]{testpair});
			fail("Should have thrown an exception!");
		} catch (BlatException e) {
			//es muss mindestens ein Match gefunden werden, und zwar das originale pcroduct
			assertTrue(true);
		}
		
	}
    /**
     * Test, ob als Blatanfrage nur der TEil der GEsamtseq. verwendet wird, im dem die PCRPrimer liegen.
     *
     */
	public void testCutPCRProduct() {
        //testet, ob wirklich nur der benoetigte teil der seq. als pcrproduct an blat geschickt wird
        control.setDefaultMatcher(MockControl.EQUALS_MATCHER);
        mock.getBlatResults(seq.substring(49980,50211));
        control.setReturnValue(new LinkedList());
        control.replay();
        
        BLAT blat;
        try {
            blat = new BLAT(cfg, mock,false);
            blat.calcScores(pairs);        
            fail("Should have thrown BlatException!");
        } catch (BlatException e) {}        
    }
    
    /**
     * Test, ob ein Primerpaar genau einmal im Genom gefunden wurde.
     *
     */
	public void testOnlyOriginalMatchSmall(){
        control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
		BLAT.BlatResultEntry eleft = new BlatResultEntry(0,15,1);
		BLAT.BlatResultEntry eright = new BlatResultEntry(25,39,1);
		Collection resultlist = new LinkedList();
		resultlist.add(eleft);
		resultlist.add(eright);
		
		mock.getBlatResults("bla");		
		control.setReturnValue(resultlist);
		control.replay();
		
		PrimerPair testpair = new PrimerPair(0,10,39,10);
		BLAT blat =new BLAT(cfg, mock,false);
		blat.calcScores(new PrimerPair[]{testpair});
		
		assertEquals("Es gab kein match, also darf es auch keine Punkte geben.",0,testpair.getOverallScore());
	}
    
    /**
     * Test, ob vier PrimerPaare, die innerhalb einer abgeschickten Seq. liegen genau einmal
     * im Genom gefunden werden.
     *
     */
    public void testOnlyOriginalMatches(){
        //testet, ob fuer unsre defaultpaare alle single und bothmatches gefunden werden.
        control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        BLAT.BlatResultEntry eleft = new BlatResultEntry(1,321,6);
        Collection resultlist = new LinkedList();
        resultlist.add(eleft);
        
        mock.getBlatResults("bla");     
        control.setReturnValue(resultlist);
        control.replay();
        
        BLAT blat =new BLAT(cfg, mock,false);
        blat.calcScores(pairs);
        for (int i = 0; i < pairs.length; i++) {
            assertEquals("Es gab kein match, also darf es auch keine Punkte geben.",0,pairs[i].getOverallScore());
            
        }
    }
    
    public void testAnwendungsfall() {
        /*
         * pairs[0] = new PrimerPair(50000,20,50100,20); // a1: 21 - 40  a2: 102 - 121
           pairs[1] = new PrimerPair(50000,20,50105,20); // b1: 21 - 40   b2: 107 - 126
           pairs[2] = new PrimerPair(50020,20,50210,21); // c1: 41 - 60   c2:  211 -231
           pairs[3] = new PrimerPair(49980,22,50105,20); // d1: 1-21      d2:  107 - 126 
         */
        cfg.setProperty("SCORE_BLAT_FOUND_SEQ","100");//bothscore
        cfg.setProperty("SCORE_BLAT_FOUND_PRIMER","50");//singlescore
        control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        Collection resultlist = new LinkedList();
        resultlist.add(new BlatResultEntry(1,231,6));//alle mindestmatches, blatergebnisse fangen immer bei 1 an, nie bei 0
        resultlist.add(new BlatResultEntry(1,55,10));
        resultlist.add(new BlatResultEntry(105,120,10));
        resultlist.add(new BlatResultEntry(1,55,7));
        resultlist.add(new BlatResultEntry(98,111,9));
        
        mock.getBlatResults("bla");     
        control.setReturnValue(resultlist);
        control.replay();

        BLAT blat =new BLAT(cfg, mock,false);
        blat.calcScores(pairs);
        int[] scores = new int[] {75,175,0,175};
        
        for (int i = 0; i < pairs.length; i++) {
            assertEquals("Es gab mehrere matches, aber falsche Punkte. Paar: "+pairs[i],scores[i],pairs[i].getOverallScore());            
        }
    }
    /**
     * TEst, ob auch matches genau auf den Grenzen stimmen, also matches genau in der Mitte eines Primers etc.
     *
     */
    public void testBorderMatches() {
        /*
         * pairs[0] = new PrimerPair(50000,20,50100,20); // a1: 21 - 40  a2: 102 - 121
           pairs[1] = new PrimerPair(50000,20,50105,20); // b1: 21 - 40   b2: 107 - 126
           pairs[2] = new PrimerPair(50020,20,50210,21); // c1: 41 - 60   c2:  211 -231
           pairs[3] = new PrimerPair(49980,22,50105,20); // d1: 1-21      d2:  107 - 126 
         */
        cfg.setProperty("SCORE_BLAT_FOUND_SEQ","100");//bothscore
        cfg.setProperty("SCORE_BLAT_FOUND_PRIMER","50");//singlescore
        control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        Collection resultlist = new LinkedList();
        resultlist.add(new BlatResultEntry(1,231,6));//alle mindestmatches, blatergebnisse fangen immer bei 1 an, nie bei 0
        resultlist.add(new BlatResultEntry(12,40,10));//mitte von d1, ende von a1,b1 ==> a1,b1,d1 
        resultlist.add(new BlatResultEntry(103,116,10));//nicht mehr a2, mitte von b2, d2 ==> b2,d2
        resultlist.add(new BlatResultEntry(12,40,7));//wie 2., aber anderes Chromosom ==> a1,b1,d1
        resultlist.add(new BlatResultEntry(102,111,9));//ende a2, mitte von a2 ==> a2
        /*
         * a: 3 single
         * b: 3 single, 1both
         * c: 0 single, 0both
         * d: 3 single, 1both
         */
        
        mock.getBlatResults("bla");     
        control.setReturnValue(resultlist);
        control.replay();
        
        BLAT blat =new BLAT(cfg, mock,false);
        blat.calcScores(pairs);
        int[] scores = new int[] {75,175,0,175};
        
        for (int i = 0; i < pairs.length; i++) {
            assertEquals("Es gab mehrere matches, aber falsche Punkte. Paar: "+pairs[i],scores[i],pairs[i].getOverallScore());            
        }
    }
    public void testPercentMatches() {
        BLAT blat =new BLAT(cfg, mock,false);
        //links, 50%
        assertEquals(50010,blat.getPosForPercentBinding(50000, 20, 0.5f, true));
        assertEquals(50010,blat.getPosForPercentBinding(50000, 21, 0.5f, true));
        assertEquals(50011,blat.getPosForPercentBinding(50000, 22, 0.5f, true));
        //rechts 50%
        assertEquals(50011, blat.getPosForPercentBinding(50020, 20, 0.5f, false));
        assertEquals(50010, blat.getPosForPercentBinding(50020, 21, 0.5f, false));//TODO entspricht nich dem linken, sollte 50011 sein 
        assertEquals(50010, blat.getPosForPercentBinding(50020, 22, 0.5f, false));
        //links 30%
        assertEquals(50014,blat.getPosForPercentBinding(50000, 20, 0.3f, true));  
        assertEquals(50014,blat.getPosForPercentBinding(50000, 21, 0.3f, true));
        assertEquals(50015,blat.getPosForPercentBinding(50000, 22, 0.3f, true));

        
        
    }
}

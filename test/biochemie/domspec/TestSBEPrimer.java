/*
 * Created on 18.10.2004 by Steffen Dienst
 *
 */
package biochemie.domspec;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import biochemie.sbe.SBEOptionsProvider;
import biochemie.sbe.io.SBEConfig;

/**
 * @author Steffen Dienst
 * 18.10.2004
 */
public class TestSBEPrimer extends TestCase {
    SBEOptionsProvider cfg;
    
    public void setUp() {
        cfg= new SBEConfig();
        cfg.setCrossimerWindowsizes("7");
        cfg.setCrossdimerMinbinds("6");
        cfg.setPhotolinkerPositions(new int[] {9,8,10,11,12,13,14,15});
        cfg.setCalcDaltonVerbFrom(new double[] {1000.0});
        cfg.setCalcDaltonVerbTo(new double[] {2000.0});
    }
    public static SBEOptionsProvider getConfig(String filename) throws IOException {
        SBEConfig cfg = new SBEConfig();
        cfg.readConfigFile(filename);
        return cfg;
    }
    public void testCrossDimer() {
        SBEPrimer p1= new SBEPrimer(cfg,"197","GGACCAGAGATTCTTTCTTGCACAT","AG",SBEPrimer._5_,"",0,true);
        p1.setBruchstelle(9);
        SBEPrimer p2= new SBEPrimer(cfg,"165","TGAGGAAATTGTAGTTAAATAATTAGAAAG","AG",SBEPrimer._5_,"",0,true);
        p2.setBruchstelle(9);
        Set cd=SekStrukturFactory.getCrossdimer(p1,p2,cfg);
        
        /*
         * Macht keinen crossdimer, weil nur das ende von 197 an 165 bindet, aber nicht 6 von 7.
         */
        assertEquals(0,cd.size());
    }
    public void testAnotherSNP() {
        SBEPrimer p1= new SBEPrimer(cfg,"197","GGACCAGAGATTCTTTCTTGCACAT","AG",SBEPrimer._5_,"",0,true);
        p1.setBruchstelle(9);
        SBEPrimer p2= new SBEPrimer(cfg,"165","TGAGGAAATTGTAGTTAAATAATTAGAAAG","AG",SBEPrimer._5_,"",0,true);
        p2.setBruchstelle(9);
        Set cd=SekStrukturFactory.getCrossdimer(p2,p1,cfg);
        System.out.println(cd);
        //genau ein crossdimer muss gefunden werden
        assertEquals(1,cd.size());
        
        SBESekStruktur s=(SBESekStruktur) cd.iterator().next();
        //muss ein crossdimer
        assertEquals(SekStruktur.CROSSDIMER,s.getType());
        
        assertEquals("Sollte ein A einbauen!",'A',s.bautEin());
        assertEquals("165 bindet an 197 an der Pos. 13",13, s.getPosFrom3());
        assertTrue("Ein A wird eingebaut, ist im SNP enthalten, also inkompatibel.",s.isIncompatible());//nicht kompatibel, weil 165 AG snp ist. 
        assertFalse("Der Photolinker kann den Crossdimer nicht verhindern.",s.isVerhindert());
        
        //assertEquals("197: TGAGGAAATTGTAGTTAAATAATTAGAAAG,PL= 9, incompatible crossdimer with ID 165: TGAGGAAATTGTAGTTAAATAATTAGAAAG,PL= 9",s.toString());

    }
    public void testCorrectSNPs() {
        SBEPrimer p = new SBEPrimer(cfg,"id","AAAA","AC",SBEPrimer._5_,"",0,false);
        assertEquals("AC",p.getSNP());

        SBEPrimer p2 = new SBEPrimer(cfg,"id","AAAA","AC",SBEPrimer._3_,"",0,false);
        assertEquals("TG",p2.getSNP());
        
    }
    
    public void testNonBadCrossdimers() {
        cfg.setAllCrossdimersAreEvil(false);
        cfg.setCrossdimerMinbinds("5");
        cfg.setCrossimerWindowsizes("6");

        SBEPrimer p1= new SBEPrimer(cfg,"330ctla4","agctagctagctagctagctaaaaaggt","AG",SBEPrimer._5_,"",0,true);
        p1.setBruchstelle(12);
        SBEPrimer p2= new SBEPrimer(cfg,"331ctla4","ttggttggttggttggttggttttt","CT",SBEPrimer._5_,"",0,true);
        p2.setBruchstelle(12);
        
        assertTrue(p1.passtMitCrossdimern(p2));
        //--------------------------------------------------
        cfg.setAllCrossdimersAreEvil(true);
        
        assertFalse(p1.passtMitCrossdimern(p2));
        
    }
    
    public void testCalcDaltonPasst() {
        cfg.setCalcDaltonAllowOverlap(true);
        cfg.setCalcDaltonPeaks(10.0);
        cfg.setCalcDaltonFrom(new double[] {-152.0, -116.0, 0});
        cfg.setCalcDaltonTo(new double[] {-150, -114, 50});
        
        SBEPrimer p1= new SBEPrimer(cfg,"328ctla4","AACCAGAGGCAGCTTCTTTTC","AG",SBEPrimer._5_,"",0,true);
        p1.setBruchstelle(8);
        SBEPrimer p2= new SBEPrimer(cfg,"329ctla4","CTATCATGATCATGGGTTTAGCTG","CT",SBEPrimer._5_,"",0,true);
        p2.setBruchstelle(8);
        
        assertFalse(p1.passtMitCalcDalton(p2));
    }
    
    public void testHairpinBug() {
        SBEPrimer p1= new SBEPrimer(cfg,"0360_CD24","ACTGCAGGGCACCACCAGCC","AG",SBEPrimer._5_,"",0,false);
        p1.setBruchstelle(10);
        cfg.setHairpinWindowsizes("3");
        cfg.setHairpinMinbinds("3");
        Set s=SekStrukturFactory.getSecStruks(p1,cfg);
        //System.out.println(s);
        assertTrue(s.size() == 1);//pl ist genau nach den drei bindenden nukleotiden, kann den hairpin also nicht verhindern.

        SBEPrimer p2= new SBEPrimer(cfg,"0360_CD24","ACTGCAGGGCACCACCAGCC","AG",SBEPrimer._5_,"",0,false);
        p2.setBruchstelle(11);
        s=SekStrukturFactory.getSecStruks(p2,cfg);
        //System.out.println(s);
        assertTrue(s.size() == 0);//pl ersetzt die dritte bindende base, also binden nur 2/3
    }
    public void testHomodimerNichtVerhindertBug() {
        cfg.setHairpinWindowsizes("3");
        cfg.setHairpinMinbinds("3");
        SBEPrimer p = new SBEPrimer(cfg,"","GCTTACTTTCTGTTGCAGAAAGTGTAAAAATTATTA","TG",SBEPrimer._5_,"",0,false);
        p.setBruchstelle(13);
        Set s=SekStrukturFactory.getSecStruks(p,cfg);
        
        for (Iterator it = s.iterator(); it.hasNext();) {
            SBESekStruktur sek = (SBESekStruktur) it.next();
            if(sek.getPosFrom3() == 12)
                assertTrue(sek.isVerhindert());
        }
    }
}

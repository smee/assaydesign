/*
 * Created on 18.10.2004 by Steffen Dienst
 *
 */
package biochemie.domspec;

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
        
        //genau ein crossdimer muss gefunden werden
        assertEquals(1,cd.size());
        
        SBESekStruktur s=(SBESekStruktur) cd.iterator().next();
        //muss ein crossdimer
        assertEquals(SekStruktur.CROSSDIMER,s.getType());
        
        assertEquals("Sollte ein A einbauen!",'A',s.bautEin());
        assertEquals("165 bindet an 197 an der Pos. 13",13, s.getPosFrom3());
        assertTrue("Ein A wird eingebaut, ist im SNP enthalten, also inkompatibel.",s.isIncompatible());//nicht kompatibel, weil 165 AG snp ist. 
        assertFalse("Der Photolinker kann den Crossdimer nicht verhindern.",s.isVerhindert());
        System.out.println(s.toString());
        assertEquals("197: GGACCAGAGATTCTTTCTTGCACAT,PL= 9, incompatible crossdimer with ID 165: TGAGGAAATTGTAGTTAAATAATTAGAAAG,PL= 9",s.toString());

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
    
    public void testCrossdimerTestfall1(){
    	/*
    	 * primerA macht Crossdimer mit primerB, inkompatibel, nicht verhindert
    	 */
    	cfg.setCrossdimerMinbinds("6");
    	cfg.setCrossimerWindowsizes("7");
    	SBEPrimer primerA = new SBEPrimer(cfg,"primera","TTACAATTCTTCTTGTLAGTTCTCA","AC",SBEPrimer._5_,"",0,true);//TODO soll auch L erkennen und PL setzen!
    	primerA.setBruchstelle(9);
    	SBEPrimer primerB = new SBEPrimer(cfg,"primerb","CTGTAAAATTAGGACCALTTGAGAAAC","TG",SBEPrimer._5_,"",0,true);//soll auch L erkennen und PL setzen!
    	primerB.setBruchstelle(10);
    	
    	//durch einen Crossdimer wird an einem Primer ein Nukl. eingebaut. TODO Der Crossdimer ist also dem Primer zuzuordnen, bei dem etwas angebaut wird!
    	Set cross2=SekStrukturFactory.getCrossdimer(primerB,primerA,cfg);
    	assertTrue(cross2.size()==0);
    	
    	Set cross1=SekStrukturFactory.getCrossdimer(primerA,primerB,cfg);
    	assertEquals(1, cross1.size());
    	
    	SBESekStruktur sek = (SBESekStruktur) cross1.iterator().next();
    	
    	assertEquals(SekStruktur.CROSSDIMER, sek.getType());
    	assertEquals(8, sek.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals('A',sek.bautEin());
    	assertFalse(sek.isVerhindert());    	
    	assertTrue(sek.isIncompatible());//ist inkompatibel
    }
    public void testCrossdimerTestfall2(){
    	/*
    	 * primerA  : baut A ein, inkompatibel, nicht verhindert
    	 * primerB  : baut anti-pl ein, ist verhindert, 
    	 */
    	cfg.setCrossdimerMinbinds("6");
    	cfg.setCrossimerWindowsizes("7");
    	SBEPrimer primerA = new SBEPrimer(cfg,"primera","TTACAATTCTTCTTGTLAGTTCTCA","AC",SBEPrimer._5_,"",0,true);//TODO soll auch L erkennen und PL setzen!
    	primerA.setBruchstelle(9);
    	SBEPrimer primerB = new SBEPrimer(cfg,"primerb","CTGTAAAATTAGGACCALTTGAGAACT","TG",SBEPrimer._5_,"",0,true);//TODO soll auch L erkennen und PL setzen!
    	primerB.setBruchstelle(10);
    	
    	Set cross2=SekStrukturFactory.getCrossdimer(primerB,primerA,cfg);
    	assertEquals(1,cross2.size());
    	SBESekStruktur sek2 = (SBESekStruktur) cross2.iterator().next();    	
    	assertEquals(1, cross2.size());
    	assertEquals(SekStruktur.CROSSDIMER, sek2.getType());
    	assertEquals(8, sek2.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals('K',sek2.bautEin());//liegt gegenueber dem PL, baut also "Anti-PL" ein, nennen wir es K :)
    	assertTrue(sek2.isVerhindert());    	
    	assertFalse(sek2.isIncompatible());//ist kompatibel
    	
    	Set cross1=SekStrukturFactory.getCrossdimer(primerA,primerB,cfg);    	
    	assertEquals(1,cross1.size());
    	SBESekStruktur sek1 = (SBESekStruktur) cross1.iterator().next();    	
    	assertEquals(SekStruktur.CROSSDIMER, sek1.getType());
    	assertEquals(8, sek1.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals('A',sek1.bautEin());
    	assertFalse(sek1.isVerhindert());    	
    	assertTrue(sek1.isIncompatible());//ist inkompatibel
    }
    public void testCrossdimer3(){
    	/*
    	 * primerA : beide nicht verhindert, nicht kompatibel, pos: 8 und 34
    	 * primerB :
    	 */
    	cfg.setCrossdimerMinbinds("6");
    	cfg.setCrossimerWindowsizes("7");
    	SBEPrimer primerA = new SBEPrimer(cfg,"primera","TTACAATTCTTCTTGTLAGTTCTCA","AC",SBEPrimer._5_,"",0,true);
    	primerA.setBruchstelle(9);
    	SBEPrimer primerB = new SBEPrimer(cfg,"primerb","CTGTAAAATTAGGACCATTGAGAAACCTGTAAAATTAGGACCALTTGAGAAAC","TG",SBEPrimer._5_,"",0,true);//TODO soll auch L erkennen und PL setzen!
    	primerB.setBruchstelle(10);
    	
    	Set cross1=SekStrukturFactory.getCrossdimer(primerA,primerB,cfg);    	
    	assertEquals(2,cross1.size());
    	Iterator it = cross1.iterator();
    	
    	SBESekStruktur sek1 = (SBESekStruktur) it.next();    	
    	assertEquals(SekStruktur.CROSSDIMER, sek1.getType());
    	assertEquals(8, sek1.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals('A',sek1.bautEin());
    	assertFalse(sek1.isVerhindert());    	
    	assertTrue(sek1.isIncompatible());//ist inkompatibel
    	SBESekStruktur sek2 = (SBESekStruktur) it.next();    	
    	assertEquals(SekStruktur.CROSSDIMER, sek1.getType());
    	assertEquals(34, sek1.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals('A',sek1.bautEin());
    	assertFalse(sek1.isVerhindert());    	
    	assertTrue(sek1.isIncompatible());//ist inkompatibel  	
    	
    }
    public void testCrossdimer4(){
    	/*
    	 * primerA : beide nicht verhindert, kompatibel und inkompatibel, pos: 8 und 34
    	 * primerB :
    	 */
    	cfg.setCrossdimerMinbinds("6");
    	cfg.setCrossimerWindowsizes("7");
    	SBEPrimer primerA = new SBEPrimer(cfg,"primera","TTACAATTCTTCTTGTLAGTTCTCA","AC",SBEPrimer._5_,"",0,true);
    	primerA.setBruchstelle(9);
    	SBEPrimer primerB = new SBEPrimer(cfg,"primerb","CTGTAAAATAAGGACCATTGAGAAACCTGTAAAATTAGGACCALTTGAGAAAC","CG",SBEPrimer._5_,"",0,true);//TODO soll auch L erkennen und PL setzen!
    	primerB.setBruchstelle(10);
    	
    	Set cross1=SekStrukturFactory.getCrossdimer(primerA,primerB,cfg);    	
    	assertEquals(2,cross1.size());
    	Iterator it = cross1.iterator();
    	
    	SBESekStruktur sek1 = (SBESekStruktur) it.next();    	
    	assertEquals(SekStruktur.CROSSDIMER, sek1.getType());
    	assertEquals(8, sek1.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals('A',sek1.bautEin());
    	assertFalse(sek1.isVerhindert());    	
    	assertTrue(sek1.isIncompatible());//ist inkompatibel
    	SBESekStruktur sek2 = (SBESekStruktur) it.next();    	
    	assertEquals(SekStruktur.CROSSDIMER, sek1.getType());
    	assertEquals(34, sek1.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals('T',sek1.bautEin());
    	assertFalse(sek1.isVerhindert());    	
    	assertFalse(sek1.isIncompatible());//ist kompatibel  	
    	
    }
}

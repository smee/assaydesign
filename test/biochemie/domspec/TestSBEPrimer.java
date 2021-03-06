/*
 * Created on 18.10.2004 by Steffen Dienst
 *
 */
package biochemie.domspec;

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.io.SBEConfig;

/**
 * @author Steffen Dienst
 * 18.10.2004
 */
public class TestSBEPrimer extends TestCase {
    SBEOptions cfg;

    public void setUp() {
        cfg= new SBEConfig();
        cfg.setCrossimerWindowsizes("7");
        cfg.setCrossdimerMinbinds("6");
        cfg.setPhotolinkerPositions(new int[] {9,8,10,11,12,13,14,15});
        cfg.setCalcDaltonVerbFrom(new double[] {1000.0});
        cfg.setCalcDaltonVerbTo(new double[] {2000.0});
    }

    public void testCrossDimer() {
        //pl==9
        SBEPrimer p1= new SBEPrimer(cfg,"197","GGACCAGAGATTCTTTLTTGCACAT","AG",SBEPrimer._5_,"",0,true);
        //pl==9
        SBEPrimer p2= new SBEPrimer(cfg,"165","TGAGGAAATTGTAGTTAAATALTTAGAAAG","AG",SBEPrimer._5_,"",0,true);
        Set cd=SekStrukturFactory.getCrossdimer(p1,p2,cfg);

        /*
         * Macht keinen crossdimer, weil nur das ende von 197 an 165 bindet, aber nicht 6 von 7.
         */
        assertEquals(0,cd.size());
    }
    public void testAnotherSNP() {
        //pl jeweils 9
        SBEPrimer p1= new SBEPrimer(cfg,"197","GGACCAGAGATTCTTTLTTGCACAT","AG",SBEPrimer._5_,"",0,true);
        SBEPrimer p2= new SBEPrimer(cfg,"165","TGAGGAAATTGTAGTTAAATALTAAGAAAG","AG",SBEPrimer._5_,"",0,true);
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
        //System.out.println(s.toString());
        assertEquals("165(PL=9) with 197(PL=9): incompatible crossdimer, relevant",s.toString());

    }
    public void testCorrectSNPs() {
        SBEPrimer p = new SBEPrimer(cfg,"id","LAAAA","AC",SBEPrimer._5_,"",0,false);
        assertEquals("AC",p.getSNP());

        SBEPrimer p2 = new SBEPrimer(cfg,"id","LAAAA","AC",SBEPrimer._3_,"",0,false);
        assertEquals("TG",p2.getSNP());

    }

    public void testNonBadCrossdimers() {
        cfg.setAllCrossdimersAreEvil(false);
        cfg.setCrossdimerMinbinds("5");
        cfg.setCrossimerWindowsizes("6");
        //pl jeweils 12
        SBEPrimer p1= new SBEPrimer(cfg,"330ctla4","agctagctagctagctLgctaaaaaggt","AG",SBEPrimer._5_,"",0,true);
        SBEPrimer p2= new SBEPrimer(cfg,"331ctla4","ttggttggttggtLggttggttttt","CT",SBEPrimer._5_,"",0,true);

        assertTrue(p1.passtMitCrossdimern(p2,false));
        //TODO an neue Funktionalitaet von allCDAreEvil anpassen! --------------------------------------------------
        //cfg.setAllCrossdimersAreEvil(true);
        //assertFalse(p1.passtMitCrossdimern(p2));

    }

    public void testCalcDaltonPasst() {
        cfg.setCalcDaltonAllowOverlap(true);
        cfg.setCalcDaltonPeaks(10.0);
        cfg.setCalcDaltonFrom(new double[] {-152.0, -116.0, 0});
        cfg.setCalcDaltonTo(new double[] {-150, -114, 50});
        //pl jeweils 8
        SBEPrimer p1= new SBEPrimer(cfg,"328ctla4","AACCAGAGGCAGCLTCTTTTC","AG",SBEPrimer._5_,"",0,true);
        SBEPrimer p2= new SBEPrimer(cfg,"329ctla4","CTATCATGATCATGGGLTTAGCTG","CT",SBEPrimer._5_,"",0,true);

        assertFalse(p1.passtMitCalcDalton(p2));
    }

    public void testCrossdimerTestfall1(){
    	/*
    	 * primerA macht Crossdimer mit primerB, inkompatibel, nicht verhindert
    	 */
    	cfg.setCrossdimerMinbinds("6");
    	cfg.setCrossimerWindowsizes("7");
        //pl==9
    	SBEPrimer primerA = new SBEPrimer(cfg,"primera","TTACAATTCTTCTTGTLAGTTCTCA","AC",SBEPrimer._5_,"",0,true);
        //pl==10
        SBEPrimer primerB = new SBEPrimer(cfg,"primerb","CTGTAAAATTAGGACCALTTGAGAAAC","TG",SBEPrimer._5_,"",0,true);//soll auch L erkennen und PL setzen!

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
        //pl==9
    	SBEPrimer primerA = new SBEPrimer(cfg,"primera","TTACAATTCTTCTTGTLAGTTCTCA","AC",SBEPrimer._5_,"",0,true);
        //pl==10
        SBEPrimer primerB = new SBEPrimer(cfg,"primerb","CTGTAAAATTAGGACCALTTGAGAACT","TG",SBEPrimer._5_,"",0,true);

    	Set cross2=SekStrukturFactory.getCrossdimer(primerB,primerA,cfg);
    	assertEquals(1,cross2.size());
    	SBESekStruktur sek2 = (SBESekStruktur) cross2.iterator().next();
    	assertEquals(1, cross2.size());
    	assertEquals(SekStruktur.CROSSDIMER, sek2.getType());
    	assertEquals(8, sek2.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals('L',sek2.bautEin());//liegt gegenueber dem PL, baut also "Anti-PL" ein, der einfachheithalber auch L
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
        //pl==9
    	SBEPrimer primerA = new SBEPrimer(cfg,"primera","TTACAATTCTTCTTGTLAGTTCTCA","AC",SBEPrimer._5_,"",0,true);
        //pl == 10
        SBEPrimer primerB = new SBEPrimer(cfg,"primerb","CTGTAAAATTAGGACCATTGAGAAACCTGTAAAATTAGGACCALTTGAGAAAC","TG",SBEPrimer._5_,"",0,true);

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
    	assertEquals(35, sek2.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals('A',sek2.bautEin());
    	assertFalse(sek2.isVerhindert());
    	assertTrue(sek2.isIncompatible());//ist inkompatibel

    }
    public void testCrossdimer4(){
    	/*
    	 * primerA : beide nicht verhindert, kompatibel und inkompatibel, pos: 8 und 34
    	 * primerB :
    	 */
    	cfg.setCrossdimerMinbinds("6");
    	cfg.setCrossimerWindowsizes("7");
        //pl ==  9
        SBEPrimer primerA = new SBEPrimer(cfg,"primera","TTACAATTCTTCTTGTLAGTTCTCA","AC",SBEPrimer._5_,"",0,true);
    	//pl == 10
        SBEPrimer primerB = new SBEPrimer(cfg,"primerb","CTGTAAAATAAGGACCAATGAGAAACCTGTAAAATTAGGACCALTTGAGAAAC","CG",SBEPrimer._5_,"",0,true);

    	Set cross1=SekStrukturFactory.getCrossdimer(primerA,primerB,cfg);
    	assertEquals(2,cross1.size());
    	Iterator it = cross1.iterator();

    	SBESekStruktur sek1 = (SBESekStruktur) it.next();
        //System.out.println("testCrossdimer4 sek1:"+sek1);
        //System.out.println(sek1.getAsciiArt());
    	assertEquals(SekStruktur.CROSSDIMER, sek1.getType());
    	assertEquals(8, sek1.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals('A',sek1.bautEin());
    	assertFalse(sek1.isVerhindert());
    	assertTrue(sek1.isIncompatible());//ist inkompatibel

    	SBESekStruktur sek2 = (SBESekStruktur) it.next();
        //System.out.println("testCrossdimer4 sek2:"+sek1);
        //System.out.println(sek2.getAsciiArt());
    	assertEquals(SekStruktur.CROSSDIMER, sek1.getType());
    	assertEquals(35, sek2.getPosFrom3()); //pos. 35 vom 3'-ende von primerB
    	assertEquals('T',sek2.bautEin());
    	assertFalse(sek2.isVerhindert());
    	assertFalse(sek2.isIncompatible());//ist kompatibel

    }
}

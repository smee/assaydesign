/*
 * Created on 18.10.2004 by Steffen Dienst
 *
 */
package biochemie.domspec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.SecStrucOptions;
import biochemie.sbe.io.SBEConfig;
import biochemie.sbe.io.SecStrucConfig;
import biochemie.util.Helper;

/**
 * @author Steffen Dienst
 * 18.10.2004
 */
public class TestSBEPrimer extends TestCase {
    SBEConfig cfg;

    public void setUp() {
        cfg= new SBEConfig();
        cfg.getSecStrucOptions().setCrossimerWindowsizes("7");
        cfg.getSecStrucOptions().setCrossdimerMinbinds("6");
        cfg.setPhotolinkerPositions(new int[] {9,8,10,11,12,13,14,15});
        cfg.setCalcDaltonVerbFrom(new double[] {1000.0});
        cfg.setCalcDaltonVerbTo(new double[] {2000.0});
    }

    public void testCrossDimer() {
        //pl==9
        CleavablePrimer p1= new CleavablePrimer(cfg,"197","GGACCAGAGATTCTTTLTTGCACAT","AG",CleavablePrimer._5_,"",0,true);
        //pl==9
        CleavablePrimer p2= new CleavablePrimer(cfg,"165","TGAGGAAATTGTAGTTAAATALTTAGAAAG","AG",CleavablePrimer._5_,"",0,true);
        Set cd=SekStrukturFactory.getCrossdimer(p1,p2,cfg.getSecStrucOptions());

        /*
         * Macht keinen crossdimer, weil nur das ende von 197 an 165 bindet, aber nicht 6 von 7.
         */
        assertEquals(0,cd.size());
    }
    public void testAnotherSNP() {
        //pl jeweils 9
        CleavablePrimer p1= new CleavablePrimer(cfg,"197","GGACCAGAGATTCTTTLTTGCACAT","AG",CleavablePrimer._5_,"",0,true);
        CleavablePrimer p2= new CleavablePrimer(cfg,"165","TGAGGAAATTGTAGTTAAATALTAAGAAAG","AG",CleavablePrimer._5_,"",0,true);
        Set cd=SekStrukturFactory.getCrossdimer(p2,p1,cfg.getSecStrucOptions());

        //genau ein crossdimer muss gefunden werden
        assertEquals(1,cd.size());

        CleavableSekStruktur s=(CleavableSekStruktur) cd.iterator().next();
        //muss ein crossdimer
        assertEquals(SekStruktur.CROSSDIMER,s.getType());

        assertEquals("Sollte ein A einbauen!","A",s.bautEin());
        assertEquals("165 bindet an 197 an der Pos. 13",13, s.getPosFrom3());
        assertTrue("Ein A wird eingebaut, ist im SNP enthalten, also inkompatibel.",s.isIncompatible());//nicht kompatibel, weil 165 AG snp ist.
        assertFalse("Der Photolinker kann den Crossdimer nicht verhindern.",s.isVerhindert());
        //System.out.println(s.toString());
        assertEquals("165(PL=9) with 197(PL=9): incompatible crossdimer, relevant",s.toString());

    }
    public void testCorrectSNPs() {
        CleavablePrimer p = new CleavablePrimer(cfg,"id","LAAAA","AC",CleavablePrimer._5_,"",0,false);
        assertEquals("AC",p.getSNP());

        CleavablePrimer p2 = new CleavablePrimer(cfg,"id","LAAAA","AC",CleavablePrimer._3_,"",0,false);
        assertEquals("AC",p2.getSNP());

    }

    public void testNonBadCrossdimers() {
        cfg.getSecStrucOptions().setIgnoreCompCrossdimers(true);
        cfg.getSecStrucOptions().setCrossdimerMinbinds("5");
        cfg.getSecStrucOptions().setCrossimerWindowsizes("6");
        //pl jeweils 12
        CleavablePrimer p1= new CleavablePrimer(cfg,"330ctla4","agctagctagctagctLgctaaaaaggt","AG",CleavablePrimer._5_,"",0,true);
        CleavablePrimer p2= new CleavablePrimer(cfg,"331ctla4","ttggttggttggtLggttggttttt","CT",CleavablePrimer._5_,"",0,true);

        assertTrue(p1.passtMitCrossdimern(p2));
    }

    public void testCalcDaltonPasst() {
        cfg.setCalcDaltonAllowOverlap(true);
        cfg.setCalcDaltonAssayPeaks(new double[]{10.0,2000,10});
        cfg.setCalcDaltonProductPeaks(new double[]{10.0,2000,10});
        cfg.setCalcDaltonFrom(new double[] {-152.0, -116.0, 0});
        cfg.setCalcDaltonTo(new double[] {-150, -114, 50});
        Helper.createAndRememberCalcDaltonFrom(cfg);
        //pl jeweils 8
        CleavablePrimer p1= new CleavablePrimer(cfg,"328ctla4","AACCAGAGGCAGCLTCTTTTC","AG",CleavablePrimer._5_,"",0,true);
        CleavablePrimer p2= new CleavablePrimer(cfg,"329ctla4","CTATCATGATCATGGGLTTAGCTG","CT",CleavablePrimer._5_,"",0,true);

        assertFalse(p1.passtMitCalcDalton(p2));
    }

    public void testCrossdimerTestfall1(){
    	/*
    	 * primerA macht Crossdimer mit primerB, inkompatibel, nicht verhindert
    	 */
    	cfg.getSecStrucOptions().setCrossdimerMinbinds("6");
    	cfg.getSecStrucOptions().setCrossimerWindowsizes("7");
        //pl==9
    	CleavablePrimer primerA = new CleavablePrimer(cfg,"primera","TTACAATTCTTCTTGTLAGTTCTCA","AC",CleavablePrimer._5_,"",0,true);
        //pl==10
        CleavablePrimer primerB = new CleavablePrimer(cfg,"primerb","CTGTAAAATTAGGACCALTTGAGAAAC","TG",CleavablePrimer._5_,"",0,true);//soll auch L erkennen und PL setzen!

    	Set cross2=SekStrukturFactory.getCrossdimer(primerB,primerA,cfg.getSecStrucOptions());
    	assertTrue(cross2.size()==0);

    	Set cross1=SekStrukturFactory.getCrossdimer(primerA,primerB,cfg.getSecStrucOptions());
    	assertEquals(1, cross1.size());

    	CleavableSekStruktur sek = (CleavableSekStruktur) cross1.iterator().next();

    	assertEquals(SekStruktur.CROSSDIMER, sek.getType());
    	assertEquals(8, sek.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals("A",sek.bautEin());
    	assertFalse(sek.isVerhindert());
    	assertTrue(sek.isIncompatible());//ist inkompatibel
    }
    public void testCrossdimerTestfall2(){
    	/*
    	 * primerA  : baut A ein, inkompatibel, nicht verhindert
    	 * primerB  : baut anti-pl ein, ist verhindert,
    	 */
    	cfg.getSecStrucOptions().setCrossdimerMinbinds("6");
    	cfg.getSecStrucOptions().setCrossimerWindowsizes("7");
        //pl==9
    	CleavablePrimer primerA = new CleavablePrimer(cfg,"primera","TTACAATTCTTCTTGTLAGTTCTCA","AC",CleavablePrimer._5_,"",0,true);
        //pl==10
        CleavablePrimer primerB = new CleavablePrimer(cfg,"primerb","CTGTAAAATTAGGACCALTTGAGAACT","TG",CleavablePrimer._5_,"",0,true);

    	Set cross2=SekStrukturFactory.getCrossdimer(primerB,primerA,cfg.getSecStrucOptions());
    	assertEquals(1,cross2.size());
    	CleavableSekStruktur sek2 = (CleavableSekStruktur) cross2.iterator().next();
    	assertEquals(1, cross2.size());
    	assertEquals(SekStruktur.CROSSDIMER, sek2.getType());
    	assertEquals(8, sek2.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals("L",sek2.bautEin());//liegt gegenueber dem PL, baut also "Anti-PL" ein, der einfachheithalber auch L
    	assertTrue(sek2.isVerhindert());
    	assertFalse(sek2.isIncompatible());//ist kompatibel

    	Set cross1=SekStrukturFactory.getCrossdimer(primerA,primerB,cfg.getSecStrucOptions());
    	assertEquals(1,cross1.size());
    	CleavableSekStruktur sek1 = (CleavableSekStruktur) cross1.iterator().next();
    	assertEquals(SekStruktur.CROSSDIMER, sek1.getType());
    	assertEquals(8, sek1.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals("A",sek1.bautEin());
    	assertFalse(sek1.isVerhindert());
    	assertTrue(sek1.isIncompatible());//ist inkompatibel
    }
    public void testCrossdimer3(){
    	/*
    	 * primerA : beide nicht verhindert, nicht kompatibel, pos: 8 und 34
    	 * primerB :
    	 */
    	cfg.getSecStrucOptions().setCrossdimerMinbinds("6");
    	cfg.getSecStrucOptions().setCrossimerWindowsizes("7");
        //pl==9
    	CleavablePrimer primerA = new CleavablePrimer(cfg,"primera","TTACAATTCTTCTTGTLAGTTCTCA","AC",CleavablePrimer._5_,"",0,true);
        //pl == 10
        CleavablePrimer primerB = new CleavablePrimer(cfg,"primerb","CTGTAAAATTAGGACCATTGAGAAACCTGTAAAATTAGGACCALTTGAGAAAC","TG",CleavablePrimer._5_,"",0,true);

    	List cross1=createdSortedSecStrucList(SekStrukturFactory.getCrossdimer(primerA,primerB,cfg.getSecStrucOptions()));

    	assertEquals(2,cross1.size());
    	Iterator it = cross1.iterator();

    	CleavableSekStruktur sek1 = (CleavableSekStruktur) it.next();
        System.out.println(sek1.getAsciiArt());
    	assertEquals(SekStruktur.CROSSDIMER, sek1.getType());
    	assertEquals(8, sek1.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals("A",sek1.bautEin());
    	assertFalse(sek1.isVerhindert());
    	assertTrue(sek1.isIncompatible());//ist inkompatibel

    	CleavableSekStruktur sek2 = (CleavableSekStruktur) it.next();
    	assertEquals(SekStruktur.CROSSDIMER, sek1.getType());
    	assertEquals(35, sek2.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals("A",sek2.bautEin());
    	assertFalse(sek2.isVerhindert());
    	assertTrue(sek2.isIncompatible());//ist inkompatibel

    }
    public static List createdSortedSecStrucList(Collection coll) {
        List cross1=new ArrayList(coll);
        Collections.sort(cross1,new Comparator(){
            public int compare(Object arg0, Object arg1) {
                SekStruktur s1= (SekStruktur)arg0;
                SekStruktur s2= (SekStruktur)arg1;
                return s1.getPosFrom3()-s2.getPosFrom3();
            }
            
        });
        return cross1;
    }

    public void testCrossdimer4(){
    	/*
    	 * primerA : beide nicht verhindert, kompatibel und inkompatibel, pos: 8 und 34
    	 * primerB :
    	 */
    	cfg.getSecStrucOptions().setCrossdimerMinbinds("6");
    	cfg.getSecStrucOptions().setCrossimerWindowsizes("7");
        //pl ==  9
        CleavablePrimer primerA = new CleavablePrimer(cfg,"primera","TTACAATTCTTCTTGTLAGTTCTCA","AC",CleavablePrimer._5_,"",0,true);
    	//pl == 10
        CleavablePrimer primerB = new CleavablePrimer(cfg,"primerb","CTGTAAAATAAGGACCAATGAGAAACCTGTAAAATTAGGACCALTTGAGAAAC","CG",CleavablePrimer._5_,"",0,true);

        List cross1=createdSortedSecStrucList(SekStrukturFactory.getCrossdimer(primerA,primerB,cfg.getSecStrucOptions()));
    	assertEquals(2,cross1.size());
    	Iterator it = cross1.iterator();

    	CleavableSekStruktur sek1 = (CleavableSekStruktur) it.next();
        //System.out.println("testCrossdimer4 sek1:"+sek1);
        //System.out.println(sek1.getAsciiArt());
    	assertEquals(SekStruktur.CROSSDIMER, sek1.getType());
    	assertEquals(8, sek1.getPosFrom3()); //pos. 8 am 3'-ende von primerA
    	assertEquals("A",sek1.bautEin());
    	assertFalse(sek1.isVerhindert());
    	assertTrue(sek1.isIncompatible());//ist inkompatibel

    	CleavableSekStruktur sek2 = (CleavableSekStruktur) it.next();
        //System.out.println("testCrossdimer4 sek2:"+sek1);
        //System.out.println(sek2.getAsciiArt());
    	assertEquals(SekStruktur.CROSSDIMER, sek1.getType());
    	assertEquals(35, sek2.getPosFrom3()); //pos. 35 vom 3'-ende von primerB
    	assertEquals("T",sek2.bautEin());
    	assertFalse(sek2.isVerhindert());
    	assertFalse(sek2.isIncompatible());//ist kompatibel

    }
}

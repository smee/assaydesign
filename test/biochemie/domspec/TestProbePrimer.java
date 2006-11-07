package biochemie.domspec;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import biochemie.CalcDaltonTests;
import biochemie.MyAssert;
import biochemie.sbe.ProbePrimerFactory;
import biochemie.sbe.SecStrucOptions;
import biochemie.sbe.io.SBEConfig;
import biochemie.sbe.io.SecStrucConfig;
import biochemie.util.Helper;
import junit.framework.TestCase;

public class TestProbePrimer extends TestCase {
    SBEConfig cfg;
    
    protected void setUp() throws Exception{
        super.setUp();
        cfg=new SBEConfig();
        CalcDaltonTests.loadConfigFromString("#Mon Nov 06 18:19:12 CET 2006\r\n" + 
                "sbe.temperature.min=48.0\r\n" + 
                "sbe.gc.max=80.0\r\n" + 
                "sbe.temperature.max=62.0\r\n" + 
                "sbe.polyx=5\r\n" + 
                "sbe.mincandlen=18\r\n" + 
                "misc.drawgraph=true\r\n" + 
                "sbe.prodlendiff=0\r\n" + 
                "sbe.temperature.opt=58.0\r\n" + 
                "sbe.gc.min=20.0\r\n" + 
                "sbe.maxplex=6\r\n" + 
                "calcdalton.verbto=2080.0 2172.0 2258.0 2395.0 2435.0\r\n" + 
                "calcdalton.primermasses={A=313.2071, C=289.1823, T=304.1937, G=329.2066}\r\n" + 
                "calcdalton.addonmasses={A=297.2072, C=273.1824, T=288.1937, G=313.2066}\r\n" + 
                "calcdalton.allowoverlap=true\r\n" + 
                "misc.biotin=bio\r\n" + 
                "calcdalton.maxmass=30000.0\r\n" + 
                "calcdalton.halfmass=true\r\n" + 
                "misc.debug=true\r\n" + 
                "calcdalton.plmass=18.02\r\n" + 
                "calcdalton.assaypeaks=10.0 3000.0 10.0\r\n" + 
                "calcdalton.showions=true\r\n" + 
                "calcdalton.extension=false\r\n" + 
                "calcdalton.productpeaks=8.0 2000.0 8.0\r\n" + 
                "calcdalton.from=-152.0 -136.0 -112.0 0.0\r\n" + 
                "calcdalton.plmassidx=3\r\n" + 
                "calcdalton.verbfrom=2070.0 2162.0 2248.0 2385.0 2425.0\r\n" + 
                "calcdalton.to=-150.0 -134.0 -110.0 50.0\r\n" + 
                "misc.maxcalctime=10\r\n" + 
                "calcdalton.pl=9 8 10 11 12 13 14 15 16\r\n" + 
                "sbe.crossdimer.minbinds=4 4\r\n" + 
                "sbe.crossdimer.windowsizes=6 4\r\n" + 
                "sbe.hairpin.windowsizes=6 4\r\n" + 
                "sbe.hairpin.minbinds=4 4\r\n" + 
                "sbe.homodimer.minbinds=4 4\r\n" + 
                "sbe.homodimer.windowsizes=6 4\r\n" + 
                "sbe.crossdimer.areallevil=false",cfg);
        Helper.createAndRememberCalcDaltonFrom(cfg);
    }

    public void testCDParamline(){
        ProbePrimer p=new ProbePrimer("ID1","AGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGGACTTTTAGAGAGAAAA",Primer._5_,"AG",12,ProbePrimerFactory.generateAddons(12,"ACCAGAGAGAGAGAGAGAGAGAG","AG"),0,cfg.getSecStrucOptions(),0);
        MyAssert.assertEquals(p.getCDParamLine(),new String[]{"AGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGGACTTTTAGAGAGAAAA","AAC","GAC"});
    }
    public void testIncompHairpins(){
        ProbePrimer p=new ProbePrimer("ID1","AGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGGACTTTTAGAGAGAAAA",Primer._5_,"AG",12,ProbePrimerFactory.generateAddons(11,"ACCAGAGAGAGAGAGAGAGAGAG","AG"),0,cfg.getSecStrucOptions(),0);
        List seclist=TestSBEPrimer.createdSortedSecStrucList(p.getSecStrucs());
        Iterator it = seclist.iterator();
        ProbeSekStruktur sek = (ProbeSekStruktur) it.next();
        assertTrue(sek.isIncompatible());
        //assertEquals(55,sek.getPosFrom3());
    }
    public void testCompHairpins(){
        SecStrucOptions ssc=new SecStrucConfig();
        ProbePrimer p=new ProbePrimer("ID1","AGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGAGGACTTTTAGAGAGAAAA",Primer._5_,"CT",12,ProbePrimerFactory.generateAddons(11,"CCCAGAGAGAGAGAGAGAGAGAG","CT"),0,ssc,0);
        List seclist=TestSBEPrimer.createdSortedSecStrucList(p.getSecStrucs());
        Iterator it = seclist.iterator();
        ProbeSekStruktur sek = (ProbeSekStruktur) it.next();
        assertFalse(sek.isIncompatible());
        //assertEquals(55,sek.getPosFrom3());
    }

}

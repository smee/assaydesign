package biochemie;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.ArrayUtils;

import junit.framework.TestCase;
import biochemie.calcdalton.CalcDalton;
import biochemie.domspec.CleavablePrimer;
import biochemie.domspec.Primer;
import biochemie.sbe.io.SBEConfig;

public class CalcDaltonTests extends TestCase {
    SBEConfig cfg;
    CleavablePrimer p1,p2;
    
    protected void setUp() throws Exception {
        cfg=new SBEConfig();
    }
    
    private void loadConfigFromString(String s){
        InputStream is=new ByteArrayInputStream(s.getBytes());
        try {
            cfg.readConfig(is);
        } catch (IOException e) {
            fail();
        }
    }
    private void assertFitsWithCalcdalton(String cfgString,boolean fits) {
        loadConfigFromString(cfgString);
        initPrimers();
        CalcDalton cd=new CalcDalton(cfg);
        String[][] params=new String[][]{p1.getCDParamLine(),p2.getCDParamLine()};
        int[] fest=new int[] {ArrayUtils.indexOf(cfg.getPhotolinkerPositions(),p1.getBruchstelle())
                ,ArrayUtils.indexOf(cfg.getPhotolinkerPositions(),p2.getBruchstelle())};
        if(fits)
            assertEquals(cd.calc(params,fest).length,1);
        else
            assertEquals(cd.calc(params,fest).length,0);
    }
    private void initPrimers(){
        p1=new CleavablePrimer(cfg,"ID1","AGAGAGAGAGACCCCCCCC",9,"CG",Primer._5_,"",0,true);
        p2=new CleavablePrimer(cfg,"ID2","AGAGAGAGAGAGGGAGGGACCCACC",15,"CG",Primer._5_,"",0,true);
    }
    public void testDouble01(){
        assertFitsWithCalcdalton("#Fri Oct 27 16:09:30 CEST 2006\r\n" + 
                "sbe.temperature.min=48.0\r\n" + 
                "sbe.gc.max=80.0\r\n" + 
                "sbe.temperature.max=62.0\r\n" + 
                "sbe.polyx=5\r\n" + 
                "sbe.mincandlen=18\r\n" + 
                "misc.drawgraph=false\r\n" + 
                "sbe.prodlendiff=0\r\n" + 
                "sbe.temperature.opt=58.0\r\n" + 
                "sbe.gc.min=20.0\r\n" + 
                "sbe.maxplex=6\r\n" + 
                "calcdalton.verbto=\r\n" + 
                "calcdalton.primermasses={A=313.2071, C=289.1823, T=304.1937, G=329.2066}\r\n" + 
                "calcdalton.addonmasses={A=297.2072, C=273.1824, T=288.1937, G=313.2066}\r\n" + 
                "calcdalton.allowoverlap=false\r\n" + 
                "misc.biotin=bio\r\n" + 
                "calcdalton.maxmass=30000.0\r\n" + 
                "calcdalton.halfmass=true\r\n" + 
                "misc.debug=false\r\n" + 
                "calcdalton.plmass=18.02\r\n" + 
                "calcdalton.assaypeaks=5.0 3000.0 5.0\r\n" + 
                "calcdalton.showions=true\r\n" + 
                "calcdalton.extension=false\r\n" + 
                "calcdalton.productpeaks=5.0 2000.0 5.0\r\n" + 
                "calcdalton.from=\r\n" + 
                "calcdalton.plmassidx=3\r\n" + 
                "calcdalton.verbfrom=\r\n" + 
                "calcdalton.to=\r\n" + 
                "misc.maxcalctime=10\r\n" + 
                "calcdalton.pl=9 8 10 11 12 13 14 15 16\r\n" + 
                "sbe.crossdimer.minbinds=\r\n" + 
                "sbe.crossdimer.windowsizes=\r\n" + 
                "sbe.hairpin.windowsizes=6 4\r\n" + 
                "sbe.hairpin.minbinds=4 4\r\n" + 
                "sbe.homodimer.minbinds=4 4\r\n" + 
                "sbe.homodimer.windowsizes=6 4\r\n" + 
                "sbe.crossdimer.areallevil=false",true);
    }
    public void testDouble02(){
        assertFitsWithCalcdalton("#Fri Oct 27 16:09:59 CEST 2006\r\n" + 
                "sbe.temperature.min=48.0\r\n" + 
                "sbe.gc.max=80.0\r\n" + 
                "sbe.temperature.max=62.0\r\n" + 
                "sbe.polyx=5\r\n" + 
                "sbe.mincandlen=18\r\n" + 
                "misc.drawgraph=false\r\n" + 
                "sbe.prodlendiff=0\r\n" + 
                "sbe.temperature.opt=58.0\r\n" + 
                "sbe.gc.min=20.0\r\n" + 
                "sbe.maxplex=6\r\n" + 
                "calcdalton.verbto=\r\n" + 
                "calcdalton.primermasses={A=313.2071, C=289.1823, T=304.1937, G=329.2066}\r\n" + 
                "calcdalton.addonmasses={A=297.2072, C=273.1824, T=288.1937, G=313.2066}\r\n" + 
                "calcdalton.allowoverlap=false\r\n" + 
                "misc.biotin=bio\r\n" + 
                "calcdalton.maxmass=30000.0\r\n" + 
                "calcdalton.halfmass=true\r\n" + 
                "misc.debug=false\r\n" + 
                "calcdalton.plmass=18.02\r\n" + 
                "calcdalton.assaypeaks=6.0 3000.0 6.0\r\n" + 
                "calcdalton.showions=true\r\n" + 
                "calcdalton.extension=false\r\n" + 
                "calcdalton.productpeaks=5.0 2000.0 5.0\r\n" + 
                "calcdalton.from=\r\n" + 
                "calcdalton.plmassidx=3\r\n" + 
                "calcdalton.verbfrom=\r\n" + 
                "calcdalton.to=\r\n" + 
                "misc.maxcalctime=10\r\n" + 
                "calcdalton.pl=9 8 10 11 12 13 14 15 16\r\n" + 
                "sbe.crossdimer.minbinds=\r\n" + 
                "sbe.crossdimer.windowsizes=\r\n" + 
                "sbe.hairpin.windowsizes=6 4\r\n" + 
                "sbe.hairpin.minbinds=4 4\r\n" + 
                "sbe.homodimer.minbinds=4 4\r\n" + 
                "sbe.homodimer.windowsizes=6 4\r\n" + 
                "sbe.crossdimer.areallevil=false",false);
    }
    public void testDouble03(){
        assertFitsWithCalcdalton("#Fri Oct 27 16:10:29 CEST 2006\r\n" + 
                "sbe.temperature.min=48.0\r\n" + 
                "sbe.gc.max=80.0\r\n" + 
                "sbe.temperature.max=62.0\r\n" + 
                "sbe.polyx=5\r\n" + 
                "sbe.mincandlen=18\r\n" + 
                "misc.drawgraph=false\r\n" + 
                "sbe.prodlendiff=0\r\n" + 
                "sbe.temperature.opt=58.0\r\n" + 
                "sbe.gc.min=20.0\r\n" + 
                "sbe.maxplex=6\r\n" + 
                "calcdalton.verbto=\r\n" + 
                "calcdalton.primermasses={A=313.2071, C=289.1823, T=304.1937, G=329.2066}\r\n" + 
                "calcdalton.addonmasses={A=297.2072, C=273.1824, T=288.1937, G=313.2066}\r\n" + 
                "calcdalton.allowoverlap=true\r\n" + 
                "misc.biotin=bio\r\n" + 
                "calcdalton.maxmass=30000.0\r\n" + 
                "calcdalton.halfmass=true\r\n" + 
                "misc.debug=false\r\n" + 
                "calcdalton.plmass=18.02\r\n" + 
                "calcdalton.assaypeaks=6.0 3000.0 6.0\r\n" + 
                "calcdalton.showions=true\r\n" + 
                "calcdalton.extension=false\r\n" + 
                "calcdalton.productpeaks=5.0 2000.0 5.0\r\n" + 
                "calcdalton.from=\r\n" + 
                "calcdalton.plmassidx=3\r\n" + 
                "calcdalton.verbfrom=\r\n" + 
                "calcdalton.to=\r\n" + 
                "misc.maxcalctime=10\r\n" + 
                "calcdalton.pl=9 8 10 11 12 13 14 15 16\r\n" + 
                "sbe.crossdimer.minbinds=\r\n" + 
                "sbe.crossdimer.windowsizes=\r\n" + 
                "sbe.hairpin.windowsizes=6 4\r\n" + 
                "sbe.hairpin.minbinds=4 4\r\n" + 
                "sbe.homodimer.minbinds=4 4\r\n" + 
                "sbe.homodimer.windowsizes=6 4\r\n" + 
                "sbe.crossdimer.areallevil=false",true);
    }
    public void testDouble04(){
        assertFitsWithCalcdalton("#Fri Oct 27 16:11:12 CEST 2006\r\n" + 
                "sbe.temperature.min=48.0\r\n" + 
                "sbe.gc.max=80.0\r\n" + 
                "sbe.temperature.max=62.0\r\n" + 
                "sbe.polyx=5\r\n" + 
                "sbe.mincandlen=18\r\n" + 
                "misc.drawgraph=false\r\n" + 
                "sbe.prodlendiff=0\r\n" + 
                "sbe.temperature.opt=58.0\r\n" + 
                "sbe.gc.min=20.0\r\n" + 
                "sbe.maxplex=6\r\n" + 
                "calcdalton.verbto=\r\n" + 
                "calcdalton.primermasses={A=313.2071, C=289.1823, T=304.1937, G=329.2066}\r\n" + 
                "calcdalton.addonmasses={A=297.2072, C=273.1824, T=288.1937, G=313.2066}\r\n" + 
                "calcdalton.allowoverlap=true\r\n" + 
                "misc.biotin=bio\r\n" + 
                "calcdalton.maxmass=30000.0\r\n" + 
                "calcdalton.halfmass=true\r\n" + 
                "misc.debug=false\r\n" + 
                "calcdalton.plmass=18.02\r\n" + 
                "calcdalton.assaypeaks=5.0 3000.0 5.0\r\n" + 
                "calcdalton.showions=true\r\n" + 
                "calcdalton.extension=true\r\n" + 
                "calcdalton.productpeaks=5.0 2000.0 5.0\r\n" + 
                "calcdalton.from=\r\n" + 
                "calcdalton.plmassidx=3\r\n" + 
                "calcdalton.verbfrom=\r\n" + 
                "calcdalton.to=\r\n" + 
                "misc.maxcalctime=10\r\n" + 
                "calcdalton.pl=9 8 10 11 12 13 14 15 16\r\n" + 
                "sbe.crossdimer.minbinds=\r\n" + 
                "sbe.crossdimer.windowsizes=\r\n" + 
                "sbe.hairpin.windowsizes=6 4\r\n" + 
                "sbe.hairpin.minbinds=4 4\r\n" + 
                "sbe.homodimer.minbinds=4 4\r\n" + 
                "sbe.homodimer.windowsizes=6 4\r\n" + 
                "sbe.crossdimer.areallevil=false",true);
    }
    public void testDouble05(){
        assertFitsWithCalcdalton("#Fri Oct 27 16:11:55 CEST 2006\r\n" + 
                "sbe.temperature.min=48.0\r\n" + 
                "sbe.gc.max=80.0\r\n" + 
                "sbe.temperature.max=62.0\r\n" + 
                "sbe.polyx=5\r\n" + 
                "sbe.mincandlen=18\r\n" + 
                "misc.drawgraph=false\r\n" + 
                "sbe.prodlendiff=0\r\n" + 
                "sbe.temperature.opt=58.0\r\n" + 
                "sbe.gc.min=20.0\r\n" + 
                "sbe.maxplex=6\r\n" + 
                "calcdalton.verbto=\r\n" + 
                "calcdalton.primermasses={A=313.2071, C=289.1823, T=304.1937, G=329.2066}\r\n" + 
                "calcdalton.addonmasses={A=297.2072, C=273.1824, T=288.1937, G=313.2066}\r\n" + 
                "calcdalton.allowoverlap=true\r\n" + 
                "misc.biotin=bio\r\n" + 
                "calcdalton.maxmass=30000.0\r\n" + 
                "calcdalton.halfmass=true\r\n" + 
                "misc.debug=false\r\n" + 
                "calcdalton.plmass=18.02\r\n" + 
                "calcdalton.assaypeaks=1.0 3000.0 1.0\r\n" + 
                "calcdalton.showions=true\r\n" + 
                "calcdalton.extension=true\r\n" + 
                "calcdalton.productpeaks=5.0 2000.0 5.0\r\n" + 
                "calcdalton.from=\r\n" + 
                "calcdalton.plmassidx=3\r\n" + 
                "calcdalton.verbfrom=\r\n" + 
                "calcdalton.to=\r\n" + 
                "misc.maxcalctime=10\r\n" + 
                "calcdalton.pl=9 8 10 11 12 13 14 15 16\r\n" + 
                "sbe.crossdimer.minbinds=\r\n" + 
                "sbe.crossdimer.windowsizes=\r\n" + 
                "sbe.hairpin.windowsizes=6 4\r\n" + 
                "sbe.hairpin.minbinds=4 4\r\n" + 
                "sbe.homodimer.minbinds=4 4\r\n" + 
                "sbe.homodimer.windowsizes=6 4\r\n" + 
                "sbe.crossdimer.areallevil=false",true);
    }
    public void testDouble06(){
        assertFitsWithCalcdalton("#Fri Oct 27 16:12:15 CEST 2006\r\n" + 
                "sbe.temperature.min=48.0\r\n" + 
                "sbe.gc.max=80.0\r\n" + 
                "sbe.temperature.max=62.0\r\n" + 
                "sbe.polyx=5\r\n" + 
                "sbe.mincandlen=18\r\n" + 
                "misc.drawgraph=false\r\n" + 
                "sbe.prodlendiff=0\r\n" + 
                "sbe.temperature.opt=58.0\r\n" + 
                "sbe.gc.min=20.0\r\n" + 
                "sbe.maxplex=6\r\n" + 
                "calcdalton.verbto=\r\n" + 
                "calcdalton.primermasses={A=313.2071, C=289.1823, T=304.1937, G=329.2066}\r\n" + 
                "calcdalton.addonmasses={A=297.2072, C=273.1824, T=288.1937, G=313.2066}\r\n" + 
                "calcdalton.allowoverlap=true\r\n" + 
                "misc.biotin=bio\r\n" + 
                "calcdalton.maxmass=30000.0\r\n" + 
                "calcdalton.halfmass=true\r\n" + 
                "misc.debug=false\r\n" + 
                "calcdalton.plmass=18.02\r\n" + 
                "calcdalton.assaypeaks=3.0 3000.0 3.0\r\n" + 
                "calcdalton.showions=true\r\n" + 
                "calcdalton.extension=true\r\n" + 
                "calcdalton.productpeaks=5.0 2000.0 5.0\r\n" + 
                "calcdalton.from=\r\n" + 
                "calcdalton.plmassidx=3\r\n" + 
                "calcdalton.verbfrom=\r\n" + 
                "calcdalton.to=\r\n" + 
                "misc.maxcalctime=10\r\n" + 
                "calcdalton.pl=9 8 10 11 12 13 14 15 16\r\n" + 
                "sbe.crossdimer.minbinds=\r\n" + 
                "sbe.crossdimer.windowsizes=\r\n" + 
                "sbe.hairpin.windowsizes=6 4\r\n" + 
                "sbe.hairpin.minbinds=4 4\r\n" + 
                "sbe.homodimer.minbinds=4 4\r\n" + 
                "sbe.homodimer.windowsizes=6 4\r\n" + 
                "sbe.crossdimer.areallevil=false",true);
    }
    public void testDouble07(){
        assertFitsWithCalcdalton("#Fri Oct 27 16:12:41 CEST 2006\r\n" + 
                "sbe.temperature.min=48.0\r\n" + 
                "sbe.gc.max=80.0\r\n" + 
                "sbe.temperature.max=62.0\r\n" + 
                "sbe.polyx=5\r\n" + 
                "sbe.mincandlen=18\r\n" + 
                "misc.drawgraph=false\r\n" + 
                "sbe.prodlendiff=0\r\n" + 
                "sbe.temperature.opt=58.0\r\n" + 
                "sbe.gc.min=20.0\r\n" + 
                "sbe.maxplex=6\r\n" + 
                "calcdalton.verbto=\r\n" + 
                "calcdalton.primermasses={A=313.2071, C=289.1823, T=304.1937, G=329.2066}\r\n" + 
                "calcdalton.addonmasses={A=297.2072, C=273.1824, T=288.1937, G=313.2066}\r\n" + 
                "calcdalton.allowoverlap=true\r\n" + 
                "misc.biotin=bio\r\n" + 
                "calcdalton.maxmass=30000.0\r\n" + 
                "calcdalton.halfmass=true\r\n" + 
                "misc.debug=false\r\n" + 
                "calcdalton.plmass=18.02\r\n" + 
                "calcdalton.assaypeaks=3.0 3000.0 3.0\r\n" + 
                "calcdalton.showions=true\r\n" + 
                "calcdalton.extension=false\r\n" + 
                "calcdalton.productpeaks=5.0 2000.0 5.0\r\n" + 
                "calcdalton.from=\r\n" + 
                "calcdalton.plmassidx=3\r\n" + 
                "calcdalton.verbfrom=\r\n" + 
                "calcdalton.to=\r\n" + 
                "misc.maxcalctime=10\r\n" + 
                "calcdalton.pl=9 8 10 11 12 13 14 15 16\r\n" + 
                "sbe.crossdimer.minbinds=\r\n" + 
                "sbe.crossdimer.windowsizes=\r\n" + 
                "sbe.hairpin.windowsizes=6 4\r\n" + 
                "sbe.hairpin.minbinds=4 4\r\n" + 
                "sbe.homodimer.minbinds=4 4\r\n" + 
                "sbe.homodimer.windowsizes=6 4\r\n" + 
                "sbe.crossdimer.areallevil=false",true);
    }
    public void testDouble08(){
        assertFitsWithCalcdalton("#Fri Oct 27 16:13:05 CEST 2006\r\n" + 
                "sbe.temperature.min=48.0\r\n" + 
                "sbe.gc.max=80.0\r\n" + 
                "sbe.temperature.max=62.0\r\n" + 
                "sbe.polyx=5\r\n" + 
                "sbe.mincandlen=18\r\n" + 
                "misc.drawgraph=false\r\n" + 
                "sbe.prodlendiff=0\r\n" + 
                "sbe.temperature.opt=58.0\r\n" + 
                "sbe.gc.min=20.0\r\n" + 
                "sbe.maxplex=6\r\n" + 
                "calcdalton.verbto=\r\n" + 
                "calcdalton.primermasses={A=313.2071, C=289.1823, T=304.1937, G=329.2066}\r\n" + 
                "calcdalton.addonmasses={A=297.2072, C=273.1824, T=288.1937, G=313.2066}\r\n" + 
                "calcdalton.allowoverlap=true\r\n" + 
                "misc.biotin=bio\r\n" + 
                "calcdalton.maxmass=30000.0\r\n" + 
                "calcdalton.halfmass=true\r\n" + 
                "misc.debug=false\r\n" + 
                "calcdalton.plmass=18.02\r\n" + 
                "calcdalton.assaypeaks=5.0 3000.0 5.0\r\n" + 
                "calcdalton.showions=true\r\n" + 
                "calcdalton.extension=false\r\n" + 
                "calcdalton.productpeaks=5.0 2000.0 5.0\r\n" + 
                "calcdalton.from=\r\n" + 
                "calcdalton.plmassidx=3\r\n" + 
                "calcdalton.verbfrom=\r\n" + 
                "calcdalton.to=\r\n" + 
                "misc.maxcalctime=10\r\n" + 
                "calcdalton.pl=9 8 10 11 12 13 14 15 16\r\n" + 
                "sbe.crossdimer.minbinds=\r\n" + 
                "sbe.crossdimer.windowsizes=\r\n" + 
                "sbe.hairpin.windowsizes=6 4\r\n" + 
                "sbe.hairpin.minbinds=4 4\r\n" + 
                "sbe.homodimer.minbinds=4 4\r\n" + 
                "sbe.homodimer.windowsizes=6 4\r\n" + 
                "sbe.crossdimer.areallevil=false",true);
    }
    public void testDouble09(){
        assertFitsWithCalcdalton("#Fri Oct 27 16:13:39 CEST 2006\r\n" + 
                "sbe.temperature.min=48.0\r\n" + 
                "sbe.gc.max=80.0\r\n" + 
                "sbe.temperature.max=62.0\r\n" + 
                "sbe.polyx=5\r\n" + 
                "sbe.mincandlen=18\r\n" + 
                "misc.drawgraph=false\r\n" + 
                "sbe.prodlendiff=0\r\n" + 
                "sbe.temperature.opt=58.0\r\n" + 
                "sbe.gc.min=20.0\r\n" + 
                "sbe.maxplex=6\r\n" + 
                "calcdalton.verbto=\r\n" + 
                "calcdalton.primermasses={A=313.2071, C=289.1823, T=304.1937, G=329.2066}\r\n" + 
                "calcdalton.addonmasses={A=297.2072, C=273.1824, T=288.1937, G=313.2066}\r\n" + 
                "calcdalton.allowoverlap=false\r\n" + 
                "misc.biotin=bio\r\n" + 
                "calcdalton.maxmass=30000.0\r\n" + 
                "calcdalton.halfmass=true\r\n" + 
                "misc.debug=false\r\n" + 
                "calcdalton.plmass=18.02\r\n" + 
                "calcdalton.assaypeaks=1.0 3000.0 1.0\r\n" + 
                "calcdalton.showions=true\r\n" + 
                "calcdalton.extension=true\r\n" + 
                "calcdalton.productpeaks=5.0 2000.0 5.0\r\n" + 
                "calcdalton.from=\r\n" + 
                "calcdalton.plmassidx=3\r\n" + 
                "calcdalton.verbfrom=\r\n" + 
                "calcdalton.to=\r\n" + 
                "misc.maxcalctime=10\r\n" + 
                "calcdalton.pl=9 8 10 11 12 13 14 15 16\r\n" + 
                "sbe.crossdimer.minbinds=\r\n" + 
                "sbe.crossdimer.windowsizes=\r\n" + 
                "sbe.hairpin.windowsizes=6 4\r\n" + 
                "sbe.hairpin.minbinds=4 4\r\n" + 
                "sbe.homodimer.minbinds=4 4\r\n" + 
                "sbe.homodimer.windowsizes=6 4\r\n" + 
                "sbe.crossdimer.areallevil=false",true);
    }
    public void testDouble10(){
        assertFitsWithCalcdalton("#Fri Oct 27 16:14:03 CEST 2006\r\n" + 
                "sbe.temperature.min=48.0\r\n" + 
                "sbe.gc.max=80.0\r\n" + 
                "sbe.temperature.max=62.0\r\n" + 
                "sbe.polyx=5\r\n" + 
                "sbe.mincandlen=18\r\n" + 
                "misc.drawgraph=false\r\n" + 
                "sbe.prodlendiff=0\r\n" + 
                "sbe.temperature.opt=58.0\r\n" + 
                "sbe.gc.min=20.0\r\n" + 
                "sbe.maxplex=6\r\n" + 
                "calcdalton.verbto=\r\n" + 
                "calcdalton.primermasses={A=313.2071, C=289.1823, T=304.1937, G=329.2066}\r\n" + 
                "calcdalton.addonmasses={A=297.2072, C=273.1824, T=288.1937, G=313.2066}\r\n" + 
                "calcdalton.allowoverlap=false\r\n" + 
                "misc.biotin=bio\r\n" + 
                "calcdalton.maxmass=30000.0\r\n" + 
                "calcdalton.halfmass=true\r\n" + 
                "misc.debug=false\r\n" + 
                "calcdalton.plmass=18.02\r\n" + 
                "calcdalton.assaypeaks=3.0 3000.0 3.0\r\n" + 
                "calcdalton.showions=true\r\n" + 
                "calcdalton.extension=true\r\n" + 
                "calcdalton.productpeaks=5.0 2000.0 5.0\r\n" + 
                "calcdalton.from=\r\n" + 
                "calcdalton.plmassidx=3\r\n" + 
                "calcdalton.verbfrom=\r\n" + 
                "calcdalton.to=\r\n" + 
                "misc.maxcalctime=10\r\n" + 
                "calcdalton.pl=9 8 10 11 12 13 14 15 16\r\n" + 
                "sbe.crossdimer.minbinds=\r\n" + 
                "sbe.crossdimer.windowsizes=\r\n" + 
                "sbe.hairpin.windowsizes=6 4\r\n" + 
                "sbe.hairpin.minbinds=4 4\r\n" + 
                "sbe.homodimer.minbinds=4 4\r\n" + 
                "sbe.homodimer.windowsizes=6 4\r\n" + 
                "sbe.crossdimer.areallevil=false",false);
    }
    public void testDouble11(){
        assertFitsWithCalcdalton("#Fri Oct 27 16:14:31 CEST 2006\r\n" + 
                "sbe.temperature.min=48.0\r\n" + 
                "sbe.gc.max=80.0\r\n" + 
                "sbe.temperature.max=62.0\r\n" + 
                "sbe.polyx=5\r\n" + 
                "sbe.mincandlen=18\r\n" + 
                "misc.drawgraph=false\r\n" + 
                "sbe.prodlendiff=0\r\n" + 
                "sbe.temperature.opt=58.0\r\n" + 
                "sbe.gc.min=20.0\r\n" + 
                "sbe.maxplex=6\r\n" + 
                "calcdalton.verbto=\r\n" + 
                "calcdalton.primermasses={A=313.2071, C=289.1823, T=304.1937, G=329.2066}\r\n" + 
                "calcdalton.addonmasses={A=297.2072, C=273.1824, T=288.1937, G=313.2066}\r\n" + 
                "calcdalton.allowoverlap=false\r\n" + 
                "misc.biotin=bio\r\n" + 
                "calcdalton.maxmass=30000.0\r\n" + 
                "calcdalton.halfmass=false\r\n" + 
                "misc.debug=false\r\n" + 
                "calcdalton.plmass=18.02\r\n" + 
                "calcdalton.assaypeaks=6.0 3000.0 6.0\r\n" + 
                "calcdalton.showions=true\r\n" + 
                "calcdalton.extension=true\r\n" + 
                "calcdalton.productpeaks=5.0 2000.0 5.0\r\n" + 
                "calcdalton.from=\r\n" + 
                "calcdalton.plmassidx=3\r\n" + 
                "calcdalton.verbfrom=\r\n" + 
                "calcdalton.to=\r\n" + 
                "misc.maxcalctime=10\r\n" + 
                "calcdalton.pl=9 8 10 11 12 13 14 15 16\r\n" + 
                "sbe.crossdimer.minbinds=\r\n" + 
                "sbe.crossdimer.windowsizes=\r\n" + 
                "sbe.hairpin.windowsizes=6 4\r\n" + 
                "sbe.hairpin.minbinds=4 4\r\n" + 
                "sbe.homodimer.minbinds=4 4\r\n" + 
                "sbe.homodimer.windowsizes=6 4\r\n" + 
                "sbe.crossdimer.areallevil=false",true);
    }
}

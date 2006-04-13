package biochemie.domspec;

import junit.framework.TestCase;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.io.SBEConfig;

public class TestCaldaltonParameters extends TestCase{
    
    SBEOptions cfg;
    protected void setUp() throws Exception {
        cfg=new SBEConfig();
    }
    
    public void testNurSNPAnhaenge() {
        cfg.setCalcDaltonAllExtensions(true);
        SBEPrimer p=new SBEPrimer(cfg,"primer1","AAAAAAAALAAAAAAAAA","AG", SBEPrimer._5_,"",100,true);
        String[] arr=Primer.getCDParamLine(p);
        
        assertEquals(3,arr.length);
        String[] exp=new String[] {"foobar","A","G"};
        for (int i = 1; i < arr.length; i++) {
            assertEquals(exp[i],arr[i]);
        }
    }
    public void testNurSNPAnhaenge2() {
        cfg.setCalcDaltonAllExtensions(true);
        SBEPrimer p=new SBEPrimer(cfg,"primer1","AAAAAAAALAAAAAAAAA","AG", SBEPrimer._5_,"none",100,true);
        String[] arr=Primer.getCDParamLine(p);
        
        assertEquals(3,arr.length);
        String[] exp=new String[] {"foobar","A","G"};
        for (int i = 1; i < arr.length; i++) {
            assertEquals(exp[i],arr[i]);
        }
    }
    
    public void testSNPUndHairpinAnhaenge() {
        cfg.setCalcDaltonAllExtensions(true);
        SBEPrimer p=new SBEPrimer(cfg,"primer1","AAAAAAAALAAAAAAAAA","AG", SBEPrimer._5_,"T",100,true);
        String[] arr=Primer.getCDParamLine(p);
        
        assertEquals(4,arr.length);
        String[] exp=new String[] {"foobar","A","G","T"};
        for (int i = 1; i < arr.length; i++) {
            assertEquals(exp[i],arr[i]);
        }
    }
    public void testSNPUndHairpinAnhaenge2() {
        cfg.setCalcDaltonAllExtensions(true);
        cfg.getSecStrucOptions().setHairpinWindowsizes("4");
        cfg.getSecStrucOptions().setHairpinMinbinds("4");
        SBEPrimer p=new SBEPrimer(cfg,"primer1","GTTTTAAAALAAAAAAAAA","AG", SBEPrimer._5_,"",100,false);
        String[] arr=Primer.getCDParamLine(p);
        
        assertEquals(4,arr.length);
        String[] exp=new String[] {"foobar","A","C","G"};
        for (int i = 1; i < arr.length; i++) {
            assertEquals(exp[i],arr[i]);
        }
    }
    
}

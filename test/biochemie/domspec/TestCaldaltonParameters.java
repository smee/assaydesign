package biochemie.domspec;

import junit.framework.TestCase;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.io.SBEConfig;
import biochemie.util.Helper;

public class TestCaldaltonParameters extends TestCase{
    
    SBEOptions cfg;
    protected void setUp() throws Exception {
        cfg=new SBEConfig();
    }
    
    public void testNurSNPAnhaenge() {
        cfg.setCalcDaltonAllExtensions(true);
        Helper.createAndRememberCalcDaltonFrom(cfg);
        CleavablePrimer p=new CleavablePrimer(cfg,"primer1","AAAAAAAALAAAAAAAAA","AG", CleavablePrimer._5_,"",100,true);
        String[] arr=p.getCDParamLine();
        
        assertEquals(5,arr.length);
        String[] exp=new String[] {"AAAAAAAALAAAAAAAAA","A",">C","G",">T"};
        for (int i = 1; i < arr.length; i++) {
            assertEquals(exp[i],arr[i]);
        }
    }
    public void testNurSNPAnhaenge2() {
        cfg.setCalcDaltonAllExtensions(false);
        Helper.createAndRememberCalcDaltonFrom(cfg);
        CleavablePrimer p=new CleavablePrimer(cfg,"primer1","AAAAAAAALAAAAAAAAA","AG", CleavablePrimer._5_,"none",100,true);
        String[] arr=p.getCDParamLine();
        
        assertEquals(5,arr.length);
        String[] exp=new String[] {"AAAAAAAALAAAAAAAAA","A",">C","G",">T"};
        for (int i = 1; i < arr.length; i++) {
            assertEquals(exp[i],arr[i]);
        }
    }
    
    public void testSNPUndHairpinAnhaenge() {
        cfg.setCalcDaltonAllExtensions(true);
        Helper.createAndRememberCalcDaltonFrom(cfg);
        CleavablePrimer p=new CleavablePrimer(cfg,"primer1","AAAAAAAALAAAAAAAAA","AG", CleavablePrimer._5_,"T",100,true);
        String[] arr=p.getCDParamLine(false);
        
        assertEquals(5,arr.length);
        String[] exp=new String[] {"foobar","A",">C","G",">T"};
        for (int i = 1; i < arr.length; i++) {
            assertEquals(exp[i],arr[i]);
        }
    }
    public void testSNPUndHairpinAnhaenge2() {
        cfg.setCalcDaltonAllExtensions(true);
        cfg.getSecStrucOptions().setHairpinWindowsizes("4");
        cfg.getSecStrucOptions().setHairpinMinbinds("4");
        Helper.createAndRememberCalcDaltonFrom(cfg);
        CleavablePrimer p=new CleavablePrimer(cfg,"primer1","GTTTTAAAALAAAAAAAAA","AG", CleavablePrimer._5_,"",100,false);
        String[] arr=p.getCDParamLine();
        
        assertEquals(5,arr.length);
        String[] exp=new String[] {"foobar","A","C","G",">T"};
        for (int i = 1; i < arr.length; i++) {
            assertEquals(exp[i],arr[i]);
        }
    }
    
}

package biochemie.domspec;

import biochemie.sbe.io.SBEConfig;
import biochemie.util.Helper;
import junit.framework.TestCase;

public class TestSekStruktur extends TestCase {
    
    public void testLoopLength() {
        assertEquals(6,SekStruktur.getLoopLength(12,3));
        assertEquals(4,SekStruktur.getLoopLength(12,4));
        assertEquals(2,SekStruktur.getLoopLength(12,5));
        try {
            SekStruktur.getLoopLength(12,7);
            fail();
        }catch(IllegalArgumentException e) {
            
        }        
    }
    public void testgetBindingSeq() {
        String[] primers=new String[] {"AAAGGGTTTCCC","AAAGGGTTTLCC","AAAGGLTTTCCC","AAAGLGTTTCCC"};
        String[] res=new String[] {"CCC","CC","CC","C"};
        int[] positions=new int[] {9,9,9,9};
        for (int i = 0; i < primers.length; i++) {
            assertEquals(res[i],SekStruktur.getBindingSeq(primers[i],Helper.revcomplPrimer(primers[i]),positions[i]));            
        }
    }
    
    public void testEnthalpiesHairpin() {
        SBEConfig cfg=new SBEConfig();
        cfg.getSecStrucOptions().setHairpinMinbinds("3");
        Primer p=new CleavablePrimer(cfg,"p1","AAAAAACCCCCLAAAAAGGCGGG","AT",CleavablePrimer._5_,"",0,false);
        SekStruktur s=new SekStruktur(p,SekStruktur.HAIRPIN,17);
        assertEquals(-2.63,s.getEnthalpy(),1e-2);
    }
    public void testEnthalpiesHomodimer() {
        SBEConfig cfg=new SBEConfig();
        cfg.getSecStrucOptions().setHairpinMinbinds("3");
        Primer p=new CleavablePrimer(cfg,"p1","AAAAAACCCCCLAAAAAGGCGGG","AT",CleavablePrimer._5_,"",0,false);
        SekStruktur h = new SekStruktur(p,SekStruktur.HOMODIMER,17);
        assertEquals(-7.14,h.getEnthalpy(),1e-2);
    }
    public void testEnthalpiesCrossdimer() {
        SBEConfig cfg=new SBEConfig();
        cfg.getSecStrucOptions().setCrossdimerMinbinds("4");
        cfg.getSecStrucOptions().setCrossimerWindowsizes("6");
        Primer p1=new CleavablePrimer(cfg,"p1","AAAAAACCCCCLAAAAAGGCGGG","AT",CleavablePrimer._5_,"",0,false);
        Primer p2=new CleavablePrimer(cfg,"p1","CAGGTACCGTLTACGTGTTCCCACTGAATCCCGAT","AT",CleavablePrimer._5_,"",0,false);
        SekStruktur h = new SekStruktur(p1,p2,6);

        assertEquals(-10.75,h.getEnthalpy(),1e-2);
    }
    public void testEnthalpiesCrossdimer2() {
        SBEConfig cfg=new SBEConfig();
        cfg.getSecStrucOptions().setCrossdimerMinbinds("4");
        cfg.getSecStrucOptions().setCrossimerWindowsizes("6");
        Primer p1=new CleavablePrimer(cfg,"p1","ATCAGAGCTTAALACTGGGAAGCTGGTGGTAGGAACTGTAAAATTAGGACCACTTGAGAAAC","AT",CleavablePrimer._5_,"",0,false);
        Primer p2=new CleavablePrimer(cfg,"p1","TCATTTTACCACLAGAGGGTAAAAATTCAACACAGATTGCTATTGTTCTGGGACAGTGTTT","AT",CleavablePrimer._5_,"",0,false);
        
        SekStruktur h = new SekStruktur(p1,p2,4);        
        assertEquals(-6.23,h.getEnthalpy(),1e-2);
        
        h = new SekStruktur(p1,p2,17);        
        assertEquals(-4.29,h.getEnthalpy(),1e-2);
    }
}

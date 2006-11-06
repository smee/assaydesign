package biochemie.domspec;

import biochemie.MyAssert;
import biochemie.sbe.io.SBEConfig;
import junit.framework.TestCase;

public class TestProbePrimer extends TestCase {

    /*
     * Test method for 'biochemie.domspec.ProbePrimer.getCDParamLine()'
     */
    public void testGetCDParamLine() {
        ProbePrimer p=new ProbePrimer("test1","acccgggtttaa",Primer._5_,"ag",13,"attccg",0,new SBEConfig().getSecStrucOptions(),0);
        MyAssert.assertEquals(p.getCDParamLine(),new String[]{"acccgggtttaa","a",">c","g",">t"});
    }
    public void testCombinationCleavable(){
        CleavablePrimer cleav=new CleavablePrimer(new SBEConfig(),"cleav","acccgggtttaa",9,"ag",Primer._5_,"",0,true);
        ProbePrimer probe=new ProbePrimer(cleav,13,"acccgggtttaa");
        MyAssert.assertEquals(probe.getCDParamLine(),new String[]{"accLgggtttaaacccgggttta","a"});
        assertEquals(probe.getCompletePrimerSeq(),"accLgggtttaaacccgggtttaa");
    }

}

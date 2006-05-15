package biochemie.domspec;

import biochemie.MyAssert;
import biochemie.sbe.io.SBEConfig;
import junit.framework.TestCase;

public class TestPinpointPrimer extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }
    public void testCalcDalton(){
        PinpointPrimer p=new PinpointPrimer("test1","AACCGGTTAACCGGTT",Primer._5_,"AG","TTTTT",new SBEConfig());
        String[] params=p.getCDParamLine();
        MyAssert.assertEquals(params,new String[]{"TTTTTAACCGGTTAACCGGTT","A",">C","G",">T"});
    }
}

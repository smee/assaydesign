package biochemie.sbe;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import biochemie.domspec.Primer;
import biochemie.sbe.io.SBEConfig;

public class ProbePrimerFactoryTest extends TestCase {
    ProbePrimerFactory fac;
    protected void setUp() throws Exception {
        fac=new ProbePrimerFactory(new SBEConfig(),"testseq","acccgggtttaa","ag","ttccggaaaaagt","","",0,"",-1,-1,false,"",false);
    }

    /*
     * Test method for 'biochemie.sbe.ProbePrimerFactory.createPossiblePrimers(String, String)'
     */
    public void testCreatePossiblePrimers() {

    }

    /*
     * Test method for 'biochemie.sbe.ProbePrimerFactory.generateAddons(String, int)'
     */
    public void testGenerateAddonsSimple() {
        List is=fac.generateAddons(Primer._5_,0);
        String[] shallBe=new String[]{"a","g"};
        compareListToArray(shallBe,is);
        is=fac.generateAddons(Primer._3_,0);
        shallBe=new String[]{"t","c"};
        compareListToArray(shallBe,is);
    }
    public void testGenerateAddons() {
        List is=fac.generateAddons(Primer._5_,13);
        String[] shallBe=new String[]{"attccg","g"};
        compareListToArray(shallBe,is);
        is=fac.generateAddons(Primer._3_,13);
        shallBe=new String[]{"tttaaacccg","cttaaacccg"};
        compareListToArray(shallBe,is);
    }

    private void compareListToArray(String[] shallBe,List is) {
        assertEquals(is.size(),shallBe.length);
        int i=0;
        for (Iterator it = is.iterator(); it.hasNext();i++) {
            String s = (String) it.next();
            assertEquals("entry "+i,shallBe[i],s);
        }
    }

}

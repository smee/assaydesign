/*
 * Created on 20.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.sbe.filter;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import biochemie.domspec.SBEPrimer;
import biochemie.sbe.SBEOptionsProvider;
import biochemie.sbe.io.SBEConfig;

/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestUnwantedPrimers extends TestCase {
    KandidatenFilter filt;
    SBEOptionsProvider cfg;
    List sbec;
    
    protected void setUp() throws Exception {
        super.setUp();
        cfg = new SBEConfig();
        cfg.setPhotolinkerPositions(new int[] {9,8,10,11,12,13,14,15});
        sbec = new LinkedList();
        SBEPrimer p = new SBEPrimer(cfg,"20","AAAAAAAAAAAAAAAAAAAA","AC",SBEPrimer._5_,"",0,true);
        p.setBruchstelle(9);
        sbec.add(p);
        p = new SBEPrimer(cfg,"22","TTTTTTTTTTTTTTTTTTTTTTTT","AC",SBEPrimer._3_,"",0,true);
        p.setBruchstelle(10);
        sbec.add(p);
    }

    public void testEmptyUnwanteds() {
        filt=new UnwantedPrimerFilter(cfg,null);
        filt.filter(sbec);
        
        assertEquals(0,filt.rejectedCount());
        assertEquals(2,sbec.size());
    }
    public void testFilterFirst() {
        filt=new UnwantedPrimerFilter(cfg,"5_20_9");//filtert den ersten, weil alles passt
        filt.filter(sbec);
        
        assertEquals(1,filt.rejectedCount());
        assertEquals(1,sbec.size());
    }
    public void testFilterNoneType() {
        filt=new UnwantedPrimerFilter(cfg,"3_20_9");//filtert den ersten nicht, weil falscher Typ
        filt.filter(sbec);
        
        assertEquals(0,filt.rejectedCount());
        assertEquals(2,sbec.size());
    }
    public void testFilterNoneLen() {
        filt=new UnwantedPrimerFilter(cfg,"5_200_9");//filtert den ersten nicht, weil falsche Laenge
        filt.filter(sbec);
        
        assertEquals(0,filt.rejectedCount());
        assertEquals(2,sbec.size());
    }
    public void testFilterNonePL() {
        filt=new UnwantedPrimerFilter(cfg,"5_200_5");//filtert den ersten nicht, weil falscher PL
        filt.filter(sbec);
        
        assertEquals(0,filt.rejectedCount());
        assertEquals(2,sbec.size());
    }

}

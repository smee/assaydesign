/*
 * Created on 05.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.sbe.multiplex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org._3pq.jgrapht.UndirectedGraph;

import junit.framework.TestCase;
import biochemie.domspec.SBEPrimer;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.io.SBEConfig;
import biochemie.sbe.multiplex.Multiplexer.SBEPrimerProxy;

/**
 * @author IBM Anwender
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MultiplexerTest extends TestCase {

	List primers;
	SBEOptions cfg;
	Multiplexer m;
	SBEPrimer primerA;
	SBEPrimer primerB;
	SBEPrimer primerC;
	
	protected void setUp() throws Exception {
		cfg=new SBEConfig();
		cfg.getSecStrucOptions().setCrossimerWindowsizes("5");
		cfg.getSecStrucOptions().setCrossdimerMinbinds("5");
		//alle passen eigentlich miteinander, aber wenn a und b zusammen sind, bauen sie ein G ein, sind dann also nicht mehr mit c kompatibel
		primerA=new SBEPrimer(cfg,"primerA","TGTLGTGTGTGTGT","AT",SBEPrimer._5_,"",1,true);
		primerB=new SBEPrimer(cfg,"primerB","ACALCACACACACA","AT",SBEPrimer._5_,"",1,true);
		primerC=new SBEPrimer(cfg,"primerC","TTTLTTTTTTTTTTTT","CG",SBEPrimer._5_,"",1,true);
		primers=new ArrayList();
		primers.add(primerA);
		primers.add(primerB);
		primers.add(primerC);
		m=new Multiplexer(cfg){
			public void findMultiplexes(UndirectedGraph sbec) {
			}
		};
	}
	public void testEnhancePrimerList(){
        cfg.getSecStrucOptions().setAllCrossdimersAreEvil(false);//sollte keine Auswirkungen haben
		assertTrue(primerA.passtMitKompCD(primerB));
		assertTrue(primerA.passtMitKompCD(primerC));
		assertTrue(primerB.passtMitKompCD(primerC));
		assertFalse(primerA.passtMit(primerB));
		assertTrue(primerA.passtMit(primerC));
		assertTrue(primerB.passtMit(primerC));

		List result = m.getEnhancedPrimerList(primers, cfg);
		assertEquals(4, result.size());
		boolean foundProxy=false;
		Multiplexer.SBEPrimerProxy proxy=null;
		
		for (Iterator it = result.iterator(); it.hasNext();) {
			Object element = (Object) it.next();
			if(element instanceof Multiplexer.SBEPrimerProxy){
				foundProxy=true;
				proxy=(SBEPrimerProxy) element;
				break;
			}
		}
		assertTrue(foundProxy);
		assertNotNull(proxy);
		
		assertFalse(primerA.passtMit(proxy));
		assertFalse(primerB.passtMit(proxy));
		assertFalse(primerC.passtMit(proxy));
	}
}

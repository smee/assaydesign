/*
 * Created on 13.09.2003
 *
 * Oberklasse fuer alle Analysemodule. Jedes Modul holt sich seine 
 * Infos selber aus dem Configobjekt.
 */
package biochemie.pcr.modules;

import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.PCRConfig;
import biochemie.sbe.WrongValueException;

/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AnalyseModul {
	PCRConfig config;
	protected final int maxscore;
	
	public AnalyseModul(PCRConfig cfg) {
		this.config=cfg;
		int score=100;
		try {
			if(cfg != null)
				score=cfg.getInteger("SCORE_MAXSCORE");
		} catch (WrongValueException e) {
			e.printStackTrace();
		}finally{
			maxscore=score;
		}
	}
	
	public abstract void calcScores(PrimerPair[] pps);
}

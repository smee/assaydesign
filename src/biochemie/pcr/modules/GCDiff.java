/*
 * Created on 28.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package biochemie.pcr.modules;

import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.PCRConfig;
import biochemie.pcr.io.UI;

/**
 * Analysemodul fuer GC-Differenz. Es wird berechnet: abs(gc linkerprimer-gc rechter Primer)*produktlaenge.
 * Dieses Produkt muss zwischen upper_bound und lower_bound liegen. Entsprechend wird Punktzahl zwischen null
 * und maxscore zurueckgegeben.
 * @author Steffen
 *
 */
public class GCDiff extends AnalyseModul{
	int lower=0;
	int upper=0;

	/**
	 * Wenn ein Parameter keine Zahl ist wird das Programm beendet.
	 * @param cfg
	 */	
	public GCDiff(PCRConfig cfg) {
		super(cfg);
		String temp=cfg.getProperty("PARAM_GCDIFF_LOWER_BOUND").trim();
		int l;
		int u;
		try {
			l= Integer.parseInt(temp);
			temp=cfg.getProperty("PARAM_GCDIFF_UPPER_BOUND").trim();
			u= Integer.parseInt(temp);
			if(0 > l) {
				l=0;
			}else if(0 > u) {
				u=0;
			}
			if(l<u) {
				this.lower=l;
				this.upper=u;
			}else {
				this.lower=u;
				this.upper=l;
			}
		} catch (NumberFormatException e) {
			UI.errorDisplay("Fehler beim Einlesen von LOWER_BOUND bzw. UPPER_BOUND -> " +
				"Keine korrekte Zahl.");
		}
	}
	/**
	 * Berechnet die Strafpunkte f&uuml;r die GC-Differenz. Formel:  gcdifferenz*produktl&auml;nge muss 
	 * zwischen lowerbound und upperbound liegen. Wenn drunter -> 0, wenn dr&uuml;ber -> maxscore, ansonsten linear
	 * zum Produkt, also zwischen 0%*maxscore bis 100%*maxscore.
	 * @param gcdiff GC-Differenz der Primer
	 * @param productsize L&auml;nge des PCR-Produkts
	 * @return
	 */
	private int calcGCDiffPoints(float gcdiff,int productsize) {
		gcdiff=Math.abs(gcdiff);
		float product=gcdiff*productsize;
		if(product<lower) {
			return 0;
		}else if(product>upper) {
			return 100;
		}
		return Math.round((product-lower)*maxscore/(upper-lower));
	}

	public void calcScores(PrimerPair[] pps) {
		if(null == pps)
            return;
        for(int i=0;i<pps.length;i++) {
            pps[i].addGCScore(this.calcGCDiffPoints(pps[i].gcdiff,pps[i].productlen));
		}
	}
}

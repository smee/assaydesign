/*
 * Created on 14.10.2003
 *
*/
package biochemie.pcr.modules;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.FeatureHolder;
import org.biojava.bio.symbol.Location;
import org.biojava.bio.symbol.LocationTools;

import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.PCRConfig;


/**
 * @author Steffen
 *
 */
public class ExonIntron extends AnalyseModul {
	Location[] locs=null;
    
	public ExonIntron(PCRConfig cfg) {
		this(cfg,null);
	}
	public ExonIntron(PCRConfig cfg, FeatureHolder feat) {
		super(cfg);
		if(null == feat) {
			parseExonStringToLocations(config.getProperty("EXONS"));
		}else {
			getLocationsOfExons(feat);
		}
	}

	/**
     * 
     */
    private void parseExonStringToLocations(String exons) {
        if (null == exons || 0 == exons.length()) {
            System.err.println("EXONS enthaelt keine Werte!");
            locs=new Location[0];
            return;
        }
        StringTokenizer st= new StringTokenizer(exons);
        String temp;
        locs=new Location[st.countTokens()];
        for (int i= 0; st.hasMoreTokens(); i++) {
            temp= st.nextToken();
            locs[i]= LocationTools.makeLocation(Integer.parseInt(temp.substring(0, temp.indexOf(','))),
                                             Integer.parseInt(temp.substring(temp.indexOf(',') + 1)));
        }
    }
    private void getLocationsOfExons(FeatureHolder feat) {
		Vector vec=new Vector() ;
		for (Iterator i = feat.features(); i.hasNext(); ) {
			  Feature f = (Feature)i.next();
			  vec.add(f.getLocation());
		}
		locs=new Location[vec.size()];
		locs=(Location[]) vec.toArray(locs);
	}
	/**
	 * Wenn PCRProdukt nicht über eine Exon/Intron-Grenze geht, wird das entsprechende PrimerPaar
	 * entfernt.
	 */
	public void calcScores(PrimerPair[] pps) {
        if(null == pps)
            return;
        if(0 == locs.length) {
            return;     
        }
        for (int i= 0; i < pps.length; i++) {
            int tempscore=maxscore;
            Location pploc=LocationTools.makeLocation(pps[i].leftpos+pps[i].leftlen,pps[i].rightpos-pps[i].rightlen);
            for(int j=0;j<locs.length;j++) {
				if(pploc.contains(locs[j].getMin()) || pploc.contains(locs[j].getMax())) {
					tempscore=0;
				}
			}
            pps[i].addExonScore(tempscore);
		}
	}
}

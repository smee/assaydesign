/*
 * Created on 02.09.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package biochemie.pcr.modules;
import java.util.StringTokenizer;

import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.PCRConfig;

/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RepetetiveSeq extends AnalyseModul {
	private int[] repends;
	private int[] repstarts;
	/**
	 * Wenn Primer zu mehr als x% in einer repetetiven Sequenz liegt ->rauswerf
	 */
	int x;
	//Location[] locs;
	//   /**
	//    * @deprecated
	//    * @param cfg
	//    */
	//   public RepetetiveSeq(PCRConfig cfg) {
	//   		this(cfg,null);   
	//   }
	//   public RepetetiveSeq(PCRConfig cfg, FeatureHolder feat) {
	//		super(cfg);
	//		if(feat==null) {
	//			locs=new Location[0];
	//		}
	//		try {
	//			this.x=Integer.parseInt(config.getProperty("PARAM_REPETETIVE_SEQ"));
	//		} catch (NumberFormatException e) {
	//			UI.errorDisplay("PARAM_REPETETIVE_SEQ enthaelt keine gueltige Zahl!");
	//		}
	//		this.feat=feat;
	//		getRepetetiveSeqLocations();
	//   }   
	public RepetetiveSeq(PCRConfig cfg, boolean debug) {
		super(cfg,debug);
		try {
			this.x= Integer.parseInt(config.getProperty("PARAM_REPETETIVE_SEQ").trim());
		} catch (NumberFormatException e) {
			System.err.println("PARAM_REPETETIVE_SEQ enthaelt keine gueltige Zahl!");
        }
		parseRepSeqString(config.getProperty("REP_SEQ_LIST"));
	}
	/**
	 * Parst Liste von repetetiven Sequenzen der Form: "start,length", jeweils
	 * mit Leerzeichen getrennt. Wenn leerer String -> abbruch.
	 * @param repseqstring
	 */
	private void parseRepSeqString(String repseqstring) {
		if (null == repseqstring || 0 == repseqstring.length()) {
			System.err.println("REP_SEQ_LIST enthaelt keine Werte!");
            repstarts=new int[0];
            repends=new int[0];
            return;
		}
		StringTokenizer st= new StringTokenizer(repseqstring);
		repstarts= new int[st.countTokens()];
		repends= new int[st.countTokens()];
		String temp;
		for (int i= 0; st.hasMoreTokens(); i++) {
			temp= st.nextToken();
			repstarts[i]= Integer.parseInt(temp.substring(0, temp.indexOf(',')));
			repends[i]= repstarts[i]+Integer.parseInt(temp.substring(temp.indexOf(',') + 1));
		}
	}
	private int calcRepSeqPoints(int primerstart, int primerlength, int[] repetitiveseqsstart, int[] repetitiveseqsend) {
		int len= repetitiveseqsstart.length;
		for (int i= 0; i < len; i++) {
			if ((primerstart > repetitiveseqsend[i])
				|| (primerstart + primerlength) < repetitiveseqsstart[i]) { //primer liegt ausserhalb
				continue;
			} else if ((primerstart >= repetitiveseqsstart[i])
					&& (primerstart + primerlength) <= repetitiveseqsend[i]) { //primer liegt ganz drin
                     return maxscore;
			} else if ((primerstart >= repetitiveseqsstart[i])
					&& (primerstart + primerlength) > repetitiveseqsend[i]) { //primer liegt hinten halb drin
				if ((repetitiveseqsend[i] - primerstart) * 100 / primerlength >= x) {
					return maxscore;
				} else
					continue;
			} else if (
				primerstart < repetitiveseqsstart[i]
					&& (primerstart + primerlength) <= repetitiveseqsend[i]) { //primer liegt vorn halb drin
				if ((primerstart + primerlength - repetitiveseqsstart[i]) * 100 / primerlength >= x) {
					return maxscore;
				} else
					continue;
			} else { //primer länger als rep. Seq.
				if ((repetitiveseqsend[i] - repetitiveseqsstart[i]) * 100 / primerlength >= x) {
					return maxscore;
				} else
					continue;
			}
		}
		return 0;
	}
	//	/**
	// * 
	// */
	//	private void getRepetetiveSeqLocations() {
	//		Vector vec=new Vector() ;
	//		for (Iterator i = feat.features(); i.hasNext(); ) {
	//			  Feature f = (Feature)i.next();
	//			  vec.add(f.getLocation());
	//		}
	//		locs=new Location[vec.size()];
	//		locs=(Location[]) vec.toArray(locs);	
	//		}
	public void calcScores(PrimerPair[] pps) {
        if(null == pps)
            return;
        if (0 == repstarts.length) {
            return;
        }
        for (int i= 0; i < pps.length; i++) {
				pps[i].addRepSeqScore(this.calcRepSeqPoints(pps[i].leftpos, pps[i].leftlen, repstarts, repends));
				pps[i].addRepSeqScore(this.calcRepSeqPoints(pps[i].rightpos-pps[i].rightlen+1,pps[i].rightlen, repstarts, repends));
		}
	}
	//	public PrimerPair[] calcScores(PrimerPair[] pps) {
	//		if(locs.length!=0) {
	//			for(int i=0;i<pps.length;i++) {
	//				pps[i].repseqscore+=this.calcRepSeqPoints(pps[i]);
	//			}
	//		}
	//		return pps;
	//	}
	//
	//	/**
	//	 * @param pair
	//	 * @return
	//	 */
	//	private int calcRepSeqPoints(PrimerPair pair) {
	//		int score=0;
	//		for (int i= 0; i < locs.length; i++) {
	//			int repseqstart=locs[i].getMin();
	//			int repseqend=locs[i].getMax();
	//			score+=calcRepSeqPoints(pair.leftpos,pair.leftlen,repseqstart,repseqend);
	//			score+=calcRepSeqPoints(pair.rightpos-pair.leftlen+1,pair.rightpos,repseqstart,repseqend);
	//		}
	//		return score;
	//	}
	//	/**
	//	 * Berechnung der Punkte f&uuml;r repetetive Sequenzen. Wenn ein Primer zu  mehr als X% in einer
	//	 * solchen Sequenz liegt -> rauswerf, also maxscore zur&uuml;ck.
	//	 * @param primerstart Startindex des Primers, Start des Index bei 0!
	//	 * @param primerlength L&auml;nge des Primers
	//	 * @return steht noch nicht genau fest, momentan entweder 0 oder maxscore
	//	 */
	//	private int calcRepSeqPoints(int primerstart, int primerlength, int repseqstart, int repseqend) {
	//		int len= repseqend - repseqstart + 1;
	//		if (primerstart > repseqend || (primerstart + primerlength) < repseqstart) { //primer liegt ausserhalb
	//			return 0;
	//		} else if (primerstart >= repseqstart && (primerstart + primerlength) <= repseqend) { //primer liegt ganz drin
	//			return PCR.maxscore;
	//		} else if (primerstart >= repseqstart && (primerstart + primerlength) > repseqend) { //primer liegt hinten halb drin
	//			if ((repseqend - primerstart) * 100 / primerlength >= x) {
	//				return PCR.maxscore;
	//			} else
	//				return 0;
	//		} else if (primerstart < repseqstart && (primerstart + primerlength) <= repseqend) { //primer liegt vorn halb drin
	//			if ((primerstart + primerlength - repseqstart) * 100 / primerlength >= x) {
	//				return PCR.maxscore;
	//			} else
	//				return 0;
	//		} else { //primer länger als rep. Seq.
	//			if ((repseqend - repseqstart) * 100 / primerlength >= x) {
	//				return PCR.maxscore;
	//			} else
	//				return 0;
	//		}
	//	}
}

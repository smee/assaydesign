/*
 * Created on 02.09.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package biochemie.pcr.modules;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import biochemie.pcr.PCR;
import biochemie.pcr.PrimerPair;
import biochemie.pcr.io.PCRConfig;
import biochemie.util.BLATPageInvalid;
/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class BLAT extends AnalyseModul {
	/**
     * zu untersuchende Sequenz
     */
    String sequence= null;
    /**
     * Rauswerf, wenn kompletter Match?
     */
    boolean both= true;
    /**
     * RAuswerf, wenn nur ein Primer gefunden? Siehe Holgers mail.
     */
    boolean single= false;

    /**
     * von BLAT vergebene id für diese Anfrage
     */
    String hgsid= "";
    /**
     * Strafpunkte, wenn ein Primer die Kriterien erf&uuml;llt.
     */
    private int singleprimerscore;
    /**
     * Strafpunkte, wenn beide Primer die Kriterien erf&uuml;llen.
     */
    private int bothprimerscore;
    /**
     * parameter PERFORM_BLAT_WITH_BOTH_SIDES_INDEPENDENTLY_IF_PCR_LARGER_THAN
     */
    private int independant_blat_len;
    /**
     * Parameter MIN_ACCEPTED_MISSPRIMING_PRIMER_DISTANCE
     */
    private int min_distance;
	private final BlatSource src;
    /**
     * Wirft BLATPageInvalid, wenn die BLATPage geändert wurde. In diesem Fall muss dieses File angepasst werden,
     * da momentan auf aktuelle Syntax angepasst.
     * @param sequence
     * @param both
     * @param single
     * @throws BLATPageInvalid
     */
    public BLAT(PCRConfig cfg) throws BlatException {
        super(cfg);
        initProperties();
        src = new InetSource(cfg);
    }
    /**
	 * @param cfg
	 * @param object
	 */
	public BLAT(PCRConfig cfg, BlatSource src) {
		super(cfg);
		if(src == null)
			throw new NullPointerException("No blatsource given!");
		this.src = src;
		
		initProperties();
	}
	private void initProperties() {
		String temp= config.getProperty("SEQUENCE");
        if (null != temp) {
            this.sequence= temp;
        }
        this.bothprimerscore= config.getInteger("SCORE_BLAT_FOUND_SEQ",maxscore);
        this.singleprimerscore= config.getInteger("SCORE_BLAT_FOUND_PRIMER",maxscore);
        this.independant_blat_len= config.getInteger("PERFORM_BLAT_WITH_BOTH_SIDES_INDEPENDENTLY_IF_PCR_LARGER_THAN",500);            		
        this.min_distance= config.getInteger("MIN_ACCEPTED_MISSPRIMING_PRIMER_DISTANCE",3000);
        this.single = config.getBoolean("BLAT_SINGLE", true);
        this.both = config.getBoolean("BLAT_BOTH", true);
	}
	public void setSequence(String seq) {
        this.sequence= seq;
    }
 

    public static class BlatResultEntry implements Comparable{
    	/**
		 * @param start
		 * @param end
		 * @param chromosome
		 */
		public BlatResultEntry(int start, int end, int chromosome) {
			this.startpos = start;
			this.endpos = end;
			this.chromosom =chromosome;
		}
		//startindex in der angefragten sequenz
    	int startpos;
    	//endindex
    	int endpos;
    	// neg. values mean unknown
    	int chromosom;
    	//true means + and false means -
    	boolean strand;
    	
    	public String toString() {
    		StringBuffer buffer = new StringBuffer();
    		buffer.append("[BlatResultEntry:");
    		buffer.append(" startpos: ");
    		buffer.append(startpos);
    		buffer.append(" endpos: ");
    		buffer.append(endpos);
    		buffer.append(" chromosom: ");
    		buffer.append(chromosom);
    		buffer.append("]");
    		return buffer.toString();
    	}

    	/**
    	 * Sortiere nach: startpos, endpos, chromosome, strand
    	 * @param o
    	 * @return
    	 */
		public int compareTo(Object o) {
			BlatResultEntry other =(BlatResultEntry) o;
			
			if(other.startpos != startpos)
				return startpos - other.startpos;
			if(other.endpos != endpos)
				return endpos - other.endpos;
			if(other.chromosom != chromosom)
				return chromosom - other.chromosom;
			return 0;
			
		}
    }
    /**
     * Berechnung der Strafpunkte des BLATModuls. pps wird geändert!
     * @param pps
     * @return pps mit addierten Blat-Punkten
     */
    public void calcScores(PrimerPair[] pps) {
		if (null == pps || 0 == pps.length)
			return;
		if(sequence == null || sequence.length() == 0)
			throw new BlatException(new IllegalStateException("Sequence is not known to the BLAT module!"));
		int start=Integer.MAX_VALUE;
		int end=Integer.MIN_VALUE;
		
		for (int i = 0; i < pps.length; i++) {
			start=Math.min(start,pps[i].leftpos);
			end=Math.max(end,pps[i].rightpos);
		}
		int productlen = end - start +1;
		
		
		//sequenz, die alle primer enthaelt
		String seqtosend=sequence.substring(start, end+1);
        
		SortedSet blaterg=new TreeSet();
        if(PCR.debug)
        	System.out.println("Using offset = "+start);
		if(productlen > 25000){
			final int OFFSETINC = 24000;
			int offset = 0;
            while(offset < productlen) {
                String nextpart=seqtosend.substring(offset, offset + OFFSETINC);
                blaterg.addAll(adjustPositionsBy(src.getBlatResults(nextpart), offset + start));
                offset+=OFFSETINC;                

            }
		}else{
				blaterg.addAll(adjustPositionsBy(src.getBlatResults(seqtosend), start - 1));//primerpaare fangen bei 0 an, blatergebnisse bei 1
		}
		
				
			if (PCR.debug) {
				System.out.println("   Werte BLAT-Ergebnisse aus...");
			}

			for (int i = 0; i< pps.length; i++) {
				pps[i].addBlatScore(calcExtendedBLATSCORE(pps[i], blaterg));
			}
		 

	}
    /**
	 * @param blatResults
	 * @param i
	 * @return
	 */
	private Collection adjustPositionsBy(Collection blatResults, int offset) {
		for (Iterator it = blatResults.iterator(); it.hasNext();) {
			BlatResultEntry entry = (BlatResultEntry) it.next();
			entry.startpos += offset;
			entry.endpos += offset;
		}
		return blatResults;
	}



    /**
     * score = (#singlem - 2)/(#singlem - 1) * singlescore
     * wenn bothmatch > 1 ==> score += bothscore
     * 
	 * @param erg
	 * @param erg2
	 * @param pair
	 * @return
	 */
    private int calcExtendedBLATSCORE(PrimerPair pair, SortedSet blaterg) {
        int singlecount= 0, bothcount= 0;
        List lefthit = new LinkedList();
        List righthit = new LinkedList();
        for (Iterator it = blaterg.iterator(); it.hasNext();) {
        	BlatResultEntry b = (BlatResultEntry) it.next();
        	
        	if (b.startpos <= pair.leftpos+ (pair.leftlen/2)  && b.endpos >= pair.leftpos+pair.leftlen - 1) {
        		if (PCR.debug) {
        			System.out.println(
        					"Single Match (links) für Primerpair\n" + pair + "\nBLAT lieferte: " + b + '\n');
        		}
        		lefthit.add(new BlatResultEntry(Math.max(b.startpos, pair.leftpos),b.endpos,b.chromosom)); //fuer die both-auswertung interessiert mich nur, wo im primer der match beginnt
        		singlecount++;
        	}
        	if (b.startpos <= pair.rightpos-pair.rightlen+1 && b.endpos >= (pair.rightpos - pair.rightlen/2)) {
        		if (PCR.debug) {
        			System.out.println(
        					"Single Match (rechts) für Primerpair\n" + pair + "\nBLAT lieferte: " + b + '\n');
        		}
        		righthit.add(new BlatResultEntry(b.startpos,Math.min(b.endpos,pair.rightpos),b.chromosom));
        		singlecount++;
        	}
        }
        for (Iterator it = lefthit.iterator(); it.hasNext();) {
			BlatResultEntry lh = (BlatResultEntry) it.next();
			for (Iterator it2 = righthit.iterator(); it2.hasNext();) {
				BlatResultEntry rh = (BlatResultEntry) it2.next();
				if(lh.chromosom == rh.chromosom
						&& (Math.abs(lh.startpos - rh.endpos) < min_distance
								|| Math.abs(rh.startpos - lh.endpos) < min_distance)){
					bothcount++;
				}
			}
		}
        if(singlecount < 2 || bothcount < 1)
        	throw new BlatException("Didn't find original pcrproduct in blat's anwer. Bug in BLAT?");
        int score = (int) (((float)(singlecount-2)/(singlecount -1)) * maxscore);
        score += (bothcount -1 ) > 0 ? maxscore : 0;
        return score;
    }
    /**
     * Die letzten percent Prozent des Primers sollen binden, bzw die ersten percent Prozent.
     * @param startpos
     * @param len
     * @param percent
     * @param left
     * @return
     */
    protected int getPosForPercentBinding(int startpos, int len, float percent, boolean left) {
        float inc = len * percent;
        if(left)
            return  (int)(startpos + len  - inc);
        return (int)(startpos + 1 - inc);
    }
}

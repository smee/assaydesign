/*
 * Created on 10.10.2003
 *
 */
package biochemie.pcr.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.FeatureFilter;
import org.biojava.bio.seq.FeatureHolder;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;
import org.biojava.bio.seq.io.SeqIOTools;
import org.biojava.bio.symbol.Location;

/**
 *
 * @author Steffen
 *
 */
public class EMBLParser {
    Sequence seq=null;
    public EMBLParser(String filename) throws FileNotFoundException {
        BufferedReader br= null;
        //create a buffered reader to read the sequence file specified by args[0]
        br= new BufferedReader(new FileReader(filename));
        SequenceIterator sequence= SeqIOTools.readEmbl(br);
        try {
            seq=sequence.nextSequence();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        } catch (BioException e) {
            e.printStackTrace();
        }
        if(sequence.hasNext()) {
            throw new RuntimeException("Es darf nur eine Sequenz in "+filename+" enthalten sein!");
        }
    }
    /**
     * Gibt repetetive Sequenzen als String zurück. Form: start,laenge-Paare
     * @return
     */
    public String getRepetetiveSeqsAsString() {
        StringBuffer sb=new StringBuffer();
        for (Iterator fi= seq.features(); fi.hasNext();) {
            Feature f= (Feature) fi.next();
            if(f.getType().equals("repeat_region")) {
                Location loc=f.getLocation();
                if(!loc.isContiguous()) {
                    System.err.println("repeat_region nicht durchgängig!");
                    continue;
                }
                if(Integer.MAX_VALUE == loc.getMin()) {
                    continue;
                }
                sb.append(loc.getMin()+","+(loc.getMax()-loc.getMin()+1)+' ');
            }
        }
        return sb.toString();
    }
    public FeatureHolder getRepetetiveSeqFeatures() {
    	return seq.filter(new FeatureFilter.ByType("repeat_region"));
    }
	/**
	 * @return
	 */
    public String getAllSNPsAsString() {
        StringBuffer sb=new StringBuffer();
         for (Iterator fi= seq.features(); fi.hasNext();) {
             Feature f= (Feature) fi.next();
             if(f.getType().equals("variation")) {
                 Location loc=f.getLocation();
                 if(Integer.MAX_VALUE == loc.getMin()) {
                     continue;
                 }
                 int len=loc.getMax()-loc.getMin();
                 if(0 == len) {
                     sb.append(loc.getMin()+" ");
                 }else {
                     for(int i=loc.getMin();i<loc.getMax();i++)
                        sb.append(i+" ");
                 }
            }
         }
         return sb.toString();
    }
	public FeatureHolder getSNPFeatures() {
		return seq.filter(new FeatureFilter.ByType("variation"));
	}
	/**
	 * @return
	 */
   public String getAllExonsAsString() {
		StringBuffer sb=new StringBuffer();
		 for (Iterator fi= seq.features(); fi.hasNext();) {
			 Feature f= (Feature) fi.next();
			 if(f.getType().equals("exon")) {
				Location loc=f.getLocation();
				if(!loc.isContiguous()) {
					System.err.println("exon nicht durchgängig!");
					continue;
				}
				if(Integer.MAX_VALUE == loc.getMin()) {
					continue;
				}
				sb.append(loc.getMin()+","+loc.getMax()+' ');
            
			 }
		 }
		 return sb.toString();
	}
	public FeatureHolder getExonFeatures() {
		return seq.filter(new FeatureFilter.ByType("exon"));
	}
    /**
     * @return
     */
    public String getSequence() {
        return seq.seqString();
    }
    
    public Sequence getSeq() {
    	return this.seq;
    }
}

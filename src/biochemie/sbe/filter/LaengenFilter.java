/*
 * Created on 20.02.2004
 *
 */
package biochemie.sbe.filter;

import java.util.Iterator;
import java.util.List;

import biochemie.domspec.SBEPrimer;
import biochemie.sbe.SBEOptionsProvider;

/**
 *
 * @author Steffen
 *
 */
public class LaengenFilter extends AbstractKandidatenFilter {

    private final int len;
    
    public LaengenFilter(SBEOptionsProvider cfg){
    	super(cfg);
        this.len=cfg.getMinCandidateLen();
    }
    /**
     * Filtert alle Seq., deren Laenge kleiner einer Laenge len ist.
     */
    public void filter(List cand) {
        for (Iterator it= cand.iterator(); it.hasNext();) {
            SBEPrimer  p = (SBEPrimer) it.next();
            if(p.getSeq().length()<len){
                it.remove();
                count++;
				if(debug)
                	System.out.println("not considering "+p.getSeq()+", PL="+p.getBruchstelle()+", length<"+len);
            }
        }
    }
    private int count=0;
    private final String reason="primer too short: ";

    public int rejectedCount() {
        return count;
    }

    public String rejectReason() {
        return reason;
    }
}

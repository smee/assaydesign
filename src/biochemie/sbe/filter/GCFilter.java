/*
 * Created on 20.02.2004
 *
 */
package biochemie.sbe.filter;

import java.util.Iterator;
import java.util.List;

import biochemie.domspec.Primer;
import biochemie.sbe.SBEOptionsProvider;

/**
 *
 * @author Steffen
 *
 */
public class GCFilter extends AbstractKandidatenFilter {

    private final double gcMin;
    private final double gcMax;
    
    public GCFilter(SBEOptionsProvider cfg){
    	super(cfg);
        this.gcMin=cfg.getMinGC();
        this.gcMax=cfg.getMaxGC();
    }
    /**
     * Filtert alle Sequenzen, deren GC-Gehalt ausserhalb von <code>gcMin</code> und <code>gcMax</code> liegt.
     * @see KandidatenFilter#filter(List)
     */
    public void filter(List cand) {
        for (Iterator it= cand.iterator(); it.hasNext();) {
            Primer p=(Primer) it.next();
            double gc=p.getGCGehalt();
            if(gc<gcMin || gc>gcMax){
                it.remove();
                count++;
                if(debug)
                	System.out.println("not considering "+p.getSeq()+", GC-Value="+gc);
            }
        }
    }
    private int count=0;
    private final String reason="out of GC: ";

    public int rejectedCount() {
        return count;
    }

    public String rejectReason() {
        return reason;
    }
}

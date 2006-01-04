/*
 * Created on 20.02.2004
 *
 */
package biochemie.sbe.filter;

import java.util.Iterator;
import java.util.List;

import biochemie.domspec.Primer;
import biochemie.domspec.SBEPrimer;
import biochemie.sbe.SBEOptions;

/**
 *
 * @author Steffen
 *
 */
public class GCFilter extends AbstractKandidatenFilter {

    private final double gcMin;
    private final double gcMax;
    
    public GCFilter(SBEOptions cfg){
    	super(cfg);
        this.gcMin=cfg.getMinGC();
        this.gcMax=cfg.getMaxGC();
    }
    /**
     * Filtert alle Sequenzen, deren GC-Gehalt ausserhalb von <code>gcMin</code> und <code>gcMax</code> liegt.
     * @see KandidatenFilter#filter(List)
     */
    public void filter(List cand) {
    	StringBuffer sb=new StringBuffer("Primers out of GC:\n");
        for (Iterator it= cand.iterator(); it.hasNext();) {
            SBEPrimer p=(SBEPrimer) it.next();
            double gc=p.getGCGehalt();
            if(gc<gcMin || gc>gcMax){
                it.remove();
                count++;
                sb.append(getSBEPrimerDescription(p)).append(", ");
                sb.append(markRed("GC="+nf.format(gc)+"%"));
                sb.append("\n");
            }
        }
        System.out.println(sb);
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

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
public class TemperaturFilter extends AbstractKandidatenFilter {
    private double maxT;
    private double minT;
    
    public TemperaturFilter(SBEOptionsProvider cfg) {
    	super(cfg);
        this.minT= cfg.getMinTemperature();
        this.maxT= cfg.getMaxTemperature();
    }
    /**
     * Filtere alle Sequenzen, deren Temperatur ausserhalb des zulaessigen Bereiches liegt.
     * @see KandidatenFilter#filter(List) 
     */
    public void filter(List cand) {
        for (Iterator it= cand.iterator(); it.hasNext();) {
            SBEPrimer p=(SBEPrimer) it.next();

            if(p.getTemperature() < minT || p.getTemperature() > maxT){
                it.remove();
                count++;
				if(debug)
                	System.out.println("not considering "+p.getSeq()+", PL="+p.getBruchstelle()+", Temperature="+p.getTemperature());
            }
        }
    }
    private int count=0;
    private final String reason="out of TM: ";

    public int rejectedCount() {
        return count;
    }

    public String rejectReason() {
        return reason;
    }
}

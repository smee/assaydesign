/*
 * Created on 20.02.2004
 *
 */
package biochemie.sbe.filter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import biochemie.domspec.SBEPrimer;
import biochemie.sbe.SBEOptions;
/**
 *
 * @author Steffen
 *
 */
public class TemperaturFilter extends AbstractKandidatenFilter {
    private double maxT;
    private double minT;
    private static NumberFormat nf =new DecimalFormat("0.00");
    public TemperaturFilter(SBEOptions cfg) {
    	super(cfg);
        this.minT= cfg.getMinTemperature();
        this.maxT= cfg.getMaxTemperature();
    }
    /**
     * Filtere alle Sequenzen, deren Temperatur ausserhalb des zulaessigen Bereiches liegt.
     * @see KandidatenFilter#filter(List) 
     */
    public void filter(List cand) {
    	StringBuffer sb=new StringBuffer("Primers out of TM:\n");
    	
        for (Iterator it= cand.iterator(); it.hasNext();) {
            SBEPrimer p=(SBEPrimer) it.next();

            if(p.getTemperature() < minT || p.getTemperature() > maxT){
                it.remove();
                count++;
                sb.append(p.getSeq()+", PL="+p.getBruchstelle()+", Tm="+nf.format(p.getTemperature())+"°");
                sb.append("\n");
            }
        }
        System.out.println(sb);
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

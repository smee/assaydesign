/*
 * Created on 30.11.2004
 *
 */
package biochemie.sbe.filter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import biochemie.domspec.SBEPrimer;
import biochemie.sbe.SBEOptions;

/**
 * @author Steffen Dienst
 *
 */
public abstract class AbstractKandidatenFilter implements KandidatenFilter{
	protected SBEOptions cfg;
	protected final boolean debug;

	public AbstractKandidatenFilter(SBEOptions cfg){
		this.cfg = cfg;
        this.debug=cfg.isDebug();;
	}
    protected static NumberFormat nf =new DecimalFormat("0.00");
    protected static String getSBEPrimerDescription(SBEPrimer p) {
        StringBuffer sb=new StringBuffer();
        sb.append(p.getSeq()).append(", PL=").append(p.getBruchstelle()).
        append(", GC=").append(nf.format(p.getGCGehalt())).append('%').
        append(", Tm=").append(nf.format(p.getTemperature())).append('°');
        return sb.toString();
    }
    protected static String markRed(String string) {
        return "<FONT COLOR=\"FF0000\">"+string+"</FONT>";
    }
}

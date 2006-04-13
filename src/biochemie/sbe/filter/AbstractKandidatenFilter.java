/*
 * Created on 30.11.2004
 *
 */
package biochemie.sbe.filter;

import biochemie.domspec.Primer;
import biochemie.domspec.SBEPrimer;
import biochemie.sbe.SBEOptions;
import biochemie.util.Helper;

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
    protected static String getPrimerDescription(Primer p) {
        StringBuffer sb=new StringBuffer();
        sb.append(p.getSeq());
        if(p instanceof SBEPrimer)
            sb.append(", PL=").append(((SBEPrimer)p).getBruchstelle());
        sb.append(", length=").append(p.getSeq().length()).
        append(", GC=").append(Helper.format(p.getGCGehalt())).append('%').
        append(", Tm=").append(Helper.format(p.getTemperature())).append('°');
        return sb.toString();
    }
    protected static String markRed(String string) {
        return "<FONT COLOR=\"FF0000\">"+string+"</FONT>";
    }
}

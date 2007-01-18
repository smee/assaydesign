/*
 * Created on 30.11.2004
 *
 */
package biochemie.sbe.filter;

import biochemie.domspec.Primer;
import biochemie.sbe.SBEOptions;

/**
 * @author Steffen Dienst
 *
 */
public abstract class AbstractKandidatenFilter implements KandidatenFilter{
	protected SBEOptions cfg;
	protected final boolean debug;
	protected int count;
    protected String reason;
    
	public AbstractKandidatenFilter(SBEOptions cfg){
		this.cfg = cfg;
        this.debug=cfg.isDebug();
        count=0;
	}
    protected static String getPrimerDescription(Primer p) {
//        StringBuffer sb=new StringBuffer();
//        sb.append(p.getCompletePrimerSeq());
//        if(p instanceof CleavablePrimer)
//            sb.append(", PL=").append(((CleavablePrimer)p).getBruchstelle());
//        sb.append(", length=").append(p.getCompletePrimerSeq().length()).
//        append(", GC=").append(Helper.format(p.getGCGehalt())).append('%').
//        append(", Tm=").append(Helper.format(p.getTemperature())).append('°');
//        return sb.toString();
        return p.toString();
    }
    protected static String markRed(String string) {
        return "<FONT COLOR=\"FF0000\">"+string+"</FONT>";
    }
    public int rejectedCount() {
        return count;
    }
    public String rejectReason() {
        return reason;
    }
}

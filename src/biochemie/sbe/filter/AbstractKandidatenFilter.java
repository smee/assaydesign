/*
 * Created on 30.11.2004
 *
 */
package biochemie.sbe.filter;

import biochemie.sbe.SBEOptionsProvider;

/**
 * @author Steffen Dienst
 *
 */
public abstract class AbstractKandidatenFilter implements KandidatenFilter{
	protected SBEOptionsProvider cfg;
	protected final boolean debug;

	public AbstractKandidatenFilter(SBEOptionsProvider cfg){
		this.cfg = cfg;
        this.debug=cfg.isDebug();;
	}
}

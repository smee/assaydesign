/*
 * Created on 30.11.2004
 *
 */
package biochemie.sbe.filter;

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
}

/*
 * Created on 30.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.sbe.filter;

import biochemie.sbe.SBEOptionsProvider;

/**
 * @author Steffen Dienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractKandidatenFilter implements KandidatenFilter{
	protected SBEOptionsProvider cfg;
	protected final boolean debug;
    
	public AbstractKandidatenFilter(SBEOptionsProvider cfg){
		this.cfg = cfg;
        this.debug=cfg.isDebug();;
	}
}

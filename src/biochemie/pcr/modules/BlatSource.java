/*
 * Created on 25.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.pcr.modules;

import java.util.Collection;

/**
 * Retrieves results for a BLAT request. Encapsulates the source.
 * @author Steffen Dienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface BlatSource {
	/**
	 * Returns a collection of BlatResultEntry.
	 * @param pcrproduct
	 * @return
	 * @throws BlatException if there are any problems with the chosen blatsource
	 */
	public Collection getBlatResults(String pcrproduct) throws BlatException; 
}

/*
 * Created on 25.11.2004
 *
 */
package biochemie.pcr.modules;

import java.util.Collection;

/**
 * Retrieves results for a BLAT request. Encapsulates the source.
 * @author Steffen Dienst
 *
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

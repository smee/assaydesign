/*
 * Created on 06.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.pcr.matcher;

import java.util.Collection;
import java.util.List;

/**
 * @author Steffen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface MatcherStrategy {
    /**
     * Findet eine Liste von PCRPair instanzen, die moeglichst optimal ist nach dem Kriterium:
     * - moeglichst kleiner Durchschnittswert der Pos. innerhalb der PCRdatei
     * 
     * @param pcrpairs
     * @return
     */
    public Collection getBestPCRPrimerSet(List pcrpairs);

}

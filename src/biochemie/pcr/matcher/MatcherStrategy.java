/*
 * Created on 06.11.2004
 *
 */
package biochemie.pcr.matcher;

import java.util.Collection;
import java.util.List;

/**
 * @author Steffen
 *
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

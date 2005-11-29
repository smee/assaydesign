/*
 * Created on 06.11.2004
 *
 */
package biochemie.pcr.matcher;

import java.util.Collection;
import java.util.List;

import biochemie.sbe.multiplex.Multiplexable;

/**
 * @author Steffen
 *
 */
public interface MatcherStrategy {
    /**
     * Findet eine Liste von PCRPair instanzen, die moeglichst optimal ist nach dem Kriterium:
     * - moeglichst kleiner Durchschnittswert der Pos. innerhalb der PCRdatei
     * - needed ist darin enthalten, wenn needed!=null 
     * 
     *
     * @param pcrpairs
     * @return
     */
    public Collection getBestPCRPrimerSet(List pcrpairs, Multiplexable needed);

}

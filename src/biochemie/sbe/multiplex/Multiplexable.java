/*
 * Created on 24.10.2004 by Steffen Dienst
 *
 */
package biochemie.sbe.multiplex;

import java.util.List;

import org._3pq.jgrapht.Edge;

/**
 * Objekt, das bestimmten Clustern zugeordnet werden soll. Kann aus anderen Multiplexables bestehen.
 * @author Steffen Dienst
 * 24.10.2004
 */
public interface Multiplexable {
    /**
     * Weist einem Multiplexable eine ID zu, die fuer eine bestimmte
     * Gruppe einzigartig ist. Auf diese Weise koennen zusammengehoerige 
     * Dinge klassifiziert werden.
     * @param s
     */
    public void setPlexID(String s);

    
    public String getName();
    /**
     * Symetrische Beziehung zwischen Multiplexables.
     * @param other
     * @return
     */
    public boolean passtMit(Multiplexable other);
    /**
     * Anzahl der Elemente in diesem Knoten enthalten sind.
     * @return
     */
    public int realSize();

    public Edge getLastEdge();

    /**
     * Alle Multiplexables, die in diesem enthalten sind. Wenn keines enthalten ist, dann this.
     * @return
     */
    public List getIncludedElements();
}

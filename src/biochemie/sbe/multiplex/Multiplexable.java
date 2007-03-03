/*
 * Created on 24.10.2004 by Steffen Dienst
 *
 */
package biochemie.sbe.multiplex;

import java.util.Collection;
import java.util.List;

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

    public String getPlexID();
    
    public String getName();
    /**
     * Symetrische Beziehung zwischen Multiplexables.
     * @param other
     * @return
     */
    public boolean passtMit(Multiplexable other);
    /**
     * Anzahl der Elemente, die in diesem Knoten enthalten sind.
     * @return
     */
    public int realSize();

    public Collection getLastEdges();

    /**
     * Alle Multiplexables, die in diesem enthalten sind. Wenn keines enthalten ist, dann this.
     * @return
     */
    public List getIncludedElements();
}

/*
 * Created on 24.10.2004 by Steffen Dienst
 *
 */
package biochemie.sbe.calculators;

/**
 * Objekt, das bestimmten Clustern zugeordnet werden soll.
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
     * MAximale Groesse der Gruppe, in die diese Instanz darf
     * @return
     */
    public int maxPlexSize();
    /**
     * Fuer debugzwecke. XXX
     * @return
     */
    public String getEdgeReason();
}

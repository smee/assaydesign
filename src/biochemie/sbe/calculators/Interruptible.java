/*
 * Created on 15.06.2004 by Steffen
 *
 */
package biochemie.sbe.calculators;

/**
 * @author Steffen
 * 15.06.2004
 */
public interface Interruptible {
    /**
     * Startet den Task. Blockiert, bis der Task beendet ist bzw. abgebrochen wird.
     *
     */
    public void start();
    /**
     * Beendet den Task so schnell, wie möglich. 
     *
     */
    public void stop();
    /**
     * Liefert Ergebnis des Tasks. Achtung: Es wird nicht sichergestellt, dass das
     * Ergebnis schon existiert! Das Ergebnis eines abgebrochenen Tasks ist nicht
     * spezifiziert.
     * @return
     */
    public Object getResult();
}

/*
 * Created on 26.10.2004 by Steffen Dienst
 *
 */
package biochemie.sbe.multiplex;

import java.util.List;

/**
 * @author Steffen Dienst
 * 26.10.2004
 */
public interface MultiplexableFactory {
    /**
     * Liefert Liste von Instanzen von <link>Multiplexable</link>.
     * @return
     */
    public List getMultiplexables();
    
    public String getCSVRow();
    /**
     * @return Returns the csvheader.
     */
    public String[] getCsvheader() ;
}

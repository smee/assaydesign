/*
 * Created on 10.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.util.config;

/**
 * Generelle Parameter, die fuer alle Anwendungen von Interesse sind.
 * @author sdienst
 *
 */
public interface GeneralOptionsProvider {
    public int[] getPhotolinkerPositions();
    public void setPhotolinkerPositions(int[] br);
    
    public boolean isDebug();
    public void setDebug(boolean b);
}

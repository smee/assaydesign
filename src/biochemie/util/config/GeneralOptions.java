/*
 * Created on 10.12.2004
 *
 */
package biochemie.util.config;

/**
 * Generelle Parameter, die fuer alle Anwendungen von Interesse sind.
 * @author sdienst
 *
 */
public interface GeneralOptions {
    public int[] getPhotolinkerPositions();
    public void setPhotolinkerPositions(int[] br);

    public boolean isDebug();
    public void setDebug(boolean b);
}

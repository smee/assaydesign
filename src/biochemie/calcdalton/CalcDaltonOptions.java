/*
 * Created on 04.12.2004
 *
 */
package biochemie.calcdalton;

import biochemie.util.config.GeneralOptions;


/**
 * Haelt alle Optionen bereit, die CalcDalton fuer seine Arbeit braucht.
 * @author sdienst
 *
 */
public interface CalcDaltonOptions extends GeneralOptions{


    /**
     * CalcDalton kann bestimmte Abstaende zwischen den Massen ausschliessen.
     * Diese Methode liefert die Startwerte der auszuschliesenden Intervalle.
     * @return
     */
    public double[] getCalcDaltonFrom();
    public void setCalcDaltonFrom(double[] arr);

    /**
     * Endwerte der auszuschliessenden Abstandsintervalle.
     * @return
     */
    public double[] getCalcDaltonTo();
    public void setCalcDaltonTo(double[] arr);

    /**
     * Startwerte von Intervallen von Massebereichen, in denen keine Werte fallen duerfen.
     * @return
     */
    public double[] getCalcDaltonVerbFrom();
    public void setCalcDaltonVerbFrom(double[] arr);

    /**
     * Endwerte von Intervallen von Massebereichen, in denen keine Werte fallen duerfen.
     * @return
     */
    public double[] getCalcDaltonVerbTo();
    public void setCalcDaltonVerbTo(double[] arr);

    /**
     * Get minimum weight distance.
     * @return
     */
    public double getCalcDaltonPeaks();
    public void setCalcDaltonPeaks(double val);
    /**
     * Get minimum meltingtemperature.
     * @return
     */
    public boolean getCalcDaltonAllowOverlap();
    public void setCalcDaltonAllowOverlap(boolean val);
}
/*
 * Created on 04.12.2004
 *
 */
package biochemie.calcdalton;

import java.util.Map;

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
    
    /**
     * If checked, the program reserves the appropriate mass range for all possible extension products A, C, G and T of every primer.
     * Otherwise the mass range is reserved for the expected products only
     * 
     * @return
     */
    public boolean getCalcDaltonAllExtensions();
    public void setCalcDaltonAllExtensions(boolean val);
    
    public Map getCalcDaltonPrimerMassesMap();
    public void setCalcDaltonPrimerMassesMap(Map m);
    
    /**
     * Get maximum time for long calculation in seconds. 0 means no limit.
     * @return
     */
    public int getCalcTime();
    public void setCalcTime(int val);
    
    public Map getCalcDaltonAddonMassesMap();
    public void setCalcDaltonAddonMassesMap(Map m);
    public double getCalcDaltonPLMass();
    public void setCalcDaltonPLMass(double val);
    public int getCalcDaltonSelectedPLMass();
    /**
     * 0..3
     * @param val
     */
    public void setCalcDaltonSelectedPLMass(int val);
    public boolean getCalcDaltonShowIons();
    public void setCalcDaltonShowIons(boolean val);
    
    public String getBiotinString();
    public void setBiotinString(String biotin);
}
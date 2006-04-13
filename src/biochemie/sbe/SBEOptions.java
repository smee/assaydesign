/*
 * Created on 29.11.2004
 *
 */
package biochemie.sbe;

import biochemie.calcdalton.CalcDaltonOptions;

/**
 * Kaspelt alle Optionen, die MiniSBE braucht.
 * @author Steffen Dienst
 *
 */
public interface SBEOptions extends CalcDaltonOptions {

	/**
	 * Get minimum meltingtemperature.
	 * @return
	 */
	public double getMinTemperature();
	public void setMinTemperature(double val);

	/**
	 * Get optimum meltingtemperature.
	 * @return
	 */
	public double getOptTemperature();
	public void setOptTemperature(double val);
	/**
	 * Get maximum meltingtemperature.
	 * @return
	 */
	public double getMaxTemperature();
	public void setMaxTemperature(double val);

	/**
	 * Get minimum gc amount. ()in percent
	 * @return
	 */
	public double getMinGC();
	public void setMinGC(double val);
	/**
	 * Get maximum gc amount. ()in percent
	 * @return
	 */
	public double getMaxGC();
	public void setMaxGC(double val);
	/**
	 * Get polyx, the maximum number of times that a nucleotid repeats
	 * @return
	 */
	public int getPolyX();
	public void setPolyX(int val);
	/**
	 * Get maxplex, the maximum multiplexsize
	 * @return
	 */
	public int getMaxPlex();
	public void setMaxPlex(int val);
	/**
	 * Min. length of valid sbeprimers
	 * @return
	 */
	public int getMinCandidateLen();
	public void setMinCandidateLen(int len);
	/**
	 * Min. difference between pcrproducts of primers
	 * @return
	 */
	public int getMinProductLenDiff();
	public void setMinProductLenDiff(int len);

    public double getMaxMass();
    public void setMaxMass(double m);
    
    /**
     * True, wenn dateien mit den nichtvertraeglichkeitsgraphen ausgegeben werden sollen.
     * @return
     */
    public boolean isDrawGraphes();
    public void setDrawGraphes(boolean b);
    
    public SecStrucOptions getSecStrucOptions();
    public void setSecStrucOptions(SecStrucOptions opt);
}

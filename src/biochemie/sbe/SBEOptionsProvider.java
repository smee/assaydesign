/*
 * Created on 29.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.sbe;

import biochemie.calcdalton.CalcDaltonOptionsProvider;

/**
 * Kaspelt alle Optionen, die MiniSBE braucht.
 * @author Steffen Dienst
 *
 */
public interface SBEOptionsProvider extends CalcDaltonOptionsProvider {

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
	 * Get maximum time for long calculation in seconds. 0 means no limit.
	 * @return
	 */
	public int getCalcTime();
	public void setCalcTime(int val);
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
	/**
	 * Returns the windowsizes of predicted hairpins. Spaceseparated list of ints.
	 * @return
	 */
	public String getHairpinWindowsizes();
	public void setHairpinWindowsizes(String w);
	/**
	 * Returns the minbinds of predicted hairpins. Spaceseparated list of ints.
	 * @return
	 */
	public String getHairpinMinbinds();
	public void setHairpinMinbinds(String w);
	
	/**
	 * Returns the windowsizes of predicted homodimers. Spaceseparated list of ints.
	 * @return
	 */
	public String getHomodimerWindowsizes();
	public void setHomodimerWindowsizes(String w);
	
	/** 
	 * Returns the Minbinds of predicted homodimers. Spaceseparated list of ints.
	 * @return
	 */
	public String getHomodimerMinbinds();
	public void setHomodimerMinbinds(String w);
	
	/**
	 * Returns the windowsizes of predicted crossdimers. Spaceseparated list of ints.
	 * @return
	 */
	public String getCrossDimerWindowsizes();
	public void setCrossimerWindowsizes(String w);
	
	/**
	 * Returns the Minbinds of predicted crossdimers. Spaceseparated list of ints.
	 * @return
	 */
	public String getCrossdimerMinbinds();
	public void setCrossdimerMinbinds(String w);
	
	/**
	 * If all crossdimers aren't evil, only incompatible crossdimers are considered
	 * as such.
	 * @return
	 */
	public boolean getAllCrossdimersAreEvil();
	public void setAllCrossdimersAreEvil(boolean val);
    /**
     * True, wenn dateien mit den nichtvertraeglichkeitsgraphen ausgegeben werden sollen.
     * @return
     */
    public boolean isDrawGraphes();
    public void setDrawGraphes(boolean b);
}

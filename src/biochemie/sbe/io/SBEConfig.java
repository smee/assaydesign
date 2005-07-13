/*
 * Created on 29.11.2004
 *
 */
package biochemie.sbe.io;

import java.io.IOException;
import java.util.StringTokenizer;

import biochemie.calcdalton.CDOptionsImpl;
import biochemie.calcdalton.CalcDaltonOptions;
import biochemie.sbe.SBEOptions;
import biochemie.util.config.GeneralConfig;

/**
 *
 * @author Steffen Dienst
 *
 */
public class SBEConfig extends GeneralConfig implements SBEOptions{
    CalcDaltonOptions cdopt;

    /**
	 * Returns defaultvalues
	 */
	public SBEConfig() {
		super();
        cdopt = new CDOptionsImpl();
	}

	/**
     * @param provider
     */
    public SBEConfig(CalcDaltonOptions c) {
        super();
        cdopt = c;
    }
    protected String[][] getInitializedProperties() {
        return new String[][]{
             {"sbe.temperature.min","48"}
            ,{"sbe.temperature.opt","58"}
            ,{"sbe.temperature.max","62"}
            ,{"sbe.gc.min","20"}
            ,{"sbe.gc.max","80"}
            ,{"sbe.polyx","5"}
            ,{"sbe.maxplex","6"}
            ,{"sbe.hairpin.windowsizes","6 4"}
            ,{"sbe.hairpin.minbinds","4 4"}
            ,{"sbe.homodimer.windowsizes","6 4"}
            ,{"sbe.homodimer.minbinds","4 4"}
            ,{"sbe.mincandlen","18"}
            ,{"sbe.prodlendiff","0"}
            ,{"sbe.crossdimer.windowsizes","6 4"}
            ,{"sbe.crossdimer.minbinds","4 4"}
            ,{"sbe.crossdimer.areallevil","false"}
            ,{"misc.drawgraph","false"}
            ,{"misc.maxcalctime","10"}};
    }



    //MiniSBE parameter:
    //--------------------------------------------------------------

    public boolean getAllCrossdimersAreEvil() {
        return getBoolean("sbe.crossdimer.areallevil",false);
    }
	public double getMinTemperature() {
		return getDouble("sbe.temperature.min",-1);
	}

	public void setMinTemperature(double val) {
		setProperty("sbe.temperature.min",Double.toString(val));
	}

	public double getOptTemperature() {
		return getDouble("sbe.temperature.opt",-1);
	}

	public void setOptTemperature(double val) {
		setProperty("sbe.temperature.opt",Double.toString(val));
	}

	public double getMaxTemperature() {
		return getDouble("sbe.temperature.max",-1);
	}

	public void setMaxTemperature(double val) {
		setProperty("sbe.temperature.max",Double.toString(val));
	}

	public double getMinGC() {
		return getDouble("sbe.gc.min",20);
	}

	public void setMinGC(double val) {
		setProperty("sbe.gc.min",Double.toString(val));
	}

	public double getMaxGC() {
		return getDouble("sbe.gc.max",80);
	}

	public void setMaxGC(double val) {
		setProperty("sbe.gc.max",Double.toString(val));
	}

	public int getPolyX() {
		return getInteger("sbe.polyx",5);
	}

	public void setPolyX(int val) {
		setProperty("sbe.polyx",Integer.toString(val));
	}

	public int getMaxPlex() {
		return getInteger("sbe.maxplex",6);
	}

	public void setMaxPlex(int val) {
		setProperty("sbe.maxplex",Integer.toString(val));
	}

	public int getCalcTime() {
		return getInteger("misc.maxcalctime",20);
	}

	public void setCalcTime(int val) {
		setProperty("misc.maxcalctime",Integer.toString(val));
	}

	public String getHairpinWindowsizes() {
		return getString("sbe.hairpin.windowsizes","");
	}

	public void setHairpinWindowsizes(String w) {
		if(w != null) {
			setProperty("sbe.hairpin.windowsizes",w);
        }
	}

	public String getHairpinMinbinds() {
		return getString("sbe.hairpin.minbinds","");
	}

	public void setHairpinMinbinds(String w) {
		if(w != null) {
			setProperty("sbe.hairpin.minbinds",w);
        }
	}

	public String getHomodimerMinbinds() {
		return getString("sbe.homodimer.minbinds","");
	}

	public void setHomodimerMinbinds(String w) {
		if(w != null) {
			setProperty("sbe.homodimer.minbinds",w);
            notifyObservers();
        }
	}

	public String getHomodimerWindowsizes() {
		return getString("sbe.homodimer.windowsizes","");
	}

	public void setHomodimerWindowsizes(String w) {
		if(w != null) {
			setProperty("sbe.homodimer.windowsizes",w);
            }
	}

	public String getCrossdimerMinbinds() {
		return getString("sbe.crossdimer.minbinds","");
	}

	public void setCrossdimerMinbinds(String w) {
		if(w != null) {
			setProperty("sbe.crossdimer.minbinds",w);
        }
	}

	public String getCrossDimerWindowsizes() {
		return getString("sbe.crossdimer.windowsizes","");
	}

	public void setCrossimerWindowsizes(String w) {
		if(w != null) {
			setProperty("sbe.crossdimer.windowsizes",w);
            }
	}

	public int getMinCandidateLen() {
		return getInteger("sbe.mincandlen",18);
	}

	public void setMinCandidateLen(int len) {
		setProperty("sbe.mincandlen",Integer.toString(len));
	}



	public void setAllCrossdimersAreEvil(boolean val) {
		setProperty("sbe.crossdimer.areallevil",Boolean.toString(val));
	}

	public int getMinProductLenDiff() {
		return getInteger("sbe.prodlendiff",0);
	}

	public void setMinProductLenDiff(int len) {
		setProperty("sbe.prodlendiff",Integer.toString(len));
	}
    public boolean isDrawGraphes() {
        return getBoolean("misc.drawgraph",false);
    }

    public void setDrawGraphes(boolean b) {
        setProperty("misc.drawgraph",Boolean.toString(b));
    }
    /**
     * @param line
     * @return
     */
    public static  boolean isEmptyRow(String line) {
        if(0 == line.length())
            return true;
        StringTokenizer st=new StringTokenizer(line,";\"");
        if(0 == st.countTokens())
            return true;
        return false;
    }

    //proxy for CalcDaltonOptionsProvider--------------------------------------------------------
    public int[] getPhotolinkerPositions() {
        return cdopt.getPhotolinkerPositions();
    }    public void setPhotolinkerPositions(int[] arg0) {
        cdopt.setPhotolinkerPositions(arg0);
    }    public double[] getCalcDaltonFrom() {
        return cdopt.getCalcDaltonFrom();
    }    public void setCalcDaltonFrom(double[] arg0) {
        cdopt.setCalcDaltonFrom(arg0);
    }    public double[] getCalcDaltonTo() {
        return cdopt.getCalcDaltonTo();
    }    public void setCalcDaltonTo(double[] arg0) {
        cdopt.setCalcDaltonTo(arg0);
    }    public double[] getCalcDaltonVerbFrom() {
        return cdopt.getCalcDaltonVerbFrom();
    }    public void setCalcDaltonVerbFrom(double[] arg0) {
        cdopt.setCalcDaltonVerbFrom(arg0);
    }    public double[] getCalcDaltonVerbTo() {
        return cdopt.getCalcDaltonVerbTo();
    }    public void setCalcDaltonVerbTo(double[] arg0) {
        cdopt.setCalcDaltonVerbTo(arg0);
    }    public double getCalcDaltonPeaks() {
        return cdopt.getCalcDaltonPeaks();
    }    public void setCalcDaltonPeaks(double arg0) {
        cdopt.setCalcDaltonPeaks(arg0);
    }    public boolean getCalcDaltonAllowOverlap() {
        return cdopt.getCalcDaltonAllowOverlap();
    }    public void setCalcDaltonAllowOverlap(boolean arg0) {
        cdopt.setCalcDaltonAllowOverlap(arg0);
    }
    public boolean isDebug() {
        return cdopt.isDebug();
    }

    public void setDebug(boolean b) {
        cdopt.setDebug(b);
    }
    public boolean getCalcDaltonAllExtensions() {
        return cdopt.getCalcDaltonAllExtensions();
    }
    
    public void setCalcDaltonAllExtensions(boolean val) {
        cdopt.setCalcDaltonAllExtensions(val);
    }
    //proxy end -----------------------------------------------------------------------------------------

    public void readConfigFile(String f) throws IOException {
        if(cdopt instanceof GeneralConfig)
            ((GeneralConfig)cdopt).readConfigFile(f);
        super.readConfigFile(f);
    }

    public void updateConfigFile(String filename) throws IOException {
        super.updateConfigFile(filename);
        if(cdopt instanceof GeneralConfig)
            ((GeneralConfig)cdopt).updateConfigFile(filename);
    }

    public void writeConfigTo(String filename) throws IOException {
        super.writeConfigTo(filename);
        if(cdopt instanceof GeneralConfig)
            ((GeneralConfig)cdopt).updateConfigFile(filename);
    }





 }

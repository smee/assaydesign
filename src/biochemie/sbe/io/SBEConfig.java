/*
 * Created on 29.11.2004
 *
 */
package biochemie.sbe.io;

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

import biochemie.calcdalton.CDOptionsImpl;
import biochemie.calcdalton.CalcDaltonOptions;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.SecStrucOptions;
import biochemie.util.config.GeneralConfig;

/**
 *
 * @author Steffen Dienst
 *
 */
public class SBEConfig extends GeneralConfig implements SBEOptions{
    private CalcDaltonOptions cdopt;
    private SecStrucOptions secopt;
    /**
	 * Returns defaultvalues
	 */
	public SBEConfig() {
		super();
        cdopt = new CDOptionsImpl();
        secopt=new SecStrucConfig();
	}

	/**
     * @param provider
     */
    public SBEConfig(CalcDaltonOptions c, SecStrucOptions s) {
        super();
        cdopt = c;
        secopt=s;
    }
    public SBEConfig(SBEOptions cfg){//FIXME expecting instances of SBEConfig is pretty bad
        super(((SBEConfig)cfg).prop);
        SBEConfig c=(SBEConfig) cfg;
        cdopt=new CDOptionsImpl((CDOptionsImpl) c.cdopt);
        secopt=new SecStrucConfig((SecStrucConfig) c.secopt);
    }
    protected String[] getInitializedProperties() {
        return new String[]{
             "sbe.temperature.min"
            ,"sbe.temperature.opt"
            ,"sbe.temperature.max"
            ,"sbe.gc.min"
            ,"sbe.gc.max"
            ,"sbe.polyx"
            ,"sbe.maxplex"
            ,"sbe.mincandlen"
            ,"sbe.prodlendiff"
            ,"misc.drawgraph"};
    }

    //MiniSBE parameter:
    //--------------------------------------------------------------

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


	public int getMinCandidateLen() {
		return getInteger("sbe.mincandlen",18);
	}

	public void setMinCandidateLen(int len) {
		setProperty("sbe.mincandlen",Integer.toString(len));
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
    }    public double[] getCalcDaltonAssayPeaks() {
        return cdopt.getCalcDaltonAssayPeaks();
    }    public void setCalcDaltonAssayPeaks(double[] arg0) {
        cdopt.setCalcDaltonAssayPeaks(arg0);
    }    public double[] getCalcDaltonProductPeaks() {
        return cdopt.getCalcDaltonProductPeaks();
    }    public void setCalcDaltonProductPeaks(double[] arg0) {
        cdopt.setCalcDaltonProductPeaks(arg0);
    }    public boolean isCalcDaltonAllowOverlap() {
        return cdopt.isCalcDaltonAllowOverlap();
    }    public void setCalcDaltonAllowOverlap(boolean arg0) {
        cdopt.setCalcDaltonAllowOverlap(arg0);
    }    public Map getCalcDaltonPrimerMassesMap() {
        return cdopt.getCalcDaltonPrimerMassesMap();
    }    public void setCalcDaltonPrimerMassesMap(Map m) {
        cdopt.setCalcDaltonPrimerMassesMap(m);
    }    public Map getCalcDaltonAddonMassesMap() {
        return cdopt.getCalcDaltonAddonMassesMap();
    }    public void setCalcDaltonAddonMassesMap(Map m) {
        cdopt.setCalcDaltonAddonMassesMap(m);
    }    public boolean isDebug() {
        return cdopt.isDebug();
    }    public void setDebug(boolean b) {
        cdopt.setDebug(b);
    }    public boolean isCalcDaltonAllExtensions() {
        return cdopt.isCalcDaltonAllExtensions();
    }    public void setCalcDaltonAllExtensions(boolean val) {
        cdopt.setCalcDaltonAllExtensions(val);
    }    public double getCalcDaltonPLMass() {
        return cdopt.getCalcDaltonPLMass();
    }    public void setCalcDaltonPLMass(double val) {
        cdopt.setCalcDaltonPLMass(val);
    }    public int getCalcTime() {
        return cdopt.getCalcTime();
    }    public void setCalcTime(int val) {
        cdopt.setCalcTime(val);
    }    public boolean isCalcDaltonShowIons() {
        return cdopt.isCalcDaltonShowIons();
    }    public void setCalcDaltonShowIons(boolean val) {
        cdopt.setCalcDaltonShowIons(val);        
    }    public int getCalcDaltonSelectedPLMass() {
        return cdopt.getCalcDaltonSelectedPLMass();
    }    public void setCalcDaltonSelectedPLMass(int val) {
        cdopt.setCalcDaltonSelectedPLMass(val);
    }    public String getBiotinString() {
        return cdopt.getBiotinString();
    }    public void setBiotinString(String biotin) {
        cdopt.setBiotinString(biotin);
    }    public double getCalcDaltonMaxMass() {
        return cdopt.getCalcDaltonMaxMass();
    }    public void setCalcDaltonMaxMass(double mass) {
        cdopt.setCalcDaltonMaxMass(mass);
    }    public boolean isCalcDaltonForbidHalfMasses() {
        return cdopt.isCalcDaltonForbidHalfMasses();
    }    public void setCalcDaltonForbidHalfMasses(boolean val) {
        cdopt.setCalcDaltonForbidHalfMasses(val);
    }
    //proxy end -----------------------------------------------------------------------------------------

    public void readConfigFile(String f) throws IOException {
        if(cdopt instanceof GeneralConfig)
            ((GeneralConfig)cdopt).readConfigFile(f);
        if(secopt instanceof GeneralConfig)
            ((GeneralConfig)secopt).readConfigFile(f);
        super.readConfigFile(f);
    }
//    public synchronized void readConfig(InputStream f) throws IOException{
//        if(cdopt instanceof GeneralConfig){
//            f.mark(100000);
//            ((GeneralConfig)cdopt).readConfig(f);
//            f.reset();
//        }
//        if(secopt instanceof GeneralConfig){
//            f.mark(100000);
//            ((GeneralConfig)secopt).readConfig(f);
//            f.reset();
//        }
//        super.readConfig(f);        
//    }
    public void updateConfigFile(String filename) throws IOException {
        super.updateConfigFile(filename);
        if(cdopt instanceof GeneralConfig)
            ((GeneralConfig)cdopt).updateConfigFile(filename);
        if(secopt instanceof GeneralConfig)
            ((GeneralConfig)secopt).updateConfigFile(filename);
    }

    public void writeConfigTo(String filename) throws IOException {
        super.writeConfigTo(filename);
        if(cdopt instanceof GeneralConfig)
            ((GeneralConfig)cdopt).updateConfigFile(filename);
        if(secopt instanceof GeneralConfig)
            ((GeneralConfig)secopt).updateConfigFile(filename);
    }

    public SecStrucOptions getSecStrucOptions() {
        return secopt;
    }

    public void setSecStrucOptions(SecStrucOptions opt) {
        this.secopt=opt;
    }


    public void makeDefault() {
        super.makeDefault();
        if(cdopt instanceof GeneralConfig)
            ((GeneralConfig)cdopt).makeDefault();
        else
            throw new RuntimeException("Can't save default values for calcdalton options!");
        if(secopt instanceof GeneralConfig)
            ((GeneralConfig)secopt).makeDefault();
        else
            throw new RuntimeException("Can't save default values for secstruc options!");
    }
    public void loadDefault(){
        super.loadDefault();
        if(cdopt==null)
            cdopt=new CDOptionsImpl();
        if(secopt==null)
            secopt=new SecStrucConfig();
        if(cdopt instanceof GeneralConfig)
            ((GeneralConfig)cdopt).loadDefault();
        else
            throw new RuntimeException("Can't load default values for calcdalton options!");
        if(secopt instanceof GeneralConfig)
            ((GeneralConfig)secopt).loadDefault();
        else
            throw new RuntimeException("Can't load default values for secstruc options!");
        
    }


 }

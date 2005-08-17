/*
 * Created on 04.12.2004
 *
 */
package biochemie.calcdalton;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.Ostermiller.util.StringTokenizer;

import biochemie.util.Helper;
import biochemie.util.config.GeneralConfig;

/**
 * @author sdienst
 *
 */
public class CDOptionsImpl extends GeneralConfig implements CalcDaltonOptions {
    public CDOptionsImpl() {
        super();
    }

    protected String[][] getInitializedProperties() {
        return new String[][]{
            {"calcdalton.pl","9 8 10 11 12 13 14 15"}
           ,{"calcdalton.from","20.0 36.0"}
           ,{"calcdalton.to","24.0 40.0"}
           ,{"calcdalton.verbfrom","1000"}
           ,{"calcdalton.verbto","2000"}
           ,{"calcdalton.allowoverlap","false"}
           ,{"calcdalton.peaks","4"}
           ,{"calcdalton.extension","true"}
           ,{"calcdalton.primermasses","{A=313.2071, C=289.1823, T=304.1937, G=329.2066}"}
           ,{"calcdalton.addonmasses","{A=297.2072, C=273.1824, T=288.1937, G=313.2066}"}
           ,{"calcdalton.plmass","18.02"}
           ,{"misc.debug","false"}
           };
    }
    public int[] getPhotolinkerPositions(){
        String pl = getString("calcdalton.pl");
        int[] br=Helper.tokenizeToInt(pl);
        return br;
    }
    public void setPhotolinkerPositions(int[] br){
        setProperty("calcdalton.pl",StringUtils.join(ArrayUtils.toObject(br),' '));
    }

    public double[] getCalcDaltonFrom() {
        String pl = getString("calcdalton.from");
        double[] from=Helper.tokenizeToDouble(pl);
        return from;
    }

    public void setCalcDaltonFrom(double[] arr) {
        setProperty("calcdalton.from",StringUtils.join(ArrayUtils.toObject(arr),' '));
    }

    public double[] getCalcDaltonTo() {
        String pl = getString("calcdalton.to");
        double[] to=Helper.tokenizeToDouble(pl);
        return to;
    }

    public void setCalcDaltonTo(double[] arr) {
        setProperty("calcdalton.to",StringUtils.join(ArrayUtils.toObject(arr),' '));
    }

    public double[] getCalcDaltonVerbFrom() {
        String pl = getString("calcdalton.verbfrom");
        double[] verbfrom=Helper.tokenizeToDouble(pl);
        return verbfrom;
    }

    public void setCalcDaltonVerbFrom(double[] arr) {
        setProperty("calcdalton.verbfrom",StringUtils.join(ArrayUtils.toObject(arr),' '));
    }

    public double[] getCalcDaltonVerbTo() {
        String pl = getString("calcdalton.verbto");
        double[] verbto=Helper.tokenizeToDouble(pl);
        return verbto;
    }

    public void setCalcDaltonVerbTo(double[] arr) {
        setProperty("calcdalton.verbto",StringUtils.join(ArrayUtils.toObject(arr),' '));
    }
    public double getCalcDaltonPeaks() {
        return getDouble("calcdalton.peaks",4.0);
    }
    public void setCalcDaltonPeaks(double val) {
        setProperty("calcdalton.peaks",Double.toString(val));
    }

    public boolean getCalcDaltonAllowOverlap() {
        return getBoolean("calcdalton.allowoverlap",false);
    }

    public void setCalcDaltonAllowOverlap(boolean val) {
        setProperty("calcdalton.allowoverlap",Boolean.toString(val));
    }

    public boolean getAllCrossdimersAreEvil() {
        return getBoolean("sbe.crossdimer.areallevil",false);
    }

    public boolean isDebug() {
        return getBoolean("misc.debug",false);
    }

    public void setDebug(boolean b) {
        setProperty("misc.debug",Boolean.toString(b));
    }

    public boolean getCalcDaltonAllExtensions() {
        return getBoolean("calcdalton.extension",true);
    }

    public void setCalcDaltonAllExtensions(boolean val) {
        setProperty("calcdalton.extension",Boolean.toString(val));
    }

    public Map getCalcDaltonPrimerMassesMap() {
        String s=getProperty("calcdalton.primermasses");
        Map m=new HashMap();
        StringTokenizer st=new StringTokenizer(s,"{,} ");
        while(st.hasMoreTokens()) {
            String tok=st.nextToken();
            Character key=Character.valueOf(tok.charAt(0));
            Double val=Double.valueOf(tok.substring(tok.indexOf('=')+1));
            m.put(key,val);
        }
        return m;
    }

    public void setCalcDaltonPrimerMassesMap(Map m) {
        setProperty("calcdalton.primermasses",m.toString());
    }

    public Map getCalcDaltonAddonMassesMap() {
        String s=getProperty("calcdalton.addonmasses");
        Map m=new HashMap();
        StringTokenizer st=new StringTokenizer(s,"{,}");
        while(st.hasMoreTokens()) {
            String tok=st.nextToken();
            Character key=Character.valueOf(tok.charAt(0));
            Double val=Double.valueOf(tok.substring(tok.indexOf('=')+1));
            m.put(key,val);
        }
        return m;
    }

    public void setCalcDaltonAddonMassesMap(Map m) {
        setProperty("calcdalton.addonmasses",m.toString());
    }

    public double getCalcDaltonPLMass() {
        return getDouble("calcdalton.plmass",18.02);
    }

    public void setCalcDaltonPLMass(double val) {
        setProperty("calcdalton.plmass",Double.toString(val));
    }

}

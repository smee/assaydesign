/*
 * Created on 04.12.2004
 *
 */
package biochemie.calcdalton;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;


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
            {"calcdalton.pl","9 8 10 11 12 13 14 15 16"}
           ,{"calcdalton.from","-152.0 -136.0 -112.0 0.0"}
           ,{"calcdalton.to","-150.0 -134.0 -110.0 50.0"}
           ,{"calcdalton.verbfrom","2070.0 2162.0 2248.0 2385.0 2425.0 5000.0"}
           ,{"calcdalton.verbto","2080.0 2172.0 2258.0 2395.0 2435.0 9.99999999E8"}
           ,{"calcdalton.maxmass","30000"}
           ,{"calcdalton.allowoverlap","true"}
           ,{"calcdalton.assaypeaks","10.0 2000 10.0"}
           ,{"calcdalton.productpeaks","10.0 2000 10.0"}
           ,{"calcdalton.extension","false"}
           ,{"calcdalton.primermasses","{A=313.2071, C=289.1823, T=304.1937, G=329.2066}"}
           ,{"calcdalton.addonmasses","{A=297.2072, C=273.1824, T=288.1937, G=313.2066}"}
           ,{"calcdalton.plmass","18.02"}
           ,{"calcdalton.plmassidx","3"}
           ,{"calcdalton.showions","true"}
           ,{"misc.maxcalctime","10"}
           ,{"misc.debug","false"}
           ,{"misc.biotin","bio"}
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
    public double[] getCalcDaltonAssayPeaks() {
        String pl = getString("calcdalton.assaypeaks");
        double[] peaks=Helper.tokenizeToDouble(pl);
        return peaks;
    }
    public void setCalcDaltonAssayPeaks(double[] vals) {
        setProperty("calcdalton.assaypeaks",StringUtils.join(ArrayUtils.toObject(vals),' '));
    }
    public double[] getCalcDaltonProductPeaks() {
        String pl = getString("calcdalton.productpeaks");
        double[] peaks=Helper.tokenizeToDouble(pl);
        return peaks;
    }
    public void setCalcDaltonProductPeaks(double[] vals) {
        setProperty("calcdalton.productpeaks",StringUtils.join(ArrayUtils.toObject(vals),' '));
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
    public int getCalcTime() {
        return getInteger("misc.maxcalctime",20);
    }

    public void setCalcTime(int val) {
        setProperty("misc.maxcalctime",Integer.toString(val));
    }
    public Map getCalcDaltonPrimerMassesMap() {
        String s=getProperty("calcdalton.primermasses");
        Map m=new HashMap();
        StringTokenizer st=new StringTokenizer(s,"{,} ");
        while(st.hasMoreTokens()) {
            String tok=st.nextToken();
            Character key=new Character(tok.charAt(0));
            Double val=new Double(tok.substring(tok.indexOf('=')+1));
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
        StringTokenizer st=new StringTokenizer(s,"{,} ");
        while(st.hasMoreTokens()) {
            String tok=st.nextToken();
            Character key=new Character(tok.charAt(0));
            Double val=new Double(tok.substring(tok.indexOf('=')+1));
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

    public boolean getCalcDaltonShowIons() {
        return getBoolean("calcdalton.showions",true);
    }

    public void setCalcDaltonShowIons(boolean val) {
        setProperty("calcdalton.showions",Boolean.toString(val));
    }

    public int getCalcDaltonSelectedPLMass() {
        return getInteger("calcdalton.plmassidx",3);
    }

    public void setCalcDaltonSelectedPLMass(int val) {
        setProperty("calcdalton.plmassidx",Integer.toString(val));
    }
    public String getBiotinString() {
        return this.getString("misc.biotin","bio");
    }

    public void setBiotinString(String biotin) {
        setProperty("misc.biotin",biotin);
    }

    public double getCalcDaltonMaxMass() {
        return getDouble("calcdalton.maxmass",30000);
    }

    public void setCalcDaltonMaxMass(double mass) {
        setProperty("calcdalton.maxmass",Double.toString(mass));
    }
}

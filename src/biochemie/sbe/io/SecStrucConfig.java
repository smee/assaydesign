package biochemie.sbe.io;

import biochemie.sbe.SecStrucOptions;
import biochemie.util.config.GeneralConfig;

public class SecStrucConfig extends GeneralConfig implements SecStrucOptions{
    
    

    protected String[][] getInitializedProperties() {
        return new String[][]{
                 {"sbe.hairpin.windowsizes","6 4"}
                ,{"sbe.hairpin.minbinds","4 4"}
                ,{"sbe.homodimer.windowsizes","6 4"}
                ,{"sbe.homodimer.minbinds","4 4"}
                ,{"sbe.crossdimer.windowsizes","6 4"}
                ,{"sbe.crossdimer.areallevil","false"}
                ,{"sbe.crossdimer.minbinds","4 4"}};
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

    public boolean getAllCrossdimersAreEvil() {
        return getBoolean("sbe.crossdimer.areallevil",false);
    }

    public void setAllCrossdimersAreEvil(boolean val) {
        setProperty("sbe.crossdimer.areallevil",Boolean.toString(val));
    }
}

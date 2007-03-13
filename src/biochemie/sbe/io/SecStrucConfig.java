package biochemie.sbe.io;

import biochemie.sbe.SecStrucOptions;
import biochemie.util.config.GeneralConfig;

public class SecStrucConfig extends GeneralConfig implements SecStrucOptions{
    
    public SecStrucConfig(){
        super();
    }
    public SecStrucConfig(SecStrucConfig cfg){
        super(cfg.prop);
    }
    protected String[] getInitializedProperties() {
        return new String[]{
                 "sbe.hairpin.windowsizes"
                ,"sbe.hairpin.minbinds"
                ,"sbe.homodimer.windowsizes"
                ,"sbe.homodimer.minbinds"
                ,"sbe.crossdimer.windowsizes"
                ,"sbe.crossdimer.areallevil"
                ,"sbe.crossdimer.ignorecomp"
                ,"sbe.compHAIRP2Edge"
                ,"sbe.crossdimer.minbinds"};
    }
    
    public String getHairpinWindowsizes() {
        return getString("sbe.hairpin.windowsizes","6 4");
    }

    public void setHairpinWindowsizes(String w) {
        if(w != null) {
            setProperty("sbe.hairpin.windowsizes",w);
        }
    }

    public String getHairpinMinbinds() {
        return getString("sbe.hairpin.minbinds","4 4");
    }

    public void setHairpinMinbinds(String w) {
        if(w != null) {
            setProperty("sbe.hairpin.minbinds",w);
        }
    }

    public String getHomodimerMinbinds() {
        return getString("sbe.homodimer.minbinds","4 4");
    }

    public void setHomodimerMinbinds(String w) {
        if(w != null) {
            setProperty("sbe.homodimer.minbinds",w);
            notifyObservers();
        }
    }

    public String getHomodimerWindowsizes() {
        return getString("sbe.homodimer.windowsizes","6 4");
    }

    public void setHomodimerWindowsizes(String w) {
        if(w != null) {
            setProperty("sbe.homodimer.windowsizes",w);
            }
    }

    public String getCrossdimerMinbinds() {
        return getString("sbe.crossdimer.minbinds","4 4");
    }

    public void setCrossdimerMinbinds(String w) {
        if(w != null) {
            setProperty("sbe.crossdimer.minbinds",w);
        }
    }

    public String getCrossDimerWindowsizes() {
        return getString("sbe.crossdimer.windowsizes","6 4");
    }

    public void setCrossimerWindowsizes(String w) {
        if(w != null) {
            setProperty("sbe.crossdimer.windowsizes",w);
            }
    }

    public boolean isIgnoreCompCrossdimers() {
        return getBoolean("sbe.crossdimer.ignorecomp",false);
    }

    public void setIgnoreCompCrossdimers(boolean val) {
        setProperty("sbe.crossdimer.ignorecomp",Boolean.toString(val));
    }
    public boolean isAllCrossdimersAreEvil() {
        return getBoolean("sbe.crossdimer.areallevil",false);
    }
    public void setAllCrossdimersAreEvil(boolean val) {
        setProperty("sbe.crossdimer.areallevil",Boolean.toString(val));
    }
    public boolean isSecStrucEdgeCreating() {
        return getBoolean("sbe.compHAIRP2Edge",true);
    }

    public void setSecStrucEdgeCreating(boolean val) {
        setProperty("sbe.compHAIRP2Edge",Boolean.toString(val));
    }
}

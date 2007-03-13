package biochemie.sbe;

public interface SecStrucOptions {
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
    public boolean isIgnoreCompCrossdimers();
    public void setIgnoreCompCrossdimers(boolean val);
    
    public boolean isAllCrossdimersAreEvil(); 
    public void setAllCrossdimersAreEvil(boolean val); 
}

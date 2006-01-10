// Decompiled by DJ v3.5.5.77 Copyright 2003 Atanas Neshkov  Date: 25.09.2003 09:25:04
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Dimer.java

package netprimer;


public class Dimer
    implements Comparable
{

    public Dimer()
    {
    }

    Dimer(String theUpperFragment, String theLowerFragment, String theBondString, double theDg, boolean the3PrimeEnd)
    {
        itsUpperFragment = theUpperFragment;
        itsLowerFragment = theLowerFragment;
        itsBondString = theBondString;
        itsDg = theDg;
        its3PrimeEnd = the3PrimeEnd;
    }

    public String get_UpperFragment()
    {
        return itsUpperFragment;
    }

    public String get_LowerFragment()
    {
        return itsLowerFragment;
    }

    public String get_BondString()
    {
        return itsBondString;
    }

    public double get_Dg()
    {
        return itsDg;
    }

    public boolean is3PrimeEnd()
    {
        return its3PrimeEnd;
    }

    public int compareTo(Object theObj)
    {
        Dimer aDimer = (Dimer)theObj;
        return itsDg == aDimer.itsDg ? 0 : itsDg < aDimer.itsDg ? -1 : 1;
    }

    private String itsUpperFragment;
    private String itsLowerFragment;
    private String itsBondString;
    private double itsDg;
    private boolean its3PrimeEnd;
}
// Decompiled by DJ v3.5.5.77 Copyright 2003 Atanas Neshkov  Date: 25.09.2003 09:25:00
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Xdimer.java

package netprimer;


public class Xdimer
    implements Comparable
{

    public Xdimer()
    {
    }

    Xdimer(StringBuffer theUpperFragment, StringBuffer theLowerFragment, StringBuffer theBondString, double theDg, int theContBonds, boolean the3PrimeEnd)
    {
        itsUpperFragment = theUpperFragment.toString();
        itsLowerFragment = theLowerFragment.toString();
        itsBondString = theBondString.toString();
        itsDg = theDg;
        itsContBonds = theContBonds;
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

    public int get_ContBonds()
    {
        return itsContBonds;
    }

    public boolean is3PrimeEnd()
    {
        return its3PrimeEnd;
    }

    public int compareTo(Object theObj)
    {
        Xdimer aXDimer = (Xdimer)theObj;
        return itsDg == aXDimer.itsDg ? 0 : itsDg < aXDimer.itsDg ? -1 : 1;
    }

    private String itsUpperFragment;
    private String itsLowerFragment;
    private String itsBondString;
    private double itsDg;
    private int itsContBonds;
    private boolean its3PrimeEnd;
}
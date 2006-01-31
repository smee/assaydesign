// Decompiled by DJ v3.5.5.77 Copyright 2003 Atanas Neshkov  Date: 25.09.2003 09:25:04
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Hairpin.java

package netprimer;

import biochemie.util.Helper;


public class Hairpin
    implements Comparable
{

    public Hairpin()
    {
    }

    Hairpin(String theUpperFragment, String theLowerFragment, String theBondString, String theMiddleBase, double theSeqDg, boolean theis3PrimeEnd, 
            int theContBonds, int theNoOfBasesinLoop)
    {
        itsUpperFragment = theUpperFragment;
        itsLowerFragment = theLowerFragment;
        itsBondString = theBondString;
        itsMiddleBase = theMiddleBase;
        itsDg = theSeqDg + Helper.LoopEnergy(theNoOfBasesinLoop);
        is3PrimeEnd = theis3PrimeEnd;
        itsContBonds = theContBonds;
    }

    public String get_UpperFragment()
    {
        return String.valueOf(String.valueOf(itsUpperFragment.toString())).concat(" 5'");
    }

    public String get_LowerFragment()
    {
        return String.valueOf(String.valueOf(itsLowerFragment.toString())).concat(" 3'");
    }

    public String get_BondString()
    {
        return itsBondString;
    }

    public String get_MiddleBase()
    {
        return itsMiddleBase;
    }

    public double get_Dg()
    {
        return itsDg;
    }

    public boolean is3PrimeEnd()
    {
        return is3PrimeEnd;
    }

    public int get_ContBonds()
    {
        return itsContBonds;
    }

    public int compareTo(Object theObj)
    {
        Hairpin aHairpin = (Hairpin)theObj;
        return itsDg == aHairpin.itsDg ? 0 : itsDg < aHairpin.itsDg ? -1 : 1;
    }

    public String toString()
    {
        StringBuffer sb=new StringBuffer("DG:");
        sb.append(itsDg).append("\n").
        append(this.itsUpperFragment).append("\n").
        append(itsBondString).append("\n").
        append(itsLowerFragment);
        return sb.toString();
    }

    private String itsUpperFragment;
    private String itsLowerFragment;
    private String itsBondString;
    private String itsMiddleBase;
    private double itsDg;
    private boolean is3PrimeEnd;
    private int itsContBonds;
}
// Decompiled by DJ v3.5.5.77 Copyright 2003 Atanas Neshkov  Date: 25.09.2003 09:25:04
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Hairpin.java

package netprimer;


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
        itsDg = theSeqDg + LoopEnergy(theNoOfBasesinLoop);
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

    private static double LoopEnergy(int NoOfBases)
    {
        switch(NoOfBases)
        {
        case 3: // '\003'
            return 5.2000000000000002D;

        case 4: // '\004'
            return 4.5D;

        case 5: // '\005'
            return 4.4000000000000004D;

        case 6: // '\006'
            return 4.2999999999999998D;

        case 7: // '\007'
            return 4.0999999999999996D;

        case 8: // '\b'
            return 4.0999999999999996D;

        case 9: // '\t'
            return 4.2000000000000002D;

        case 10: // '\n'
            return 4.2999999999999998D;

        case 11: // '\013'
            return 4.5D;

        case 12: // '\f'
            return 4.9000000000000004D;

        case 13: // '\r'
            return 5.2000000000000002D;

        case 14: // '\016'
            return 5.5999999999999996D;

        case 15: // '\017'
            return 5.7999999999999998D;

        case 16: // '\020'
            return 6.0999999999999996D;

        case 17: // '\021'
            return 6.4000000000000004D;

        case 18: // '\022'
            return 6.7000000000000002D;

        case 19: // '\023'
            return 6.9000000000000004D;

        case 20: // '\024'
            return 7.0999999999999996D;

        case 21: // '\025'
            return 7.2999999999999998D;

        case 22: // '\026'
            return 7.5D;

        case 23: // '\027'
            return 7.7000000000000002D;

        case 24: // '\030'
            return 7.9000000000000004D;

        case 25: // '\031'
            return 8.0999999999999996D;

        case 26: // '\032'
            return 8.3000000000000007D;

        case 27: // '\033'
            return 8.4000000000000004D;

        case 28: // '\034'
            return 8.5999999999999996D;

        case 29: // '\035'
            return 8.8000000000000007D;

        case 30: // '\036'
            return 8.9000000000000004D;
        }
        if(NoOfBases > 30 && NoOfBases <= 35)
            return 9.0999999999999996D;
        if(NoOfBases > 35 && NoOfBases <= 40)
            return 9.6999999999999993D;
        if(NoOfBases > 40 && NoOfBases <= 45)
            return 10.199999999999999D;
        if(NoOfBases > 45 && NoOfBases <= 50)
            return 10.6D;
        if(NoOfBases > 50 && NoOfBases <= 55)
            return 11D;
        if(NoOfBases > 55 && NoOfBases <= 60)
            return 11.300000000000001D;
        if(NoOfBases > 60 && NoOfBases <= 65)
            return 11.5D;
        if(NoOfBases > 65 && NoOfBases <= 70)
            return 11.699999999999999D;
        if(NoOfBases > 70 && NoOfBases <= 80)
            return 11.9D;
        if(NoOfBases > 80 && NoOfBases <= 90)
            return 12.1D;
        if(NoOfBases > 90 && NoOfBases <= 100)
            return 12.300000000000001D;
        else
            return NoOfBases > 100 ? 12.300000000000001D : 0.0D;
    }

    public int compareTo(Object theObj)
    {
        Hairpin aHairpin = (Hairpin)theObj;
        return itsDg == aHairpin.itsDg ? 0 : itsDg < aHairpin.itsDg ? -1 : 1;
    }

    public String toString()
    {
        return "DG:".concat(String.valueOf(String.valueOf(itsDg)));
    }

    private String itsUpperFragment;
    private String itsLowerFragment;
    private String itsBondString;
    private String itsMiddleBase;
    private double itsDg;
    private boolean is3PrimeEnd;
    private int itsContBonds;
}
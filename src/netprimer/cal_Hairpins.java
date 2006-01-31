// Decompiled by DJ v3.5.5.77 Copyright 2003 Atanas Neshkov  Date: 25.09.2003 09:25:01
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   cal_Hairpins.java

package netprimer;

import java.util.Collection;
import java.util.Vector;

import biochemie.util.Helper;

// Referenced classes of package netprimer.primercore:
//            PrimerAnalysis, Hairpin

public class cal_Hairpins
{

    public static Collection getHairpins(CharSequence theSequence)
    {
        int i = 0;
        int k = 0;
        int blnk = 0;
        int breakchk = 0;
        int causebonds = 0;
        int loopstart = 0;
        int start = 0;
        int PrimerLength = theSequence.length();
        boolean notfound = true;
        StringBuffer SeqForDg = new StringBuffer(2 * theSequence.length() + 1);
        StringBuffer UpperFragment = new StringBuffer(2 * theSequence.length() + 1);
        StringBuffer LowerFragment = new StringBuffer(2 * theSequence.length() + 1);
        StringBuffer BondString = new StringBuffer(2 * theSequence.length() + 1);
        StringBuffer MiddleBase = new StringBuffer(" ");
        double SeqDg = 0.0D;
        int ContBonds = 0;
        Collection itsHairpinList = new Vector();
        for(blnk = 0; blnk < theSequence.length(); blnk++)
            BondString.append(' ');

        for(i = 3; i < PrimerLength - 3; i++)
        {
            for(int NotMidBonding = 0; NotMidBonding <= 1; NotMidBonding++)
            {
                UpperFragment = new StringBuffer(theSequence.toString().substring(0, (i - 1) + NotMidBonding + 1));
                UpperFragment.reverse();
                LowerFragment = new StringBuffer(theSequence.toString().substring(i + 1, theSequence.length()));
                SeqForDg = new StringBuffer(2 * theSequence.length() + 1);
                if(NotMidBonding == 1)
                    MiddleBase.setCharAt(0, ' ');
                else
                    MiddleBase.setCharAt(0, theSequence.charAt(i));
                if(NotMidBonding == 1)
                    start = 2;
                else
                    start = 1;
                for(k = start; k < UpperFragment.length() && k < LowerFragment.length() && notfound; k++)
                    if(UpperFragment.charAt(k) == Helper.complNucl(LowerFragment.charAt(k)))
                    {
                        if(++ContBonds >= 3)
                        {
                            if(ContBonds == 3 && k >= (LowerFragment.length() - 1 - 7) + 3)
                            {
                                notfound = false;
                                causebonds = 4;
                            }
                            if(ContBonds >= 3)
                            {
                                notfound = false;
                                causebonds = 4;
                            }
                            if(!notfound)
                            {
                                ContBonds = 0;
                                breakchk = 0;
                                for(int ik = 1; ik < UpperFragment.length() && ik < LowerFragment.length(); ik++)
                                    if(UpperFragment.charAt(ik) == Helper.complNucl(LowerFragment.charAt(ik)))
                                        if(ik > k - causebonds && (ik - breakchk == 1 || breakchk == 0))
                                        {
                                            if(++ContBonds == 1)
                                                loopstart = ik;
                                            if(NotMidBonding == 1 && ik == 1)
                                            {
                                                BondString.setCharAt(ik, '*');
                                            } else
                                            {
                                                BondString.setCharAt(ik, '|');
                                                SeqForDg.append(UpperFragment.charAt(ik));
                                            }
                                            breakchk = ik;
                                        } else
                                        {
                                            BondString.setCharAt(ik, '*');
                                        }

                                SeqForDg.reverse();
                                SeqDg = Helper.cal_dG_secondaryStruct(SeqForDg);
                                boolean is3PrimeEnd;
                                if(BondString.toString().lastIndexOf('|') >= LowerFragment.length() - 4)
                                {
                                    is3PrimeEnd = true;
                                    SeqDg--;
                                } else
                                {
                                    is3PrimeEnd = false;
                                }
                                int NoOfBases;
                                if(NotMidBonding == 1)
                                    NoOfBases = 2 * (loopstart + 1) - 2;
                                else
                                    NoOfBases = (2 * (loopstart + 1) + 1) - 2;
                                Hairpin hpin = new Hairpin(UpperFragment.toString(), LowerFragment.toString(), BondString.toString(), MiddleBase.toString(), SeqDg, is3PrimeEnd, ContBonds, NoOfBases);
                                if(hpin.get_Dg() < 0.0D)
                                    itsHairpinList.add(hpin);
                                ContBonds = 0;
                            }
                        }
                    } else
                    {
                        ContBonds = 0;
                    }

                ContBonds = 0;
                notfound = true;
                int lenofbondstring = BondString.length();
                for(blnk = 0; blnk < lenofbondstring; blnk++)
                    BondString.setCharAt(blnk, ' ');

                SeqForDg = null;
            }

        }
        return itsHairpinList;
    }
    public static void main(String[] args) {
        System.out.println(getHairpins("AAAAAACCCCCCAAAAAGGCGGG"));
        System.out.println(Helper.cal_dG_secondaryStruct("GGG")+Helper.LoopEnergy(10)-1);
        System.out.println(Helper.cal_dG_secondaryStruct("GGG")+Helper.LoopEnergy(9)-1);
        System.out.println(Helper.cal_dG_secondaryStruct("GGG")+Helper.LoopEnergy(8)-1);
        System.out.println();
        System.out.println(Helper.cal_dG_secondaryStruct("ACCGAA")+Helper.LoopEnergy(8)-1);
        System.out.println(Helper.cal_dG_secondaryStruct("TTCGGT")+Helper.LoopEnergy(8)-1);
    }
}
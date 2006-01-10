// Decompiled by DJ v3.5.5.77 Copyright 2003 Atanas Neshkov  Date: 25.09.2003 09:25:03
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   cal_Xdimers.java

package netprimer;

import java.util.Collection;
import java.util.Vector;

import biochemie.util.Helper;

// Referenced classes of package netprimer.primercore:
//            PrimerAnalysis, Xdimer

public class cal_Xdimers
{

    public static Collection cal_Xdimers(String theSensePrimer, String theAntiSensePrimer)
    {
        CharSequence SensePrimer = theSensePrimer;
        CharSequence AntiSensePrimer = Helper.revPrimer(theAntiSensePrimer);
        int SenseLength = SensePrimer.length();
        int AntiSenseLength = AntiSensePrimer.length();
        int ContBonds = 0;
        int maxLength = Math.max(SenseLength, AntiSenseLength) + 6;
        StringBuffer SeqForDg = new StringBuffer(2 * maxLength);
        int causeBonds = 0;
        StringBuffer UpperFragment = new StringBuffer(2 * maxLength);
        StringBuffer LowerFragment = new StringBuffer(2 * maxLength);
        StringBuffer BondString = new StringBuffer(2 * maxLength);
        boolean is3PrimeEnd = false;
        Collection itsXDimerList = new Vector();
        for(int blnk = 0; blnk < 2 * maxLength; blnk++)
        {
            UpperFragment.append(' ');
            LowerFragment.append(' ');
            BondString.append(' ');
        }

        boolean notfound = true;
        for(int i = -AntiSenseLength + 4; i <= SenseLength - 4; i++)
        {
            for(int j = Math.max(0, i); j < Math.min(SenseLength, AntiSenseLength + i) && notfound; j++)
                if(SensePrimer.charAt(j) == Helper.complNucl(AntiSensePrimer.charAt(-i + j)))
                {
                    if(++ContBonds >= 4 || ContBonds == 3 && j >= SensePrimer.length() - 3 || ContBonds == 3 && -i + j <= 5)
                    {
                        notfound = false;
                        if(ContBonds >= 4)
                            causeBonds = 4;
                        else
                            causeBonds = 3;
                        UpperFragment.setCharAt(Math.max(0, -i), '5');
                        UpperFragment.setCharAt(Math.max(1, -i + 1), '\'');
                        UpperFragment.setCharAt(Math.max(2, -i + 2), ' ');
                        int blnk = Math.max(3, -i + 3);
                        int ik;
                        for(ik = 0; ik < SenseLength; ik++)
                        {
                            UpperFragment.setCharAt(blnk, SensePrimer.charAt(ik));
                            blnk++;
                        }

                        UpperFragment.setCharAt(blnk, ' ');
                        UpperFragment.setCharAt(blnk + 1, '3');
                        UpperFragment.setCharAt(blnk + 2, '\'');
                        LowerFragment.setCharAt(Math.max(0, i), '3');
                        LowerFragment.setCharAt(Math.max(1, i + 1), '\'');
                        LowerFragment.setCharAt(Math.max(2, i + 2), ' ');
                        blnk = Math.max(3, i + 3);
                        for(ik = 0; ik < AntiSenseLength; ik++)
                        {
                            LowerFragment.setCharAt(blnk, AntiSensePrimer.charAt(ik));
                            blnk++;
                        }

                        LowerFragment.setCharAt(blnk, ' ');
                        LowerFragment.setCharAt(blnk + 1, '5');
                        LowerFragment.setCharAt(blnk + 2, '\'');
                        char ch = '|';
                        int ijkl = 0;
                        ContBonds = 0;
                        boolean bondcomplete = false;
                        ik = 0;
                        do
                        {
                            if("ACGT".indexOf(UpperFragment.charAt(ik)) != -1 && "ACGT".indexOf(LowerFragment.charAt(ik)) != -1)
                            {
                                if(UpperFragment.charAt(ik) == Helper.complNucl(LowerFragment.charAt(ik)))
                                {
                                    if(++ContBonds == causeBonds && !bondcomplete)
                                    {
                                        for(ijkl = ik - 1; ijkl > ik - causeBonds; ijkl--)
                                        {
                                            BondString.setCharAt(ijkl, '|');
                                            SeqForDg.append(UpperFragment.charAt(ijkl));
                                        }

                                        SeqForDg.reverse();
                                        for(ijkl = ik; UpperFragment.charAt(ijkl) != ' ' && LowerFragment.charAt(ijkl) != ' '; ijkl++)
                                            if(UpperFragment.charAt(ijkl) == Helper.complNucl(LowerFragment.charAt(ijkl)))
                                            {
                                                BondString.setCharAt(ijkl, ch);
                                                if(ch == '|')
                                                {
                                                    ContBonds++;
                                                    SeqForDg.append(UpperFragment.charAt(ijkl));
                                                }
                                            } else
                                            {
                                                ch = '*';
                                                BondString.setCharAt(ijkl, ' ');
                                            }

                                        bondcomplete = true;
                                        break;
                                    }
                                    BondString.setCharAt(ik, '*');
                                } else
                                {
                                    ContBonds = 0;
                                    BondString.setCharAt(ik, ' ');
                                }
                            } else
                            {
                                ContBonds = 0;
                                BondString.setCharAt(ik, ' ');
                            }
                            ik++;
                        } while(true);
                        double SeqDg = Helper.cal_dG_secondaryStruct(SeqForDg);
                        if(causeBonds == 3)
                        {
                            SeqDg--;
                            is3PrimeEnd = true;
                        } else
                        {
                            is3PrimeEnd = false;
                        }
                        itsXDimerList.add(new Xdimer(UpperFragment, LowerFragment, BondString, SeqDg, ContBonds, is3PrimeEnd));
                    }
                } else
                {
                    ContBonds = 0;
                }

            ContBonds = 0;
            notfound = true;
            for(int blnk = 0; blnk < 2 * maxLength; blnk++)
            {
                UpperFragment.setCharAt(blnk, ' ');
                LowerFragment.setCharAt(blnk, ' ');
                BondString.setCharAt(blnk, ' ');
            }

            SeqForDg = null;
            SeqForDg = new StringBuffer(2 * maxLength);
        }
        return itsXDimerList;
    }
}
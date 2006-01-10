// Decompiled by DJ v3.5.5.77 Copyright 2003 Atanas Neshkov  Date: 25.09.2003 09:25:00
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   cal_Dimers.java

package netprimer;

import java.util.Collection;
import java.util.Vector;

import biochemie.util.Helper;

// Referenced classes of package netprimer.primercore:
//            PrimerAnalysis, Dimer

public class cal_Dimers
{

    public static Collection cal_Dimers(String aPrimerSeq)
    {
        String compl = Helper.complPrimer(aPrimerSeq);
        String aRevComp = Helper.revPrimer(compl);
        String aRevSeq = Helper.revPrimer(aPrimerSeq);
        int ContBonds = 0;
        int i = 0;
        int j = 0;
        int aPrimerLength = aPrimerSeq.length();
        double dg = 0.0D;
        boolean is3PrimeEnd = false;
        int causeBonds1 = 0;
        int causeBonds2 = 0;
        Collection itsDimerList = new Vector();
        for(i = 0; i < aPrimerLength; i++)
        {
            int x = 0;
            int k = 0;
            if(i <= aPrimerLength - 4)
            {
                StringBuffer aUpperFragment = new StringBuffer(2 * aPrimerLength + 10);
                StringBuffer aLowerFragment = new StringBuffer(2 * aPrimerLength + 10);
                StringBuffer aBondString = new StringBuffer(2 * aPrimerLength + 10);
                ContBonds = 0;
                if(i != 0)
                    for(j = i; j <= aPrimerLength; j++)
                    {
                        if(j != aPrimerLength && aPrimerSeq.charAt(j) == compl.charAt(((aPrimerLength - j) + i) - 1))
                        {
                            ContBonds++;
                            continue;
                        }
                        if(ContBonds >= 4 || ContBonds == 3 && j >= aPrimerLength - 3 || ContBonds == 3 && -i + j <= 5)
                        {
                            for(x = 0; x <= 2 * aPrimerLength + 10; x++)
                            {
                                aUpperFragment.append(' ');
                                aLowerFragment.append(' ');
                                aBondString.append(' ');
                            }

                            k = 0;
                            aUpperFragment.setCharAt(k, '5');
                            aUpperFragment.setCharAt(++k, '\'');
                            x = 0;
                            for(k = 3; x < aPrimerSeq.length(); k++)
                            {
                                aUpperFragment.setCharAt(k, aPrimerSeq.charAt(x));
                                x++;
                            }

                            aUpperFragment.setCharAt(++k, ' ');
                            aUpperFragment.setCharAt(k, '3');
                            aUpperFragment.setCharAt(++k, '\'');
                            k = i;
                            aLowerFragment.setCharAt(k, '3');
                            aLowerFragment.setCharAt(++k, '\'');
                            k += 2;
                            for(x = 0; x < aPrimerSeq.length();)
                            {
                                aLowerFragment.setCharAt(k, aRevSeq.charAt(x));
                                x++;
                                k++;
                            }

                            aLowerFragment.setCharAt(++k, ' ');
                            aLowerFragment.setCharAt(k, '5');
                            aLowerFragment.setCharAt(++k, '\'');
                            String tempStr = aPrimerSeq.toString().substring(0, j);
                            dg = Helper.cal_dG_secondaryStruct(new StringBuffer(tempStr.substring(tempStr.length() - ContBonds)));
                            for(k = i; k < aPrimerLength; k++)
                                if(aPrimerSeq.charAt(k) == compl.charAt(((aPrimerLength - k) + i) - 1))
                                    aBondString.setCharAt(k + 3, '*');

                            x = 0;
                            if((x + j + 3) - ContBonds <= aLowerFragment.toString().lastIndexOf("3") + 6)
                                causeBonds2 = ContBonds;
                            else
                                causeBonds2 = 0;
                            for(x = 0; x < ContBonds; x++)
                                aBondString.setCharAt((x + j + 3) - ContBonds, '|');

                            if(aBondString.toString().lastIndexOf("|") >= aUpperFragment.toString().lastIndexOf("3") - 5)
                                causeBonds1 = ContBonds;
                            else
                                causeBonds1 = 0;
                            if(causeBonds1 != 0 || causeBonds2 != 0)
                            {
                                dg--;
                                is3PrimeEnd = true;
                            } else
                            {
                                is3PrimeEnd = false;
                            }
                            itsDimerList.add(new Dimer(aUpperFragment.toString(), aLowerFragment.toString(), aBondString.toString(), dg, is3PrimeEnd));
                            ContBonds = 0;
                            break;
                        }
                        ContBonds = 0;
                    }

            }
            ContBonds = 0;
            if(i > 2 && i <= aPrimerLength - 1)
            {
                StringBuffer aUpperFragment2 = new StringBuffer(2 * aPrimerLength + 10);
                StringBuffer aLowerFragment2 = new StringBuffer(2 * aPrimerLength + 10);
                StringBuffer aBondString2 = new StringBuffer(2 * aPrimerLength + 10);
                for(j = 0; j <= i; j++)
                {
                    if(aPrimerSeq.charAt(j) == aRevComp.charAt((aPrimerLength - i - 1) + j))
                        ContBonds++;
                    if((j != i || aPrimerSeq.charAt(j) != aRevComp.charAt((aPrimerLength - i - 1) + j)) && aPrimerSeq.charAt(j) == aRevComp.charAt((aPrimerLength - i - 1) + j))
                        continue;
                    if(j == i && aPrimerSeq.charAt(j) == aRevComp.charAt((aPrimerLength - i - 1) + j))
                        j++;
                    if(ContBonds >= 4 || aPrimerLength - i - 1 >= 0 && ((aPrimerLength - i - 1) + j) - 4 <= 2 && ContBonds == 3)
                    {
                        for(x = 0; x <= 2 * aPrimerLength + 10; x++)
                        {
                            aUpperFragment2.append(' ');
                            aLowerFragment2.append(' ');
                            aBondString2.append(' ');
                        }

                        k = aPrimerLength - i - 1;
                        aUpperFragment2.setCharAt(k, '5');
                        aUpperFragment2.setCharAt(++k, '\'');
                        k += 2;
                        for(x = 0; x < aPrimerSeq.length();)
                        {
                            aUpperFragment2.setCharAt(k, aPrimerSeq.charAt(x));
                            x++;
                            k++;
                        }

                        aUpperFragment2.setCharAt(++k, ' ');
                        aUpperFragment2.setCharAt(k, '3');
                        aUpperFragment2.setCharAt(++k, '\'');
                        k = 0;
                        aLowerFragment2.setCharAt(k, '3');
                        aLowerFragment2.setCharAt(++k, '\'');
                        x = 0;
                        for(k = 3; x < aPrimerSeq.length(); k++)
                        {
                            aLowerFragment2.setCharAt(k, aRevSeq.charAt(x));
                            x++;
                        }

                        aLowerFragment2.setCharAt(++k, ' ');
                        aLowerFragment2.setCharAt(k, '5');
                        aLowerFragment2.setCharAt(++k, '\'');
                        String tempStr = aPrimerSeq.toString().substring(0, j);
                        dg = Helper.cal_dG_secondaryStruct(new StringBuffer(tempStr.substring(tempStr.length() - ContBonds)));
                        for(k = 0; k <= i; k++)
                            if(aPrimerSeq.charAt(k) == aRevComp.charAt((aPrimerLength - i - 1) + k))
                                aBondString2.setCharAt((aPrimerLength - i - 1) + k + 3, '*');

                        x = 0;
                        if((((x + aPrimerLength) - i - 1) + j + 3) - ContBonds <= aLowerFragment2.toString().lastIndexOf("3") + 6)
                            causeBonds2 = ContBonds;
                        else
                            causeBonds2 = 0;
                        for(x = 0; x < ContBonds; x++)
                            aBondString2.setCharAt((((x + aPrimerLength) - i - 1) + j + 3) - ContBonds, '|');

                        if(aBondString2.toString().lastIndexOf("|") >= aUpperFragment2.toString().lastIndexOf("3") - 5)
                            causeBonds1 = ContBonds;
                        else
                            causeBonds1 = 0;
                        if(causeBonds2 != 0 || causeBonds1 != 0)
                        {
                            dg--;
                            is3PrimeEnd = true;
                        } else
                        {
                            is3PrimeEnd = false;
                        }
                        itsDimerList.add(new Dimer(aUpperFragment2.toString(), aLowerFragment2.toString(), aBondString2.toString(), dg, is3PrimeEnd));
                        ContBonds = 0;
                        break;
                    }
                    ContBonds = 0;
                }

            }
        }
        return itsDimerList;
    }
}
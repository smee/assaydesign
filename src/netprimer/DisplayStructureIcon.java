// Decompiled by DJ v3.5.5.77 Copyright 2003 Atanas Neshkov  Date: 25.09.2003 09:23:19
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   DisplayStructure.java

package netprimer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;

import biochemie.domspec.SekStruktur;

// Referenced classes of package netprimer:
//            NetPrimerMain

public class DisplayStructureIcon
    implements Icon
{
    public static final Font PLAINCOURIER12 = new Font("Courier", 0, 12);
    static String deltaOrd = new String("\u0394");
    
    public DisplayStructureIcon(Collection strucs, int type)
    {
        strucType = type;
        itsStructList = strucs;
        itsStructCount = strucs.size();
        itsIconHeight=itsStructCount * 80;
    }

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        Dimer aDimer = null;
        Hairpin aHairPin = null;
        Xdimer aXDimer = null;
        String blank = " ";
        String astr = " ";
        FontMetrics fm = g.getFontMetrics(PLAINCOURIER12);
        int h = fm.getHeight();
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        int xPos = 20;
        int yPos = 20;
        int index = 1;
        g.setFont(PLAINCOURIER12);
        if(itsStructList.size() != 0)
        {
            for(Iterator it=itsStructList.iterator();it.hasNext();)
            {
                if(strucType == SekStruktur.HAIRPIN)
                {
                    aHairPin = (Hairpin)it.next();
                    if(aHairPin.is3PrimeEnd())
                        astr = "(3' Hairpin)";
                    else
                        astr = "";
                    g.drawString(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(Integer.toString(index))))).append(".").append(blank).append(deltaOrd).append("G = ").append(nf.format(aHairPin.get_Dg())).append(" kcal/mol  ").append(astr))), xPos - 5, yPos);
                    yPos += h;
                    yPos = PaintSecondaryStructure.DisplayHairPin(g, xPos, yPos, aHairPin, PLAINCOURIER12);
                }
                if(strucType == SekStruktur.HOMODIMER)
                {
                    aDimer = (Dimer)it.next();
                    if(aDimer.is3PrimeEnd())
                        astr = "(3' Dimer)";
                    else
                        astr = "";
                    g.drawString(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(Integer.toString(index))))).append(".").append(blank).append(deltaOrd).append("G = ").append(nf.format(aDimer.get_Dg())).append(" kcal/mol  ").append(astr))), xPos - 5, yPos);
                    yPos += h;
                    yPos = PaintSecondaryStructure.DisplayDimer(g, xPos, yPos, aDimer, PLAINCOURIER12);
                }
                if(strucType == SekStruktur.CROSSDIMER)
                {
                    aXDimer = (Xdimer)it.next();
                    if(aXDimer.is3PrimeEnd())
                        astr = "(3' Cross Dimer)";
                    else
                        astr = "";
                    g.drawString(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(Integer.toString(index))))).append(".").append(blank).append(deltaOrd).append("G = ").append(nf.format(aXDimer.get_Dg())).append(" kcal/mol  ").append(astr))), xPos - 5, yPos);
                    yPos += h;
                    yPos = PaintSecondaryStructure.DisplayXDimer(g, xPos, yPos, aXDimer, PLAINCOURIER12);
                }
                yPos += 15;
                index++;
            }

            itsIconHeight = yPos;
        }
    }

    public int getIconWidth()
    {
        return 1080;
    }

    public int getIconHeight()
    {
        int yPos = 20;
        int h = 17;
        for(int i = 0; i < itsStructCount; i++)
        {
            yPos += h;
            yPos += h / 5;
            yPos = (int)((double)yPos + ((double)h * 0.90000000000000002D + (double)h));
            yPos += h;
            yPos += 15;
        }

        if(itsIconHeight == 0)
            return 150;
        else
            return yPos;
    }

    private Graphics g;
    private Color color;
    private int itsIconHeight;
    private int strucType;
    private Collection itsStructList;
    private int itsStructCount;
}
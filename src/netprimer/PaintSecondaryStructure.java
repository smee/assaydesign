// Decompiled by DJ v3.5.5.77 Copyright 2003 Atanas Neshkov  Date: 25.09.2003 09:25:06
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PaintSecondaryStructure.java

package netprimer;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

// Referenced classes of package netprimer.primercore:
//            Dimer, Hairpin, Xdimer, Repeat_Run, 
//            Palindrome

public class PaintSecondaryStructure
{

    public PaintSecondaryStructure()
    {
    }

    public static int DisplayDimer(Graphics g, int xPos, int yPos, Dimer theDimer, Font thefont)
    {
        FontMetrics fm = g.getFontMetrics(thefont);
        int h = fm.getHeight();
        int w = fm.charWidth('A');
        g.setFont(thefont);
        int abondlen = h;
        xPos += w;
        g.drawString(theDimer.get_UpperFragment(), xPos + w, yPos);
        yPos += h / 5;
        drawBonds(g, xPos + w + w / 2, yPos, theDimer.get_BondString(), thefont);
        yPos = (int)((double)yPos + ((double)h * 0.90000000000000002D + (double)abondlen));
        g.drawString(theDimer.get_LowerFragment(), xPos + w, yPos);
        yPos += h;
        return yPos;
    }

    public static int DisplayHairPin(Graphics g, int xPos, int yPos, Hairpin theHairPin, Font thefont)
    {
        FontMetrics fm = g.getFontMetrics(thefont);
        int h = fm.getHeight();
        int w = fm.charWidth('A');
        g.setFont(thefont);
        xPos += w;
        int abondlen = h;
        g.drawString(theHairPin.get_UpperFragment().toString(), xPos + w, yPos);
        g.drawLine(xPos + w / 2, yPos - 3, xPos + w * 1, yPos - 3);
        g.drawLine(xPos + w / 2, yPos - 3, xPos + w / 2, yPos + (int)((double)h * 0.47999999999999998D));
        g.drawString(theHairPin.get_MiddleBase(), xPos, yPos + (int)((double)h * 1.2D));
        if(theHairPin.get_MiddleBase().compareTo(" ") == 1)
            g.drawLine(xPos + w / 2, yPos + (int)((double)h * 0.47999999999999998D), xPos + w / 2, yPos + (int)((double)h * 1.8D));
        else
            g.drawLine(xPos + w / 2, yPos + (int)((double)h * 1.3D), xPos + w / 2, yPos + (int)((double)h * 1.8D));
        g.drawLine(xPos + w / 2, yPos + (int)((double)h * 1.8D), xPos + w * 1, yPos + (int)((double)h * 1.8D));
        yPos += h / 5;
        drawBonds(g, xPos + w + w / 2, yPos, theHairPin.get_BondString(), thefont);
        yPos = (int)((double)yPos + ((double)h * 0.90000000000000002D + (double)abondlen));
        g.drawString(theHairPin.get_LowerFragment().toString(), xPos + w, yPos);
        yPos += h;
        return yPos;
    }

    public static int DisplayXDimer(Graphics g, int xPos, int yPos, Xdimer theXDimer, Font thefont)
    {
        FontMetrics fm = g.getFontMetrics(thefont);
        int h = fm.getHeight();
        int w = fm.charWidth('G');
        g.setFont(thefont);
        xPos += w;
        int abondlen = h;
        g.drawString(theXDimer.get_UpperFragment(), xPos + w, yPos);
        yPos += h / 5;
        drawBonds(g, xPos + w + w / 2, yPos, theXDimer.get_BondString(), thefont);
        yPos = (int)((double)yPos + ((double)h * 0.90000000000000002D + (double)abondlen));
        g.drawString(theXDimer.get_LowerFragment(), xPos + w, yPos);
        yPos += h;
        return yPos;
    }

    private static void drawBonds(Graphics g, int xPos, int yPos, String bondString, Font thefont)
    {
        FontMetrics fm = g.getFontMetrics(thefont);
        int w = fm.charWidth('G');
        int h = fm.getHeight();
        g.setFont(thefont);
        int x1 = xPos;
        int y1 = yPos;
        int abondlen = h;
        for(int k = 0; k < bondString.length();)
        {
            if(bondString.charAt(k) == '|')
                g.drawLine(x1, y1, x1, y1 + abondlen);
            if(bondString.charAt(k) == '*')
            {
                g.drawLine(x1, y1, x1, y1 + abondlen / 3);
                g.drawLine(x1, y1 + (2 * abondlen) / 3, x1, y1 + abondlen);
            }
            k++;
            x1 += w;
        }

    }

}
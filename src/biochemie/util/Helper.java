/*
 * Created on 03.09.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package biochemie.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;





/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Helper {


    public static int[] clone(int[]arr) {
        if(null == arr)
            return null;
        int[] newarr=new int[arr.length];
        System.arraycopy(arr, 0, newarr, 0, arr.length);
        return newarr;
    }
    public static double[] clone(double[]arr) {
        if(null == arr)
            return null;
        double[] newarr=new double[arr.length];
        System.arraycopy(arr, 0, newarr, 0, arr.length);
        return newarr;
    }
    public static float[] clone(float[]arr) {
        if(null == arr)
            return null;
        float[] newarr=new float[arr.length];
        System.arraycopy(arr, 0, newarr, 0, arr.length);
        return newarr;
    }
    /**
     * Gibt komplement&auml;ren Primer zur&uuml;ck.
     * @param primer
     * @return
     */
    public static String complPrimer(String primer) {
        StringBuffer rev= new StringBuffer(primer);
        char letter;
        for (int i= 0; i < rev.length(); i++) {

            switch (rev.charAt(i)) {
                case 'A' :
                case 'a' :
                    letter= 'T';
                    break;
                case 'C' :
                case 'c' :
                    letter= 'G';
                    break;
                case 'G' :
                case 'g' :
                    letter= 'C';
                    break;
                case 'T' :
                case 't' :
                    letter= 'A';
                    break;

                default :
                    letter= Character.toUpperCase(rev.charAt(i));
                    break;
            }
            rev.setCharAt(i, letter);
        }
        return rev.toString();
    }
    /**
     * Gibt revers-komplement&auml;ren Primer zurück.
     * @param primer
     * @return
     */
    public static String revcomplPrimer(String primer) {
        return complPrimer(revPrimer(primer));
    }
    public static String dateFunc() {
        Calendar calendar= Calendar.getInstance();
        String YY= (calendar.get(Calendar.YEAR) + "");
        String MM= ((calendar.get(Calendar.MONTH) + 1) + "");
        String DD= (calendar.get(Calendar.DAY_OF_MONTH) + "");
        int hour= calendar.get(Calendar.HOUR);
        String MI= (calendar.get(Calendar.MINUTE) + "");
        String SEC= (calendar.get(Calendar.SECOND) + "");
        int ampm= calendar.get(Calendar.AM_PM);
        if (Calendar.PM == ampm) {
            hour += 12;
        }
        String HH= Integer.toString(hour);
        if (1 == MM.length()) {
            MM= '0' + MM;
        }
        if (1 == DD.length()) {
            DD= '0' + DD;
        }
        if (1 == HH.length()) {
            HH= '0' + HH;
        }
        if (1 == MI.length()) {
            MI= '0' + MI;
        }
        if (1 == SEC.length()) {
            SEC= '0' + SEC;
        }
        return (HH + '-' + MI + '-' + SEC + '_' + DD + '-' + MM + '-' + YY);
    }

    /**
     *
     * @param primer
     * @param i von 3' aus gerechnet mit 0-Index
     * @param windowsize
     */
    public static String outputHairpin(String primer, int i, int windowsize) {
            StringBuffer sb= new StringBuffer();
        try {
            int start= primer.length() - i - 1;
            int breakPos= (primer.length() - start) / 2 + start - 1;

            sb.append(primer.substring(0, breakPos + 1));

            sb.append("-|\n");
            for (int j= 0; j < start; j++)
                sb.append(' ');
            String complp= Helper.complPrimer(primer);

            int complindex= complp.length() - 1;
            for (int j= start; j < start + windowsize && j < primer.length(); j++, complindex--) {
                if (primer.charAt(j) == complp.charAt(complindex) && isNukleotid(primer.charAt(j)))
                    sb.append('|');
                else
                    sb.append(' ');
            }
            sb.append('\n');
            for (int j= 0; j < start; j++)
                sb.append(' ');
            sb.append(new StringBuffer(primer.substring(breakPos + 1)).reverse().toString());
            sb.append("-|");
        } catch (StringIndexOutOfBoundsException e) {}
        return new String(sb);
    }
    /**
     * Erwartet zwei Primer in 5'-3' Ausrichtung, reverst dann den zweiten und zeigt Matches an.
     * @param primer
     * @param primer2
     * @param pos anzahl der leerzeichen, die der zweite primer verschoben werden muss, damit es matcht
     */
    public static String outputXDimer(String primer, String primer2, int pos, int windowsize) {
            StringBuffer sb= new StringBuffer();
            /*
             * Es kann sein, dass  pos negativ ist, dann muss die erste Zeile auch nach rechts verschoben werden.
             */
            if(pos < 0 ) {
                sb.append(StringUtils.repeat(" ", Math.abs(pos)));
            }
            sb.append(primer);
            sb.append('\n');
            sb.append(StringUtils.repeat(" ",Math.abs(pos)));

            String rcPrimer= revcomplPrimer(primer2);

            for (int i= Math.max(pos,0); i < primer.length() && i - pos < rcPrimer.length(); i++) {
                if (primer.charAt(i) == rcPrimer.charAt(i - pos) && isNukleotid(primer.charAt(i)))
                    sb.append('|');
                else
                    sb.append(' ');
            }
            sb.append('\n');
            sb.append(StringUtils.repeat(" ",pos));
            sb.append(revPrimer(primer2));
        return new String(sb);
    }
    /**
     * @param primer
     * @return
     */
    public static String revPrimer(String primer) {
        return new StringBuffer(primer).reverse().toString();
    }
    /**
     * Melting Temperature
     * The melting temperature is calculated using the formula based on the nearest neighbor
     * thermodynamic theory. It is the temperature at which half of the oligonucleotides are bonded.
     * The formula is from the paper by Freier et. al. These are the latest and most accurate nearest neighbor
     * based Tm calculations.
     * @param primer
     * @param monoval_ion Monovalent ion concentration - Default value = 50 mM.
     * @param freemg2 Free [Mg2+] ion concentration - Default value = 1.5 mM.
     * @return
     */
    public static double calcTM(String primer, double monoval_ion, double freemg2) {
        /* monoval_ion Monovalent ion concentration - Default value = 50 mM.
         * This preference should be set to the sum of the concentrations of all the monovalent ions
         * present in the reaction mixture.*/

        /* freemg2 Free [Mg2+] ion concentration - Default value = 1.5 mM.
         * This preference should be set to the concentration of Mg2+ ions, used as binders,
         * in a reaction mixture.*/
        if (null == primer || 0 == primer.length())
            return 0;
        primer=Helper.getNuklFromString(primer);//weil ich kenne nur ACGT
        /**
         * Nucleic acid concentration - Default value = 250 pM.
         * The value of nucleic acid concentration (C) is the concentration of the target sequence.
         * Since it keeps on changing empiricaly with the advent of reaction, its value is set virtually
         * based on the experimental data4 . It is therefore advised not to change this value unless
         * experimental conditions are very uncommon. This value is used for calculating melting
         * temperature of primer.
         */
        double C= 2.5E-10;
        /**
         * R is molar gas constant (1.987 cal/Grad C * mol)
         */
        double R= 1.987;
        /**
         * Total [Na+] equivalent
         * This value is calculated using the value of the Monovalent ion concentration and Free [Mg2+] ion concentration.
         * The formula for calculating Total [Na+] equivalent is as follows:
         * Total [Na+] equivalent = [Monovalent ion concentration] + 4 X sqrt (Free [Mg2+] ion concentration X 1000)
         * This value is used as salt concentration for calculating the melting temperature of the primer.
         */
        double K= (monoval_ion + 4 * Math.sqrt(freemg2 * 1000)) / 1000;
        /**
         * This is the entropy of the primer as calculated by the nearest neighbor method of Breslauer,
         */
        double dS= calcDeltaS(primer);
        /**
         * This is the enthalpy of the primer as calculated by the nearest neighbor method of Breslauer, K.J. et. al.
         */
        double dH= calcDeltaH(primer);
        //System.out.println("R+...="+(R*Math.log(C/4)));
        //System.out.println(".../(dS+...)="+(dS+R*Math.log(C/4)));
        //System.out.println("16.6*...="+(16.6*Math.log(K/(1+0.7*K))/2.3026-273.15)); funzt!
        double tm= dH / (dS + R * Math.log(C / 4)) + 16.6 * Math.log(K / (1 + 0.7 * K)) / 2.3026 - 273.15;
        return tm;
    }
    /**
     * dH is enthalpy for helix formation.
     * dH for a pentamer is calculated as follows:
     * dH(ATGCA) = dH(AT) + dH(TG) + dH(GC) + dH(CA)
     * The individual values of dH for nucleotide pairs are taken from the table given in Appendix A.
     * Example: Say the primer sequence is ATCGATACGTAG. Its dH will be
     * (8600 + 5600 + 11900 + 5600 + 8600 + 6000 + 6500 + 11900 + 6500 + 6000 + 7800) = -85000 cal/mol
     * = -85 kcal/mol.
     */
    private static double calcDeltaH(String primer) {
        double[][] lookup= { { 9100, 6500, 7800, 8600 }, {
                5800, 11000, 11900, 7800 }, {
                5600, 11100, 11000, 6500 }, {
                6000, 5600, 5800, 9100 }
        };
        double dH= 0;
        int x= 0, y= 0;
        for (int i= 0; i < primer.length() - 1; i++) {
            x= "ACGT".indexOf(primer.charAt(i));
            if (-1 == x)
                x= "acgt".indexOf(primer.charAt(i));
            y= "ACGT".indexOf(primer.charAt(i + 1));
            if (-1 == y)
                y= "acgt".indexOf(primer.charAt(i + 1));
            dH += lookup[x][y];
        }
        return -dH;
    }
    /**
     * sS is entropy for helix formation.
     * z.B. dS(ATGCA) = dS(AT) + dS(TG) + dS(GC) + dS(CA)
     * An initiation value of 15.1 is added to the dS calculation. The individual values of dS
     * for nucleotide pairs are taken from the table given in Appendix A.
     * Example: Say the primer sequence is ATCGATACGTAG. Its dS will be
     * (23.9 + 13.5 + 27.8 + 13.5 + 23.9 + 16.9 + 17.3 + 27.8 + 17.3 + 16.9 + 20.8) + 15.1 = -234.7 cal/K/mol
     * = -0.23 kcal/K/mol.
     */
    private static double calcDeltaS(String primer) {
        double[][] lookup= { { 24, 17.3, 20.8, 23.9 }, {
                12.9, 26.6, 27.8, 20.8 }, {
                13.5, 26.7, 26.6, 17.3 }, {
                16.9, 13.5, 12.9, 24 }
        };
        double dS= 15.1;
        int x= 0, y= 0;
        for (int i= 0; i < primer.length() - 1; i++) {
            x= "ACGT".indexOf(primer.charAt(i));
            if (-1 == x)
                x= "acgt".indexOf(primer.charAt(i));
            y= "ACGT".indexOf(primer.charAt(i + 1));
            if (-1 == y)
                y= "acgt".indexOf(primer.charAt(i + 1));

            dS += lookup[x][y];
        }
        return -dS;
    }
    public static double calcTM(String primer) {
        return calcTM(primer, 50, 1.5);
    }
    public static String toString(int[] array) {
        if(null == array)
            return "null";
        if (0 == array.length) {
            return "[]";
        }
        StringBuffer sb= new StringBuffer(array.length * 2);
		sb.append('[');
        for (int i= 0; i < array.length; i++) {
            sb.append(array[i] + ";");
        }
		sb.deleteCharAt(sb.length()-1);
		sb.append(']');
        return sb.toString();
    }
    public static String toString(Object[] array) {
        if(null == array)
            return "null";
        if (0 == array.length) {
            return "[]";
        }
        StringBuffer sb= new StringBuffer();
		sb.append('[');
        for (int i= 0; i < array.length; i++) {
            sb.append(array[i] );
            sb.append("; ");
        }
		sb.append(']');
        return sb.toString();
    }
    public static String toStringln(Object[] array) {
        if(null == array)
            return "null";
        if (0 == array.length) {
            return "[]";
        }
        StringBuffer sb= new StringBuffer();
		sb.append('[');
        for (int i= 0; i < array.length; i++) {
            sb.append(array[i] );
            sb.append('\n');
        }
		sb.append(']');
        return sb.toString();
    }
    public static String toStringln(Collection list) {
        return toStringln(list.toArray(new Object[list.size()]));
    }
    public static String toString(double[] array) {
        if(null == array)
            return "null";
        if (0 == array.length) {
            return "[]";
        }
        StringBuffer sb= new StringBuffer(array.length * 2);
        sb.append('[');
        for (int i= 0; i < array.length; i++) {
            sb.append(array[i] + ";");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(']');
        return sb.toString();
    }
    /**
     * Returns true, if this Programm is running in a VM version 1.4 or higher
     * @return
     */
    public static boolean isJava14() {
        try {
            Class.forName("java.awt.HeadlessException");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }
    /**
     * Berechnet den prozentualen Anteil der Basen in in <code>nukl</code>.
     * Beispiel: <code>Helper.getXGehalt("ACGTggtTacg","cCgG");</code> für Berechung
     *  des GC-Gehaltes.
     * @param primer Sequenz, die untersucht wird.
     * @param nukl String, der Buchstaben enthält, deren prozentualer Anteil interessiert.
     * @return
     */
    public static double getXGehalt(String primer, String nukl) {
        int xCount= 0;
        for (int i= 0; i < primer.length(); i++)
            if (-1 != nukl.indexOf(primer.charAt(i)))
                xCount++;
        return ((double)xCount) / ((double)primer.length()) * 100;
    }

    /**
     * Test, ob das Nukleotid, welches vor dem Hairpin eingebaut wird, in SNP liegt oder nicht
     * @param primer
     * @param bruchstelle
     * @return true, wenn das einzubauende Nukleotid im SNP liegt
     */
    public static boolean isInkompatibleSekStruktur(String primer, int pos, String snp) {
        char einbau= sekundaerStrukturBautEin(primer, pos);//nimm das Nukleotid VOR der Position!
        return -1 != snp.indexOf(einbau);
    }
    /**
     * Liefert das Nukleotid, welches von einer SEkundärstruktur (HAirpin, Homodimer, Crossdimer)
     * eingebaut werden würde.
     * @param primer
     * @param bruchstelle
     * @return
     */
    public static char sekundaerStrukturBautEin(String primer, int pos) {
        if(0 == primer.length())
            throw new IllegalArgumentException("primer mustn't have length 0!");
        if(0 > pos || pos>=primer.length())
            throw new IllegalArgumentException("pos "+pos+" out of range!");
        return Helper.complPrimer("" + primer.charAt(primer.length() - pos-1)).charAt(0);
    }

    /**
     * Entfernt alles ausser den Nukleotiden (acgtACGT).
     * @param snp
     * @return
     */
    public static String getNuklFromString(String seq) {
        if(seq == null)
        	return "";
    	StringBuffer sb= new StringBuffer();
        String allnukl= "acgtACGT";
        for (int i= 0; i < seq.length(); i++) {
            if (-1 != allnukl.indexOf(seq.charAt(i)))
                sb.append(Character.toUpperCase(seq.charAt(i)));
        }
        return sb.toString();
    }


    /**
     * Liefert den groessten int-Wert in einem Array.
     * @param arr
     * @return
     */
    public static int findMaxIn(int[] arr) {
        if(null == arr || 0 == arr.length)
            throw new IllegalArgumentException("Array mustn't be null or empty!");
        int max=Integer.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            if(arr[i]>max)
                max=arr[i];
        }
        return max;
    }
    public static int findMinIn(int[] arr) {
        if(null == arr || 0 == arr.length)
            throw new IllegalArgumentException("Array mustn't be null or empty!");
        int min=Integer.MAX_VALUE;
        for (int i = 0; i < arr.length; i++) {
            if(arr[i]<min)
                min=arr[i];
        }
        return min;
    }
    /**
     * Durchsucht intArray nach einem Wert. Liefert dessen Index zurueck, wenn der Wert enthalten ist, sonst -1.
     * @param array
     * @param value
     * @return
     */
    public static int findIndexOf(int[] array, int value) {
        int i= -1;
        for (int j= 0; j < array.length; j++) {
            if (array[j] == value) {
                i= j;
                break;
            }
        }
        return i;
    }
    /**
     * Durchsucht Array nach einem Wert. Liefert dessen Index zurueck, wenn der Wert enthalten ist, sonst -1.
     * @param array
     * @param value
     * @return
     */
    public static Comparable findIndexOf(Comparable[] array, Comparable value) {
        Comparable val=null;
        for (int j= 0; j < array.length; j++) {
            if (array[j].equals(value)) {
                val=array[j];
                break;
            }
        }
        return val;
    }
    /**
     * Durchsucht Array nach einem Wert. Liefert dessen Index zurueck, wenn der Wert enthalten ist, sonst -1.
     * Liefert immer den ersten gefundenen Wert.
     * @param array
     * @param value
     * @return
     */
    public static Object findIndexOf(Object[] array, Object value, Comparator c) {
        Object val=null;
        for (int j= 0; j < array.length; j++) {
            if (0 == c.compare(array[j], value)) {
                val=array[j];
                break;
            }
        }
        return val;
    }


	public static void copyFile(File srcFile, File dstFile)	throws IOException {
	    BufferedInputStream in =
	        new BufferedInputStream(new FileInputStream(srcFile));
	    BufferedOutputStream out =
	        new BufferedOutputStream(new FileOutputStream(dstFile));
	    byte[] buffer = new byte[2048];
	    int count;

	    while (-1 != (count = in.read(buffer))) {
	        out.write(buffer, 0, count);
	    }

	    in.close();
	    out.close();
	}
	public static List toList(Object[] array) {
	    List l=new ArrayList(array.length);
	    for (int i = 0; i < array.length; i++) {
            l.add(array[i]);
        }
	    return l;
	}
    public static String readAllLines(String filename, boolean remainEndl) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			return readAllLines(br,remainEndl);
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	/**
	 * Liest alle Zeilen einer datei ein und speichert sie in einem String.
	 * @param br
	 * @param remainEndl laesst alle \n im String wenn true, sonst nicht
	 * @return null, wenn ein Fehler auftrat
	 */
	public static String readAllLines(BufferedReader br, boolean remainEndl) {
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			while((line = br.readLine()) != null){
				sb.append(line);
				if(remainEndl)
					sb.append('\n');
			}
		} catch (IOException e) {
			return null;
		}
		return new String(sb);
	}
	/**
	 * Zerlegt einen von Whitespaces getrenneten String von integern in ein int[].
	 * Alle werte, die keine gueltige Zahl sind, werden ignoriert. Null oder ein leerer String fuehren
	 * zu einem leeren Array als Rueckgabewert.
	 * @param s
	 */
	public static int[] tokenizeToInt(String s) {
		if(s == null || s.length() == 0)
			return new int[0];

		List l = new LinkedList();
		StringTokenizer st =new StringTokenizer(s);
		while(st.hasMoreTokens()){
			try{
				int val = Integer.parseInt(st.nextToken());
				l.add(new Integer(val));
			}catch (NumberFormatException e) {
			}
		}
		int[] br = new int[l.size()];
		int i=0;
		for (Iterator it = l.iterator(); it.hasNext();i++) {
			Integer integer = (Integer) it.next();
			br[i]=integer.intValue();
		}
		return br;
	}
	/**
	 * Zerlegt einen von Whitespaces getrenneten String von floats in ein float[]
	 * @param s
	 */
	public static float[] tokenizeToFloat(String s) {
		if(s == null || s.length() == 0)
			return new float[0];

		List l = new LinkedList();
		StringTokenizer st =new StringTokenizer(s);
		while(st.hasMoreTokens()){
			try{
				float val = Float.parseFloat(st.nextToken());
				l.add(new Float(val));
			}catch (NumberFormatException e) {
			}
		}
		float[] br = new float[l.size()];
		int i=0;
		for (Iterator it = l.iterator(); it.hasNext();i++) {
			Float f = (Float) it.next();
			br[i]=f.floatValue();
		}
		return br;
	}
	/**
	 * Zerlegt einen von Whitespaces getrenneten String von double in ein double[]
	 * @param s
	 */
	public static double[] tokenizeToDouble(String s) {
		if(s == null || s.length() == 0)
			return new double[0];

		List l = new LinkedList();
		StringTokenizer st =new StringTokenizer(s);
		while(st.hasMoreTokens()){
			try{
				double val = Double.parseDouble(st.nextToken());
				l.add(new Double(val));
			}catch (NumberFormatException e) {
			}
		}
		double[] br = new double[l.size()];
		int i=0;
		for (Iterator it = l.iterator(); it.hasNext();i++) {
			Double d = (Double) it.next();
			br[i]=d.doubleValue();
		}
		return br;
	}
    /**
     * @param c
     * @return
     */
    public static boolean isNukleotid(char c) {
        return "ACGTacgt".indexOf(c)!=-1;
    }
    /**
     * Replaces the nucleotide at the position bruchstelle (start at 3'end, 1-index) with L, if bruchstelle > 0
     * @param seq
     * @param bruchstelle
     * @return
     */
    public static String replacePL(String seq, int pos) {
        if(pos >0)
            return seq.substring(0,seq.length() - pos )+"L"+seq.substring(seq.length() - pos + 1);
        return seq;
    }
    public static int getPosOfPl(String seq) {
        int pos = seq.indexOf('L');
        if(pos == -1)
            return -1;
        return seq.length() - pos;
    }
    /**
     * Fuegt leerzeichen ein, damit Stringtokenizer funzt
     * @param string
     */
    public static String clearEmptyCSVEntries(String string) {
        int index=0,oldindex=0;

        StringBuffer sb=new StringBuffer(string.length()+10);
        if(';' == string.charAt(0))
            sb.append(' ');
        while(0 <= (index = string.indexOf(";;", oldindex))) {
            sb.append(string.substring(oldindex,index+1));
            sb.append(' ');
            oldindex=index+1;
        }
        sb.append(string.substring(oldindex));
        return sb.toString();
    }
}
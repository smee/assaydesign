/*
 * Created on 07.03.2004
 *
 */
package biochemie.calcdalton;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author Steffen
 *
 */
public class CalcDaltonNative extends CalcDalton {
    public int getMaxReachedDepth() {
        return getMaxReachedDepthNative();
    }
    /**
     * @return
     */
    private native int getMaxReachedDepthNative();
    
    private static boolean useNative=false;
    static{
        try{
            System.loadLibrary("calcd");
            useNative=true;
            if(CalcDaltonNative.debug)
                System.out.println("using dll routine.");
        }catch(UnsatisfiedLinkError e){
            if(CalcDaltonNative.debug)
                System.out.println("using java routine.");
            useNative=false;
        }
    }
    
    public CalcDaltonNative(int[] br, double[] abstaendeFrom, double[] abstaendeTo, double peaks, boolean overlap){
        super(br,abstaendeFrom,abstaendeTo,peaks,overlap);
        if(overlap)
            useNative=false; //noch nicht implementiert
        if(useNative)
            initNative(br,abstaendeFrom,abstaendeTo,peaks);
    }
    
    private native int[][] calcNative(String[][] sbedata, int[] fest);
    private native void initNative(int[] br, double[] abstaendeFrom, double[] abstaendeTo, double peaks);
    
    protected String[] makeColumn(String[] sbe, int ptr,int i,int bruch) {
        String[] Tabellendaten= new String[12];
        DecimalFormat df= new DecimalFormat("0.00",new DecimalFormatSymbols(Locale.US));
        double[] sbe_massen;
        if(useNative)
            sbe_massen=calcSBEMass(sbe,bruch);
        else{
            return super.makeColumn(sbe,ptr,i,bruch);
        }
        Tabellendaten[0]=
            sbe[0].substring(0, sbe[0].length() - bruch)
                + "[L]"
                + sbe[0].substring((sbe[0].length() - bruch) + 1);//das Nukleotid an dieser Stelle wird entfernt!
        Tabellendaten[1]= df.format(sbe_massen[0]);
        Tabellendaten[2]= (new Integer(bruch)).toString();
        String temp=sbe[0].substring((sbe[0].length() - bruch) + 1);//übriger Teil des Primers
        for (int c= 1; c < sbe.length; c++)
            switch (sbe[c].charAt(sbe[c].length() - 1)) {
                case 'A' :  
                    Tabellendaten[4]= "[L]" + temp + sbe[c];
                    Tabellendaten[5]= df.format(sbe_massen[c]);
                    break;
                case 'C' : 
                    Tabellendaten[6]= "[L]" + temp + sbe[c];
                    Tabellendaten[7]= df.format(sbe_massen[c]);
                    break;
                case 'G' : 
                    Tabellendaten[8]= "[L]" + temp + sbe[c];
                    Tabellendaten[9]= df.format(sbe_massen[c]);
                    break;
                case 'T' : 
                    Tabellendaten[10]= "[L]" + temp + sbe[c];
                    Tabellendaten[11]= df.format(sbe_massen[c]);
                    break;
            }
        return Tabellendaten;
    }
    public int[][] calc(String[][] sbeData, int[] fest){
        return calcNative(sbeData,fest);
    }
    public SBETable calc(String[][] sbeData, SBETable sbeTable, int[] fest) {
        if(useNative){
            int[][] erg= calcNative(sbeData,fest);
            for(int i=0;i<erg.length;i++){
                String[][] temptable=new String[sbeData.length][];
                    for(int j=0;j<sbeData.length;j++) {
                        temptable[j]=makeColumn(sbeData[j],j,erg[i][j],br[erg[i][j]]);
                    }
                    sbeTable.addTabelle(temptable);
            }

        }else
            super.calc(sbeData, sbeTable, fest);
        return sbeTable;
    }

    public void outputState() {
        System.out.println("CalcDaltonNative-State:\n-----------------");
        System.out.println("Peaks: "+peaks);
        System.out.println("Abstaende from: "+biochemie.util.Helper.toString(from));
        System.out.println("Abstaende to: "+biochemie.util.Helper.toString(to));
        System.out.println("Bruchstellen: "+biochemie.util.Helper.toString(br));
        System.out.println();
    }

}

/*

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package biochemie.calcdalton;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import biochemie.domspec.CleavablePrimer;
import biochemie.domspec.Primer;
import biochemie.sbe.calculators.Interruptible;
import biochemie.util.Helper;
public class CalcDalton implements Interruptible{
	protected int solutionSize;
    protected final boolean overlap;
    protected final double[] assaypeaks;
    protected double[] productpeaks;
    protected double[] to;
    protected double[] from;
	protected double[] toAbs;
	protected double[] fromAbs;
    
	private double[] verbMasseTo;
	private double[] verbMasseFrom;
    protected int[] br;
    protected int[] laufvar;
    final protected int brlen;
    protected int anzahlVarsNeeded=0;
    static boolean debug=false,progress=false;
    protected int maxreacheddepth=0;
    private double[][][] massenList;
    int anzahl_sbe=0;
    
    Thread calcThread;
    private Object result;
    final private Map primerMasses;
    final private Map addonMasses;
    final private double plMass;
    final private boolean allExtension;
    final private boolean halfMassForbidden;    

	public CalcDalton(int[] br, double[] abstaendeFrom, double[] abstaendeTo
					, double[] assaypeaks
					, double[] productpeaks
					, double[] verbMasseFrom, double[] verbMasseTo
					, boolean overlap
                    , boolean allExtensions
                    , boolean halfMassForbidden
                    , Map primerMasses
                    , Map addonMasses
                    , double plMass){
        this.primerMasses=primerMasses;
        this.addonMasses=addonMasses;
        this.plMass=plMass;
		this.br=Helper.clone(br);
        if(br==null || br.length==0)
            this.brlen=1;
        else
            this.brlen=br.length;
		this.from=Helper.clone(abstaendeFrom);
		this.to=Helper.clone(abstaendeTo);
		this.fromAbs=Helper.clone(abstaendeFrom);
		this.toAbs=Helper.clone(abstaendeTo);
		for (int i = 0; i < fromAbs.length; i++) {
			fromAbs[i]=Math.abs(fromAbs[i]);
			toAbs[i]=Math.abs(toAbs[i]);
			if(toAbs[i]<fromAbs[i]){
				double t=toAbs[i];
				toAbs[i]=fromAbs[i];
				fromAbs[i]=t;
			}
		}
		this.verbMasseFrom=Helper.clone(verbMasseFrom);
		this.verbMasseTo=Helper.clone(verbMasseTo);		
		if(assaypeaks==null || assaypeaks.length<3)
            throw new IllegalArgumentException("array for assaypeaks needs to have exactly 3 values!");
        this.assaypeaks=assaypeaks;
		this.productpeaks=productpeaks;
		this.overlap=overlap;
		this.halfMassForbidden=halfMassForbidden;
        this.allExtension=allExtensions;
		solutionSize=Integer.MAX_VALUE;
		assert verbMasseFrom.length==verbMasseTo.length;
		assert abstaendeFrom.length==abstaendeTo.length;		
	}
    /**
     * @param cfg
     */
    public CalcDalton(CalcDaltonOptions c) {
        this(c.getPhotolinkerPositions()
                ,c.getCalcDaltonFrom()
                ,c.getCalcDaltonTo()
                ,c.getCalcDaltonAssayPeaks()
                ,c.getCalcDaltonProductPeaks()
                ,c.getCalcDaltonVerbFrom()
                ,c.getCalcDaltonVerbTo()
                ,c.isCalcDaltonAllowOverlap()
                ,c.isCalcDaltonAllExtensions()
                ,c.isCalcDaltonForbidHalfMasses()
                ,c.getCalcDaltonPrimerMassesMap()
                ,c.getCalcDaltonAddonMassesMap()
                ,c.getCalcDaltonPLMass());
    }

    public double[] getMasses(Primer primer) {
        String[] params=primer.getCDParamLine(false);
        if (primer instanceof CleavablePrimer) {
            return this.calcSBEMass(params,((CleavablePrimer)primer).getBruchstelle(),allExtension);
        }else
            return this.calcSBEMass(params,allExtension);
    }
	/**
	 * Berechnung der Masse einer einzelnen Sequenz.
	 * @param seq
	 * @return
	 */
    public double calcPrimerMasse(String seq) {
        double summe= 0.0D;
        if (null == seq)
            return 0.0D;
        for (int i= 0; i < seq.length(); i++) {
            Character c=new Character(Character.toUpperCase(seq.charAt(i)));
            if(primerMasses.keySet().contains(c))
                summe+=((Double)primerMasses.get(c)).doubleValue();
        }
        return summe;
    }
    
    public double calcPrimerMasseWithPL(String seq) {
        return calcPrimerMasse(seq)+plMass;//masse des pl
    }
	/**
	 * Berechnung der Masse einer Sequenz mit angehängtem Nukleotid.
	 * @param seq
	 * @param addon
	 * @return
	 */
	public double calcPrimerAddonMasse(double mass, String addon) {
		if (null == addon)
			return mass;
		for (int i= 0; i < addon.length(); i++) {
            Character c=new Character(Character.toUpperCase(addon.charAt(i)));
			if(addonMasses.keySet().contains(c))
                mass+=((Double)addonMasses.get(c)).doubleValue();
		}
		return mass;//die 18.02 sind schon drauf von calcPrimerMass!
	}

	/**
	 * Berechnung aller Massen einer SBE. Dabei wird der String von hinten abgeschnitten,
	 * je nach dem Wert von bruch
	 * @param p1
	 * @param bruch
	 * @return
	 */
	public double[] calcSBEMass(String[] p1, int bruch,boolean allExtension) {
        if(1 > p1.length)
            return new double[0];
		String temp=p1[0];
		p1[0]=temp.substring((temp.length() - bruch) + 1);
        double[] res=calcSBEMass(p1,allExtension);
        p1[0]=temp;
        return res;
	}
    public double[] calcSBEMass(String[] p1, boolean allExtension) {
        if(1 > p1.length)
            return new double[0];
        List masses=new ArrayList();
        final double m=calcPrimerMasseWithPL(p1[0]);
        masses.add( new Double(m));
        for (int i= 1; i < p1.length; i++)
            if(allExtension || p1[i].charAt(0)!='>')
                 masses.add(new Double(calcPrimerAddonMasse(m, p1[i])));
        double[] ergebnis=new double[masses.size()];
        for (int i = 0; i < ergebnis.length; i++) {
            ergebnis[i]=((Double)masses.get(i)).doubleValue();
        }
        return ergebnis;
    }
    private boolean isMassForbidden(double m){
        for (int i = 0; i < verbMasseFrom.length; i++) {
            if(m>=verbMasseFrom[i] && m<=verbMasseTo[i])
                return true;
        }
        return false;
        
    }
    public boolean invalidMassesIn(double[] massen){
        int start=overlap?1:0;//wenn overlap an ist darf der primer in die verbotenen massebereiche fallen
        for (int j = start; j < massen.length; j++) {
            if(isMassForbidden(massen[j]))
                return true;
//            if(halfMassForbidden) //halbe massen fallen nicht in die verbotenen massebereiche
//                if(isMassForbidden(massen[j]/2.0d))
//                    return true;                    
        }
        return false;
    }
    public boolean invalidPeakDiffIn(double[] massen){
        double peak;
        for (int i = 0; i < massen.length; i++) {
            for (int j = i+1; j < massen.length; j++) {
                peak = getCurrentPeak(massen[i],massen[j],productpeaks);
                if(Math.abs(massen[i]-massen[j])<=peak)
                    return true;
            }
        }
        return false;
    }
    
    private boolean fitsAllPeakRules(double m1,double m2, double[] massesFrom, double[] massesTo, boolean useAbsDiff){
        double peak = getCurrentPeak(m1,m2,assaypeaks);
        double diff=m1 - m2;
        if(useAbsDiff)
            diff=Math.abs(diff);
        
        //forbidden peakdiffs
        for(int k=0;k<massesFrom.length;k++)
            if(diff>=massesFrom[k] && diff<=massesTo[k])
                return false;
        
        //min. peakdiff
        if(Math.abs(diff)<peak){
            return false;
        }
        return true;
    }
    /**
     * @param mass1 half of this mass mustn't fall into peak diff
     * @param mass2
     * @return
     */
    private boolean fitsHalfMassPeakRules(double mass1, double mass2) {
        if(halfMassForbidden)
            return Math.abs(mass1/2d - mass2)>=getCurrentPeak(mass1/2.0d,mass2,assaypeaks);
        else
            return true;
    }
    
	/**
	 * Test, ob berechnete SBEGewichte nach allen geforderten Kriterien unterschiedlich sind.
	 * Testet, ob keine in Config verlangten Abstände verletzt sind.
	 * @param p1_massen
	 * @param p2_massen
	 * @return
	 */
    protected boolean isDiffOkay(double[] p1_massen,double[] p2_massen) {
        for (int i= 0; i < p1_massen.length; i++) {
            for (int j= 0; j < p2_massen.length; j++) {
                if(!fitsAllPeakRules(p1_massen[i],p2_massen[j],fromAbs,toAbs,true) ||
                   !fitsHalfMassPeakRules(p1_massen[i],p2_massen[j]) ||
                   !fitsHalfMassPeakRules(p2_massen[j],p1_massen[i]))
                    return false;
            }
        }
        return true;
    }
    protected boolean isDiffOverlapOkay(double[] masses1,double[] masses2) {
         //vgl. sbe ohne anhang miteineander
         // -> fällt weg wegen is nich
         //vgl. sbe1 ohne anhang mit allen sbe2 mit anhang
         for (int i= 1; i < masses2.length; i++) {
             if(!fitsAllPeakRules(masses1[0],masses2[i],from,to,false) ||
                !fitsHalfMassPeakRules(masses1[0],masses2[i]))
                 return false;
         }
        //vgl. sbe2 ohne anhang mit allen sbe1 mit anhang
         for (int i= 1; i < masses1.length; i++) {
             if(!fitsAllPeakRules(masses1[i],masses2[0],from,to,false) ||
                     !fitsHalfMassPeakRules(masses1[i],masses2[0]))
                 return false;
         }
        //vgl. den rest miteinander
        for (int i= 1; i < masses1.length; i++) {
             for (int j= 1; j < masses2.length; j++) {
                 if(!fitsAllPeakRules(masses1[i],masses2[j],fromAbs,toAbs,true) ||
                    !fitsHalfMassPeakRules(masses1[i],masses2[j]) ||
                    !fitsHalfMassPeakRules(masses2[j],masses1[i]))
                     return false;
             }
         }
        return true;
    }
    /**
     * @param p2_massen
     * @param i
     * @return
     */
    private double getCurrentPeak(double m1,double m2, double[] peakSizes) {//wann welche???
        return (m1<peakSizes[1] || m2<peakSizes[1])?peakSizes[0]:peakSizes[2];
    }
	/**
	 * Gibt String[] zurück, der in einer Spalte alle Werte einer SBE enthält.
	 * @param sbe String[] mit Sequenz + Anhängen
	 * @param bruch berechnete Bruchstelle
	 * @return Tabellenspalte
	 */
    protected String[] makeColumn(String[] sbe,int bruch) {
		String[] Tabellendaten= new String[] {"","","","","","","",""};
		DecimalFormat df= new DecimalFormat("0.00",new DecimalFormatSymbols(Locale.US));
        double[] sbe_massen=calcSBEMass(sbe,bruch,true);
		Tabellendaten[0]=
			sbe[0].substring(0, sbe[0].length() - bruch)
				+ "[L]"
				+ sbe[0].substring((sbe[0].length() - bruch) + 1);//das Nukleotid an dieser Stelle wird entfernt!
		Tabellendaten[1]= df.format(sbe_massen[0]);
		Tabellendaten[2]= (new Integer(bruch)).toString();
        Tabellendaten[3]="";
		for (int c= 1; c < sbe.length; c++) {
            boolean useKlammern=sbe[c].charAt(0)=='>';
			switch (sbe[c].charAt(sbe[c].length() - 1)) {
				case 'A' :
                case 'a' :  
					Tabellendaten[4]= df.format(sbe_massen[c]);                  
                    if(useKlammern)
                        Tabellendaten[4]= "["+Tabellendaten[4]+"]";                  
					break;
				case 'C' : 
				case 'c' : 
					Tabellendaten[5]= df.format(sbe_massen[c]);
					if(useKlammern)
					    Tabellendaten[5]= "["+Tabellendaten[5]+"]";                  
					break;
				case 'G' : 
				case 'g' : 
					Tabellendaten[6]= df.format(sbe_massen[c]);
					if(useKlammern)
					    Tabellendaten[6]= "["+Tabellendaten[6]+"]";                  
					break;
				case 'T' : 
				case 't' : 
					Tabellendaten[7]= df.format(sbe_massen[c]);
					if(useKlammern)
					    Tabellendaten[7]= "["+Tabellendaten[7]+"]";                  
					break;
			}
        }
		return Tabellendaten;
	}

    public SBETable calc(String[][] sbeData,SBETable sbeTable, int[] fest) {//TODO verbotene massebereiche ausschaltbar!
//        System.out.println("------------------");
//        for (int i = 0; i < fest.length; i++) {
//          System.out.print(ArrayUtils.toString(sbeData[i]));
//          System.out.println(" "+fest[i]);
//        }
//        System.out.println("------------------");
        int[][] erg=calc(sbeData,fest);
        for(int i=0;i<erg.length;i++){
            int[] line=erg[i];
            String[][] temptable=new String[sbeData.length][];
            for(int k=0;k<sbeData.length;k++) {
                temptable[k]=makeColumn(sbeData[k],br[line[k]]);
            }
            sbeTable.addTabelle(temptable);
        }
        return sbeTable;   
    }
    public void calc(String[][] sbeData, SBETable sbetable) {
        int[] fest=new int[sbeData.length];
        Arrays.fill(fest,-1);
        calc(sbeData,sbetable,fest);
    }
    public int[][] calc(String[][] sbeData) {
        int[] fest=new int[sbeData.length];
        Arrays.fill(fest,-1);
        return calc(sbeData,fest);
    }
    /**
	 * Berechnung.
	 * @param paneldata Jede Zeile enthält Sequenz, Anhang1, Anhang 2... 
	 * @param sbetable tablemodel fuer ergebnisse
     * @param fest array mit indizes der festen bruchstellen, wenn egal, dann -1
	 */
    public int[][] calc(String[][] sbeData, int[] fest) {
        if(sbeData.length == 0 || fest.length != sbeData.length)
            return new int[0][];
        calcThread = Thread.currentThread();
        
        List erglist=new ArrayList();
        boolean[] brIstFest=new boolean[fest.length]; //Feld fuer feste Bruchstellen
        solutionSize=Integer.MAX_VALUE;
        anzahl_sbe=sbeData.length;        //wieviele SBE-Primer?
        //anzahlVarsNeeded=(int) Math.ceil(Math.log(brlen)/Math.log(10000));
        anzahlVarsNeeded=6 > anzahl_sbe?anzahl_sbe:6;
        /*Workaround für feste Bruchstelle:
         * ich muss sicherstellen, dass bei fester gewählter Bruchstelle die jeweilige
         * Schleife jeweils nur genau einmal durchlaufen wird. Deshalb wird flags[i] auf false
         * gesetzt, wenn die jeweilige Bruchstelle fest sein soll. Dann wird die zugehörige 
         * Schleife jeweils nur genau einmal durchlaufen.
         */
        for (int i= 0; i < brIstFest.length; i++) {
            if(-1 < fest[i]) {
                brIstFest[i]=true;
            }
            else {
                brIstFest[i]=false;
                fest[i]=0;//sonst ist die noch auf -1, also falscher Index
            }
            
        }
        /*-------------------------- hier gehts los------------------------------*/
        maxreacheddepth=-1;
        initializeMassen(sbeData);
        laufvar=new int[anzahl_sbe];//enthaelt k,l,m,...
        double[][] sbe_masse=new double[anzahl_sbe][];
        int ptr=0;//Laufvariable
        laufvar[0]=fest[0];
        int[][][][] checked=new int[anzahl_sbe][brlen][anzahl_sbe][brlen];
        do {
            boolean okay=true;
            sbe_masse[ptr]=getMassenArray(ptr,laufvar[ptr]);
            if(sbe_masse[ptr]==null){
                okay=false;
            }
            for(int i=0;i<ptr && okay;i++) {//vergleiche diese Masse mit allen drueber
                if(0 == checked[ptr][laufvar[ptr]][i][laufvar[i]]){//wurde noch nicht getestet
                    boolean flag;
                    if(!overlap) //count++;
                        flag=isDiffOkay(sbe_masse[i],sbe_masse[ptr]);
                    else
                        flag=isDiffOverlapOkay(sbe_masse[i],sbe_masse[ptr]);
                    if(!flag){
                        okay=false;
                        checked[ptr][laufvar[ptr]][i][laufvar[i]]=-1;
                    }else{
                        checked[ptr][laufvar[ptr]][i][laufvar[i]]=1;
                    }
                }else{//schon verglichen!
                    if(0 > checked[ptr][laufvar[ptr]][i][laufvar[i]]) {
                        okay=false;
                    }
                }
                
            }
            if(okay) {//passt alles bisher
                maxreacheddepth=(ptr>maxreacheddepth)?ptr:maxreacheddepth;
                if(ptr==anzahl_sbe-1) {//sind wir ganz unten? wenn ja -->tabelle bauen
                    int sum=sum(ptr);
                    if(sum<=solutionSize){
                        if(sum<solutionSize)
                            erglist.clear();
                        setSolutionSize();
                        if(CalcDalton.debug){ 
                            System.out.print("Lsg. new: "+biochemie.util.Helper.toString(laufvar));
                            System.out.println();
                        }
                        erglist.add(Helper.clone(laufvar));
                    }
                }else {//noch nicht ganz unten
                    ptr++;//also eins runter
                    laufvar[ptr]=fest[ptr];
                    continue;
                }
            }
            laufvar[ptr]++;
            if(laufvar[ptr]==brlen || brIstFest[ptr] || noSmallerSolutionPossible(ptr) || calcThread.isInterrupted()) {//bin in dieser zeile fertig oder feste br
                ptr--;          //und eins hoch
                while(-1 != ptr && (brIstFest[ptr] || laufvar[ptr]==brlen-1))//ueber alle festen und fertigen
                    ptr--;
                if(-1 == ptr) //abbruch, falls ganz oben
                    break;
                laufvar[ptr]++;
            }
        }while(laufvar[0]!=brlen);//wenn die erste Laufvariable auf brlen steht, simmer ferdsch
        int[][] erg=new int[erglist.size()][];
        for (int i= 0; i < erglist.size(); i++) {
            Object obj=erglist.get(i);
            erg[i]=(int[]) obj;
        }
        return erg;
    }
    /**
     * 
     */
    protected void setSolutionSize() {
        solutionSize=sum( laufvar.length-1);
    }

    /**
     * @return
     */
    protected boolean noSmallerSolutionPossible(int ptr) {
        int sum=sum(ptr);
        if(sum>solutionSize)
            return true;
        return false;
    }

    	protected int sum(int ptr){
            int sum=0;
            for (int i= 0; i <= ptr; i++) {
                sum+=laufvar[i];
            }
            return sum;
    	}
    protected void initializeMassen(String[][] sbedata){
        massenList=new double[sbedata.length][][];
        for(int i=0;i<sbedata.length;i++){
            massenList[i]=new double[brlen][];
        }
        for(int i=0;i<sbedata.length;i++){
            for(int j=0;j<brlen;j++){
                double[] massenArray=null;
                if(br == null || br.length == 0)
                    massenArray=calcSBEMass(sbedata[i],this.allExtension);
                else
                    massenArray=calcSBEMass(sbedata[i],br[j],this.allExtension);
                if(invalidMassesIn(massenArray)){
                    massenArray=null;
                }
                massenList[i][j]=massenArray;
            }
        }   
    }

    protected double[] getMassenArray(int ptr, int i) {
        //System.out.println("getMassenArray("+ptr+", "+i+"), has dims=["+massenList.length+","+massenList[ptr].length+"]");
        return massenList[ptr][i];
    }

    /**
     * Setze aktuellerWert automatisch.
     * @param indices Feld mit Werten der jeweiligen Indices
     */
    public synchronized int getAktuellerWert() {
        if(null == laufvar)
            return 0;
        int aktuellerWert=0;
        for(int i=0;i<anzahlVarsNeeded;i++) {
            aktuellerWert*=brlen;
            aktuellerWert+=(laufvar[i]);
        }
        return aktuellerWert;       
    }
    public int getMax() {
        int aktuellerWert=1;
        
        for(int i=0;i<anzahlVarsNeeded;i++) {
            aktuellerWert*=brlen;
            aktuellerWert+=brlen;
        }
        return aktuellerWert;
    }

    /**
     * @return
     */
    public int getMaxReachedDepth() {
        return maxreacheddepth;
    }
    //--------------- Interruptible ---------------------------------------
    private String[][] sbeData;
    private SBETable sbeTable;
    private int[] fest;
    public void setParameter(String[][] sbeData,SBETable sbeTable, int[] fest) {
        setParameter(sbeData, fest);
        this.sbeTable=sbeTable;
    }
    public void setParameter(String [][] sbeData, int[] fest) {
        this.sbeTable=null;
        this.sbeData=sbeData;
        this.fest=fest;        
    }
    /* (non-Javadoc)
     * @see biochemie.sbe.calculators.Interruptible#start()
     */
    public void start() {
        if(sbeTable == null) 
            result = calc(sbeData,fest);
        else
            result = calc(sbeData,sbeTable,fest);
    }
    /* (non-Javadoc)
     * @see biochemie.sbe.calculators.Interruptible#stop()
     */
    public void stop() {
        if(calcThread != null)
            calcThread.interrupt();
    }
    /* (non-Javadoc)
     * @see biochemie.sbe.calculators.Interruptible#getResult()
     */
    public Object getResult() {
        return result;
    }
}
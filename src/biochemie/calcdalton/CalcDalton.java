

package biochemie.calcdalton;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import biochemie.sbe.calculators.Interruptible;
import biochemie.util.Helper;
public class CalcDalton implements Interruptible{
	protected int solutionSize;
    protected final boolean overlap;
    protected double peaks;
    protected double[] to;
    protected double[] from;
	protected double[] toAbs;
	protected double[] fromAbs;
    
	private double[] verbMasseTo;
	private double[] verbMasseFrom;
    protected int[] br;
    protected int[] laufvar;
    protected int brlen;
    protected int anzahlVarsNeeded=0;
    static boolean debug=false,progress=false;
    protected int maxreacheddepth=0;
    private double[][][] massenList;
    int anzahl_sbe=0;
    
    Thread calcThread;
    private Object result;    
    /**
     * @param br
     * @param abstaendeFrom
     * @param abstaendeTo
     * @param peaks
     * @param b
     */
    public CalcDalton(int[] br, double[] abstaendeFrom, double[] abstaendeTo, double peaks, boolean overlap) {
		this(br,abstaendeFrom,abstaendeTo,peaks,new double[0],new double[0],overlap);
    }
	public CalcDalton(int[] br, double[] abstaendeFrom, double[] abstaendeTo
					, double peaks
					, double[] verbMasseFrom, double[] verbMasseTo
					, boolean overlap){
		this.br=Helper.clone(br);
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
		this.peaks=peaks;
		this.overlap=overlap;
		solutionSize=Integer.MAX_VALUE;
		assert verbMasseFrom.length==verbMasseTo.length;
		assert abstaendeFrom.length==abstaendeTo.length;		
	}
    /**
     * @param cfg
     */
    public CalcDalton(CalcDaltonOptions c) {
        this(c.getPhotolinkerPositions(),c.getCalcDaltonFrom(),c.getCalcDaltonTo(),c.getCalcDaltonPeaks(),c.getCalcDaltonVerbFrom(),c.getCalcDaltonVerbTo(),c.getCalcDaltonAllowOverlap());
    }
    public void outputState(){
       System.out.println("CalcDalton-State:\n-----------------");
       System.out.println("Peaks: "+peaks);
       System.out.println("Abstaende from: "+biochemie.util.Helper.toString(from));
       System.out.println("Abstaende to: "+biochemie.util.Helper.toString(to));
	   System.out.println("Verbotene Massen From: "+biochemie.util.Helper.toString(verbMasseFrom));
	   System.out.println("Verbotene Massen To: "+biochemie.util.Helper.toString(verbMasseTo));
       System.out.println("Bruchstellen: "+biochemie.util.Helper.toString(br));
       System.out.println();
    }    
	/**
	 * Berechnung der Masse einer einzelnen Sequenz.
	 * @param seq
	 * @return
	 */
	private static double calcPrimerMasse(String seq) {
		double summe= 0.0D;
		if (null == seq)
			return 0.0D;
		for (int i= 0; i < seq.length(); i++)
			switch (seq.charAt(i)) {
				case 'A' : 
                case 'a' :
					summe += 313.2071;
					break;
				case 'G' :
                case 'g' :  
					summe += 329.2066;
					break;
				case 'T' :
                case 't' :  
					summe += 304.1937;
					break;
				case 'C' :
                case 'c' :  
					summe += 289.1823;
					break;
			}
		return summe + 18.02D;//masse des pl
	}
    
	/**
	 * Berechnung der Masse einer Sequenz mit angehängtem Nukleotid.
	 * @param seq
	 * @param addon
	 * @return
	 */
	private static  double calcPrimerAddonMasse(String seq, String addon) {
		if (null == seq || null == addon)
			return 0.0D;
        double summe= calcPrimerMasse(seq);
		for (int i= 0; i < addon.length(); i++)
			switch (addon.charAt(i)) {
				case 'A' :
                case 'a' : 
					summe += 297.2072;
					break;
				case 'G' :
                case 'g' : 
					summe += 313.2066;
					break;
				case 'T' :
                case 't' : 
					summe += 288.1937;
					break;
				case 'C' : 
                case 'c' :
					summe += 273.1824;
					break;
			}
		return summe;//die 18.02 sind schon drauf von calcPrimerMass!
	}

	/**
	 * Berechnung aller Massen einer SBE. Dabei wird der String von hinten abgeschnitten,
	 * je nach dem Wert von bruch
	 * @param p1
	 * @param bruch
	 * @return
	 */
	public static double[] calcSBEMass(String[] p1, int bruch) {
        if(1 > p1.length)
            return new double[0];
		String temp= p1[0];//sonst wird wieder nur die Referenz übergeben
		p1[0]= temp.substring((p1[0].length() - bruch) + 1);
        double[] ergebnis= new double[p1.length];
        ergebnis[0]= calcPrimerMasse(p1[0]);
        for (int i= 1; i < p1.length; i++)
            ergebnis[i]= calcPrimerAddonMasse(p1[0], p1[i]);
		p1[0]= temp;
		return ergebnis;
	}

	private boolean invalidMassesIn(double[] massen){
		for (int i = 0; i < verbMasseFrom.length; i++) {
			for (int j = 0; j < massen.length; j++) {
				if(massen[j]>=verbMasseFrom[i] && massen[j]<=verbMasseTo[i])
					return true;
			}
		}
		return false;
	}
	/**
	 * Test, ob berechnete SBEGewichte nach allen geforderten Kriterien unterschiedlich sind.
	 * Testet, ob keine in Config verlangten Abstände verletzt sind.
	 * @param p1_massen
	 * @param p2_massen
	 * @return
	 */
    protected boolean isDiffOkay(double[] p1_massen,double[] p2_massen) {
        double temp;
        if(invalidMassesIn(p1_massen)||invalidMassesIn(p2_massen))
        	return false;
        for (int i= 0; i < p1_massen.length; i++) {
            for (int j= 0; j < p2_massen.length; j++) {
                temp=Math.abs(p1_massen[i] - p2_massen[j]);
                if(temp<peaks){
                    return false;
                }
                for(int k=0;k<from.length;k++) {
                    if(temp>=fromAbs[k] && temp<=toAbs[k]){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    protected boolean isDiffOverlapOkay(double[] p1_massen,double[] p2_massen) {
         double temp;
         //vgl. sbe ohne anhang miteineander
//        temp=Math.abs(p1_massen[0] - p2_massen[0]);
//        if(temp<peaks)
//            return false;
//        for(int k=0;k<from.length;k++) {
//            if(temp>=from[k] && temp<=to[k]) 
//                return false;
//        }
//fällt weg wegen is nich
         //vgl. sbe1 ohne anhang mit allen sbe2 mit anhang
		if(invalidMassesIn(p1_massen)||invalidMassesIn(p2_massen))
			return false;
         for (int i= 1; i < p2_massen.length; i++) {
             temp=p2_massen[i] - p1_massen[0];
             if(Math.abs(temp)<peaks)
                 return false;
             for(int k=0;k<from.length;k++) {
                 if(temp>=from[k] && temp<=to[k]) 
                     return false;
             }
         }
        //vgl. sbe2 ohne anhang mit allen sbe1 mit anhang
         for (int i= 1; i < p1_massen.length; i++) {
             temp=p1_massen[i] - p2_massen[0];
             if(Math.abs(temp)<peaks)
                 return false;
             for(int k=0;k<from.length;k++) {
                 if(temp>=from[k] && temp<=to[k]) 
                     return false;
             }
        }
        //vgl. den rest miteinander
        for (int i= 1; i < p1_massen.length; i++) {
             for (int j= 1; j < p2_massen.length; j++) {
                 temp=Math.abs(p1_massen[i] - p2_massen[j]);
                 if(temp<peaks)
                     return false;
                 for(int k=0;k<from.length;k++) {
                     if(temp>=fromAbs[k] && temp<=toAbs[k]) 
                         return false;
                 }
             }
         }
        return true;
    }
	/**
	 * Gibt String[] zurück, der in einer Spalte alle Werte einer SBE enthält.
	 * @param sbe String[] mit Sequenz + Anhängen
	 * @param bruch berechnete Bruchstelle
	 * @return Tabellenspalte
	 */
    protected String[] makeColumn(String[] sbe, int ptr,int i,int bruch) {
		String[] Tabellendaten= new String[8];
		DecimalFormat df= new DecimalFormat("0.00",new DecimalFormatSymbols(Locale.US));
        double[] sbe_massen;
        sbe_massen = getMassenArray(ptr,i);
		Tabellendaten[0]=
			sbe[0].substring(0, sbe[0].length() - bruch)
				+ "[L]"
				+ sbe[0].substring((sbe[0].length() - bruch) + 1);//das Nukleotid an dieser Stelle wird entfernt!
		Tabellendaten[1]= df.format(sbe_massen[0]);
		Tabellendaten[2]= (new Integer(bruch)).toString();
        Tabellendaten[3]="";
		for (int c= 1; c < sbe.length; c++)
			switch (sbe[c].charAt(sbe[c].length() - 1)) {
				case 'A' :  
					Tabellendaten[4]= df.format(sbe_massen[c]);
					break;
				case 'C' : 
					Tabellendaten[5]= df.format(sbe_massen[c]);
					break;
				case 'G' : 
					Tabellendaten[6]= df.format(sbe_massen[c]);
					break;
				case 'T' : 
					Tabellendaten[7]= df.format(sbe_massen[c]);
					break;
			}
		return Tabellendaten;
	}

    public SBETable calc(String[][] sbeData,SBETable sbeTable, int[] fest) {
        int[][] erg=calc(sbeData,fest);
        for(int i=0;i<erg.length;i++){
            int[] line=erg[i];
            String[][] temptable=new String[sbeData.length][];
            for(int k=0;k<sbeData.length;k++) {
                temptable[k]=makeColumn(sbeData[k],k,line[k],br[line[k]]);
            }
            sbeTable.addTabelle(temptable);
        }
        return sbeTable;   
    }
    
   
    /**
	 * Berechnung.
	 * @param paneldata Jede Zeile enthält Sequenz, Anhang1, Anhang 2... 
	 * @param sbetable tablemodel fuer ergebnisse
     * @param fest array mit indizes der festen bruchstellen, wenn egal, dann -1
	 */
	public int[][] calc(String[][] sbeData, int[] fest) {
        calcThread = Thread.currentThread();
        
        List erglist=new ArrayList();
        boolean[] brIstFest=new boolean[fest.length]; //Feld fuer feste Bruchstellen
        solutionSize=Integer.MAX_VALUE;
		anzahl_sbe=fest.length;        //wieviele SBE-Primer?
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
        maxreacheddepth=0;
        initializeMassen(sbeData);
		if(1 == sbeData.length) {
			erglist.add(new int[]{fest[0]});
		}
		else {
            laufvar=new int[anzahl_sbe];//enthaelt k,l,m,...
            double[][] sbe_masse=new double[anzahl_sbe][];
            int ptr=0;//Laufvariable
            laufvar[0]=fest[0];
            int[][][][] checked=new int[anzahl_sbe][brlen][anzahl_sbe][brlen];
            do {
                sbe_masse[ptr]=getMassenArray(ptr,laufvar[ptr]);
                boolean okay=true;
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
	}
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
            massenList[i]=new double[br.length][];
        }
        for(int i=0;i<sbedata.length;i++){
            for(int j=0;j<br.length;j++){
                double[] massenArray=null;
                massenArray=calcSBEMass(sbedata[i],br[j]);
                massenList[i][j]=massenArray;
            }
        }   
    }

    protected double[] getMassenArray(int ptr, int i) {
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
    //--------------- Interrubtible ---------------------------------------
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
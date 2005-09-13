package biochemie.calcdalton;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import biochemie.util.Helper;
/*
 * Created on 01.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SBETable implements TableModel {
	private final int anzahlSBE;
	private int aktuelleTabelle=0;
 	private final String[] columnNames;
	private final String[] ersteSpalte;

    int[] br;
    int[] tablebruchstellen;
    List ergebnisse;
    
    
public SBETable(String[] sbeNames,int[] br) {
	this.anzahlSBE=sbeNames.length; 
    ergebnisse=new ArrayList();
    this.br=br;     
    tablebruchstellen=new int[anzahlSBE];
	initTableBruchstellen();
	columnNames=new String[anzahlSBE+1];	
	columnNames[0]="";
    if(null == sbeNames) {
        sbeNames=new String[anzahlSBE];
        for(int i=0;i<sbeNames.length;i++)
            sbeNames[i]="SBE "+i;
    }
    System.arraycopy(sbeNames, 0, columnNames, 1, sbeNames.length);
	ersteSpalte=new String[9];
    ersteSpalte[0]="ID:";
	ersteSpalte[1]="SBE-Primer:";
	ersteSpalte[2]="Molecular weight (unextended ):";
	ersteSpalte[3]="Cleavable linker (3'):";
	ersteSpalte[4]="";
	ersteSpalte[5]="SBE+A:";
	ersteSpalte[6]="SBE+C:";
	ersteSpalte[7]="SBE+G:";
	ersteSpalte[8]="SBE+T:";
}


private void initTableBruchstellen() {
	for (int i= 0; i < tablebruchstellen.length; i++) {
	    tablebruchstellen[i]=Integer.MAX_VALUE/1000;
	}
}


	/**
	 * Neue ergebnistabelle hinzufügen
	 * @param table
	 */
	public void addTabelle(String[][] table) {
        int[] tempbruchstellen=new int[table.length];
        for(int i=0;i<table.length;i++) {
            tempbruchstellen[i]=getIndexOf(Integer.parseInt(table[i][2]));
        }
        addTabelle(table,tempbruchstellen);
	}
    private int getIndexOf(int i) {
        for (int j= 0; j < br.length; j++) {
            if(br[j]==i)
                return j;
        }
        return -1;//darf nicht vorkommen!
    }


    public void addTabelle(String[][] table,int[] laufvar) {
        int  num;
        if(0 <= (num = this.compareSolutionSizeTo(laufvar))){
            tablebruchstellen=Helper.clone(laufvar);
            if(0 < num)//lösche alle bisherigen tabellen
                ergebnisse.clear();
            ergebnisse.add(table);
        }
    }
	private String[][] getTabelle(int index) {
        if(0 > index || index>=ergebnisse.size())
            return null;
        return (String[][])ergebnisse.get(index);
	}
    /**
     * Von CalcDalton gefundene Lösungen
     * @return
     */
	public int getNumberOfSolutions() {
		return ergebnisse.size();
	}

	public void nextLoesung() {
		if(1 == getNumberOfSolutions())
			return;
		if(aktuelleTabelle<getNumberOfSolutions()-1)
			aktuelleTabelle++;
		else aktuelleTabelle=0;
	}

	public void previousLoesung() {
		if(1 == getNumberOfSolutions())
			return;
		if(0 < aktuelleTabelle)
			aktuelleTabelle--;
		else aktuelleTabelle=getNumberOfSolutions()-1;
	}

	public int getIndex() {
		return aktuelleTabelle;
	}

	public int getRowCount() {
		return 9;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
        //return null;
    }

	public Class getColumnClass(int columnIndex) {
		return String.class;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	/**
     * 
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(0 == columnIndex)
			return ersteSpalte[rowIndex];
        if(0 == rowIndex)
            return columnNames[columnIndex];
		String res= getTabelle(aktuelleTabelle)[columnIndex-1][rowIndex-1].trim();
//		System.out.println("accessing ["+rowIndex+","+columnIndex+"] of table ["+getColumnCount()+","+getRowCount()+"]. Value: "+res);
        return res;
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(aValue instanceof String) {
			getTabelle(aktuelleTabelle)[columnIndex-1][rowIndex]=(String)aValue;
		}
	}
	
	public void addTableModelListener(TableModelListener l) {
		}
	
	public void removeTableModelListener(TableModelListener l) {
		}
    public String toString() {
        return getNumberOfSolutions()+" Loesungen";
    }


    /**
     * Vergleicht aktuelle Loesungsklasse mit neuer angebotener Loesung. Rueckgabe wie compareTo()
     * @param laufvar Int-Feld mit Laufvariablen
     * @return 0, wenn gleich, >0, wenn bisherige Loesung schlechter (groesser) als angebotene
     */
    public int compareSolutionSizeTo(int[] laufvar) {
        if(laufvar.length!=anzahlSBE)
            return -1;
        int sum=0;
        for (int i= 0; i < tablebruchstellen.length; i++) {
            sum+=tablebruchstellen[i];
            sum-=laufvar[i];
        }
        return sum;
    }
    public int getMaxLengthOfColumn(int i){
        if(i<anzahlSBE+1){
            if(0 == i){
                return ersteSpalte[3].length()*8;//laengster String in der ersten Spalte
            }else{
                return getValueAt(1,i).toString().length()*8;//geschaetzte Laenge des primers in pixeln
            }
        }
        return 0;
    }

	public void removeAllSolutions() {
		ergebnisse.clear();
		initTableBruchstellen();
	}


    /**
     * @return
     */
    public String[] getNames() {
        String[] n=new String[anzahlSBE];
        System.arraycopy(columnNames,1,n,0,anzahlSBE);
        return n;
    }


    /**
     * Array der Massen einer Spalte. Enthaelt alle 5 moeglichen Werte, auch wenn weniger verwendet wurden!
     * @param i
     * @return
     */
    public double[] getMassenOfColumn(int i) {
        double[] m = new double[5];
        String val = extractDouble((String) getValueAt(2,i));
        if(val !=null)
            m[0]=Double.parseDouble(val);
        val=extractDouble((String) getValueAt(5,i));
        if(val !=null)
            m[1]=Double.parseDouble(val);
        val=extractDouble((String) getValueAt(6,i));
        if(val !=null)
            m[2]=Double.parseDouble(val);
        val=extractDouble((String) getValueAt(7,i));
        if(val !=null)
            m[3]=Double.parseDouble(val);
        val=extractDouble((String) getValueAt(8,i));
        if(val !=null)
            m[4]=Double.parseDouble(val);
        return m;
    }


    /**
     * @param val
     * @return
     */
    private String extractDouble(String val) {
        if(val == null || val.length()==0)
            return null;
        if(val.charAt(0)=='[' && val.charAt(val.length()-1)==']')
            val=val.substring(1,val.length()-1);
        return val;
    }


    /**
     * String, der alle Nukl. enthaelt, die eingebaut wurden, also irgendwas zwischen "" und "ACGT"
     * @param i
     * @return
     */
    public String getAnbauOfColumn(int i) {
        String anbau="";
        if(getValueAt(5,i)!=null)
            anbau+="A";
        if(getValueAt(6,i)!=null)
            anbau+="C";
        if(getValueAt(7,i)!=null)
            anbau+="G";
        if(getValueAt(8,i)!=null)
            anbau+="T";
        return anbau;
    }
}

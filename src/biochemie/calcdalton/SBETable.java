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
	private int anzahlSBE;
	private int aktuelleTabelle=0;
 	private String[] columnNames;
	private String[] ersteSpalte;

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
	ersteSpalte[3]="Photolinker position (3’):";
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
		//return columnNames[columnIndex];
        return null;
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
		return getTabelle(aktuelleTabelle)[columnIndex-1][rowIndex-1];
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
    public int getMaxLengthColumn(int i){
        if(i<anzahlSBE+1){
            if(0 == i){
                return ersteSpalte[3].length()*8;//laengster String in der ersten Spalte
            }else{
                return getValueAt(1,i).toString().length()*8;
            }
        }
        return 0;
    }

	public void removeAllSolutions() {
		ergebnisse.clear();
		initTableBruchstellen();
	}
}

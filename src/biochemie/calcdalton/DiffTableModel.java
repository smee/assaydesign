/*
 * Created on 16.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package biochemie.calcdalton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;



/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DiffTableModel implements TableModel{
	ArrayList firstcolumn;
	HashMap hm;
	
public DiffTableModel(SBETable sbetable) {
		this(sbetable.getNames(),getWeightsFromTable(sbetable));//! SpaltenxZeilen!
}
/**
 * 
 * @param names
 * @param weights
 */
public DiffTableModel(String[] names, double[][] weights) {    
		initTable(names, weights);		
}

	/**
 * @param names
 * @param weights
 */
private void initTable(String[] names, double[][] weights) {
    hm=new HashMap();
    if(names.length == 1) {
        hm.put(names[0],new Double(weights[0][0]));
        hm.put("+A",new Double(weights[0][1]));
        hm.put("+C",new Double(weights[0][2]));
        hm.put("+G",new Double(weights[0][3]));
        hm.put("+T",new Double(weights[0][4]));
        firstcolumn=new ArrayList();
        firstcolumn.add(names[0]);
        firstcolumn.add("+A");
        firstcolumn.add("+C");
        firstcolumn.add("+G");
        firstcolumn.add("+T");
    }
        for (int i = 0; i < weights.length; i++) {
            for (int j = i + 1; j < weights.length; j++) {
                for (int k = 0; k < weights[i].length; k++) {
                    for (int l = 0; l < weights[j].length; l++) {
                        double d1 = weights[i][k];
                        double d2 = weights[j][l];
                        if (0 != d1 && 0 != d2)
                            hm.put(getNameFor(i, j, k, l, names), new Double(d1 - d2));
                    }
                }
            }
        }
    
    firstcolumn=new ArrayList(hm.keySet());
        Collections.sort(firstcolumn,new Comparator(){
            public int compare(Object o1, Object o2) {
                double d1=((Double) hm.get(o1)).doubleValue();
                double d2=((Double) hm.get(o2)).doubleValue();
                d1*=(0 > d1)?-1:1;
                d2*=(0 > d2)?-1:1;
                return Double.compare(d1,d2);
            }           
        });
}

    private String getNameFor(int i, int j, int k,int l,String[] names) {
		StringBuffer name=new StringBuffer(names[i]);
		switch (k) {
			case 1 :
				name.append("+A");
				break;
			case 2 :
				name.append("+C");
				break;
			case 3 :
				name.append("+G");
				break;
			case 4 :
				name.append("+T");
				break;				
			default :
				break;
		}
		name.append(" - ");
		name.append(names[j]);
		switch (l) {
			case 1 :
				name.append("+A");
				break;
			case 2 :
				name.append("+C");
				break;
			case 3 :
				name.append("+G");
				break;
			case 4 :
				name.append("+T");
				break;				
			default :
				break;
		}		
		return name.toString();
	}

	/**
	 * @param sbetable
	 * @return
	 */
	public static double[][] getWeightsFromTable(SBETable sbetable) {
		int cc=sbetable.getColumnCount()-1;//die erste Zeile enthält keine Werte
		double[][] erg=new double[cc][5];
		for (int j = 0; j < cc; j++) {
			Object entry=sbetable.getValueAt(2,j+1);
			if(null == entry)
				erg[j][0]=0;
			else
				erg[j][0]=Double.parseDouble(entry.toString());
		}
		for (int j = 0; j < cc; j++) {
			for (int i = 1; i < erg[j].length; i++) {
				Object entry=sbetable.getValueAt(i+4,j+1);
				if(null == entry)
					erg[j][i]=0;
				else
					erg[j][i]=Double.parseDouble(entry.toString());
			}
		}
		return erg;
	}

	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return firstcolumn.size();
	}
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if(0 == columnIndex)
			return firstcolumn.get(rowIndex);
		else
			return hm.get(firstcolumn.get(rowIndex));
	}
	public String getColumnName(int columnIndex) {
		return null;
	}


	public Class getColumnClass(int columnIndex) {
		if(0 == columnIndex)
			return String.class;
		return Double.class;
	}


	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	
	}


	public void addTableModelListener(TableModelListener l) {

	}


	public void removeTableModelListener(TableModelListener l) {

	}
}

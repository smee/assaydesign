package biochemie.calcdalton.gui;
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
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
public class CDMassesConfigPanel extends JPanel {

	private JLabel jLabel = null;
	private PBSequenceField plMassTf = null;
	private JTable primerMassTable = null;
	private JScrollPane jScrollPane = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JTable anhangMassTable = null;
	private JScrollPane jScrollPane1 = null;
    final private Map primerMap;
    final private Map anhangMap;
    private double plMass;
    /**
     * This is the default constructor
     */
    public CDMassesConfigPanel(Map primerMap, Map anhangMap, double plMass) {
    	super();
        this.primerMap=primerMap;
        this.anhangMap=anhangMap;
        this.plMass=plMass;
    	initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private  void initialize() {
    	jLabel2 = new JLabel();
    	jLabel1 = new JLabel();
    	GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
    	GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
    	GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
    	jLabel = new JLabel();
    	GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
    	GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
    	GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
    	this.setLayout(new GridBagLayout());
    	this.setSize(457, 354);
    	gridBagConstraints2.gridx = 0;
    	gridBagConstraints2.gridy = 0;
    	gridBagConstraints2.insets = new java.awt.Insets(10,10,10,10);
    	jLabel.setText("Mass of the photolinker");
    	gridBagConstraints3.gridx = 1;
    	gridBagConstraints3.gridy = 0;
    	gridBagConstraints3.weightx = 1.0;
    	gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
    	gridBagConstraints3.insets = new java.awt.Insets(10,10,10,10);
    	gridBagConstraints4.gridx = 0;
    	gridBagConstraints4.gridy = 2;
    	gridBagConstraints4.weightx = 1.0;
    	gridBagConstraints4.weighty = 1.0;
    	gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
    	gridBagConstraints4.gridwidth = 2;
    	gridBagConstraints4.insets = new java.awt.Insets(10,10,10,10);
    	gridBagConstraints6.gridx = 0;
    	gridBagConstraints6.gridy = 1;
    	gridBagConstraints6.insets = new java.awt.Insets(10,10,10,10);
    	gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
    	gridBagConstraints6.gridwidth = 2;
    	jLabel1.setText("Masses of primernucleotides:");
    	gridBagConstraints7.gridx = 0;
    	gridBagConstraints7.gridy = 3;
    	gridBagConstraints7.insets = new java.awt.Insets(10,10,10,10);
    	gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
    	gridBagConstraints7.gridwidth = 2;
    	jLabel2.setText("Masses of Anhang:");
    	gridBagConstraints8.gridx = 0;
    	gridBagConstraints8.gridy = 4;
    	gridBagConstraints8.weightx = 1.0;
    	gridBagConstraints8.weighty = 1.0;
    	gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
    	gridBagConstraints8.gridwidth = 2;
    	gridBagConstraints8.insets = new java.awt.Insets(10,10,10,10);
    	this.add(jLabel, gridBagConstraints2);
    	this.add(getPlMassTf(), gridBagConstraints3);
    	this.add(getJScrollPane(), gridBagConstraints4);
    	this.add(jLabel1, gridBagConstraints6);
    	this.add(jLabel2, gridBagConstraints7);
    	this.add(getJScrollPane1(), gridBagConstraints8);
    }

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private PBSequenceField getPlMassTf() {
		if (plMassTf == null) {
			plMassTf = new PBSequenceField(10,false,PBSequenceField.NUMBERS);
            plMassTf.setUniqueChars(".");
            plMassTf.setText(Double.toString(plMass));
		}
		return plMassTf;
	}
	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */    
	private JTable getPrimerMassTable() {
		if (primerMassTable == null) {
			primerMassTable = new CDMassesTable();
            primerMassTable.setModel(new CDMassesTableModel(primerMap));
		}
		return primerMassTable;
	}

    /**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getPrimerMassTable());
		}
		return jScrollPane;
	}
        static class CDMassesTable extends JTable{
        public CDMassesTable() {
            super(new CDMassesTableModel(new HashMap()));
            setPreferredScrollableViewportSize(getPreferredSize());
        }
        public void setModel(TableModel m) {
            super.setModel(m);
            setPreferredScrollableViewportSize(getPreferredSize());
        }
        public Class getColumnClass(int column) {
            switch (column) {
            case 0:
                return Character.class;
            case 1:
                return Double.class;
            default:
                return Object.class;
            }
        }
    }
    static class CDMassesTableModel extends AbstractTableModel{
        final private Map map;
        final private List keys;
        
        public CDMassesTableModel(Map map) {
            this.map=new HashMap(map);
            this.keys=new ArrayList(map.keySet());
            Collections.sort(keys);
        }
        
        public int getColumnCount() {
            return 2;
        }
        public String getColumnName(int column) {
            switch (column) {
            case 0:
                return "Character (i.e. nucleotides)";
            case 1:
                return "Mass in dalton";
            default:
                return null;
            }
        }
        public int getRowCount() {
            return map.size();
        }
        public Object getValueAt(int row, int column) {
            Object key=keys.get(row);
            if(column==0)
                return key;
            return map.get(key);
        }
        public boolean isCellEditable(int row, int column) {
            return true;
        }
        public void setValueAt(Object aValue, int row, int column) {
            Object orgKey=keys.get(row);
            if(column==0) {//new key
                map.put(aValue,map.get(orgKey));
                map.remove(orgKey);
            }else {//new value
                map.put(orgKey,aValue);
            }            
        }
    }
	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */    
	private JTable getAnhangMassTable() {
		if (anhangMassTable == null) {
			anhangMassTable = new CDMassesTable();
            anhangMassTable.setModel(new CDMassesTableModel(anhangMap));
		}
		return anhangMassTable;
	}
	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getAnhangMassTable());
		}
		return jScrollPane1;
	}
    public static void main(String[] args) {
        JFrame f=new JFrame();
        Map primerMap=new HashMap();
        primerMap.put(Character.valueOf('A'),Double.valueOf(313.2071));
        primerMap.put(Character.valueOf('G'),Double.valueOf(329.2066));
        primerMap.put(Character.valueOf('C'),Double.valueOf(289.1823));
        primerMap.put(Character.valueOf('T'),Double.valueOf(304.1937));
        Map anhangMap=new HashMap();
        anhangMap.put(Character.valueOf('A'),Double.valueOf(297.2072));
        anhangMap.put(Character.valueOf('G'),Double.valueOf(313.2066));
        anhangMap.put(Character.valueOf('C'),Double.valueOf(273.1824));
        anhangMap.put(Character.valueOf('T'),Double.valueOf(288.1937));
        f.getContentPane().add(new CDMassesConfigPanel(primerMap, anhangMap,18.02));
        f.pack();
        f.setVisible(true);
    }
     }  //  @jve:decl-index=0:visual-constraint="10,10"

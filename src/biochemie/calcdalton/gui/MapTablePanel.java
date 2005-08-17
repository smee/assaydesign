/*
 * Created on 17.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.calcdalton.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MapTablePanel extends JPanel {

	class CDMassesTableModel extends AbstractTableModel{
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
                return col0name;
            case 1:
                return col1name;
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
            fireTableRowsInserted(row,row);
        }

        public Map getMap() {
            return new HashMap(map);
        }
    }
    class CDMassesTable extends JTable{
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
    private JLabel title = null;
	private JLabel keyLabel = null;
	private JLabel valueLabel = null;
	private JTextField valueTf = null;
	private JTextField keyTf = null;
	private JTable mapTable = null;
	private JScrollPane jScrollPane = null;
    final private String col0name;
    final private String col1name;
    private String keyLabelString;
    private String valLabel;
	private JButton addButton = null;
	/**
	 * This is the default constructor
	 */
	public MapTablePanel() {
		this("Key","Value","Key:","Value:");
	}
	public MapTablePanel(String col0name, String col1name, String keyLabel, String valLabel) {
	    this.col0name=col0name;
	    this.col1name=col1name;
	    this.keyLabelString=keyLabel;
	    this.valLabel=valLabel;
        initialize();
        
    }
    /**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		valueLabel = new JLabel();
		keyLabel = new JLabel();
		title = new JLabel();
		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(300,200);
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 0;
		gridBagConstraints3.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints3.gridwidth = 4;
		title.setText("JLabel");
		gridBagConstraints9.gridx = 4;
		gridBagConstraints9.gridy = 2;
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.gridy = 2;
		gridBagConstraints4.insets = new java.awt.Insets(10,10,10,10);
		keyLabel.setText(keyLabelString);
		gridBagConstraints5.gridx = 2;
		gridBagConstraints5.gridy = 2;
		gridBagConstraints5.insets = new java.awt.Insets(10,10,10,10);
		valueLabel.setText(valLabel);
		gridBagConstraints6.gridx = 3;
		gridBagConstraints6.gridy = 2;
		gridBagConstraints6.weightx = 1.0;
		gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints6.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints7.gridx = 1;
		gridBagConstraints7.gridy = 2;
		gridBagConstraints7.weightx = 1.0;
		gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints7.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints8.gridx = 0;
		gridBagConstraints8.gridy = 1;
		gridBagConstraints8.weightx = 1.0;
		gridBagConstraints8.weighty = 1.0;
		gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints8.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints8.gridwidth = 5;
		this.add(title, gridBagConstraints3);
		this.add(keyLabel, gridBagConstraints4);
		this.add(valueLabel, gridBagConstraints5);
		this.add(getValueTf(), gridBagConstraints6);
		this.add(getAddButton(), gridBagConstraints9);
		this.add(getJScrollPane(), gridBagConstraints8);
		this.add(getKeyTf(), gridBagConstraints7);
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getValueTf() {
		if (valueTf == null) {
			valueTf = new JTextField();
            valueTf.setColumns(9);
		}
		return valueTf;
	}
	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getKeyTf() {
		if (keyTf == null) {
			keyTf = new JTextField();
            keyTf.setColumns(3);
		}
		return keyTf;
	}
	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */    
	private JTable getMapTable() {
		if (mapTable == null) {
			mapTable = new CDMassesTable();
		}
		return mapTable;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getMapTable());
		}
		return jScrollPane;
	}
    public void setTitle(String t) {
        title.setText(t);
    }
    public void setMap(Map map) {
        getMapTable().setModel(new CDMassesTableModel(map));
    }
    public Map getMap() {
        return ((CDMassesTableModel)getMapTable().getModel()).getMap();
    }
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getAddButton() {
		if (addButton == null) {
            java.net.URL url=this.getClass().getClassLoader().getResource("images/add.gif");
            Icon icon=null;
            if(null != url){
                icon=new ImageIcon(url);
            }
			addButton = new JButton();
			addButton.setText("Add");
            addButton.setIcon(icon);
			addButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
				    ((CDMassesTableModel)getMapTable().getModel()).map.put(getKeyTf().getText(),getValueTf().getText());
                    ((CDMassesTableModel)getMapTable().getModel()).fireTableRowsInserted(-1,-1);
                }
			});
		}
		return addButton;
	}
     }

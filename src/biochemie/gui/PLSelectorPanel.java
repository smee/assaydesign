/*
 * Created on 12.11.2004
 *
 */
package biochemie.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataListener;

import org.apache.commons.lang.ArrayUtils;
/**
 * @author Steffen Dienst
 *
 */
public class PLSelectorPanel extends MyPanel {

	private JComboBox comboPL = null;
	private String title="Photolinker at";
	Object[] values;

	/**
	 * This is the default constructor
	 * @param title
	 */
	public PLSelectorPanel() {
		super();
		setPLPositions(new int[]{9,8,10,11,12,13,14,15});
		initialize();
	}

	public void setPLPositions(int[] br){
        dirty();
        MutableComboBoxModel m = ((MutableComboBoxModel)getComboPL().getModel());
        int sel=getComboPL().getSelectedIndex();
        if(sel == -1)
            sel=0;
        values=new Object[br.length+1];
        values[0]="auto";
        for (int i = 0; i < br.length; i++) {
            values[i+1]=new Integer(br[i]);
        }
        while(m.getSize() != 0)
            m.removeElementAt(0);
        for (int i = 0; i < values.length; i++) {
            m.addElement(values[i]);
        }
        if(sel < br.length)
            getComboPL().setSelectedIndex(sel);
        else
            setAuto();
	}
public int getMaxSelectablePl() {
    int max=0;
    int size = getComboPL().getModel().getSize();
    for(int i=0;i<size;i++) {
        Object o = getComboPL().getModel().getElementAt(i);
        if(o instanceof Integer)
            max= Math.max(max, ((Integer)o).intValue());
    }
    return max;
}
	public int getSelectedPL(){
		Object val=getComboPL().getModel().getSelectedItem();
		if(val instanceof Integer)
			return ((Integer)val).intValue();
		return -1;
	}
	/**
     * Sets the photolinker and returns true. Returns false if <code>pl <c/ode> is not within possible range.
     * @param pl
     * @return
	 */
	public void setSelectedPL(int pl){
        dirty();
        if(pl <= 0) {
            getComboPL().getModel().setSelectedItem("auto");
            return;
        }
		Integer i=new Integer(pl);
		int pos=ArrayUtils.indexOf(values,i);
		if(pos == -1)
			return;
		getComboPL().getModel().setSelectedItem(i);
	}
    public boolean hasPL(int pl) {
        Integer i=new Integer(pl);
        int pos=ArrayUtils.indexOf(values,i);
        return pos != -1;
    }
	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private  void initialize() {
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(300,200);
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, title, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		gridBagConstraints6.insets = new java.awt.Insets(0,10,5,10);
		gridBagConstraints6.gridx = 0;
		gridBagConstraints6.gridy = 0;
		gridBagConstraints6.weightx = 1.0D;
		gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
		this.add(getComboPL(), gridBagConstraints6);
		setRekTooltip(null);
        setUnchanged();
	}
	/**
	 * This method initializes jComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	public JComboBox getComboPL() {
		if (comboPL == null) {
			comboPL = new JComboBox(new DefaultComboBoxModel());
		}
		return comboPL;
	}
	/**
	 *
	 */
	public void setAuto() {
        if(getComboPL().getModel().getSize()>0) {
            dirty();
            getComboPL().setSelectedIndex(0);
        }
	}

	/**
	 * @param string
	 */
	public void setRekTooltip(String t) {
        if(t == null)
            t="Please select Photolinker position (auto=best Photolinker is selected by the program)";
        super.setRekTooltip(t);
	}
    public void addPhotolinkerListListener(ListDataListener l) {
        getComboPL().getModel().addListDataListener(l);
    }
    public void addItemListener(ItemListener aListener) {
        getComboPL().addItemListener(aListener);
    }

    public void setModel(MutableComboBoxModel model) {
        dirty();
        getComboPL().setModel(model);
    }
    public void setTitle(String title) {
        this.title=title;
        this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, title, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
    }
 }

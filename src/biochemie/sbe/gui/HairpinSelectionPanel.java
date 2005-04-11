/*
 * Created on 11.11.2004
 *
 */
package biochemie.sbe.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import biochemie.gui.MyPanel;
/**
 * @author Steffen Dienst
 *
 */
public class HairpinSelectionPanel extends MyPanel {

    private class SelectionListener implements ItemListener{

        public void itemStateChanged(ItemEvent e) {
            dirty();
            JCheckBox src=(JCheckBox) e.getSource();
            boolean selected=e.getStateChange() == ItemEvent.SELECTED;
            if(selected == false)//dann is egal
                return;

            if(src == getCbNone() || src == getCbPred()) {
                    getCbA().setSelected(false);
                    getCbC().setSelected(false);
                    getCbG().setSelected(false);
                    getCbT().setSelected(false);
                if(src == getCbNone())
                    getCbPred().setSelected(false);
                else
                    getCbNone().setSelected(false);
            }else {//a,c,g oder t
                getCbNone().setSelected(false);
                getCbPred().setSelected(false);
            }
        }

    }
	private JCheckBox cbA = null;
	private JCheckBox cbG = null;
	private JCheckBox cbC = null;
	private JCheckBox cbT = null;

	private String title="Hairpin 5'";
	private JCheckBox cbNone = null;
	private JCheckBox cbPred = null;
    private ItemListener listener;
	/**
	 * This is the default constructor
	 */
	public HairpinSelectionPanel() {
		super();
		initialize();
	}
	public HairpinSelectionPanel(String title) {
		super();
		setTitle(title);
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private  void initialize() {

		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(154, 58);
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, title, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		gridBagConstraints7.gridx = 1;
		gridBagConstraints7.gridy = 1;
		gridBagConstraints7.insets = new java.awt.Insets(5,5,5,5);
		gridBagConstraints8.gridx = 3;
		gridBagConstraints8.gridy = 1;
		gridBagConstraints8.insets = new java.awt.Insets(5,5,5,5);
		gridBagConstraints9.gridx = 1;
		gridBagConstraints9.gridy = 2;
		gridBagConstraints9.insets = new java.awt.Insets(0,5,5,5);
		gridBagConstraints10.gridx = 3;
		gridBagConstraints10.gridy = 2;
		gridBagConstraints10.insets = new java.awt.Insets(0,5,5,5);
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.gridy = 1;
		gridBagConstraints12.gridx = 0;
		gridBagConstraints12.gridy = 2;
		gridBagConstraints13.gridx = 2;
		gridBagConstraints13.gridy = 1;
		gridBagConstraints14.gridx = 2;
		gridBagConstraints14.gridy = 2;
		gridBagConstraints1.gridx = 3;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints5.gridx = 3;
		gridBagConstraints5.gridy = 2;
		this.add(getCbA(), gridBagConstraints11);
		this.add(getCbG(), gridBagConstraints12);
		this.add(getCbC(), gridBagConstraints13);
		this.add(getCbT(), gridBagConstraints14);
		this.add(getCbNone(), gridBagConstraints1);
		this.add(getCbPred(), gridBagConstraints5);
		String t="Define experimentally observed Hairpins";
		setRekTooltip(t);
        setUnchanged();
	}

	/**
	 * This method initializes cbA
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCbA() {
		if (cbA == null) {
			cbA = new JCheckBox();
			cbA.setText("A");
            cbA.addItemListener(getMyItemListener());
		}
		return cbA;
	}
	/**
	 * This method initializes cbG
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCbG() {
		if (cbG == null) {
			cbG = new JCheckBox();
			cbG.setText("G");
            cbG.addItemListener(getMyItemListener());
		}
		return cbG;
	}
	/**
	 * This method initializes cbC
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCbC() {
		if (cbC == null) {
			cbC = new JCheckBox();
			cbC.setText("C");
            cbC.addItemListener(getMyItemListener());
		}
		return cbC;
	}
	/**
	 * This method initializes cbT
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCbT() {
		if (cbT == null) {
			cbT = new JCheckBox();
			cbT.setText("T");
            cbT.addItemListener(getMyItemListener());
		}
		return cbT;
	}
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, title, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
	}

	public String getSelectedNukleotides(){
		if(cbNone.isSelected())
			return "none";

		if(getCbPred().isSelected())
			return "";

		String result="";
		if(cbA.isSelected())
			result+="A";
		if(cbC.isSelected())
			result+="C";
		if(cbG.isSelected())
			result+="G";
		if(cbT.isSelected())
			result+="T";

		return result;
	}
    public void setSelectedNukleotides(String n) {
        dirty();
        n=n.toUpperCase();

       cbNone.setSelected(n.indexOf("NONE") != -1);
       cbA.setSelected(n.indexOf('A') != -1);
       cbC.setSelected(n.indexOf('C') != -1);
       cbG.setSelected(n.indexOf('G') != -1);
       cbT.setSelected(n.indexOf('T') != -1);
    }
	/**
	 * This method initializes cbNone
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCbNone() {
		if (cbNone == null) {
			cbNone = new JCheckBox();
			cbNone.setText("none");
			cbNone.addItemListener(getMyItemListener());
        }
		return cbNone;
	}
	/**
     * @return
     */
    private ItemListener getMyItemListener() {
        if(listener == null) {
            listener = new SelectionListener();
        }
        return listener;
    }
    /**
	 * This method initializes jCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCbPred() {
		if (cbPred == null) {
            cbPred = new JCheckBox();
            cbPred.setText("predicted");
            cbPred.addItemListener(getMyItemListener());
            cbPred.setSelected(true);
		}
		return cbPred;
	}
 }  //  @jve:decl-index=0:visual-constraint="28,8"

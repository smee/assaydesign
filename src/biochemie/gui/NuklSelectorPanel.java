/*
 * Created on 11.11.2004
 *
 */
package biochemie.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
/**
 * @author Steffen Dienst
 *
 */
public class NuklSelectorPanel extends MyPanel {

	private JCheckBox cbA = null;
	private JCheckBox cbG = null;
	private JCheckBox cbC = null;
	private JCheckBox cbT = null;

	private String title="SNP";
    private ItemListener cl=new ItemListener(){
        public void itemStateChanged(ItemEvent e) {
            dirty();            
        }        
    };
	/**
	 * This is the default constructor
	 */
	public NuklSelectorPanel() {
		super();
		initialize();
	}

	public NuklSelectorPanel(String title) {
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

		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(130, 58);
		this.setTitle("SNP");
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
		gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints12.gridx = 0;
		gridBagConstraints12.gridy = 2;
		gridBagConstraints12.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints13.gridx = 2;
		gridBagConstraints13.gridy = 1;
		gridBagConstraints13.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints14.gridx = 2;
		gridBagConstraints14.gridy = 2;
		gridBagConstraints14.fill = java.awt.GridBagConstraints.BOTH;
		this.add(getCbA(), gridBagConstraints11);
		this.add(getCbG(), gridBagConstraints12);
		this.add(getCbC(), gridBagConstraints13);
		this.add(getCbT(), gridBagConstraints14);
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
            cbA.addItemListener(cl);
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
            cbG.addItemListener(cl);
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
            cbC.addItemListener(cl);
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
            cbT.addItemListener(cl);
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
	}

	public String getSelectedNukleotides(){
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

       cbA.setSelected(n.indexOf('A') != -1);
       cbC.setSelected(n.indexOf('C') != -1);
       cbG.setSelected(n.indexOf('G') != -1);
       cbT.setSelected(n.indexOf('T') != -1);
    }

 }  //  @jve:decl-index=0:visual-constraint="10,10"

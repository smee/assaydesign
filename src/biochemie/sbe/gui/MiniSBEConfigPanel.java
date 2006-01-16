/*
 * Created on 21.11.2004
 *
 */
package biochemie.sbe.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
/**
 * @author Steffen Dienst
 *
 */
public class MiniSBEConfigPanel extends JPanel {

	private JSpinner minTspinner = null;
	private JSpinner optTspinner = null;
	private JSpinner maxTspinner = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JSpinner mingcSpinner = null;
	private JSpinner maxgcSpinner = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JPanel jPanel2 = null;
	private JSpinner polyxSpinner = null;
	private JSpinner maxplexSpinner = null;
	private JLabel jLabel5 = null;
	private JLabel jLabel6 = null;
	private JSpinner candlenSpinner = null;
	private JLabel jLabel7 = null;
	private JSpinner pcrpdiffSpinner = null;
	/**
	 * This is the default constructor
	 */
	public MiniSBEConfigPanel() {
		super();
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
		jLabel = new JLabel();
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setSize(418, 263);

		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.insets = new java.awt.Insets(0,10,0,0);
		jLabel.setText("Minimum Temp. (in °C)");
		jLabel1.setText("Optimum Temp. (in °C)");
		jLabel2.setText("Maximum Temp. (in °C)");
		this.add(getJPanel(), null);
		this.add(getJPanel1(), null);
		this.add(getJPanel2(), null);
	}
	/**
	 * This method initializes jSpinner
	 *
	 * @return javax.swing.JSpinner
	 */
	public JSpinner getMinTspinner() {
		if (minTspinner == null) {
			minTspinner = new JSpinner();
			((SpinnerNumberModel)minTspinner.getModel()).setMinimum(new Integer(0));
			minTspinner.setValue(new Integer(40));
		}
		return minTspinner;
	}
	/**
	 * This method initializes jSpinner1
	 *
	 * @return javax.swing.JSpinner
	 */
	public JSpinner getOptTspinner() {
		if (optTspinner == null) {
			optTspinner = new JSpinner();
			((SpinnerNumberModel)optTspinner.getModel()).setMinimum(new Integer(0));
			optTspinner.setValue(new Integer(58));
			optTspinner.getModel().addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					SpinnerModel mod=(SpinnerModel)e.getSource();
					Number optT=(Number) mod.getValue();
					SpinnerNumberModel minmodel=((SpinnerNumberModel)getMinTspinner().getModel());
					SpinnerNumberModel maxmodel=((SpinnerNumberModel)getMaxTspinner().getModel());
					if(((Number)minmodel.getValue()).intValue() >= optT.intValue())
						minmodel.setValue(new Integer(optT.intValue() - 1));
					if(((Number)maxmodel.getValue()).intValue() <= optT.intValue())
						maxmodel.setValue(new Integer(optT.intValue() + 1));
					minmodel.setMaximum(new Integer(optT.intValue() - 1));
					maxmodel.setMinimum(new Integer(optT.intValue() + 1));
				}
			});
		}
		return optTspinner;
	}
	/**
	 * This method initializes jSpinner2
	 *
	 * @return javax.swing.JSpinner
	 */
	public JSpinner getMaxTspinner() {
		if (maxTspinner == null) {
			maxTspinner = new JSpinner();
			((SpinnerNumberModel)maxTspinner.getModel()).setMinimum(new Integer(0));
			maxTspinner.setValue(new Integer(65));
		}
		return maxTspinner;
	}
	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			jPanel = new JPanel();
			jPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Temperatures", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			jPanel.setLayout(new GridBagLayout());
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.gridy = 1;
			gridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints17.insets = new java.awt.Insets(0,10,10,0);

			gridBagConstraints7.gridx = -1;
			gridBagConstraints7.gridy = -1;
			gridBagConstraints7.insets = new java.awt.Insets(0,10,10,10);
			gridBagConstraints8.gridx = -1;
			gridBagConstraints8.gridy = -1;
			gridBagConstraints8.insets = new java.awt.Insets(0,10,10,0);
			gridBagConstraints9.gridx = -1;
			gridBagConstraints9.gridy = -1;
			gridBagConstraints9.insets = new java.awt.Insets(0,10,10,0);
			gridBagConstraints10.gridx = 2;
			gridBagConstraints10.gridy = 1;
			gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.insets = new java.awt.Insets(0,10,10,10);
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 1;
			gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.insets = new java.awt.Insets(0,10,10,0);
			jPanel.add(jLabel, gridBagConstraints9);
			jPanel.add(jLabel1, gridBagConstraints8);
			jPanel.add(jLabel2, gridBagConstraints7);
			jPanel.add(getMinTspinner(), gridBagConstraints17);
			jPanel.add(getOptTspinner(), gridBagConstraints11);
			jPanel.add(getMaxTspinner(), gridBagConstraints10);
		}
		return jPanel;
	}
	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jLabel4 = new JLabel();
			jLabel3 = new JLabel();
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "GC", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.gridy = 1;
			gridBagConstraints18.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints18.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints19.gridx = 1;
			gridBagConstraints19.gridy = 1;
			gridBagConstraints19.insets = new java.awt.Insets(0,10,0,10);
			gridBagConstraints19.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints20.gridx = 0;
			gridBagConstraints20.gridy = 0;
			gridBagConstraints20.insets = new java.awt.Insets(10,10,10,0);
			jLabel3.setText("Min GC (in %)");
			gridBagConstraints21.gridx = 1;
			gridBagConstraints21.gridy = 0;
			gridBagConstraints21.insets = new java.awt.Insets(10,10,10,0);
			jLabel4.setText("Max GC (in %)");
			jPanel1.add(getMingcSpinner(), gridBagConstraints18);
			jPanel1.add(getMaxgcSpinner(), gridBagConstraints19);
			jPanel1.add(jLabel3, gridBagConstraints20);
			jPanel1.add(jLabel4, gridBagConstraints21);
		}
		return jPanel1;
	}
	/**
	 * This method initializes jSpinner
	 *
	 * @return javax.swing.JSpinner
	 */
	public JSpinner getMingcSpinner() {
		if (mingcSpinner == null) {
			mingcSpinner = new JSpinner();
			((SpinnerNumberModel)mingcSpinner.getModel()).setMinimum(new Integer(0));
			mingcSpinner.setValue(new Integer(20));
		}
		return mingcSpinner;
	}
	/**
	 * This method initializes jSpinner1
	 *
	 * @return javax.swing.JSpinner
	 */
	public JSpinner getMaxgcSpinner() {
		if (maxgcSpinner == null) {
			maxgcSpinner = new JSpinner();
			((SpinnerNumberModel)maxgcSpinner.getModel()).setMinimum(new Integer(0));
			maxgcSpinner.setValue(new Integer(80));
		}
		return maxgcSpinner;
	}
	/**
	 * This method initializes jPanel2
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jLabel7 = new JLabel();
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			jLabel6 = new JLabel();
			jLabel5 = new JLabel();
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			jPanel2 = new JPanel();
			jPanel2.setLayout(new GridBagLayout());
			jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Misc.", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			gridBagConstraints22.gridx = 0;
			gridBagConstraints22.gridy = 1;
			gridBagConstraints22.insets = new java.awt.Insets(0,10,10,0);
			gridBagConstraints22.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints23.gridx = 2;
			gridBagConstraints23.gridy = 1;
			gridBagConstraints23.insets = new java.awt.Insets(0,10,10,10);
			gridBagConstraints23.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints24.gridx = 0;
			gridBagConstraints24.gridy = 0;
			gridBagConstraints24.insets = new java.awt.Insets(10,10,10,0);
			jLabel5.setText("PolyX");
			gridBagConstraints25.gridx = 2;
			gridBagConstraints25.gridy = 0;
			gridBagConstraints25.insets = new java.awt.Insets(10,10,10,10);
			jLabel6.setText("Maxplexsize");
			gridBagConstraints12.gridx = 3;
			gridBagConstraints12.gridy = 0;
			jLabel7.setText("Min. PCR-product length difference");
			gridBagConstraints2.gridx = 3;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.insets = new java.awt.Insets(0,10,10,10);
			jPanel2.add(getPolyxSpinner(), gridBagConstraints22);
			jPanel2.add(getMaxplexSpinner(), gridBagConstraints23);
			jPanel2.add(jLabel5, gridBagConstraints24);
			jPanel2.add(jLabel6, gridBagConstraints25);
			jPanel2.add(jLabel7, gridBagConstraints12);
			jPanel2.add(getPcrpdiffSpinner(), gridBagConstraints2);
		}
		return jPanel2;
	}
	/**
	 * This method initializes jSpinner2
	 *
	 * @return javax.swing.JSpinner
	 */
	public JSpinner getPolyxSpinner() {
		if (polyxSpinner == null) {
			polyxSpinner = new JSpinner();
			((SpinnerNumberModel)polyxSpinner.getModel()).setMinimum(new Integer(0));
			polyxSpinner.setValue(new Integer(5));
		}
		return polyxSpinner;
	}
	/**
	 * This method initializes jSpinner3
	 *
	 * @return javax.swing.JSpinner
	 */
	public JSpinner getMaxplexSpinner() {
		if (maxplexSpinner == null) {
			maxplexSpinner = new JSpinner();
			((SpinnerNumberModel)maxTspinner.getModel()).setMinimum(new Integer(0));
			maxplexSpinner.setValue(new Integer(6));
		}
		return maxplexSpinner;
	}
	/**
	 * This method initializes jSpinner
	 *
	 * @return javax.swing.JSpinner
	 */
	public JSpinner getPcrpdiffSpinner() {
		if (pcrpdiffSpinner == null) {
			pcrpdiffSpinner = new JSpinner();
			pcrpdiffSpinner.setValue(new Integer(0));
		}
		return pcrpdiffSpinner;
	}
            }  //  @jve:decl-index=0:visual-constraint="10,10"

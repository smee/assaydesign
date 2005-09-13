package biochemie.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import biochemie.calcdalton.gui.PBSequenceField;
import biochemie.sbe.SBEOptions;

public class CalcTimePanel extends MyPanel {

    private JLabel jLabel;
    private JPanel restPanel;
    private PBSequenceField colortimeTf;

    public CalcTimePanel() {
        initialize();
    }
    /**
     * 
     */
    protected void initialize() {
        jLabel = new JLabel();
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        restPanel = new JPanel();
        restPanel.setLayout(new GridBagLayout());
        jLabel.setText("Calculationtime in s ");
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.insets = new java.awt.Insets(10,10,10,10);
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.fill = java.awt.GridBagConstraints.NONE;
        gridBagConstraints4.insets = new java.awt.Insets(0,20,10,20);
        restPanel.add(jLabel, gridBagConstraints3);
        restPanel.add(getColortimeTf(), gridBagConstraints4);
    }
    /**
     * This method initializes PBSequenceField
     *
     * @return biochemie.calcdalton.gui.PBSequenceField
     */
    private PBSequenceField getColortimeTf() {
        if (colortimeTf == null) {
            colortimeTf = new PBSequenceField();
            colortimeTf.setColumns(5);
            colortimeTf.setValidChars("0123456789");
            colortimeTf.setText("10");
        }
        return colortimeTf;
    }
    public void setPropertiesFrom(SBEOptions c) {
        getColortimeTf().setText(Integer.toString(c.getCalcTime()));
    }
    public void saveTo(SBEOptions sbeconfig) {
        sbeconfig.setCalcTime(Integer.parseInt(getColortimeTf().getText()));
    }
}

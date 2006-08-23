package biochemie.sbe.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

import biochemie.gui.MyPanel;
import biochemie.sbe.ProbePrimerFactory;
/**
 * @author Steffen Dienst
 *
 */
public class ProbeTypeSelectorPanel extends MyPanel {

    private JComboBox comboProbetype = null;
    private String title="Probe type";
    Object[] values;

    /**
     * This is the default constructor
     * @param title
     */
    public ProbeTypeSelectorPanel() {
        super();
        initialize();
    }
    /**
     * -1 means auto
     * @return
     */
    public int getSelectedType(){
        return getComboProbetype().getSelectedIndex() - 1;
    }

    public void setSelectedType(int type){
        dirty();
        if(type <= 0 || type >= getComboProbetype().getModel().getSize()) {
            getComboProbetype().getModel().setSelectedItem("auto");
            return;
        }
        getComboProbetype().setSelectedIndex(type+1);
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
        gridBagConstraints6.insets = new java.awt.Insets(0,10,5,10);
        gridBagConstraints6.gridx = 0;
        gridBagConstraints6.gridy = 0;
        gridBagConstraints6.weightx = 1.0D;
        gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.add(getComboProbetype(), gridBagConstraints6);
        setRekTooltip(null);
        setUnchanged();
        setBorder();
    }

    /**
     * 
     */
    private void setBorder() {
        TitledBorder b=javax.swing.BorderFactory.createTitledBorder(null, title, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null);
        this.setBorder(b);
        Dimension minsize=b.getMinimumSize(this);
        Dimension crntSize=getPreferredSize();
        crntSize.setSize(Math.max(minsize.getWidth(),crntSize.getWidth()),crntSize.getHeight());
        this.setPreferredSize(crntSize);
    }
    /**
     * This method initializes jComboBox
     *
     * @return javax.swing.JComboBox
     */
    public JComboBox getComboProbetype() {
        if (comboProbetype == null) {
            DefaultComboBoxModel model=new DefaultComboBoxModel();
            model.addElement("auto");
            for (int i = 0; i < ProbePrimerFactory.ASSAYTYPES_DESC.length; i++) {
                model.addElement(ProbePrimerFactory.ASSAYTYPES_DESC[i]);
            }
            comboProbetype = new JComboBox(model);
        }
        return comboProbetype;
    }
    /**
     *
     */
    public void setAuto() {
        if(getComboProbetype().getModel().getSize()>0) {
            dirty();
            getComboProbetype().setSelectedIndex(0);
        }
    }

    public void setTitle(String title) {
        this.title=title;
        setBorder();
    }
 }

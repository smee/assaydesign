package biochemie.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.MutableComboBoxModel;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import biochemie.sbe.gui.SBESequenceTextField;
import biochemie.util.Helper;
public class SBESequencePanel extends MyPanel implements DocumentListener, ItemListener {

	private SBESequenceTextField SBESequenceTextField = null;
	private JLabel seqnameLabel = null;
	private PLSelectorPanel PLSelectorPanel = null;
    private Border errorBorder;
    private Border okayBorder;

    /**
     * Enthaelt alle pls als Integer.
     */
    private Set plset;
    /**
     * Gibt an, ob gerade eine legale Eingabe vorliegt.
     */
    private boolean isOkay;
    /**
     * Enthaelt das durch L ersetzte Nukleotid.
     */
    private char replNukl;
    private int maxpl;
    /**
     * This is the default constructor
     */
    public SBESequencePanel() {
    	super();
    	initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private  void initialize() {
    	seqnameLabel = new JLabel();
    	GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
    	GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
    	GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
    	this.setLayout(new GridBagLayout());
    	this.setSize(300,200);
    	gridBagConstraints1.gridx = 0;
    	gridBagConstraints1.gridy = 1;
    	gridBagConstraints1.weightx = 1.0;
    	gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    	gridBagConstraints2.gridx = 0;
    	gridBagConstraints2.gridy = 0;
    	setTitle("Sequence:");
    	gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
    	gridBagConstraints2.insets = new java.awt.Insets(10,10,10,0);
    	gridBagConstraints1.insets = new java.awt.Insets(0,10,0,10);
    	gridBagConstraints3.gridx = 1;
    	gridBagConstraints3.gridy = 0;
    	gridBagConstraints3.gridheight = 3;
    	gridBagConstraints3.insets = new java.awt.Insets(0,0,0,5);
    	seqnameLabel.setText("Sequence:");
    	this.add(getSBESequenceTextField(), gridBagConstraints1);
    	this.add(seqnameLabel, gridBagConstraints2);
    	this.add(getPLSelectorPanel(), gridBagConstraints3);
        
        replNukl=0;
        setOkay(false);
        okayBorder = getSBESequenceTextField().getBorder();
        errorBorder=BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.red,2),okayBorder);
        setPLPositions(new int[] {9,8,10,11,12});
        handleChange();//einmal aufrufen, damit initiale Tooltips stimmen
    }

	/**
	 * This method initializes SBESequenceTextField	
	 * 	
	 * @return biochemie.sbe.gui.SBESequenceTextField	
	 */    
	private SBESequenceTextField getSBESequenceTextField() {
		if (SBESequenceTextField == null) {
			SBESequenceTextField = new SBESequenceTextField();
			SBESequenceTextField.getDocument().addDocumentListener(this);
			SBESequenceTextField.setColumns(30);
            SBESequenceTextField.setText("");
		}
		return SBESequenceTextField;
	}
	/**
	 * This method initializes PLSelectorPanel	
	 * 	
	 * @return biochemie.gui.PLSelectorPanel	
	 */    
	private PLSelectorPanel getPLSelectorPanel() {
		if (PLSelectorPanel == null) {
			PLSelectorPanel = new PLSelectorPanel();
            PLSelectorPanel.addItemListener(this);
		}
		return PLSelectorPanel;
	}

    /**
     * @param b
     */
    protected void setOkay(boolean b) {
        this.isOkay=b;        
    }

    /**
     * Liefert Sequenz, evtl. mit L an der gewaehlten PL-Stelle.
     * @return
     */
    public String getSequence() {
        return getSBESequenceTextField().getText();
    }
    public void setSequence(String seq) {
        getSBESequenceTextField().setText(seq);
    }
    public void setPLPositions(int[] br) {
        MutableComboBoxModel m = getPLModel();
        plset=new HashSet();
        maxpl=Integer.MIN_VALUE;
        int sel=getPLSelectorPanel().getComboPL().getSelectedIndex();
        if(sel == -1)
            sel=0;
        Object[] values=new Object[br.length+1];
        values[0]="auto";
        for (int i = 0; i < br.length; i++) {
            values[i+1]=new Integer(br[i]);
            plset.add(new Integer(br[i]));
            maxpl=Math.max(maxpl, br[i]);
        }
        while(m.getSize() != 0)
            m.removeElementAt(0);
        for (int i = 0; i < values.length; i++) {
            m.addElement(values[i]);
        }
        if(sel < br.length)
            getPLSelectorPanel().getComboPL().setSelectedIndex(sel);
        else
            getPLSelectorPanel().setAuto();
        
    }
    /**
     * @return
     */
    private MutableComboBoxModel getPLModel() {
        return ((MutableComboBoxModel)getPLSelectorPanel().getComboPL().getModel());
    }

    public void setTitle(String title) {
        this.seqnameLabel.setText(title);
    }
    public String getTitle() {
        return this.seqnameLabel.getText();
    }

    
    private void setToolTipAndBorder(SBESequenceTextField tf,String tooltip,boolean err) {
        this.isOkay = !err;
        tf.setToolTipText(tooltip);
        Border b = err?errorBorder:okayBorder;
        tf.setBorder(b);
    }
    
   
   /**
    * Testet auf Fehler, sobald die PL-Combobox geaendert wurde
    */
    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.DESELECTED)//geht uns nix an
            return;
    }


    public void changedUpdate(DocumentEvent e) {
        handleChange();
    }
    public void insertUpdate(DocumentEvent e) {
        handleChange();
    }
    public void removeUpdate(DocumentEvent e) {
        handleChange();                    
    }
    /**
     * Wird aufgerufen, sobald sich irgendwas an der Eingabe aendert.
     *
     */
    private void handleChange() {
        if(false) {
            getPLSelectorPanel().setEnabled(false);
            getPLSelectorPanel().setRekTooltip("Photolinker was defined by primer sequence input");
        }else {
            getPLSelectorPanel().setEnabled(true);
            getPLSelectorPanel().setRekTooltip(null);
        }
        String text=getSBESequenceTextField().getText();
        int posOfL=Helper.getPosOfPl(text);
        
        if(posOfL != -1)
            if(!plset.contains(new Integer(posOfL))) {
                getPLSelectorPanel().setEnabled(false);
                setToolTipAndBorder(getSBESequenceTextField(),"Photolinkerposition out of bounds!",true);
            }else {//L enthalten, alles I.O.
                int pos = text.indexOf('L');
                String tooltip=text.substring(0,pos)+"[L]"+text.substring(pos+1);
                setToolTipAndBorder(getSBESequenceTextField(),tooltip,false);                
            }
        else
            if(text.length() < maxpl && text.length() != 0) {
                getPLSelectorPanel().setEnabled(false);
                setToolTipAndBorder(getSBESequenceTextField(),"Sequence is too short, please enter at least "+maxpl+"characters!",true);
            }else {
                String tooltip="Insert 5\' Sequence of the SNP (A,C,G,T, L) (L=Photolinker)";
                if(text.length() != 0)
                    tooltip = text;
                setToolTipAndBorder(getSBESequenceTextField(),tooltip,false);
            }
    }                
    
  }

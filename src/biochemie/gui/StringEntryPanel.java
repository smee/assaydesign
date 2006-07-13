/*
 * Created on 19.11.2004
 *
 */
package biochemie.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import biochemie.calcdalton.gui.PBSequenceField;
/**
 * @author Steffen Dienst
 *
 */
public class StringEntryPanel extends MyPanel {

	private JTextField labelTf = null;

	private PBSequenceField PBSequenceField = null;
	/**
	 * This is the default constructor
	 */
	public StringEntryPanel() {
		this("Label");
	}
	public StringEntryPanel(String label){
		super();
		initialize();
		setLabel(label);
	}
	public void setLabel(String label){
		getLabelTf().setText(label);
	}
	public void setColumns(int col){
		getPBSequenceField().setColumns(col);
	}
	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private  void initialize() {
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(300,200);
		this.setLabel("TestLabel");
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints2.insets = new java.awt.Insets(0,5,10,5);
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 1;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints3.insets = new java.awt.Insets(0,5,0,5);
		this.add(getLabelTf(), gridBagConstraints2);
		this.add(getPBSequenceField(), gridBagConstraints3);
        getPBSequenceField().setValidChars("abcdefghijklmnopqrstuvwxyz" +
                                          "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                                          "0123456789" +
                                          ",.-=_+[]|<>?!#$%&*() ");
        setUnchanged();
	}
	/**
	 * This method initializes jTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getLabelTf() {
		if (labelTf == null) {
			labelTf = new JTextField();
			labelTf.setEditable(false);
		}
		return labelTf;
	}
	/**
	 * This method initializes PBSequenceField
	 *
	 * @return biochemie.calcdalton.gui.PBSequenceField
	 */
	public PBSequenceField getPBSequenceField() {
		if (PBSequenceField == null) {
			PBSequenceField = new PBSequenceField();
			PBSequenceField.setMaxLen(20);
			PBSequenceField.setUpper(false);
			PBSequenceField.setColumns(10);
            PBSequenceField.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    dirty();
                }
                public void insertUpdate(DocumentEvent e) {
                    dirty();                    
                }
                public void removeUpdate(DocumentEvent e) {
                    dirty();                    
                }
            });
           
		}
		return PBSequenceField;
	}
	/**
	 * @param b
	 */
	public void setResizeToStringLen(boolean b) {
		//TODO
	}

	public void setValidChars(String chars){
	    getPBSequenceField().setValidChars(chars);   
    }
    public String getValidChars(){
        return getPBSequenceField().getValidChars();
    }
	public String getText(){
		return getPBSequenceField().getText();
	}
    public int getTextAsInt(int dflt){
        return getPBSequenceField().getAsInt(dflt);
    }
    public double getTextAsDouble(double dflt){
        return getPBSequenceField().getAsDouble(dflt);
    }
	/**
	 * @param string
	 */
	public void setText(String string) {
        dirty();
		getPBSequenceField().setText(string);
	}
    public void setMaxLen(int len) {
        getPBSequenceField().setMaxLen(len);
    }
  }

/*
 * Created on 04.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.sbe.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import biochemie.calcdalton.gui.PBSequenceField;
import biochemie.gui.PLSelectorPanel;

/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SBESeqInputController implements DocumentListener, ListDataListener{
      Border errorBorder;
      Border okayBorder;
      
    private SBESequenceTextField left;
    private PBSequenceField right;
    private PLSelectorPanel plpanel;
    private boolean isOkay;

    public SBESeqInputController(SBESequenceTextField left, PBSequenceField right, PLSelectorPanel pl) {
        this.left=left;
        this.right=right;
        this.plpanel=pl;
        left.getDocument().addDocumentListener(this);
        right.getDocument().addDocumentListener(this);
        
        pl.addPhotolinkerListListener(this);
        errorBorder=BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.red,2),okayBorder);
        okayBorder = left.getBorder();
    }
    private void handleChange(DocumentEvent e){

        String ltext=left.getText();
        String rtext=right.getText();
        
        if(ltext==null && rtext == null) return;
        
        //sind die eingegebenen seq. lang genug?
        int maxpl = plpanel.getMaxSelectablePl();
        if(ltext.length() !=0 && ltext.length()<maxpl) {
            setToolTipAndBorder(left,"Sequence is too short, please enter at least "+maxpl+"characters!",true);
            return;
        }
        if(rtext.length() != 0 && rtext.length()<maxpl) {
            setToolTipAndBorder(right,"Sequence is too short, please enter at least "+maxpl+"characters!",true);
            return;
        }
        //wurde ein L eingegeben?
        int lpos=ltext.indexOf('L');
        int rpos=rtext.indexOf('L');
        if(lpos == -1 && rpos == -1)  {      //kein L in der Eingabe
            String ltool=ltext.length()==0?"Insert 5' Sequence of the SNP (A,C,G,T, L) (L=Photolinker)":ltext;
            setToolTipAndBorder(left,ltext,false);
            String rtool=rtext.length()==0?"Insert 5' Sequence of the SNP (A,C,G,T, L) (L=Photolinker)":rtext;
            setToolTipAndBorder(right,rtool,false);
            left.setEnabled(true);
            right.setEnabled(true);
            plpanel.setEnabled(true);
            return;
        }
        
        int pos=rpos;
        JTextField tfwithl=right, tfwol=left;
        
        if(lpos != -1) {
            pos = lpos;
            tfwithl=left;
            tfwol=right;
        }
        String text = tfwithl.getText();
        tfwol.setText("");
        tfwol.setEnabled(false); //schalt mer aus, brauchen wir nicht mehr
        
        int br=text.length() - pos;
        if(plpanel.setSelectedPL(br)){//es gibt diesen PL
            plpanel.setEnabled(false);
            plpanel.setRekTooltip("Photolinker was defined by primer sequence input");


            String tooltip = text.substring(0,pos)+"[L]"+text.substring(pos+1);
            setToolTipAndBorder(tfwithl,tooltip,false);
            setToolTipAndBorder(tfwol,"",false);
            return;
        }else {
            plpanel.setAuto();
            plpanel.setEnabled(true);
            setToolTipAndBorder(tfwithl,"Photolinkerposition out of bounds!",true);            
        }
    }
    /**
     * @param b
     */
    private void setToolTipAndBorder(JTextField tf,String tooltip,boolean err) {
        this.isOkay = !err;
        tf.setToolTipText(tooltip);
        Border b = err?errorBorder:okayBorder;
        tf.setBorder(b);
    }

    public void insertUpdate(DocumentEvent e) {
        handleChange(e);
    }
    public void removeUpdate(DocumentEvent e) {
        handleChange(e);
        
    }
    public void changedUpdate(DocumentEvent e) {
        handleChange(e);        
    }

    public void contentsChanged(ListDataEvent e) {
        handleChange(null);
    }

    public void intervalAdded(ListDataEvent e) {
        handleChange(null);        
    }

    public void intervalRemoved(ListDataEvent e) {
        handleChange(null);        
    }
    /**
     * @return Returns the isOkay.
     */
    public boolean isOkay() {
        return isOkay;
    }
}

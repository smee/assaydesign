package biochemie.sbe.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class AbstractSeqInputController implements ISeqInputController, DocumentListener {

    protected ISeqInputController other = null;
    protected boolean isOkay = true;
    protected final Border errorBorder;
    protected final Border okayBorder;
    protected final SBESequenceTextField left;
    protected final boolean isLeft;
    protected final int minlen;
    protected JCheckBox fixedcb;


    public AbstractSeqInputController(SBECandidatePanel panel, int minlen, boolean isLeft) {
        this(isLeft?panel.getSeq5tf():panel.getSeq3tf(),panel.getFixedPrimerCB(), minlen,isLeft);
    }

    public AbstractSeqInputController(SBESequenceTextField tf, JCheckBox fix, int minlength, boolean isLeft) {
        this.left=tf;
        this.minlen=minlength;
        this.isLeft=isLeft;
        this.fixedcb=fix;
        this.fixedcb.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                boolean sel=fixedcb.isSelected();
                setEnabled(!sel);
                if(other!=null)
                    other.setEnabled(!sel);
            }
        });
        left.getDocument().addDocumentListener(this);
        okayBorder = left.getBorder();
        errorBorder=BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.red,2),okayBorder);
    }

    public boolean isOkay() {
        return isOkay;
    }

    public void setOtherController(ISeqInputController o) {
        this.other=o;
    }

    public void setEnabled(boolean b) {
        this.left.setEnabled(b);
    }
    
    public void insertUpdate(DocumentEvent e) {
        handleSeqChange();
    }
    protected abstract void handleSeqChange();

    public void removeUpdate(DocumentEvent e) {
        handleSeqChange();
        
    }
    public void changedUpdate(DocumentEvent e) {
        handleSeqChange();
    }

    /**
     * @param b
     */
    protected void setToolTipAndBorder(String tooltip, boolean err) {
        this.isOkay = !err;
        left.setToolTipText(tooltip);
        Border b = err?errorBorder:okayBorder;
        left.setBorder(b);
    }

}

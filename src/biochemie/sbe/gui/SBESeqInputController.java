/*
 * Created on 04.12.2004
 *
 */
package biochemie.sbe.gui;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import biochemie.gui.PLSelectorPanel;
import biochemie.gui.StringEntryPanel;
import biochemie.util.Helper;

/**
 * @author sdienst
 *
 */
public class SBESeqInputController implements DocumentListener, ListDataListener, ItemListener{
    private static final String INSERT_TT = "Insert 5' Sequence of the SNP (A,C,G,T, L) (L=cleavable linker)";
    Border errorBorder;
    Border okayBorder;
    
    private SBESequenceTextField left;
    private SBESeqInputController other=null;
    private PLSelectorPanel plpanel;
    private boolean isOkay=true;
    private char replacedNukl = 0;
    /**
     * Die Sequenz, die durch die beiden GUI-Elemente repraesentiert werden soll.
     */
    private final int minlen;
    /**
     * Ich setze gerade den PL, nicht der user
     */
    private JCheckBox fixedcb;
    private boolean IamModifying = false ;
    private StringEntryPanel midtf=null;
    private boolean isRight;
    
    public SBESeqInputController(SBECandidatePanel panel, int minlen, boolean isLeft) {        
        this(isLeft?panel.getSeq5tf():panel.getSeq3tf(),isLeft?panel.getPlpanel5():panel.getPlpanel3(), panel.getFixedPrimerCB(), minlen, isLeft);
        this.midtf = panel.getMultiplexidPanel();
        if(midtf !=null) {
            midtf.setEnabled(false);
            fixedcb.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    boolean sel=fixedcb.isSelected();
                    SBESequenceTextField tf = SBESeqInputController.this.left;
                    midtf.setEnabled(sel);
                    if( sel && Helper.getPosOfPl(left.getText())<0 )//wenn das hier keine fixe seq. ist
                        sel=false;
                    if(sel == true)//nur dann!
                        plpanel.setEnabled(false);
                    else
                        plpanel.setEnabled(getReplNucl()!=0);
                    tf.setEnabled(!sel);
                }
            });
        }
    }
    public SBESeqInputController(SBESequenceTextField tf, PLSelectorPanel panel, JCheckBox fix, int minlength, boolean isLeft) {
        this.left=tf;
        this.minlen=minlength;
        this.plpanel=panel;
        this.isRight=!isLeft;
        this.fixedcb=fix==null?new JCheckBox():fix;
        fixedcb.setSelected(false);
        fixedcb.setEnabled(false);

        left.getDocument().addDocumentListener(this);
        plpanel.addPhotolinkerListListener(this);
        plpanel.addItemListener(this);
        
        errorBorder=BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.red,2),okayBorder);
        okayBorder = left.getBorder();
    }
    
    public void setOtherController(SBESeqInputController o) {
        this.other=o;
    }
    /**
     * Etwas wurde eingegeben.
     * @param e
     */
    private void handleSeqChange(){
        try {
            IamModifying=true;
            String seq = isRight?Helper.revPrimer(left.getText()):left.getText();
//          sind die eingegebenen seq. lang genug?
            int maxpl = Math.max(plpanel.getMaxSelectablePl(),minlen);        
            if(seq.length() < maxpl && seq.length() != 0) {
                String TOOSHORT="Sequence is too short, please enter at least "+maxpl+" characters!";
                plpanel.setEnabled(false);
                setToolTipAndBorder(left,TOOSHORT,true);
                return;
            }
            int pos=seq.indexOf('L');
            if(pos == -1 )  {      //kein L in der Eingabe
                replacedNukl = 0;
                String ltool=seq.length()==0?INSERT_TT:left.getText();
                setToolTipAndBorder(left,ltool,false);
                plpanel.setEnabled(true);
                plpanel.setSelectedPL(-1);//auto
                left.setEnabled(true);
                fixedcb.setSelected(false);
                fixedcb.setEnabled(false);
                if(other!=null)
                    other.setEnabled(true);
                return;
            }else {
                int br=Helper.getPosOfPl(seq);
                if(plpanel.hasPL(br)){//es gibt diesen PL
                    plpanel.setEnabled(replacedNukl!=0);
                    plpanel.setRekTooltip("Cleavable linker was defined by primer sequence input");
                    seq=left.getText();//koennte ja schon umgedreht sein
                    pos=seq.indexOf('L');
                    String tooltip = seq.substring(0,pos)+"[L]"+seq.substring(pos+1);
                    setToolTipAndBorder(left,tooltip,false);
                    fixedcb.setEnabled(true);
                    plpanel.setSelectedPL(br);
                    return;
                }else {//L an der falschen Position!
                    plpanel.setEnabled(false);
                    //plpanel.setSelectedPL(-1);
                    setToolTipAndBorder(left,"Cleavable linker out of bounds!",true);
                    fixedcb.setSelected(false);
                    fixedcb.setEnabled(false);
                }
            }
        }finally {
            IamModifying=false;
        }
    }
    private void handlePLPanelChange() {
        if(IamModifying==true)
            return;
        Object item=plpanel.getComboPL().getSelectedItem();
        if(item == null)
            return;
        String seq = isRight?Helper.revPrimer(left.getText()):left.getText();
        String newseq = seq;
        
        try {
            if(replacedNukl == 0) {//bisher kein pl
                if(item instanceof Integer) {//user hat nen pl gewaehlt
                    int pl = ((Integer)item).intValue();
                    if(seq.length() < pl) {
                        return;
                    }
                    char torepl = seq.charAt(seq.length() - pl);
                    if(torepl!='L') {
                        replacedNukl=torepl;
                        newseq=biochemie.util.Helper.replaceWithPL(seq,pl);
                    }
                }
            }else {//schon vorher was gewaehlt
                int pos = Helper.getPosOfPl(seq);
                if(pos == -1)
                    throw new IllegalStateException("There should be a PL in "+seq+"!");
                if(item instanceof String) {//auto
                    newseq=Helper.replaceNukl(seq,pos,replacedNukl);
                    replacedNukl = 0;
                }else {//pl veraendert
                    int newpl = ((Integer)item).intValue();
                    if(seq.length()< newpl) {
                        return;
                    }
                    char newrepl = seq.charAt(seq.length() - newpl);
                    if(newrepl == 'L')
                        return;
                    newseq = Helper.replaceNukl(seq, pos, replacedNukl);//ersetze alten PL durch gespeichertes Nukl.
                    newseq = Helper.replaceWithPL(newseq, newpl);
                    replacedNukl = newrepl;
                }
            }
        }finally {
            IamModifying = true;
            left.setText(isRight?Helper.revPrimer(newseq):newseq);
            IamModifying=false;
        }
        
    }
    /**
     * Liefert sequenz ohne das L, wenn vorhanden
     * @return
     */
    public String getSequenceWOL() {
        if(replacedNukl!=0) {
            String seq=left.getText();
            return Helper.replacePL(seq,replacedNukl);
        }
        return left.getText();
    }
    private void setEnabled(boolean b) {
        left.setEnabled(b);
        plpanel.setEnabled(b);
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
        handleSeqChange();
    }
    public void removeUpdate(DocumentEvent e) {
        if(IamModifying)
            return;
        handleSeqChange();
        
    }
    public void changedUpdate(DocumentEvent e) {
        handleSeqChange();
    }
    
    
    public void contentsChanged(ListDataEvent e) {
        if(e.getIndex0() == -1 && e.getIndex1() == -1)//nur selected, wird schon bearbeitet
            return;
        handlePLPanelChange();
    }
    
    public void intervalAdded(ListDataEvent e) {
        handlePLPanelChange();
    }
    
    public void intervalRemoved(ListDataEvent e) {
        handlePLPanelChange();
    }
    /**
     * @return Returns the isOkay.
     */
    public boolean isOkay() {
        return isOkay;
    }
    /**
     * Wird immer dann aufgerufen, wenn ein PL gesetzt wurde. Entweder vom user oder von uns selbst.
     */
    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED)
            handlePLPanelChange();
    }
    public char getReplNucl() {
        return replacedNukl;
    }
}

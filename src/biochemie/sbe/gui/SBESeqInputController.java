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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import biochemie.calcdalton.gui.PBSequenceField;
import biochemie.gui.PLSelectorPanel;
import biochemie.util.Helper;

/**
 * @author sdienst
 *
 */
public class SBESeqInputController implements DocumentListener, ListDataListener, ItemListener{
    private static final String INSERT_TT = "Insert 5' Sequence of the SNP (A,C,G,T, L) (L=Photolinker)";
    Border errorBorder;
    Border okayBorder;
    
    private SBESequenceTextField left;
    private PBSequenceField right;
    private PLSelectorPanel plpanel;
    private boolean isOkay;
    private char replacedNukl = 0;
    /**
     * Die Sequenz, die durch die beiden GUI-Elemente repraesentiert werden soll.
     */
    private String _seq;
    
    /**
     * Ich setze gerade den PL, nicht der user
     */
    private boolean IamModifying=false;
    private SBECandidatePanel panel;
    private JCheckBox fixedcb;
    
    public SBESeqInputController(SBECandidatePanel panel) {
        this.panel=panel;
        this.left=panel.getSeq5tf();
        this.right=panel.getSeq3tf();
        this.plpanel=panel.getPLSelectorPanel();
        
        this.fixedcb=panel.getFixedPrimerCB();
        fixedcb.setEnabled(false);
        fixedcb.setSelected(false);
        left.getDocument().addDocumentListener(this);
        plpanel.addPhotolinkerListListener(this);
        plpanel.addItemListener(this);
        
        errorBorder=BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.red,2),okayBorder);
        okayBorder = left.getBorder();
        
        _seq=left.getText();
    }
    /**
     * Etwas wurde eingegeben.
     * @param e
     */
    private void handleChange(DocumentEvent e){
        System.out.println("text: "+left.getText()+", "+_seq+", repl: "+replacedNukl);
        if(_seq.equals(left.getText()))
            return;//nix hat sich geaendert
        _seq=left.getText();
        String rtext=right.getText();


        plpanel.setEnabled(true);
        
        if(_seq==null) return;
        
        //sind die eingegebenen seq. lang genug?
        int maxpl = plpanel.getMaxSelectablePl();
        final String TOOSHORT="Sequence is too short, please enter at least "+maxpl+"characters!";
        
        if(_seq.length()<maxpl) {
            plpanel.setEnabled(false);
            setToolTipAndBorder(left,TOOSHORT,true);
            return;
        }
        if(rtext.length() != 0 && rtext.length()<maxpl) {
            setToolTipAndBorder(right,TOOSHORT,true);
            return;
        }
        //wurde ein L eingegeben?
        int pos=_seq.indexOf('L');
        
        if(pos == -1 )  {      //kein L in der Eingabe
            replacedNukl=0;
            String ltool=_seq.length()==0?INSERT_TT:_seq;
            setToolTipAndBorder(left,_seq,false);
            String rtool=rtext.length()==0?INSERT_TT:rtext;
            setToolTipAndBorder(right,rtool,false);
            plpanel.setEnabled(true);
            plpanel.setSelectedPL(-1);//auto
            left.setEnabled(true);
            right.setEnabled(true);
            fixedcb.setEnabled(false);
            fixedcb.setSelected(false);
            return;
        }
        
        right.setText("");
        right.setEnabled(false); //schalt mer aus, brauchen wir nicht mehr
        
        int br=Helper.getPosOfPl(_seq);
        if(plpanel.hasPL(br)){//es gibt diesen PL
            plpanel.setEnabled(replacedNukl!=0);
            plpanel.setRekTooltip("Photolinker was defined by primer sequence input");
            
            String tooltip = _seq.substring(0,pos)+"[L]"+_seq.substring(pos+1);
            setToolTipAndBorder(left,tooltip,false);
            setToolTipAndBorder(right,"",false);
            fixedcb.setEnabled(true);
            plpanel.setSelectedPL(br);
            return;
        }else {//L an der falschen Position!
            plpanel.setEnabled(false);
            plpanel.setSelectedPL(-1);
            setToolTipAndBorder(left,"Photolinkerposition out of bounds!",true);
            fixedcb.setEnabled(false);
            fixedcb.setSelected(false);
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
    /**
     * Wird immer dann aufgerufen, wenn ein PL gesetzt wurde. Entweder vom user oder von uns selbst.
     */
    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.DESELECTED)//geht uns nix an
            return;
        System.out.println("pl: "+e.getItem()+", "+_seq+", repl: "+replacedNukl);
        String newseq=_seq;
        try {
            Object item=e.getItem();
            if(replacedNukl == 0) {//bisher kein pl
                if(item instanceof Integer) {//user hat nen pl gewaehlt
                    int pl = ((Integer)item).intValue();
                    if(_seq.length() < pl) {
                        plpanel.setAuto();
                        return;
                    }
                    char torepl = _seq.charAt(_seq.length() - pl);
                    if(torepl!='L') {
                        replacedNukl=torepl;
                        right.setText("");
                        right.setEnabled(false);
                        newseq=biochemie.util.Helper.replacePL(_seq,pl);
                    }
                }
            }else {//schon vorher was gewaehlt
                int pos = Helper.getPosOfPl(_seq);
                if(pos == -1)
                    throw new IllegalStateException("There should be a PL in "+_seq+"!");
                
                if(item instanceof String) {//auto
                    right.setText("");
                    newseq=Helper.replaceNukl(_seq,pos,replacedNukl);
                    replacedNukl = 0;
                }else {//pl veraendert
                    int newpl = ((Integer)item).intValue();
                    if(_seq.length()< newpl) {
                        plpanel.setAuto();
                        return;
                    }
                    char newrepl = _seq.charAt(_seq.length() - newpl);
                    if(newpl == 'L')
                        return;
                    newseq = Helper.replaceNukl(_seq, pos, replacedNukl);//ersetze alten PL durch gespeichertes Nukl.
                    newseq = Helper.replacePL(newseq, newpl);
                    replacedNukl = newrepl;
                }
            }
        }finally {
            if(!left.getText().equals(newseq))
                left.setText(newseq);
                
        }
    }
}

/*
 * Created on 04.12.2004
 *
 */
package biochemie.sbe.gui;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
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
     * Ich setze gerade den PL, nicht der user
     */
    private boolean IamModifying=false;

    public SBESeqInputController(SBESequenceTextField left, PBSequenceField right, PLSelectorPanel pl) {
        this.left=left;
        this.right=right;
        this.plpanel=pl;
        left.getDocument().addDocumentListener(this);
        right.getDocument().addDocumentListener(this);

        pl.addPhotolinkerListListener(this);
        pl.addItemListener(this);
        errorBorder=BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.red,2),okayBorder);
        okayBorder = left.getBorder();
    }
    private void handleChange(DocumentEvent e){
        if(IamModifying)
            return;
        try {
            IamModifying = true;
            String ltext=left.getText();
            String rtext=right.getText();

            if(ltext==null && rtext == null) return;

            //sind die eingegebenen seq. lang genug?
            int maxpl = plpanel.getMaxSelectablePl();
            final String TOOSHORT="Sequence is too short, please enter at least "+maxpl+"characters!";

            if(ltext.length() !=0 && ltext.length()<maxpl) {
                setToolTipAndBorder(left,TOOSHORT,true);
                return;
            }
            if(rtext.length() != 0 && rtext.length()<maxpl) {
                setToolTipAndBorder(right,TOOSHORT,true);
                return;
            }
            //wurde ein L eingegeben?
            int pos=ltext.indexOf('L');

            if(pos == -1 )  {      //kein L in der Eingabe
                String ltool=ltext.length()==0?INSERT_TT:ltext;
                setToolTipAndBorder(left,ltext,false);
                String rtool=rtext.length()==0?INSERT_TT:rtext;
                setToolTipAndBorder(right,rtool,false);
                left.setEnabled(true);
                right.setEnabled(true);
                plpanel.setEnabled(true);
                //plpanel.setAuto();
                return;
            }
            if(replacedNukl != 0) {
                String tooltip = ltext.substring(0,pos)+"[L]"+ltext.substring(pos+1);
                setToolTipAndBorder(left,tooltip,false);
                return;//wurde schon alles erledigt, geht mich nix an :)
            }
            //Also wurde ein L vom user eingegeben

            right.setText("");
            right.setEnabled(false); //schalt mer aus, brauchen wir nicht mehr

            int br=ltext.length() - pos;
            if(plpanel.setSelectedPL(br)){//es gibt diesen PL
                plpanel.setEnabled(false);
                plpanel.setRekTooltip("Photolinker was defined by primer sequence input");

                String tooltip = ltext.substring(0,pos)+"[L]"+ltext.substring(pos+1);
                setToolTipAndBorder(left,tooltip,false);
                setToolTipAndBorder(right,"",false);
                return;
            }else {
                plpanel.setAuto();
                plpanel.setEnabled(true);
                setToolTipAndBorder(left,"Photolinkerposition out of bounds!",true);
            }
        }finally {
            IamModifying = false;
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
        if(IamModifying || e.getStateChange() == ItemEvent.DESELECTED)//geht uns nix an
            return;
        String newseq=left.getText();

        try {
            IamModifying=true;
            Object item=e.getItem();
            if(replacedNukl == 0) {//bisher kein pl
                if(item instanceof Integer) {//user hat nen pl gewaehlt
                    int pl = ((Integer)item).intValue();
                    String ltext = left.getText();
                    if(ltext.length() < pl) {
                        plpanel.setAuto();
                        return;
                    }
                    right.setText("");
                    right.setEnabled(false);
                    replacedNukl = ltext.charAt(ltext.length() - pl);
                    newseq=biochemie.util.Helper.replacePL(ltext,pl);
                }
            }else {//schon vorher was gewaehlt
                String ltext = left.getText();
                int pos = Helper.getPLFromSeq(ltext);
                if(pos == -1)
                    throw new IllegalStateException("There should be a PL in "+ltext+"!");

                if(item instanceof String) {//auto
                    right.setText("");
                    newseq=Helper.replaceNukl(ltext,pos,replacedNukl);
                    replacedNukl = 0;
                }else {//pl veraendert
                    int newpl = ((Integer)item).intValue();
                    if(ltext.length()< newpl) {
                        plpanel.setAuto();
                        return;
                    }
                    char newrepl = ltext.charAt(ltext.length() - newpl);
                    ltext = Helper.replaceNukl(ltext, pos, replacedNukl);//ersetze alten PL durch gespeichertes Nukl.
                    newseq = Helper.replacePL(ltext, newpl);
                    replacedNukl = newrepl;
                }
            }
        }finally {
            IamModifying=false;
            left.setText(newseq);
        }
    }
}

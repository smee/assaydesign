/*
 * Created on 18.11.2004
 *
 */
package biochemie.sbe.gui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import biochemie.calcdalton.gui.PBSequenceField;
import biochemie.util.Helper;

/**
 * Like PBSequenceField with a possible L within.
 * @author Steffen Dienst
 *
 */
public class SBESequenceTextField extends PBSequenceField  {

    public SBESequenceTextField(){
		super(100,true,"ACGTLacgt");
		setUniqueChars("L");
	}



	public String getSequence(){
        if(isEnabled())
            return Helper.getNuklFromString(super.getText().replace('L','A'));
        return "";
	}
	/* (non-Javadoc)
	 * @see biochemie.calcdalton.gui.PBSequenceField#setValidChars(java.lang.String)
	 */
	public void setValidChars(String validChars) {
		if(validChars.indexOf('L')== -1)
			validChars+='L';
		super.setValidChars(validChars);
	}



    protected Document createDefaultModel() {
        return new PLDocument();
    }
    /**
     * Liefert View auf diese Sequenz.
     * @return
     */
    public MutableComboBoxModel getComboBoxModel() {
        return (MutableComboBoxModel) getDocument();
    }
    
    /**
     * Model fuer SBESequenceTextfield UND eine Combobox. CB stellt View auf TextField dar, so dass
     * beide immer denselben PL, wenn vorhanden anzeigen.
     * @author sdienst
     *
     */
    public class PLDocument extends  CharacterDocument implements MutableComboBoxModel{
        /**
         * Groesstmoeglicher PL
         */
        int maxpl;
        private MutableComboBoxModel model;
        /**
         * Zeichen, welches durch L verdeckt wird. 0 wenn unbekannt
         */
        char replacedNukl;

        public PLDocument() {

            model=new DefaultComboBoxModel(new Object[] {"auto"});
            maxpl=Integer.MIN_VALUE;
        }
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            super.insertString(offs, str, a);
            int posofpl=Helper.getPosOfPl(getText(0,getLength()));
            if(posofpl == -1)
                setSelectedItem("auto");
            else
                setSelectedItem(new Integer(posofpl));
        }

        public Object getSelectedItem() {
            return model.getSelectedItem();
        }
        public void setSelectedItem(Object anItem) {
            if(model.getSelectedItem().equals(anItem))//brauch nix machen
                return;
            if(anItem.equals("auto")) {
                if(setPlTo(-1))//Loesche L aus der Sequenz
                    model.setSelectedItem("auto");
            }else {
                int pos=((Integer)anItem).intValue();
                if(pos<=maxpl && pos != -1 && modelContains(pos)) {//gibts diesen PL und ist die Seq. lang genug?
                    model.setSelectedItem(anItem);
                    setPlTo(pos);
                }else {
                    //throw new IllegalStateException("Seq. too short or unknown pl("+pos+")");
                }
            }
        }
        /**
         * Setzt das L im TextField entsprechend.
         * @param i
         * @return true, wenn i.O., false sonst
         */
        private boolean setPlTo(int pl) {
            try {
                String seq = getText(0, getLength());
                if (pl == -1) {//setze auf auto
                    if (replacedNukl != 0) {
                        seq = Helper.replaceNukl(seq, Helper.getPosOfPl(seq), replacedNukl);
                        replacedNukl=0;
                        setText(seq);
                } 
                }else {
                    if(replacedNukl !=0) {
                        seq=Helper.replaceNukl(seq, Helper.getPosOfPl(seq),replacedNukl);
                    }
                    char charAtPl=seq.charAt(seq.length() - pl);
                    if(charAtPl != 'L') {
                        replacedNukl=charAtPl;
                        seq=Helper.replacePL(seq,pl);
                        setText(seq);
                    }
                }
            } catch (BadLocationException e) {
            }
            return true;
        }
        private boolean modelContains(int pos) {
            for (int i=1; i < model.getSize();i++) {
                Integer pl = (Integer) model.getElementAt(i);
                if(pl.intValue()==pos)
                    return true;
            }
            return false;
        }
        /**
         * 
         * @return true, wenn der User das L selbst eingegeben hat.
         */
        public boolean hasUsergivenPL() {
            try {
                return replacedNukl==0 && Helper.getPosOfPl(getText(0,getLength())) != -1;
            } catch (BadLocationException e) {
            }
            return false;
        }
        public int getMaxPL() {
            return maxpl;
        }
        public int getSize() {
            return model.getSize();
        }
        public Object getElementAt(int index) {
            return model.getElementAt(index);
        }
        public void addListDataListener(ListDataListener l) {
            model.addListDataListener(l);
        }
        public void removeListDataListener(ListDataListener l) {
            model.removeListDataListener(l);
        }
        public void removeElementAt(int index) {
            model.removeElementAt(index);
            updateMaxPL();
        }
        public void addElement(Object obj) {
            model.addElement(obj);
            updateMaxPL();
        }
        public void removeElement(Object obj) {
            model.removeElement(obj);
            updateMaxPL();
        }
        public void insertElementAt(Object obj, int index) {
            model.insertElementAt(obj,index);
            updateMaxPL();
        }
        private void updateMaxPL() {
            maxpl=Integer.MIN_VALUE;
            for (int i=1; i < model.getSize();i++) {//0 ist auto
                Integer pl = (Integer) model.getElementAt(i);
                maxpl=Math.max(maxpl, pl.intValue());
            }
        }
    }

}

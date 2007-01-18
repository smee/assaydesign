package biochemie.calcdalton.gui;
import info.clearthought.layout.TableLayout;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.ArrayUtils;

import biochemie.gui.MyPanel;
import biochemie.gui.PLSelectorPanel;
import biochemie.sbe.gui.SBESeqInputController;
import biochemie.sbe.gui.SBESequenceTextField;
import biochemie.util.Helper;

public class SBEPanel extends MyPanel
{
    protected JPanel jp_sequence;
    protected JPanel jp_bruchstelle;
    protected JPanel jp_anhang;
    protected JCheckBox cb_G;
    protected JLabel lbLabel3;
    protected JLabel lbLabel2;
    protected JCheckBox cb_T;
    protected JCheckBox cb_A;
    protected JCheckBox cb_C;
    protected JLabel lbLabel1;
    protected JLabel lbLabel0;
    protected JLabel lbLabel4;
    protected JPanel pnSpaltstelle;
    protected PLSelectorPanel plpanel;
    protected int nummer;
    protected JLabel lbName;
    protected JLabel lbSeqTf;
    protected JTextField tfName;
    public SBESequenceTextField tfSequence;
    private SBESeqInputController controller;
    private class MyChangeListener implements DocumentListener, ChangeListener{
        public void changedUpdate(DocumentEvent e) {
            dirty();
        }
        public void insertUpdate(DocumentEvent e) {
            dirty();
        }
        public void removeUpdate(DocumentEvent e) {
            dirty();            
        }
        public void stateChanged(ChangeEvent e) {
            dirty();
        }    
    };
    final private MyChangeListener dl=new MyChangeListener();;
    /**
     * Konstruktor erwartet individuelle Nummerierung.
     * @param num
     */
    public SBEPanel(int num){
        nummer = num;
        double border=5;
        double hg=10;   //gap between
        double lg=5;    //gap between label and textfield
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;

        double[][] size=
            {{border,f,hg,p,hg,p,border},
             {border,p,border}};
        TableLayout layout=new TableLayout(size);
        setLayout(layout);

        //Erstes Panel
        double[][] seqPanelSize=
            {{border,p,lg,.20,hg,p,lg,f,border},
             {border,p,border}};
        layout=new TableLayout(seqPanelSize);
        jp_sequence = new JPanel(layout);
        jp_sequence.setBorder(BorderFactory.createTitledBorder("SBE-Primer " + num + ':'));
        jp_sequence.add(new JLabel("ID:   "),"1,1");
        tfName=new JTextField("SBE "+num) ;
        tfName.setToolTipText("Please enter the name of the SBE-primer sequence.");
        tfName.getDocument().addDocumentListener(dl);
        jp_sequence.add(tfName,"3,1");
        jp_sequence.add(new JLabel("   SBE-Primer:  "),"5,1");
        tfSequence = new SBESequenceTextField();
        tfSequence.getDocument().addDocumentListener(dl);
        tfSequence.setValidChars("acgtACGT");
        tfSequence.setMaxLen(150);
        tfSequence.setUpper(true);
        tfSequence.setToolTipText("Please insert a SBE-primer sequence with length>="+CDConfig.getInstance().getMaxBruchstelle()+" (Strg+V)");
        tfSequence.setPreferredSize(new Dimension(400,20));
        jp_sequence.add(tfSequence,"7, 1");
        add(jp_sequence,"1,1");
        
        //nr. 2
        jp_anhang = new JPanel();
        jp_anhang.setBorder(BorderFactory.createTitledBorder("ddNTPs"));
        jp_anhang.setToolTipText("Please specify ddNTPs for this SBE");
        double[][] anhangSize=
            {{0,p,0,p,0}
             ,{0,p,0,p,0}};
        jp_anhang.setLayout(new TableLayout(anhangSize));
        cb_A = new JCheckBox("A",true);
        cb_A.addChangeListener(dl);
        cb_A.setToolTipText("Please specify ddNTPs for this SBE");
        cb_C = new JCheckBox("C",true);
        cb_C.addChangeListener(dl);
        cb_C.setToolTipText("Please specify ddNTPs for this SBE");
        cb_G = new JCheckBox("G",true);
        cb_G.addChangeListener(dl);
        cb_G.setToolTipText("Please specify ddNTPs for this SBE");
        cb_T = new JCheckBox("T",true);
        cb_T.addChangeListener(dl);
        cb_T.setToolTipText("Please specify ddNTPs for this SBE");
        jp_anhang.add(cb_A,"1,1");
        jp_anhang.add(cb_C,"1,3");
        jp_anhang.add(cb_G,"3,1");
        jp_anhang.add(cb_T,"3,3");
        add(jp_anhang,"3,1");
        //nr. 3
        double[][] spaltSize={{p,30},{p}};
        plpanel=new PLSelectorPanel();
        plpanel.setValues(CDConfig.getInstance().getBruchStellenArray());
        plpanel.setRekTooltip("Specify position of the cleavable linkers, auto means the value should be choosen by the program.");

        add(plpanel,"5,1,C,C");
        
        controller=new SBESeqInputController(tfSequence,plpanel,new JCheckBox(),0,true);
        setUnchanged();
    }
    public String getSequenceWOL() {
        return controller.getSequenceWOL();
    }

    /**
     * Gibt String[] zurück mit: Sequenz,Anhang1...
     * @return
     */
    public String[] getPrimer()
    {
//        if(CDConfig.getInstance().getConfiguration().getCalcDaltonAllExtensions()) {
            String[] arr= new String[5];
            arr[0]=tfSequence.getSequence();
            arr[1]=cb_A.isSelected()==false?">A":"A";
            arr[2]=cb_C.isSelected()==false?">C":"C";
            arr[3]=cb_G.isSelected()==false?">G":"G";
            arr[4]=cb_T.isSelected()==false?">T":"T";
            return arr;
//        }
//        int i = 1;
//        if(cb_A.isSelected())
//            i++;
//        if(cb_C.isSelected())
//            i++;
//        if(cb_G.isSelected())
//            i++;
//        if(cb_T.isSelected())
//            i++;
//        String[] ergebnis = new String[i];
//        i = 1;
//        ergebnis[0] = Helper.getNuklFromString(tfSequence.getSequence());
//        if(cb_A.isSelected())
//        {
//            ergebnis[i] = "A";
//            i++;
//        }
//        if(cb_C.isSelected())
//        {
//            ergebnis[i] = "C";
//            i++;
//        }
//        if(cb_G.isSelected())
//        {
//            ergebnis[i] = "G";
//            i++;
//        }
//        if(cb_T.isSelected())
//        {
//            ergebnis[i] = "T";
//            i++;
//        }
//        return ergebnis;
    }


    /**
     * Methode gibt zurück, welche Spaltstelle als fest gewählt wurde.
     *
     * @return SpaltstelleIndex wenn gewählt,<0 sonst
     */
    public int getFestenAnhangIndex() {
        return ArrayUtils.indexOf(CDConfig.getInstance().getBruchStellenArray(),plpanel.getSelectedValue());
    }

    public String getName() {
        return tfName.getText();
    }
    /**
     * liest die comboboxen für feste bruchstellen neu ein.
     */
    public void refreshData() {
        plpanel.setValues(CDConfig.getInstance().getBruchStellenArray());
    }
    public void setSelectedPL(int pl) {
        plpanel.setSelectedValue(pl);
    }
}
package biochemie.calcdalton.gui;
import info.clearthought.layout.TableLayout;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.ArrayUtils;

import biochemie.util.Helper;

public class SBEPanel extends JPanel
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
    protected JComboBox cmbFest;
    Vector select;
    protected int nummer;
    protected JLabel lbName;
    protected JLabel lbSeqTf;
    protected JTextField tfName;
    public PBSequenceField tfSequence;
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

        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"Single Base Extension " + num + ':'));
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
        jp_sequence.add(tfName,"3,1");
        jp_sequence.add(new JLabel("   SBE-Primer:  "),"5,1");
        tfSequence = new PBSequenceField(150,true,"acgtACGT");
//        tfSequence.setValidChars("acgtACGT");
//        tfSequence.setMaxLen(150);
//        tfSequence.setUpper(true);
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
        cb_A.setToolTipText("Please specify ddNTPs for this SBE");
        cb_C = new JCheckBox("C",true);
        cb_C.setToolTipText("Please specify ddNTPs for this SBE");
        cb_G = new JCheckBox("G",true);
        cb_G.setToolTipText("Please specify ddNTPs for this SBE");
        cb_T = new JCheckBox("T",true);
        cb_T.setToolTipText("Please specify ddNTPs for this SBE");
        jp_anhang.add(cb_A,"1,1");
        jp_anhang.add(cb_C,"1,3");
        jp_anhang.add(cb_G,"3,1");
        jp_anhang.add(cb_T,"3,3");
        add(jp_anhang,"3,1");
        //nr. 3
        double[][] spaltSize={{p,30},{p}};
        pnSpaltstelle = new JPanel(new TableLayout(spaltSize));
        pnSpaltstelle.setBorder( BorderFactory.createTitledBorder( "Photolinker at:" ) );
        select=new Vector(Arrays.asList(ArrayUtils.toObject(CDConfig.getInstance().getBruchStellenArray())));
        cmbFest = new JComboBox(select);
        cmbFest.setToolTipText("Specify position of the Photolinker, auto means the value should be choosen by the program.");
        cmbFest.insertItemAt("auto",0);
        cmbFest.setSelectedIndex(0);
        pnSpaltstelle.setToolTipText("Specify position of the Photolinker, auto means the value should be choosen by the program.");
        pnSpaltstelle.add(cmbFest,"0,0");
        add(pnSpaltstelle,"5,1,C,C");
    }


    /**
     * Gibt String[] zurück mit: Sequenz,Anhang1...
     * @return
     */
    public String[] getPrimer()
    {
        int i = 1;
        if(cb_A.isSelected())
            i++;
        if(cb_C.isSelected())
            i++;
        if(cb_G.isSelected())
            i++;
        if(cb_T.isSelected())
            i++;
        String[] ergebnis = new String[i];
        i = 1;
        ergebnis[0] = Helper.getNuklFromString(tfSequence.getText());
        if(cb_A.isSelected())
        {
            ergebnis[i] = "A";
            i++;
        }
        if(cb_C.isSelected())
        {
            ergebnis[i] = "C";
            i++;
        }
        if(cb_G.isSelected())
        {
            ergebnis[i] = "G";
            i++;
        }
        if(cb_T.isSelected())
        {
            ergebnis[i] = "T";
            i++;
        }
        return ergebnis;
    }


    /**
     * Methode gibt zurück, welche Spaltstelle als fest gewählt wurde.
     *
     * @return SpaltstelleIndex wenn gewählt,<0 sonst
     */
    public int getFestenAnhangIndex() {
        if(0 < cmbFest.getSelectedIndex()) {
            return cmbFest.getSelectedIndex()-1;
        }
        return -1;
    }

    public String getName() {
        return tfName.getText();
    }
    /**
     * liest die comboboxen für feste bruchstellen neu ein.
     */
    public void refreshData() {
        select=new Vector(Arrays.asList(ArrayUtils.toObject(CDConfig.getInstance().getBruchStellenArray())));
        int index=cmbFest.getSelectedIndex();
        cmbFest.removeAllItems();
        cmbFest.insertItemAt("auto",0);
        for(int i=0;i<select.size();i++)
            cmbFest.addItem(select.get(i));
        cmbFest.setSelectedIndex(-1);
        if(index<select.size())
            cmbFest.setSelectedIndex(index);
        else
            cmbFest.setSelectedIndex(0);
        tfSequence.setToolTipText("Please insert a SBE-primer sequence with length>="+CDConfig.getInstance().getMaxBruchstelle());
        cmbFest.repaint();
    }
    public void setSelectedPL(int pl) {
        int pos=0;
        for (Iterator iter = select.iterator(); iter.hasNext();pos++) {
            Object o = iter.next();
            
            if(o instanceof Integer && ((Integer)o).intValue()==pl) {
                cmbFest.setSelectedIndex(pos);
                return;
            }
        }
    }
}
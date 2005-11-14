package biochemie.calcdalton.gui;
/*

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import biochemie.calcdalton.CalcDaltonOptions;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
public class CDMassesConfigPanel extends JPanel {

	private JLabel jLabel = null;
	final private Map primerMap;
    final private Map anhangMap;
    private double plMass;
    private MapTablePanel addonTable;
    private MapTablePanel primerTable;
    private JRadioButton jRadioButton = null;
    private JRadioButton jRadioButton1 = null;
    private JRadioButton jRadioButton2 = null;
    private JRadioButton jRadioButton3 = null;
    private PBSequenceField customPlTf = null;
    private JTextField wenzelTf = null;
    private JTextField liTf = null;
    private JTextField shceTf = null;
    /**
     * This is the default constructor
     */
    public CDMassesConfigPanel(Map primerMap, Map anhangMap, double plMass) {
    	super();
        this.primerMap=primerMap;
        this.anhangMap=anhangMap;
        this.plMass=plMass;
    	initialize();
    }
    public CDMassesConfigPanel() {
        this(getDefaultPrimermassMap(), getDefaultAddonMassMap(),getDefaultPLMass());
    }
    /**
     * This method initializes this
     * 
     * @return void
     */
    private  void initialize() {
    	GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
    	gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
    	gridBagConstraints5.gridy = 3;
    	gridBagConstraints5.weightx = 1.0;
    	gridBagConstraints5.gridx = 1;
    	GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
    	gridBagConstraints41.fill = java.awt.GridBagConstraints.HORIZONTAL;
    	gridBagConstraints41.gridy = 2;
    	gridBagConstraints41.weightx = 1.0;
    	gridBagConstraints41.gridx = 1;
    	GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
    	gridBagConstraints31.fill = java.awt.GridBagConstraints.HORIZONTAL;
    	gridBagConstraints31.gridy = 1;
    	gridBagConstraints31.weightx = 1.0;
    	gridBagConstraints31.gridx = 1;
    	GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
    	gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
    	gridBagConstraints21.gridy = 4;
    	gridBagConstraints21.weightx = 1.0;
    	gridBagConstraints21.gridx = 1;
    	GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
    	gridBagConstraints11.gridx = 0;
    	gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
    	gridBagConstraints11.insets = new java.awt.Insets(10,10,0,10);
    	gridBagConstraints11.gridy = 4;
    	GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
    	gridBagConstraints3.gridx = 0;
    	gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
    	gridBagConstraints3.insets = new java.awt.Insets(10,10,0,10);
    	gridBagConstraints3.gridy = 3;
    	GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
    	gridBagConstraints1.gridx = 0;
    	gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    	gridBagConstraints1.insets = new java.awt.Insets(10,10,0,10);
    	gridBagConstraints1.gridy = 2;
    	GridBagConstraints gridBagConstraints = new GridBagConstraints();
    	gridBagConstraints.gridx = 0;
    	gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    	gridBagConstraints.insets = new java.awt.Insets(10,10,0,10);
    	gridBagConstraints.gridy = 1;
    	GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
    	jLabel = new JLabel();
    	GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
    	GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
    	this.setLayout(new GridBagLayout());
    	this.setSize(457, 421);
    	gridBagConstraints2.gridx = 0;
    	gridBagConstraints2.gridy = 0;
    	gridBagConstraints2.insets = new java.awt.Insets(10,10,0,0);
    	jLabel.setText("Mass of cleavable linker after cleavage");
    	gridBagConstraints4.gridx = 0;
    	gridBagConstraints4.gridy = 5;
    	gridBagConstraints4.weightx = 1.0;
    	gridBagConstraints4.weighty = 1.0;
    	gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
    	gridBagConstraints4.gridwidth = 2;
    	gridBagConstraints4.insets = new java.awt.Insets(10,10,0,10);
    	gridBagConstraints8.gridx = 0;
    	gridBagConstraints8.gridy = 7;
    	gridBagConstraints8.weightx = 1.0;
    	gridBagConstraints8.weighty = 1.0;
    	gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
    	gridBagConstraints8.gridwidth = 2;
    	gridBagConstraints8.insets = new java.awt.Insets(10,10,0,10);
    	this.add(jLabel, gridBagConstraints2);
    	this.add(getPrimerMassesMapTable(), gridBagConstraints4);
    	this.add(getAddonMassesMapTable(), gridBagConstraints8);
        ButtonGroup bg=new ButtonGroup();
        bg.add(getJRadioButton());
        bg.add(getJRadioButton1());
        bg.add(getJRadioButton2());
        bg.add(getJRadioButton3());
        ItemListener l=new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JRadioButton btn=(JRadioButton) e.getSource();
                getCustomPlTf().setEnabled(btn==getJRadioButton3());
            }
        };
        getJRadioButton().addItemListener(l);
        getJRadioButton1().addItemListener(l);
        getJRadioButton2().addItemListener(l);
        getJRadioButton3().addItemListener(l);
        getJRadioButton3().setSelected(true);
    	this.add(getJRadioButton(), gridBagConstraints);
    	this.add(getJRadioButton1(), gridBagConstraints1);
    	this.add(getJRadioButton2(), gridBagConstraints3);
    	this.add(getJRadioButton3(), gridBagConstraints11);
    	this.add(getCustomPlTf(), gridBagConstraints21);
    	this.add(getWenzelTf(), gridBagConstraints31);
    	this.add(getLiTf(), gridBagConstraints41);
    	this.add(getShceTf(), gridBagConstraints5);
        String wenzelTip="<html>Wenzel T, Elssner T, Fahr K, Bimmler J, Richter S, Thomas I, Kostrzewa M,2003:Genosnip: SNP<br>" + 
                "genotyping by MALDI-TOF MS using photocleavable oligonucleotides.<br>" + 
                "Nucleosides Nucleotides Nucleic Acids. 2003 May-Aug;22(5-8):1579-81)<br></html";
        getWenzelTf().setToolTipText(wenzelTip);
        getJRadioButton().setToolTipText(wenzelTip);
        String leTip="<html>Li J, Butler JM, Tan Y, Lin H, Royer S, Ohler L, Shaler TA, Hunter JM, Pollart DJ, Monforte JA, Becker<br>" + 
                "CH, 1999: Single nucleotide polymorphism determination using primer extension and time-of-flight<br>" + 
                "mass spectrometry.Electrophoresis. Jun;20(6):1258-65.)</html>";
        getLiTf().setToolTipText(leTip);
        getJRadioButton1().setToolTipText(leTip);
        String sheTip="<html>Shchepinov MS, Denissenko MF, Smylie KJ, Worl RJ, Leppin AL, Cantor CR, Rodi CP.,2001:Matrix-<br>" + 
                "induced fragmentation of P3\'-N5\' phosphoramidate-containing DNA: high-throughput MALDI-TOF<br>" + 
                "analysis of genomic sequence polymorphisms.Nucleic Acids Res. Sep 15;29(18):3864-72.</html>";
        getShceTf().setToolTipText(sheTip);
        getJRadioButton2().setToolTipText(sheTip);
    }

	private MapTablePanel getAddonMassesMapTable() {
	    if(addonTable==null) {
	        addonTable=new MapTablePanel("Nucleotide (ddNTP)","Mass of ddNTP in Dalton","Nucleotide:","Mass:");
            addonTable.setMap(anhangMap);
            addonTable.setTitle("Masses of extension nucleotides (ddNTPs)");
        }
        return addonTable;
    }

    private MapTablePanel getPrimerMassesMapTable() {
        if(primerTable==null) {
            primerTable=new MapTablePanel("Nucleotide (dNTP)","Mass of dNTP in Dalton","Nucleotide:","Mass:");
            primerTable.setMap(primerMap);
            primerTable.setTitle("Masses of primer nucleotides (dNTPs)");
        }
        return primerTable;
    }

    /**
     * This method initializes jRadioButton	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getJRadioButton() {
        if (jRadioButton == null) {
            jRadioButton = new JRadioButton();
            jRadioButton.setText("...according to Wenzel et al. ");
        }
        return jRadioButton;
    }
    /**
     * This method initializes jRadioButton1	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getJRadioButton1() {
        if (jRadioButton1 == null) {
            jRadioButton1 = new JRadioButton();
            jRadioButton1.setText("...according Li et al.");
        }
        return jRadioButton1;
    }
    /**
     * This method initializes jRadioButton2	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getJRadioButton2() {
        if (jRadioButton2 == null) {
            jRadioButton2 = new JRadioButton();
            jRadioButton2.setText("...according to Shchepinov et al.");
        }
        return jRadioButton2;
    }
    /**
     * This method initializes jRadioButton3	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getJRadioButton3() {
        if (jRadioButton3 == null) {
            jRadioButton3 = new JRadioButton();
            jRadioButton3.setText("...custom");
        }
        return jRadioButton3;
    }
    /**
     * This method initializes PBSequenceField	
     * 	
     * @return biochemie.calcdalton.gui.PBSequenceField	
     */
    private PBSequenceField getCustomPlTf() {
        if (customPlTf == null) {
            customPlTf = new PBSequenceField(10,false,PBSequenceField.NUMBERS);
            customPlTf.setUniqueChars(".-");
            customPlTf.setText(Double.toString(plMass));
        }
        return customPlTf;
    }
    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getWenzelTf() {
        if (wenzelTf == null) {
            wenzelTf = new JTextField("18.02");
            wenzelTf.setEditable(false);
        }
        return wenzelTf;
    }
    /**
     * This method initializes jTextField1	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getLiTf() {
        if (liTf == null) {
            liTf = new JTextField("18.02");
            liTf.setEditable(false);
        }
        return liTf;
    }
    /**
     * This method initializes jTextField2	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getShceTf() {
        if (shceTf == null) {
            shceTf = new JTextField("-61.02");
            shceTf.setEditable(false);
        }
        return shceTf;
    }
    public static void main(String[] args) {
        JFrame f=new JFrame();
        Map primerMap=new HashMap();
        primerMap.put(new Character('A'),new Double(313.2071));
        primerMap.put(new Character('G'),new Double(329.2066));
        primerMap.put(new Character('C'),new Double(289.1823));
        primerMap.put(new Character('T'),new Double(304.1937));
        Map anhangMap=new HashMap();
        anhangMap.put(new Character('A'),new Double(297.2072));
        anhangMap.put(new Character('G'),new Double(313.2066));
        anhangMap.put(new Character('C'),new Double(273.1824));
        anhangMap.put(new Character('T'),new Double(288.1937));
        System.out.println(primerMap);
        System.out.println(anhangMap);
        f.getContentPane().add(new CDMassesConfigPanel(primerMap, anhangMap,18.02));
        f.pack();
        f.setVisible(true);
    }
    public void setValuesFrom(CalcDaltonOptions cfg) {
        getPrimerMassesMapTable().setMap(cfg.getCalcDaltonPrimerMassesMap());
        getAddonMassesMapTable().setMap(cfg.getCalcDaltonAddonMassesMap());
        switch (cfg.getCalcDaltonSelectedPLMass()) {
        case 0:
            getJRadioButton().setSelected(true);
            break;
        case 1:
            getJRadioButton1().setSelected(true);
            break;
        case 2:
            getJRadioButton2().setSelected(true);
            break;
        case 3:
            getJRadioButton3().setSelected(true);
            getCustomPlTf().setText(""+cfg.getCalcDaltonPLMass());
            break;
        default:
            break;
        }
    }

    public void saveToConfig(CalcDaltonOptions cfg) {
        cfg.setCalcDaltonAddonMassesMap(getAddonMassesMapTable().getMap());
        cfg.setCalcDaltonPrimerMassesMap(getPrimerMassesMapTable().getMap());
        cfg.setCalcDaltonPLMass(Double.parseDouble(getCustomPlTf().getText()));
        if(getJRadioButton().isSelected())
            cfg.setCalcDaltonSelectedPLMass(0);
        if(getJRadioButton1().isSelected())
            cfg.setCalcDaltonSelectedPLMass(1);
        if(getJRadioButton2().isSelected())
            cfg.setCalcDaltonSelectedPLMass(2);
        if(getJRadioButton3().isSelected())
            cfg.setCalcDaltonSelectedPLMass(3);
    }
    public static Map getDefaultPrimermassMap() {
        Map primerMap=new HashMap();
        primerMap.put(new Character('A'),new Double(313.2071));
        primerMap.put(new Character('G'),new Double(329.2066));
        primerMap.put(new Character('C'),new Double(289.1823));
        primerMap.put(new Character('T'),new Double(304.1937));
        return primerMap;
    }
    public static Map getDefaultAddonMassMap() {
        Map anhangMap=new HashMap();
        anhangMap.put(new Character('A'),new Double(297.2072));
        anhangMap.put(new Character('G'),new Double(313.2066));
        anhangMap.put(new Character('C'),new Double(273.1824));
        anhangMap.put(new Character('T'),new Double(288.1937));
        return anhangMap;
    }
    public static double getDefaultPLMass() {
        return 18.02;
    }
     }  //  @jve:decl-index=0:visual-constraint="10,10"

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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import biochemie.calcdalton.CalcDaltonOptions;
public class CDMassesConfigPanel extends JPanel {

	private JLabel jLabel = null;
	private PBSequenceField plMassTf = null;
    final private Map primerMap;
    final private Map anhangMap;
    private double plMass;
    private MapTablePanel addonTable;
    private MapTablePanel primerTable;
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
    	GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
    	jLabel = new JLabel();
    	GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
    	GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
    	GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
    	this.setLayout(new GridBagLayout());
    	this.setSize(457, 421);
    	gridBagConstraints2.gridx = 0;
    	gridBagConstraints2.gridy = 0;
    	gridBagConstraints2.insets = new java.awt.Insets(10,10,10,10);
    	jLabel.setText("Mass of the photolinker");
    	gridBagConstraints3.gridx = 1;
    	gridBagConstraints3.gridy = 0;
    	gridBagConstraints3.weightx = 1.0;
    	gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
    	gridBagConstraints3.insets = new java.awt.Insets(10,10,10,10);
    	gridBagConstraints4.gridx = 0;
    	gridBagConstraints4.gridy = 2;
    	gridBagConstraints4.weightx = 1.0;
    	gridBagConstraints4.weighty = 1.0;
    	gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
    	gridBagConstraints4.gridwidth = 2;
    	gridBagConstraints4.insets = new java.awt.Insets(10,10,10,10);
    	gridBagConstraints8.gridx = 0;
    	gridBagConstraints8.gridy = 4;
    	gridBagConstraints8.weightx = 1.0;
    	gridBagConstraints8.weighty = 1.0;
    	gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
    	gridBagConstraints8.gridwidth = 2;
    	gridBagConstraints8.insets = new java.awt.Insets(10,10,10,10);
    	this.add(jLabel, gridBagConstraints2);
    	this.add(getPlMassTf(), gridBagConstraints3);
    	this.add(getPrimerMassesMapTable(), gridBagConstraints4);
    	this.add(getAddonMassesMapTable(), gridBagConstraints8);
    }

	private MapTablePanel getAddonMassesMapTable() {
	    if(addonTable==null) {
	        addonTable=new MapTablePanel("Nucleotide","Mass of addon in dalton","Nucleotide:","Mass:");
            addonTable.setMap(anhangMap);
            addonTable.setTitle("Masses of addon nucleotides");
        }
        return addonTable;
    }

    private MapTablePanel getPrimerMassesMapTable() {
        if(primerTable==null) {
            primerTable=new MapTablePanel("Nucleotide","Mass of nucl. in dalton","Nucleotide:","Mass:");
            primerTable.setMap(primerMap);
            primerTable.setTitle("Masses of primer nucleotides");
        }
        return primerTable;
    }

    /**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private PBSequenceField getPlMassTf() {
		if (plMassTf == null) {
			plMassTf = new PBSequenceField(10,false,PBSequenceField.NUMBERS);
            plMassTf.setUniqueChars(".");
            plMassTf.setText(Double.toString(plMass));
		}
		return plMassTf;
	}

    public static void main(String[] args) {
        JFrame f=new JFrame();
        Map primerMap=new HashMap();
        primerMap.put(Character.valueOf('A'),Double.valueOf(313.2071));
        primerMap.put(Character.valueOf('G'),Double.valueOf(329.2066));
        primerMap.put(Character.valueOf('C'),Double.valueOf(289.1823));
        primerMap.put(Character.valueOf('T'),Double.valueOf(304.1937));
        Map anhangMap=new HashMap();
        anhangMap.put(Character.valueOf('A'),Double.valueOf(297.2072));
        anhangMap.put(Character.valueOf('G'),Double.valueOf(313.2066));
        anhangMap.put(Character.valueOf('C'),Double.valueOf(273.1824));
        anhangMap.put(Character.valueOf('T'),Double.valueOf(288.1937));
        System.out.println(primerMap);
        System.out.println(anhangMap);
        f.getContentPane().add(new CDMassesConfigPanel(primerMap, anhangMap,18.02));
        f.pack();
        f.setVisible(true);
    }
    public void setValuesFrom(CalcDaltonOptions cfg) {
        getPrimerMassesMapTable().setMap(cfg.getCalcDaltonPrimerMassesMap());
        getAddonMassesMapTable().setMap(cfg.getCalcDaltonAddonMassesMap());
        getPlMassTf().setText(""+cfg.getCalcDaltonPLMass());
    }

    public void saveToConfig(CalcDaltonOptions cfg) {
        cfg.setCalcDaltonAddonMassesMap(getAddonMassesMapTable().getMap());
        cfg.setCalcDaltonPrimerMassesMap(getPrimerMassesMapTable().getMap());
        cfg.setCalcDaltonPLMass(Double.parseDouble(getPlMassTf().getText()));
    }
    public static Map getDefaultPrimermassMap() {
        Map primerMap=new HashMap();
        primerMap.put(Character.valueOf('A'),Double.valueOf(313.2071));
        primerMap.put(Character.valueOf('G'),Double.valueOf(329.2066));
        primerMap.put(Character.valueOf('C'),Double.valueOf(289.1823));
        primerMap.put(Character.valueOf('T'),Double.valueOf(304.1937));
        return primerMap;
    }
    public static Map getDefaultAddonMassMap() {
        Map anhangMap=new HashMap();
        anhangMap.put(Character.valueOf('A'),Double.valueOf(297.2072));
        anhangMap.put(Character.valueOf('G'),Double.valueOf(313.2066));
        anhangMap.put(Character.valueOf('C'),Double.valueOf(273.1824));
        anhangMap.put(Character.valueOf('T'),Double.valueOf(288.1937));
        return anhangMap;
    }
    public static double getDefaultPLMass() {
        return 18.02;
    }
     }  //  @jve:decl-index=0:visual-constraint="10,10"

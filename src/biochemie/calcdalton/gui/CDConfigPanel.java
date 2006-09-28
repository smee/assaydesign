/*
 * Created on 21.11.2004
 *
 */
package biochemie.calcdalton.gui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import biochemie.calcdalton.CDOptionsImpl;
import biochemie.calcdalton.CalcDaltonOptions;
import biochemie.gui.DoubleValueIntervallPanel;
import biochemie.gui.StringEntryPanel;

/**
 * @author Steffen Dienst
 * TODO eigenes Model, dann entfaellt das staendige Aufrufen von setListData
 * TODO umbauen, dass der eine Instanz von Calcdaltonoptionimpl verwendet, macht alles leichter, wegen der persistenz und so
 */
public class CDConfigPanel extends JPanel{
    private final static double p=TableLayoutConstants.PREFERRED;
    private final static double f=TableLayoutConstants.FILL;
    private final static double b=5;

	//actions
    private final class DeleteSpaltStelleAction extends AbstractAction{
        Icon icon;
        public DeleteSpaltStelleAction(){
            putValue(NAME,"Delete");
            putValue(SHORT_DESCRIPTION,"Delete selected cleavable linker.");
            java.net.URL url=this.getClass().getClassLoader().getResource("images/delete.gif");
            if(null != url){
                icon=new ImageIcon(url);
                putValue(Action.SMALL_ICON,icon);
            }
        }
        public void actionPerformed(ActionEvent e) {
            String temp=(String)bruchList.getSelectedValue();
            if(null != temp){
                bruchstelleVector.remove(temp);
                bruchList.setListData(bruchstelleVector);
                if(0 == bruchstelleVector.size()){
                    spaltUpAction.setEnabled(false);
                    spaltDownAction.setEnabled(false);
                    delSpaltAction.setEnabled(false);
                }
            }
        }
    }
    private final class AddSpaltStelleAction extends AbstractAction{
        Icon icon;
        public AddSpaltStelleAction(){
            putValue(NAME,"Add");
            putValue(SHORT_DESCRIPTION,"Add cleavable linker.");
            java.net.URL url=this.getClass().getClassLoader().getResource("images/add.gif");
            if(null != url){
                icon=new ImageIcon(url);
                putValue(Action.SMALL_ICON,icon);
            }
        }
        public void actionPerformed(ActionEvent e) {
            String temp=tf_spalt.getText();
            tf_spalt.setText("");
            try {
                Integer.parseInt(temp);
                if(!bruchstelleVector.contains(temp)) {
                    bruchstelleVector.add(temp);
                    bruchList.setListData(bruchstelleVector);
                    bruchList.setSelectedIndex(bruchstelleVector.size()-1);
                    spaltUpAction.setEnabled(true);
                    spaltDownAction.setEnabled(true);
                    delSpaltAction.setEnabled(true);
                }
            }catch (NumberFormatException e1) {
             }
        }
    }
    public class OkayAction extends AbstractAction {
        Icon icon;
        public OkayAction() {
            putValue(NAME, "Okay");
            putValue(SHORT_DESCRIPTION, "Accept settings and return to the main window.");
            java.net.URL url=this.getClass().getClassLoader().getResource("images/play.gif");
            if(null != url){
                icon=new ImageIcon(url);
                putValue(Action.SMALL_ICON,icon);
            }
        }
        public void actionPerformed(ActionEvent e) {

        }
    }
    private final class SpaltStelleUpAction extends AbstractAction {
        Icon icon;
        SpaltStelleUpAction() {
            putValue(NAME, "Up");
            putValue(SHORT_DESCRIPTION, "Moves cleavable linker up.");
            java.net.URL url=this.getClass().getClassLoader().getResource("images/up.gif");
            if(null != url){
                icon=new ImageIcon(url);
                putValue(Action.SMALL_ICON,icon);
            }
        }
        public void actionPerformed(ActionEvent e) {
            int index=bruchList.getSelectedIndex();
            bruchList.setSelectedIndex(index);
            if(0 < index) {
                String temp = (String)bruchstelleVector.get(index);
                bruchstelleVector.setElementAt(bruchstelleVector.get(index-1),index);
                bruchstelleVector.setElementAt(temp,index-1);
                bruchList.setListData(bruchstelleVector);
                bruchList.setSelectedIndex(index-1);
            }
        }
    }
    private final class SpaltStelleDownAction extends AbstractAction {
        Icon icon;
        SpaltStelleDownAction() {
            putValue(NAME, "Down");
            putValue(SHORT_DESCRIPTION, "Moves cleavable linker down.");
            java.net.URL url=this.getClass().getClassLoader().getResource("images/down.gif");
            if(null != url){
                icon=new ImageIcon(url);
                putValue(Action.SMALL_ICON,icon);
            }
        }
        public void actionPerformed(ActionEvent e) {
            int index=bruchList.getSelectedIndex();
            bruchList.setSelectedIndex(index);
            if(index!=bruchstelleVector.size()-1 && -1 != index) {
                String temp = (String)bruchstelleVector.get(index);
                bruchstelleVector.setElementAt(bruchstelleVector.get(index+1),index);
                bruchstelleVector.setElementAt(temp,index+1);
                bruchList.setListData(bruchstelleVector);
                bruchList.setSelectedIndex(index+1);
            }

        }
    }
    //private vars
	private SpaltStelleDownAction spaltDownAction;
    private SpaltStelleUpAction spaltUpAction;
    private DeleteSpaltStelleAction delSpaltAction;
    private AddSpaltStelleAction addSpaltAction;
    JList bruchList;
    JTextField tf_spalt;
    JCheckBox cbAllowOverlap;
    DoubleValueIntervallPanel abstandPanel;
	DoubleValueIntervallPanel verbMassePanel;
	StringEntryPanel maxMassPanel; 
	final Vector bruchstelleVector;
    private JCheckBox cbCalcdaltonAnhaenge;
    private JCheckBox cbShowIons;
    private JPanel bruchStellenPanel;
    private PeakInputPanel assayPeakPanel;
    private PeakInputPanel productPeakPanel;
    private JCheckBox cbForbiddenHalfMasses;

    public CDConfigPanel(boolean showCL){
        bruchstelleVector = new Vector();
        abstandPanel = new DoubleValueIntervallPanel("Excluded peak distances (D)","No primer or product will have a mass distance of this range.",new double[]{20.0,36.0},new double[]{24.0, 39.9});
		verbMassePanel=new DoubleValueIntervallPanel("Excluded mass ranges (D)","No Primer or product will have a size of this mass range.",new double[0],new double[0]);
		initialize(showCL);
        setValuesFrom(new CDOptionsImpl());
    }

    public CalcDaltonOptions getCalcDaltonOptionsProvider() {
        CDOptionsImpl c=new CDOptionsImpl();
        saveToConfig(c);
        return c;
    }

    public void setValuesFrom(CalcDaltonOptions cfg) {
        bruchstelleVector.removeAllElements();
        int[] br=cfg.getPhotolinkerPositions();
        for(int i=0;i<br.length;i++)
        	bruchstelleVector.add(Integer.toString(br[i]));
        bruchList.setModel(new MyListModel(bruchstelleVector));
        
        assayPeakPanel.setPeakValues(cfg.getCalcDaltonAssayPeaks());
        productPeakPanel.setPeakValues(cfg.getCalcDaltonProductPeaks());
        
        cbAllowOverlap.setSelected(cfg.isCalcDaltonAllowOverlap());
        cbForbiddenHalfMasses.setSelected(cfg.isCalcDaltonForbidHalfMasses());
        if(null != abstandPanel)
			abstandPanel.reset(cfg.getCalcDaltonFrom(),cfg.getCalcDaltonTo());
        if(null != verbMassePanel)
            verbMassePanel.reset(cfg.getCalcDaltonVerbFrom(),cfg.getCalcDaltonVerbTo());
        cbCalcdaltonAnhaenge.setSelected(cfg.isCalcDaltonAllExtensions());
        cbShowIons.setSelected(cfg.isCalcDaltonShowIons());
        delSpaltAction.setEnabled(0 < bruchstelleVector.size()?true:false);
        maxMassPanel.setText(Double.toString(cfg.getCalcDaltonMaxMass()));
    }
    public void saveToConfig(CalcDaltonOptions cfg) {
        cfg.setCalcDaltonAssayPeaks(assayPeakPanel.getPeakValues());
        cfg.setCalcDaltonProductPeaks(productPeakPanel.getPeakValues());
        int[] br=new int[bruchstelleVector.size()];
        for (int i = 0; i < br.length; i++) {
            br[i]=Integer.parseInt((String) bruchstelleVector.get(i));
        }
        cfg.setPhotolinkerPositions(br);
        cfg.setCalcDaltonAllowOverlap(cbAllowOverlap.isSelected());
        cfg.setCalcDaltonAllExtensions(cbCalcdaltonAnhaenge.isSelected());
        cfg.setCalcDaltonForbidHalfMasses(cbForbiddenHalfMasses.isSelected());
        cfg.setCalcDaltonFrom(abstandPanel.getFrom());
        cfg.setCalcDaltonTo(abstandPanel.getTo());
        cfg.setCalcDaltonVerbFrom(verbMassePanel.getFrom());
        cfg.setCalcDaltonVerbTo(verbMassePanel.getTo());
        cfg.setCalcDaltonShowIons(cbShowIons.isSelected());
        cfg.setCalcDaltonMaxMass(maxMassPanel.getTextAsDouble(30000));
    }

    public int getMaxBruchstelle(){
        int max=0;
        for (int i= 0; i < bruchstelleVector.size(); i++) {
            int val=Integer.parseInt(bruchstelleVector.get(i).toString());
            if(val>max)
                max=val;
        }
        return max;
    }

    private void initialize(boolean showCL){
        JButton btDelspalt;
        JButton btJb_up;
        JButton btJb_down;
        JButton btAddspalt;



        double[][] settingsSize={{b,p,b},{b,p,b,p,b,p,b,p,b,p,b,p,b,p,b,p,b,p,b,p,b}};

        setLayout(new TableLayout(settingsSize));
        bruchStellenPanel = new JPanel();
        if(showCL)
            add(bruchStellenPanel,"1,1");
		double text=28;
        double[][] bruchSizes={{b,80,60,p,b},{b,text,b,text,b,text,2*b,text,b}};
        bruchStellenPanel.setLayout(new TableLayout(bruchSizes));
        bruchStellenPanel.setBorder( BorderFactory.createTitledBorder( "Positions of cleavable linkers (from 3’)" ) );
        bruchStellenPanel.setToolTipText("Preferred usage order of cleavable linkers (from 3´)");
        bruchList = new JList( bruchstelleVector );
        bruchList.setVisibleRowCount(5);
        bruchList.setToolTipText("Cleavable linker.");
        bruchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollBruchList = new JScrollPane( bruchList );
        delSpaltAction= new DeleteSpaltStelleAction();
        btDelspalt = new JButton(delSpaltAction);btDelspalt.setHorizontalAlignment(SwingConstants.LEFT);
        bruchList.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e) {
                if(KeyEvent.VK_DELETE == e.getKeyCode())
                    delSpaltAction.actionPerformed(null);
            }
        });
        spaltUpAction=new SpaltStelleUpAction();
        btJb_up = new JButton( spaltUpAction );btJb_up.setHorizontalAlignment(SwingConstants.LEFT);
        spaltDownAction=new SpaltStelleDownAction();
        btJb_down = new JButton( spaltDownAction );btJb_down.setHorizontalAlignment(SwingConstants.LEFT);
        addSpaltAction= new AddSpaltStelleAction();
        btAddspalt = new JButton(addSpaltAction );btAddspalt.setHorizontalAlignment(SwingConstants.LEFT);
        tf_spalt = new PBSequenceField(4,false,PBSequenceField.NUMBERS);
        tf_spalt.setAction(addSpaltAction);
        bruchStellenPanel.add(scrollBruchList,"1,1,1,5");
        bruchStellenPanel.add(btDelspalt,"3,1");
        bruchStellenPanel.add(btJb_up,"3,3");
        bruchStellenPanel.add(btJb_down,"3,5");
        bruchStellenPanel.add(tf_spalt,"1,7");
        bruchStellenPanel.add(btAddspalt,"3,7");

        add(abstandPanel,"1,3");
		add(verbMassePanel,"1,5");

        maxMassPanel=new StringEntryPanel("Upper mass limit (D)");
        maxMassPanel.setColumns(9);
        maxMassPanel.setValidChars(PBSequenceField.NUMBERS);
        add(maxMassPanel,"1,7");
        
        JPanel peakPanel = createPeakPanel();
        add(peakPanel,"1,9");
        
        cbAllowOverlap=new JCheckBox("Allow unextended Primer overlap",false);
        cbAllowOverlap.setToolTipText("<html>The mass of the primes is allowed to overlap with the mass of product-ion <br>" +
                "complexes as specified in \"Excluded peak distances\"</html>");
        add(cbAllowOverlap,"1,11");
        
        cbCalcdaltonAnhaenge=new JCheckBox("Allow for all extension products",true);
        cbCalcdaltonAnhaenge.setToolTipText("<html>The mass of the primers are allowed to overlap with the mass of<br>" +
                "product-ion complexes as specified in \"Excluded peak distances\"<br>" +
                "and with signals from double charged molecules</html>");
        add(cbCalcdaltonAnhaenge,"1,13");
        
        cbShowIons=new JCheckBox("Draw peaks of cationic adducts");
        cbShowIons.setToolTipText("<html>Cationic side products of each primer and product <br>will be shown in the MALDI-Preview.</html>");
        add(cbShowIons,"1,15");
        
        cbForbiddenHalfMasses=new JCheckBox("Account for double charged molecules");
        cbForbiddenHalfMasses.setToolTipText("<html>If checked, double charged molecules<br>" +
                "with mass signals at the half of<br>" +
                "the m/z value of the respective single<br>" +
                "charged molecule are considered in assay design.</html>");
        add(cbForbiddenHalfMasses,"1,17");
    }

    /**
     * @param p
     * @param f
     * @param b
     * @return
     */
    private JPanel createPeakPanel() {
        JPanel peakPanel = new JPanel();
        double[][] peakSizes={{b,p,b},{b,p,b,p,b}};
        peakPanel.setLayout(new TableLayout(peakSizes));
        peakPanel.setBorder( BorderFactory.createTitledBorder( "Minimum peak distance (D):" ) );
        peakPanel.setToolTipText("Minimum peak distance in D");
        assayPeakPanel=new PeakInputPanel();
        assayPeakPanel.setLabel("...between assays");
        peakPanel.add(assayPeakPanel,"1,1");
        productPeakPanel=new PeakInputPanel();
        productPeakPanel.setLabel("...between products");
        peakPanel.add(productPeakPanel,"1,3");
        return peakPanel;
    }

    private class MyListModel extends AbstractListModel{
        private Vector listData;
        public MyListModel(Vector coll) {
            this.listData=coll;
        }
            public int getSize() { return listData.size(); }
            public Object getElementAt(int i) { return listData.elementAt(i); }
    }

    public void showCL(boolean b) {
        bruchStellenPanel.setVisible(b);
        if(!b)
            remove(bruchStellenPanel);
        else
            add(bruchStellenPanel,"1,1");
        invalidate();
    }
    public void showProductPeaks(boolean b){
        productPeakPanel.setVisible(b);
    }
    public void showMaxMass(boolean b){
        maxMassPanel.setVisible(b);
    }

}

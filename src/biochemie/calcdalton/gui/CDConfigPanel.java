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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.jfree.ui.FilesystemFilter;

import biochemie.calcdalton.CDOptionsImpl;
import biochemie.calcdalton.CalcDaltonOptions;
import biochemie.gui.DoubleValueIntervallPanel;
import biochemie.util.FileSelector;
import biochemie.util.config.GeneralConfig;

/**
 * @author Steffen Dienst
 * TODO eigenes Model, dann entfaellt das staendige Aufrufen von setListData
 * TODO umbauen, dass der eine Instanz von Calcdaltonoptionimpl verwendet, macht alles leichter, wegen der persistenz und so
 */
public class CDConfigPanel extends JPanel{


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
    public class ResetAction extends AbstractAction {
        Icon icon;
        ResetAction() {
            putValue(NAME, "Reset");
            putValue(SHORT_DESCRIPTION, "Reset to standard settings.");
            java.net.URL url=this.getClass().getClassLoader().getResource("images/reset.gif");
            if(null != url){
                icon=new ImageIcon(url);
                putValue(Action.SMALL_ICON,icon);
            }
        }
        public void actionPerformed(ActionEvent e) {
            setValuesFrom(new CDOptionsImpl());
        }
    }
    //private vars
	private SpaltStelleDownAction spaltDownAction;
    private SpaltStelleUpAction spaltUpAction;
    private DeleteSpaltStelleAction delSpaltAction;
    private AddSpaltStelleAction addSpaltAction;
    JList bruchList;
    JTextField tf_spalt;
    PBSequenceField tfPeaks;
    JCheckBox cbAllowOverlap;
    DoubleValueIntervallPanel abstandPanel;
	DoubleValueIntervallPanel verbMassePanel;

	final Vector bruchstelleVector;
    private JCheckBox cbCalcdaltonAnhaenge;

    public CDConfigPanel(){
        bruchstelleVector = new Vector();
        abstandPanel = new DoubleValueIntervallPanel("Excluded peak distances (D)","No primer or product will have a mass distance of this range.",new double[]{20.0,36.0},new double[]{24.0, 39.9});
		verbMassePanel=new DoubleValueIntervallPanel("Excluded mass ranges (D)","No Primer or product will have a size of this mass range.",new double[0],new double[0]);
		initialize();
        setValuesFrom(new CDOptionsImpl());
    }

    public CalcDaltonOptions getCalcDaltonOptionsProvider() {
        CDOptionsImpl c=new CDOptionsImpl();

        int[] br=new int[bruchstelleVector.size()];
        int i=0;
        for (Iterator it = bruchstelleVector.iterator(); it.hasNext();i++) {
            String s = (String) it.next();
            br[i]=Integer.parseInt(s);
        }
        c.setPhotolinkerPositions(br);
        c.setCalcDaltonFrom(abstandPanel.getFrom());
        c.setCalcDaltonTo(abstandPanel.getTo());
        c.setCalcDaltonPeaks(Double.parseDouble('0'+tfPeaks.getText()));
        c.setCalcDaltonAllowOverlap(cbAllowOverlap.isSelected());
        c.setCalcDaltonVerbFrom(verbMassePanel.getFrom());
        c.setCalcDaltonVerbTo(verbMassePanel.getTo());
        c.setCalcDaltonAllExtensions(cbCalcdaltonAnhaenge.isSelected());
        return c;
    }

    public void setValuesFrom(CalcDaltonOptions cfg) {
        bruchstelleVector.removeAllElements();
        int[] br=cfg.getPhotolinkerPositions();
        for(int i=0;i<br.length;i++)
        	bruchstelleVector.add(Integer.toString(br[i]));
        bruchList.setModel(new MyListModel(bruchstelleVector));
        tfPeaks.setText(""+cfg.getCalcDaltonPeaks());
        cbAllowOverlap.setSelected(cfg.getCalcDaltonAllowOverlap());
        if(null != abstandPanel)
			abstandPanel.reset(cfg.getCalcDaltonFrom(),cfg.getCalcDaltonTo());
        if(null != verbMassePanel)
            verbMassePanel.reset(cfg.getCalcDaltonVerbFrom(),cfg.getCalcDaltonVerbTo());
        cbCalcdaltonAnhaenge.setSelected(cfg.getCalcDaltonAllExtensions());
        
        delSpaltAction.setEnabled(0 < bruchstelleVector.size()?true:false);
    }
    public void saveToConfig(CalcDaltonOptions cfg) {
        cfg.setCalcDaltonPeaks(Double.parseDouble(tfPeaks.getText()));
        int[] br=new int[bruchstelleVector.size()];
        for (int i = 0; i < br.length; i++) {
            br[i]=Integer.parseInt((String) bruchstelleVector.get(i));
        }
        cfg.setPhotolinkerPositions(br);
        cfg.setCalcDaltonAllowOverlap(cbAllowOverlap.isSelected());
        cfg.setCalcDaltonAllExtensions(cbCalcdaltonAnhaenge.isSelected());
        cfg.setCalcDaltonFrom(abstandPanel.getFrom());
        cfg.setCalcDaltonTo(abstandPanel.getTo());
        cfg.setCalcDaltonVerbFrom(verbMassePanel.getFrom());
        cfg.setCalcDaltonVerbTo(verbMassePanel.getTo());
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

    private void initialize(){
        JButton btDelspalt;
        JButton btJb_up;
        JButton btJb_down;
        JButton btAddspalt;


        double p=TableLayoutConstants.PREFERRED;
        double f=TableLayoutConstants.FILL;
        double b=5;
        double[][] settingsSize={{b,p,b},{b,p,b,p,b,p,b,p,b,p,b,p,b,p}};

        setLayout(new TableLayout(settingsSize));
        JPanel bruchStellenPanel=new JPanel();
        add(bruchStellenPanel,"1,1");
		double text=28;
        double[][] bruchSizes={{b,80,60,p,b},{b,text,b,text,b,text,2*b,text,b}};
        bruchStellenPanel.setLayout(new TableLayout(bruchSizes));
        bruchStellenPanel.setBorder( BorderFactory.createTitledBorder( "Preferred usage order of cleavable linkers" ) );
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

        JPanel peakPanel = new JPanel();
        double[][] peakSizes={{b,f,b},{b,p,b}};
        peakPanel.setLayout(new TableLayout(peakSizes));
        peakPanel.setBorder( BorderFactory.createTitledBorder( "Minimum peak distance (D):" ) );
        peakPanel.setToolTipText("Minimum peak distance (D)");
        tfPeaks=new PBSequenceField(5,false,PBSequenceField.NUMBERS);
        tfPeaks.setToolTipText("Minimum peak distance (D)");
        peakPanel.add(tfPeaks,"1,1");
        add(peakPanel,"1,7");
        cbAllowOverlap=new JCheckBox("Allow unextended Primer overlap",false);
        cbAllowOverlap.setToolTipText("<html>The mass of the primes is allowed to overlap with the mass of product-ion <br>complexes as specified in \"Excluded peak distances\"</html>");
        add(cbAllowOverlap,"1,9");
        cbCalcdaltonAnhaenge=new JCheckBox("Allow for all extension products",true);
        cbCalcdaltonAnhaenge.setToolTipText("<html>If checked, the program reserves the appropriate mass range for all<br>" +
                " possible extension products A, C, G and T of every primer.<br>" +
                "Otherwise the mass range is reserved for the expected products only.</html>");
        add(cbCalcdaltonAnhaenge,"1,11");
    }

    private class MyListModel extends AbstractListModel{
        private Vector listData;
        public MyListModel(Vector coll) {
            this.listData=coll;
        }
            public int getSize() { return listData.size(); }
            public Object getElementAt(int i) { return listData.elementAt(i); }
    }

}

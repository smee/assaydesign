package biochemie.calcdalton.gui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import biochemie.calcdalton.CalcDaltonOptions;
import biochemie.gui.CalcTimePanel;



/**
 * @author Steffen
 *
 */

public class CDConfig implements Serializable{


    private static CDConfig singleton=new CDConfig();
	
	File cfgfile;
    //GUI-vars
    private JDialog dialog;
	private CDConfigPanel cdcpanel;
    private CDMassesConfigPanel massPanel;

    private CalcTimePanel calcTimePanel;


 
    private CDConfig() {
		singleton=this;
		cdcpanel=new CDConfigPanel(true);
        massPanel=new CDMassesConfigPanel(CDMassesConfigPanel.getDefaultPrimermassMap(),CDMassesConfigPanel.getDefaultAddonMassMap(),18.02);
        calcTimePanel=new CalcTimePanel();
	}
    private JDialog initDialog(JFrame parent){

        final JDialog dialog=new JDialog(parent,true);
        
        if(biochemie.util.Helper.isJava14())
            JDialog.setDefaultLookAndFeelDecorated(false);
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //soll nur mit dem okay-Button geschlossen werden
        dialog.setResizable(false);
        dialog.getContentPane().setLayout(new BorderLayout());
        
        JTabbedPane tabPane=new JTabbedPane();
        tabPane.add("Settings",cdcpanel);
        tabPane.add("Masses",massPanel);
        tabPane.add("Calculation time",calcTimePanel);
        tabPane.add("Load/Save settings",new CDConfigPersistPanel(cdcpanel, massPanel,calcTimePanel));
        dialog.getContentPane().add(tabPane, BorderLayout.CENTER);
        JButton btOkay=new JButton(this.cdcpanel.new OkayAction(){
        	public void actionPerformed(ActionEvent e){
        		super.actionPerformed(e);
        		dialog.setVisible(false);
        		SBEGui.getInstance().refreshData();
        	}
        });
        dialog.getContentPane().add(btOkay,BorderLayout.SOUTH);
        dialog.pack();
        return dialog;
    }




    public static CDConfig getInstance() {
			return singleton;
	}

    public void showModalDialog(JFrame parent) {
        if(null == dialog)
            dialog=initDialog(parent);
        dialog.show();
    }
    public void repaint(JFrame parent){
        if(null == dialog)
            dialog=initDialog(parent); 
        dialog.repaint();
    }
    public int getMaxBruchstelle(){
        return cdcpanel.getMaxBruchstelle();
    }
    /**
     * @return
     */
    public int[] getBruchStellenArray() {
        return cdcpanel.getCalcDaltonOptionsProvider().getPhotolinkerPositions();
    }
    /**
     * @return
     */
    public double[] getFrom() {
        return cdcpanel.getCalcDaltonOptionsProvider().getCalcDaltonFrom();
    }
	public double[] getVerbMassenFrom(){
		return cdcpanel.getCalcDaltonOptionsProvider().getCalcDaltonVerbFrom();
	}
	public double[] getVerbMassenTo(){
		return cdcpanel.getCalcDaltonOptionsProvider().getCalcDaltonVerbTo();
	}
    /**
     * @return
     */
    public double[] getAssayPeaks() {
        return cdcpanel.getCalcDaltonOptionsProvider().getCalcDaltonAssayPeaks();
    }
    public double[] getProductPeaks() {
        return cdcpanel.getCalcDaltonOptionsProvider().getCalcDaltonProductPeaks();
    }

    /**
     * @return
     */
    public double[] getTo() {
        return cdcpanel.getCalcDaltonOptionsProvider().getCalcDaltonTo();
    }
    /**
     * @return
     */
    public boolean allowOverlap() {
        return cdcpanel.getCalcDaltonOptionsProvider().isCalcDaltonAllowOverlap();
    }
    public CalcDaltonOptions getConfiguration() {
        CalcDaltonOptions c=cdcpanel.getCalcDaltonOptionsProvider();
        massPanel.saveToConfig(c);
        calcTimePanel.saveTo(c);
        return c;
    }

}

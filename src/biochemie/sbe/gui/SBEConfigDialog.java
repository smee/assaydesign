/*
 * Created on 21.11.2004
 *
 */
package biochemie.sbe.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import biochemie.calcdalton.gui.CDConfigPanel;
import biochemie.calcdalton.gui.CDMassesConfigPanel;
import biochemie.calcdalton.gui.PBSequenceField;
import biochemie.gui.CalcTimePanel;
import biochemie.gui.IntegerValueIntervallPanel;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.io.SBEConfig;
import biochemie.util.FileSelector;
import biochemie.util.MyAction;
/**
 * @author Steffen Dienst
 *
 */
public class SBEConfigDialog extends JDialog {

	private javax.swing.JPanel jContentPane = null;

	private JTabbedPane jTabbedPane = null;
	private JPanel jPanel = null;
	private JButton jButton = null;
	private CDConfigPanel cdPanel = null;
	private MiniSBEConfigPanel sbePanel = null;
	private JPanel advPanel = null;
	private CalcTimePanel restPanel = null;
	private JPanel savePanel = null;
	private JScrollPane cdScrollPane = null;
	private JLabel jLabel = null;
	private PBSequenceField colortimeTf = null;
	private JButton loadButton = null;
	private JButton saveButton = null;
	private JButton resetButton = null;
	private JToggleButton jToggleButton = null;
	private JPanel advSettingsPanel = null;
	private IntegerValueIntervallPanel hairpinValuePanel = null;
	private IntegerValueIntervallPanel homodimerValuePanel = null;
	private IntegerValueIntervallPanel crossdimerValuePanel = null;
	private JScrollPane jScrollPane = null;
	private JPanel candlenpanel = null;
	private JLabel jLabel1 = null;
	private JSpinner candlenSpinner = null;
	private JCheckBox evilCrossdimerCheckBox = null;

	private JCheckBox drawGraphesCheckbox = null;
	private JCheckBox debugCheckBox = null;

    private Action saveaction;
    private Action loadaction;

    private CDMassesConfigPanel cdmasspanel;
    /**
	 * @param gui
	 */
	public SBEConfigDialog(JFrame parent) {
		super(parent,true);
		initialize();

	}
	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
        SBEOptions c = new SBEConfig();
        setPropertiesFrom(c);

		this.setTitle("Preferences");
		this.setSize(454, 638);
		this.setContentPane(getJContentPane());
        
	}
	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getJPanel(), java.awt.BorderLayout.SOUTH);
            jContentPane.registerKeyboardAction(getLoadAction(), (KeyStroke) getLoadAction().getValue(Action.ACCELERATOR_KEY),JComponent.WHEN_IN_FOCUSED_WINDOW);
            jContentPane.registerKeyboardAction(getSaveAction(), (KeyStroke) getSaveAction().getValue(Action.ACCELERATOR_KEY),JComponent.WHEN_IN_FOCUSED_WINDOW);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jTabbedPane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
	        URL url=this.getClass().getClassLoader().getResource("images/reagenz.gif");
	        Icon cdicon=null;
	        if(null != url){
	        	cdicon = new ImageIcon(url);
	        }
			jTabbedPane.addTab("CalcDalton settings", cdicon, getCdScrollPane(), null);
			jTabbedPane.addTab("CalcDalton masses", null, getCdMassPanel(), null);
			jTabbedPane.addTab("MiniSBE settings", null, getSbePanel(), null);
			jTabbedPane.addTab("Expert settings", null, getAdvPanel(), null);
			jTabbedPane.addTab("Misc settings", null, getRestPanel(), null);
			jTabbedPane.addTab("Load/save", null, getSavePanel(), null);
		}
		return jTabbedPane;
	}
	private CDMassesConfigPanel getCdMassPanel() {
	    if(cdmasspanel==null) {
            cdmasspanel=new CDMassesConfigPanel();
        }
        return cdmasspanel;
    }
    /**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.add(getOkayButton(), null);
		}
		return jPanel;
	}
	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getOkayButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Okay");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SBEConfigDialog.this.setVisible(false);
				}
			});            
		}
		return jButton;
	}
	/**
	 *
	 */
	public SBEOptions getSBEOptionsFromGui() {
        SBEOptions sbeconfig = new SBEConfig(getCdPanel().getCalcDaltonOptionsProvider());
        getRestPanel().saveTo(sbeconfig);
        sbeconfig.setCrossdimerMinbinds(StringUtils.join(ArrayUtils.toObject(getCrossdimerValuePanel().getTo()),' '));
	    sbeconfig.setCrossimerWindowsizes(StringUtils.join(ArrayUtils.toObject(getCrossdimerValuePanel().getFrom()),' '));
	    sbeconfig.setHairpinMinbinds(StringUtils.join(ArrayUtils.toObject(getHairpinValuePanel().getTo()),' '));
	    sbeconfig.setHairpinWindowsizes(StringUtils.join(ArrayUtils.toObject(getHairpinValuePanel().getFrom()),' '));
	    sbeconfig.setHomodimerMinbinds(StringUtils.join(ArrayUtils.toObject(getHomodimerValuePanel().getTo()),' '));
	    sbeconfig.setHomodimerWindowsizes(StringUtils.join(ArrayUtils.toObject(getHomodimerValuePanel().getFrom()),' '));
	    sbeconfig.setMaxGC(((Number)getSbePanel().getMaxgcSpinner().getValue()).intValue());
	    sbeconfig.setMinGC(((Number)getSbePanel().getMingcSpinner().getValue()).intValue());
	    sbeconfig.setMaxPlex(((Number)getSbePanel().getMaxplexSpinner().getValue()).intValue());
	    sbeconfig.setMinCandidateLen(((Number)getCandlenSpinner().getValue()).intValue());
	    sbeconfig.setMinProductLenDiff(((Number )getSbePanel().getPcrpdiffSpinner().getValue()).intValue());
	    sbeconfig.setMinTemperature(((Number)getSbePanel().getMinTspinner().getValue()).intValue());
	    sbeconfig.setMaxTemperature(((Number)getSbePanel().getMaxTspinner().getValue()).intValue());
	    sbeconfig.setOptTemperature(((Number)getSbePanel().getOptTspinner().getValue()).intValue());
	    sbeconfig.setDrawGraphes(getDrawGraphesCheckbox().isSelected());
	    sbeconfig.setAllCrossdimersAreEvil(getEvilCrossdimerCheckBox().isSelected());
	    sbeconfig.setPolyX(((Number)getSbePanel().getPolyxSpinner().getValue()).intValue());
        sbeconfig.setDebug(getDebugCheckBox().isSelected());
	    return sbeconfig;
	}
	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private CDConfigPanel getCdPanel() {
		if (cdPanel == null) {
			cdPanel = new CDConfigPanel();
		}
		return cdPanel;
	}
	/**
	 * This method initializes jPanel2
	 *
	 * @return javax.swing.JPanel
	 */
	private MiniSBEConfigPanel getSbePanel() {
		if (sbePanel == null) {
			sbePanel = new MiniSBEConfigPanel();
		}
		return sbePanel;
	}
	/**
	 * This method initializes jPanel3
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getAdvPanel() {
		if (advPanel == null) {
			advPanel = new JPanel();
			advPanel.setLayout(new BorderLayout());
			advPanel.add(getJToggleButton(), java.awt.BorderLayout.NORTH);
			advPanel.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return advPanel;
	}
	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private CalcTimePanel getRestPanel() {
		if (restPanel == null) {
			restPanel=new CalcTimePanel();
		}
		return restPanel;
	}
	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getSavePanel() {
		if (savePanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			savePanel = new JPanel();
			savePanel.setLayout(new GridBagLayout());
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.insets = new java.awt.Insets(20,20,20,20);
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.gridy = 1;
			gridBagConstraints9.insets = new java.awt.Insets(20,20,20,20);
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 2;
			gridBagConstraints10.insets = new java.awt.Insets(20,20,20,20);
			savePanel.add(getLoadButton(), gridBagConstraints8);
			savePanel.add(getSaveButton(), gridBagConstraints9);
			savePanel.add(getResetButton(), gridBagConstraints10);
		}
		return savePanel;
	}
	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getCdScrollPane() {
		if (cdScrollPane == null) {
			cdScrollPane = new JScrollPane();
			cdScrollPane.setViewportView(getCdPanel());
		}
		return cdScrollPane;
	}

	/**
	 * This method initializes jButton1
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getLoadButton() {
		if (loadButton == null) {
			loadButton = new JButton();
			loadButton.setAction(getLoadAction());
		}
		return loadButton;
	}
	/**
	 * This method initializes jButton2
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.setAction(getSaveAction());
		}
		return saveButton;
	}
	/**
	 * This method initializes jButton3
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getResetButton() {
		if (resetButton == null) {
			resetButton = new JButton();
			resetButton.setAction(new ResetAction());
		}
		return resetButton;
	}
	/**
	 * This method initializes jToggleButton
	 *
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getJToggleButton() {
        final String ENABLETEXT="Show expert settings";
        final String DISABLETEXT="Hide expert settings";
		if (jToggleButton == null) {
			jToggleButton = new JToggleButton();
			jToggleButton.setText(ENABLETEXT);
			jToggleButton.setToolTipText("Notice: Advanced expert settings are ignored in the standard mode!");
			jToggleButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					boolean val = e.getStateChange() == ItemEvent.SELECTED;
					getAdvSettingsPanel().setVisible(val);
                    jToggleButton.setText(val?DISABLETEXT:ENABLETEXT);
				}
			});
		}
		return jToggleButton;
	}
	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getAdvSettingsPanel() {
		if (advSettingsPanel == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints91 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			advSettingsPanel = new JPanel();
			advSettingsPanel.setLayout(new GridBagLayout());
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 4;
			gridBagConstraints11.gridwidth = 2;
			gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.insets = new java.awt.Insets(20,0,0,0);
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 5;
			gridBagConstraints1.insets = new java.awt.Insets(10,0,0,0);
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 6;
			gridBagConstraints2.insets = new java.awt.Insets(10,0,10,0);
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints41.gridx = 1;
			gridBagConstraints41.gridy = 0;
			gridBagConstraints91.gridx = 1;
			gridBagConstraints91.gridy = 1;
			gridBagConstraints12.insets = new java.awt.Insets(10,10,10,10);
			gridBagConstraints12.gridx = 1;
			gridBagConstraints12.gridy = 2;
			gridBagConstraints13.gridx = 1;
			gridBagConstraints13.gridy = 3;
			gridBagConstraints13.insets = new java.awt.Insets(10,10,10,10);
			advSettingsPanel.add(getCandlenpanel(), gridBagConstraints41);
			advSettingsPanel.add(getEvilCrossdimerCheckBox(), gridBagConstraints91);
			advSettingsPanel.add(getDrawGraphesCheckbox(), gridBagConstraints12);
			advSettingsPanel.add(getDebugCheckBox(), gridBagConstraints13);
			advSettingsPanel.add(getHairpinValuePanel(), gridBagConstraints11);
			advSettingsPanel.add(getHomodimerValuePanel(), gridBagConstraints1);
			advSettingsPanel.add(getCrossdimerValuePanel(), gridBagConstraints2);
			advSettingsPanel.setVisible(false);
		}
		return advSettingsPanel;
	}
	/**
	 * This method initializes integerValueIntervallPanel1
	 *
	 * @return biochemie.gui.IntegerValueIntervallPanel
	 */
	private IntegerValueIntervallPanel getHairpinValuePanel() {
		if (hairpinValuePanel == null) {
			hairpinValuePanel = new IntegerValueIntervallPanel();
			hairpinValuePanel.setTitle("Hairpinsettings");
			hairpinValuePanel.setTooltip("set the parameters for predicting hairpins");
			hairpinValuePanel.setDefaultValues(new int[]{6,4}, new int[]{4,4});
			hairpinValuePanel.setFromLabel("Windowsize:");
			hairpinValuePanel.setToLabel("Min. Binds:");
		}
		return hairpinValuePanel;
	}
	/**
	 * This method initializes integerValueIntervallPanel1
	 *
	 * @return biochemie.gui.IntegerValueIntervallPanel
	 */
	private IntegerValueIntervallPanel getHomodimerValuePanel() {
		if (homodimerValuePanel == null) {
			homodimerValuePanel = new IntegerValueIntervallPanel();
			homodimerValuePanel.setTitle("Homodimersettings");
			homodimerValuePanel.setTooltip("set the parameters for predicting homodimer");
			homodimerValuePanel.setDefaultValues(new int[]{6,4}, new int[]{4,4});
			homodimerValuePanel.setFromLabel("Windowsize:");
			homodimerValuePanel.setToLabel("Min. Binds:");
		}
		return homodimerValuePanel;
	}
	/**
	 * This method initializes integerValueIntervallPanel1
	 *
	 * @return biochemie.gui.IntegerValueIntervallPanel
	 */
	private IntegerValueIntervallPanel getCrossdimerValuePanel() {
		if (crossdimerValuePanel == null) {
			crossdimerValuePanel = new IntegerValueIntervallPanel();
			crossdimerValuePanel.setTitle("Crossdimerdimersettings");
			crossdimerValuePanel.setTooltip("set the parameters for predicting crossdimer");
			crossdimerValuePanel.setDefaultValues(new int[]{6,4}, new int[]{4,4});
			crossdimerValuePanel.setFromLabel("Windowsize:");
			crossdimerValuePanel.setToLabel("Min. Binds:");
		}
		return crossdimerValuePanel;
	}
	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getAdvSettingsPanel());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getCandlenpanel() {
		if (candlenpanel == null) {
			jLabel1 = new JLabel();
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			candlenpanel = new JPanel();
			candlenpanel.setLayout(new GridBagLayout());
			jLabel1.setText("Min. length of primers");
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.insets = new java.awt.Insets(5,5,10,5);
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.insets = new java.awt.Insets(0,10,10,10);
			gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			candlenpanel.add(jLabel1, gridBagConstraints5);
			candlenpanel.add(getCandlenSpinner(), gridBagConstraints7);
		}
		return candlenpanel;
	}
	/**
	 * This method initializes jSpinner1
	 *
	 * @return javax.swing.JSpinner
	 */
	private JSpinner getCandlenSpinner() {
		if (candlenSpinner == null) {
			candlenSpinner = new JSpinner();
			((SpinnerNumberModel)candlenSpinner.getModel()).setValue(new Integer(18));
		}
		return candlenSpinner;
	}
	/**
	 * This method initializes drawGraphesCheckbox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getEvilCrossdimerCheckBox() {
		if (evilCrossdimerCheckBox == null) {
			evilCrossdimerCheckBox = new JCheckBox();
			evilCrossdimerCheckBox.setText("incomp. Crossdimers only");
			evilCrossdimerCheckBox.setToolTipText("Predicts only incompatible crossdimers.");
		}
		return evilCrossdimerCheckBox;
	}
    protected void setPropertiesFrom(SBEOptions c) {
        //setze Calcdalton-Optionen
        getCdPanel().setValuesFrom(c);
        getCdMassPanel().setValuesFrom(c);
        getCandlenSpinner().setValue(new Integer(c.getMinCandidateLen()));
        getRestPanel().setValuesFrom(c);


        //setze minisbeoptionen
        MiniSBEConfigPanel mp= getSbePanel();
        mp.getMingcSpinner().setValue(new Double(c.getMinGC()));
        mp.getMaxgcSpinner().setValue(new Double(c.getMaxGC()));
        mp.getMinTspinner().setValue(new Double(c.getMinTemperature()));
        mp.getOptTspinner().setValue(new Double(c.getOptTemperature()));
        mp.getMaxTspinner().setValue(new Double(c.getMaxTemperature()));
        mp.getPolyxSpinner().setValue(new Integer(c.getPolyX()));
        mp.getMaxplexSpinner().setValue(new Integer(c.getMaxPlex()));
        mp.getPcrpdiffSpinner().setValue(new Integer(c.getMinProductLenDiff()));
        //setze expertenoptionen
        getEvilCrossdimerCheckBox().setSelected(c.getAllCrossdimersAreEvil());
        IntegerValueIntervallPanel ip=getHairpinValuePanel();
        ip.loadFromString(c.getHairpinWindowsizes(),c.getHairpinMinbinds());
        ip=getHomodimerValuePanel();
        ip.loadFromString(c.getHomodimerWindowsizes(),c.getHomodimerMinbinds());
        ip=getCrossdimerValuePanel();
        ip.loadFromString(c.getCrossDimerWindowsizes(),c.getCrossdimerMinbinds());
        getDrawGraphesCheckbox().setSelected(c.isDrawGraphes());
        getDebugCheckBox().setSelected(c.isDebug());
    }


    protected class ResetAction extends MyAction {
        ResetAction() {
            super("Reset", 
                  "Reset to standard settings.",
                  ResetAction.class.getClassLoader().getResource("images/reset.gif"),
                  null);
        }
        public void actionPerformed(ActionEvent e) {
            SBEOptions c = new SBEConfig();
            setPropertiesFrom(c);
        }
    }
    protected class LoadAction extends MyAction {
        LoadAction() {
            super("Load",
                    "Load previously saved settings from disk.",
                    LoadAction.class.getClassLoader().getResource("images/open.gif"),
                    KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        }
        public void actionPerformed(ActionEvent e) {
            FileFilter filter=new FileFilter(){
                public boolean accept(File f) {
                    if(f.isDirectory())
                        return true;
                    if(f.isFile() && (f.getName().endsWith(".cfg") || f.getName().endsWith(".CFG")))
                        return true;
                    return false;
                }
                public String getDescription() {
                    return "MiniSBE-configfiles (*.cfg)";
                }
            };
            File file = FileSelector.getUserSelectedFile(SBEConfigDialog.this,"Load config...",filter,FileSelector.OPEN_DIALOG);
            if(file != null){
                loadProperties(file);
            }

        }

        private void loadProperties(File file) {
			try {
				SBEOptions c = new SBEConfig();
                ((SBEConfig)c).readConfigFile(file.getCanonicalPath());
				setPropertiesFrom(c);
			} catch (IOException  e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(SBEConfigDialog.this,e.getMessage(),"Error loading file "+file.getName(),JOptionPane.ERROR_MESSAGE);
			}
		}
    }
    protected class SaveAction extends MyAction {
        SaveAction() {
            super("Save",
                  "Save settings to disk.",
                  SaveAction.class.getClassLoader().getResource("images/save.gif"),
                  KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        }
        public void actionPerformed(ActionEvent e) {
            FileFilter filter=new FileFilter(){
                public boolean accept(File f) {
                    if(f.isDirectory())
                        return true;
                    if(f.isFile() && (f.getName().endsWith(".cfg") || f.getName().endsWith(".CFG")))
                        return true;
                    return false;
                }
                public String getDescription() {
                    return "MiniSBE-configfiles (*.cfg)";
                }
            };
            File file = FileSelector.getUserSelectedFile( SBEConfigDialog.this,"save config...",filter,FileSelector.SAVE_DIALOG);
            if(file != null){
                String path=file.getAbsolutePath();
                if(!path.endsWith(".cfg") && !path.endsWith(".CFG"))
                    path += ".cfg";
                file=new File(path);
                try {
                    file.createNewFile();
                    SBEConfig cfg = (SBEConfig) getSBEOptionsFromGui();
                    cfg.writeConfigTo(file.getCanonicalPath());
                } catch (FileNotFoundException e1) {
                    JOptionPane.showMessageDialog(null,"Sorry, your personal settings couldn't be"
                                                    +"saved. An error occured.","", JOptionPane.WARNING_MESSAGE);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null,"Sorry, your personal settings couldn't be"
                                                    +"saved. An error occured.","", JOptionPane.WARNING_MESSAGE);
                }
            }

        }
    }

	/**
	 * This method initializes drawGraphesCheckbox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getDrawGraphesCheckbox() {
		if (drawGraphesCheckbox == null) {
			drawGraphesCheckbox = new JCheckBox();
			drawGraphesCheckbox.setText("Output graphes to file");
		}
		return drawGraphesCheckbox;
	}
	/**
	 * This method initializes jCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getDebugCheckBox() {
		if (debugCheckBox == null) {
			debugCheckBox = new JCheckBox();
			debugCheckBox.setText("Debug output");
		}
		return debugCheckBox;
	}
    /**
     * @return
     */
    private Action getSaveAction() {
        if(saveaction == null)
            saveaction = new SaveAction();
        return saveaction;
    }
    /**
     * @return
     */
    private Action getLoadAction() {
        if(loadaction == null)
            loadaction = new LoadAction();
        return loadaction;
    }
  }  //  @jve:decl-index=0:visual-constraint="10,10"

/*
 * Created on 12.11.2004
 *
 */
package biochemie.sbe.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.core.IsNull;

import biochemie.calcdalton.JTableEx;
import biochemie.domspec.SBESekStruktur;
import biochemie.sbe.MiniSBE;
import biochemie.sbe.SBECandidate;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.gui.actions.SaveResultsAction;
import biochemie.sbe.gui.actions.ShowDiffAction;
import biochemie.sbe.io.SBEPrimerReader;
import biochemie.util.ConsoleWindow;
import biochemie.util.FileSelector;
import biochemie.util.GUIHelper;
import biochemie.util.Helper;
import biochemie.util.MyAction;
import biochemie.util.SwingWorker;

/**
 * @author Steffen Dienst
 *
 */
public class MiniSBEGui extends JFrame {

	public class CalculateAction extends MyAction implements TableModelListener {
        public CalculateAction() {
            super("Calculate","Start calculation",
                    CalculateAction.class.getClassLoader().getResource("images/play.gif"),
                    (KeyStroke)null);
        }
        public void actionPerformed(ActionEvent e) {
        	final List sbec= new ArrayList(sbepanels.size());


        	final SBEOptions cfg = getConfigDialog().getSBEOptionsFromGui();
        	for (Iterator it = sbepanels.iterator(); it.hasNext();) {
        		SBECandidatePanel p = (SBECandidatePanel) it.next();
        		sbec.add(p.getSBECandidate(cfg));
        	}
        	Algorithms.remove(sbec.iterator(),IsNull.instance());
        	final List sbeccoll=SBEPrimerReader.collapseMultiplexes(sbec,cfg);
        	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            //dialog um den user zu informieren
            final JDialog dialog = new JDialog(MiniSBEGui.this,"Calculationprogress",true);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            Container pane=dialog.getContentPane();
            pane.setLayout(new BoxLayout(pane,BoxLayout.Y_AXIS));
            JProgressBar bar =new JProgressBar(JProgressBar.HORIZONTAL);
            bar.setIndeterminate(true);
            pane.add(bar);
            pane.add(new JLabel("Please have some patience, this operation might need some time."));

            GUIHelper.center(dialog, MiniSBEGui.this);

            SwingWorker sw = new SwingWorker(){
        		public Object construct() {
                    MiniSBE m = null;
                    try {
						m = new MiniSBE(sbeccoll,cfg);
						normalizeSekStruks(sbec);
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
                    return m;
        		}
        		public void finished() {
                    dialog.dispose();
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        			showResultFrame(sbec,cfg);
        		}
        	};
        	sw.start();
            dialog.pack();
            dialog.setVisible(true);
        }

        protected void normalizeSekStruks(List sbec) {
            for (Iterator iter = sbec.iterator(); iter.hasNext();) {
                SBECandidate sc = (SBECandidate) iter.next();
                if(sc.hasValidPrimer())
                    sc.normalizeCrossdimers(new HashSet(sbec));
            }

        }
        /**
         * @param sbec
         */
        private void showResultFrame(final List sbec, SBEOptions cfg) {
            JFrame frame = new JFrame("Results");
            //JDialog frame = new JDialog(MiniSBEGui.this,true);
            frame.getContentPane().setLayout(new BorderLayout());

            JTable table = createResultTable(sbec);
            table.getModel().addTableModelListener(this);
            JScrollPane scrollpane = new JScrollPane(table);
            frame.getContentPane().add(scrollpane,BorderLayout.CENTER);
            JToolBar toolbar =new JToolBar();
            frame.getContentPane().add(toolbar, BorderLayout.NORTH);

            JButton saveresultsbutton = new JButton(new SaveResultsAction(sbec));
            toolbar.add(saveresultsbutton);

            JButton showdiffs = new JButton(new ShowDiffAction(sbec,cfg,this));
            toolbar.add(showdiffs);
            frame.pack();

            ToolTipManager.sharedInstance().setDismissDelay(100000);
            frame.setVisible(true);
        }


        /* (non-Javadoc)
         * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
         */
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            TableModel model = (TableModel)e.getSource();

            Boolean filter = (Boolean) model.getValueAt(row, column);
            String id = (String) model.getValueAt(row,1);
            String filterstring;
            if(column == 20)
                filterstring=((MiniSBEResultTableModel)model).getFilterFor(id);
            else
                filterstring=((MiniSBEResultTableModel)model).getPLFilterFor(id);
            modifyUserFilterFor(id,filterstring,filter.booleanValue());
        }

    }
    private final class PreferencesAction extends MyAction {
        public PreferencesAction() {
            super("Settings", "change settings for the assay design"
                    ,PreferencesAction.class.getClassLoader().getResource("images/properti.gif")
                    ,KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        }
        public void actionPerformed(ActionEvent e) {
        	SBEConfigDialog dia=getConfigDialog();
        	dia.setVisible(true);
            System.out.println("Am I still showing preferences... ? Shouldn't...");
            for (Iterator it = sbepanels.iterator(); it.hasNext();) {
                SBECandidatePanel panel = (SBECandidatePanel) it.next();
                panel.refreshData(dia.getSBEOptionsFromGui());
            }
        }
    }
    private class AddPanelAction extends MyAction {
        public AddPanelAction() {
            super("Add", "add new SBE primer"
                    ,AddPanelAction.class.getClassLoader().getResource("images/add.gif")
                    ,KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        }
        public void actionPerformed(java.awt.event.ActionEvent e) {
            addSBECandidatePanel(sbepanels.size());
            getSbepanelsPanel().revalidate();
            getSbepanelsPanel().repaint();

        }
    }
	private class NewAssayDesignAction extends MyAction {
        public NewAssayDesignAction() {
            super("New", "New Assay Design"
                    ,NewAssayDesignAction.class.getClassLoader().getResource("images/new.gif")
                    ,KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        }
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if(sbepanels.size() == 0)
                return;//nothing to do
            int answer = askUserForSave();
            if (answer == JOptionPane.CANCEL_OPTION)
                return;

            getSbepanelsPanel().removeAll();
            sbepanels.clear();
            getSbepanelsPanel().revalidate();
            getSbepanelsPanel().repaint();
        }
    }
    private class LoadPrimerAction extends MyAction {
        public LoadPrimerAction() {
            super("Load primers"
                    ,"load SBE primers from file",
                    LoadPrimerAction.class.getClassLoader().getResource("images/open.gif")
                    ,KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        }
        public void actionPerformed(java.awt.event.ActionEvent e) {
            getNewAction().actionPerformed(e);//loesche alle bestehenden primer in der gui

            FileFilter filter = new FileFilter(){
                public boolean accept(File f) {
                    if(f.isDirectory())
                        return true;
                    if(f.isFile() && (f.getName().endsWith(".csv") || f.getName().endsWith(".CSV")))
                        return true;
                    return false;
                }
                public String getDescription() {
                    return "MiniSBE-primerfiles (.csv)";
                }
            };
            File file = FileSelector.getUserSelectedFile(MiniSBEGui.this,"Load sbeprimers...",filter,FileSelector.OPEN_DIALOG);
            if(file !=null){
                List primerlines = new LinkedList();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line=br.readLine();//skip header
                    while((line=br.readLine())!=null) {
                        primerlines.add(Helper.clearEmptyCSVEntries(line));
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(MiniSBEGui.this,"Error loading file "+file.getName(),"",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                for (int i = 0; i < primerlines.size(); i++) {
                    addSBECandidatePanel(i);
                }
                int i=0;
                for (Iterator it = primerlines.iterator(); it.hasNext();i++) {
                    try {
                        ((SBECandidatePanel)sbepanels.get(i)).setValuesFromCSVLine((String)it.next());
                    } catch (RuntimeException e2) {
                        e2.printStackTrace();
                        JOptionPane.showMessageDialog(MiniSBEGui.this,"Encountered error while parsing input file: "+file.getName(),"",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            getSbepanelsPanel().revalidate();
            getSbepanelsPanel().repaint();
        }
    }
    private class SavePrimerAction extends MyAction {
        public SavePrimerAction() {
            super("Save primers to...","save SBE primers to file"
                    ,SavePrimerAction.class.getClassLoader().getResource("images/save.gif"),
                    KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        }
        public void actionPerformed(java.awt.event.ActionEvent e) {
            File file=null;
            FileFilter filter=new FileFilter(){
                public boolean accept(File f) {
                    if(f.isDirectory())
                        return true;
                    if(f.isFile() && (f.getName().endsWith(".csv") || f.getName().endsWith(".CSV")))
                        return true;
                    return false;
                }
                public String getDescription() {
                    return "MiniSBE-primerfiles (.csv)";
                }
            };
            file = FileSelector.getUserSelectedFile(MiniSBEGui.this,"Save sbeprimers...",filter,FileSelector.SAVE_DIALOG);
            if(file != null){
                String path=file.getAbsolutePath();
                if(!path.endsWith(".csv") && !path.endsWith(".CSV"))
                    path += ".csv";
                file=new File(path);
                List sbec = new LinkedList();
                for (Iterator it = sbepanels.iterator(); it.hasNext();) {
                    SBECandidatePanel p = (SBECandidatePanel) it.next();
                    sbec.add(p);
                }
                Algorithms.remove(sbec.iterator(),IsNull.instance());
                try {
                    writeSBECandidatesFile(sbec,file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(MiniSBEGui.this,e1.getMessage(),"Error saving to file "+file.getName(),JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        /**
         * @param sbec
         * @param file
         */
        private void writeSBECandidatesFile(List sbec, File file) throws IOException{
            BufferedWriter bw=new BufferedWriter(new FileWriter(file));
            final String header = "\"SBE-ID\";" +
                    "\"5\' Sequenz (in 5\'->3\')\";" +
                    "\"Definitiver Hairpin 5\'\";" +
                    "\"SNP Variante\";" +
                    "\"3\' Sequenz (in 5\' -> 3\')\"" +
                    ";\"Definitiver Hairpin 3\'\";" +
                    "\"PCR Produkt\";" +
                    "\"Feste Photolinkerposition (leer, wenn egal)\";" +
                    "\"feste MultiplexID\";" +
                    "\"Ausgeschlossene Primer\";" +
                    "\"Primer wird verwendet as-is";
            bw.write(header);
            bw.write("\n");
            for (Iterator it = sbec.iterator(); it.hasNext();) {
                SBECandidatePanel p = (SBECandidatePanel) it.next();
                bw.write(p.getCSVLine());
                bw.write("\n");
            }
            bw.close();
        }
    }
    private class ExitAppAction extends MyAction{

        public ExitAppAction() {
            super("Exit","exits the application",ExitAppAction.class.getClassLoader().getResource("images/exit.gif"),
                    KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            exitApp();
        }

    }
    private static Preferences prefs=Preferences.userNodeForPackage(MiniSBEGui.class);
    private static final int DEFAULT_WINDOW_X = 50;
    private static final int DEFAULT_WINDOW_Y = 50;
    private static final int DEFAULT_WINDOW_WIDTH = 300;
    private static final int DEFAULT_WINDOW_HEIGHT = 100;
//  Keys for this frame's preferences
    private static final String WINDOW_X_KEY = "WINDOW_X";
    private static final String WINDOW_Y_KEY = "WINDOW_Y";
    private static final String WINDOW_WIDTH_KEY = "WINDOW_WIDTH";
    private static final String WINDOW_HEIGHT_KEY = "WINDOW_HEIGHT";

    private javax.swing.JPanel jContentPane = null;

	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem addPanelMenuItem = null;
	private JMenuItem openMenuItem = null;
	private JMenuItem saveMenuItem = null;
	private JMenuItem helpMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JMenuItem jMenuItem = null;
	private JToolBar jToolBar = null;
	private JButton prefButton = null;
	private JButton addPanelButton = null;
	private JButton jButton2 = null;
	private JPanel sbepanelsPanel = null;
	private JScrollPane jScrollPane = null;
	private JButton calcButton = null;
	private JPanel jPanel1 = null;
	private JToggleButton expertToggleButton = null;
	private SBEConfigDialog dialog = null;

	private int sbe_anzahl=-1;
	List sbepanels;

    private Action newAction;
    private Action panelAction;
    private Action loadPrimerAction;
    private Action savePrimerAction;

    private JMenuItem newDesignMenuItem;
    protected boolean expertmode;
    private JMenuItem prefMenuItem;
    private JButton consoleButton;


	/**
	 * This is the default constructor
	 */
	public MiniSBEGui() {
		super();

		while( sbe_anzahl < 1) {
			try{
                String input=JOptionPane.showInputDialog(null,"Please enter assay size level (1-?):","1");
                if(null == input)
                    System.exit(0);
				sbe_anzahl = Integer.parseInt(input);

			}
		catch(NumberFormatException nfe){}
		}
		initialize();
		int windowX = prefs.getInt(WINDOW_X_KEY, DEFAULT_WINDOW_X);
		int windowY = prefs.getInt(WINDOW_Y_KEY, DEFAULT_WINDOW_Y);
		int windowWidth = prefs.getInt(WINDOW_WIDTH_KEY, DEFAULT_WINDOW_WIDTH);
		int windowHeight = 	prefs.getInt(WINDOW_HEIGHT_KEY, DEFAULT_WINDOW_HEIGHT);
		setBounds(windowX, windowY, windowWidth, windowHeight);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				exitApp();
			}
		});
	}
	private void exitApp() {
//		 Save the state of the window as preferences
        int answer = askUserForSave();
//        if (answer == JOptionPane.CANCEL_OPTION)
//            return;
		prefs.putInt(WINDOW_WIDTH_KEY, getWidth());
		prefs.putInt(WINDOW_HEIGHT_KEY, getHeight());
		prefs.putInt(WINDOW_X_KEY, getX());
		prefs.putInt(WINDOW_Y_KEY, getY());
		System.exit(0);
		}
	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
        UIManager.put("ToolTip.font",new Font("Monospaced",Font.PLAIN,11)); //neuer font fuer tooltips, wegen nicht proportionalem font
		this.sbepanels=new LinkedList();
		getConfigDialog();//damit parameter initialisiert werden
		this.setJMenuBar(getJJMenuBar());
		this.setSize(707, 337);
		this.setContentPane(getJContentPane());
        setTitle("MiniSBE (freeze 1) BETA");
	}
	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJToolBar(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getJPanel1(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jJMenuBar
	 *
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}
	/**
	 * This method initializes jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getNewDesignMenuItem());
			fileMenu.add(getAddPanelMenuItem());
			fileMenu.add(getOpenMenuItem());
			fileMenu.add(getSaveMenuItem());
			fileMenu.add(getPreferencesMenuItem());
            fileMenu.addSeparator();
            fileMenu.add(new ExitAppAction());
		}
		return fileMenu;
	}
	/**
     * @return
     */
    private JMenuItem getPreferencesMenuItem() {
        if(prefMenuItem == null) {
            prefMenuItem = new JMenuItem(new PreferencesAction());
        }
        return prefMenuItem;
    }
    /**
     * @return
     */
    private JMenuItem getNewDesignMenuItem() {
        if(newDesignMenuItem == null) {
            newDesignMenuItem=new JMenuItem(getNewAction());
        }
        return newDesignMenuItem;
    }
    /**
     * @return
     */
    private Action getNewAction() {
        if(newAction == null) {
            newAction = new NewAssayDesignAction();
        }
        return newAction;
    }
    /**
	 * This method initializes jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Hilfe");
			helpMenu.add(getHelpMenuItem());
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}
	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getAddPanelMenuItem() {
		if (addPanelMenuItem == null) {
			addPanelMenuItem = new JMenuItem();
			addPanelMenuItem.setAction(getAddPanelAction());
		}
		return addPanelMenuItem;
	}
	/**
	 * This method initializes jMenuItem1
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getOpenMenuItem() {
		if (openMenuItem == null) {
			openMenuItem = new JMenuItem();
			openMenuItem.setAction(getLoadPrimerAction());
		}
		return openMenuItem;
	}
	/**
     * @return
     */
    private Action getLoadPrimerAction() {
        if(loadPrimerAction == null) {
            loadPrimerAction = new LoadPrimerAction();
        }
        return loadPrimerAction;
    }
    /**
	 * This method initializes jMenuItem2
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setAction(getSavePrimerAction());
		}
		return saveMenuItem;
	}
	/**
     * @return
     */
    private Action getSavePrimerAction() {
        if(savePrimerAction == null) {
            savePrimerAction = new SavePrimerAction();
        }
        return savePrimerAction;
    }
    /**
	 * This method initializes jMenuItem3
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getHelpMenuItem() {
		if (helpMenuItem == null) {
			helpMenuItem = new JMenuItem();
			helpMenuItem.setText("Hilfe");
		}
		return helpMenuItem;
	}
	/**
	 * This method initializes jMenuItem4
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
		}
		return aboutMenuItem;
	}
	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItem() {
		if (jMenuItem == null) {
			jMenuItem = new JMenuItem();
			jMenuItem.setText("Speichern unter...");
		}
		return jMenuItem;
	}
	/**
	 * This method initializes jToolBar
	 *
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getJToolBar() {
		if (jToolBar == null) {
			jToolBar = new JToolBar();
			jToolBar.add(getNewAction());
			jToolBar.add(getAddPanelAction());
			jToolBar.add(getPrefButton());
			jToolBar.add(getExpertToggleButton());
			jToolBar.add(getConsoleToggleButton());
		}
		return jToolBar;
	}
	/**
     * @return
     */
    private JButton getConsoleToggleButton() {
        if (consoleButton == null) {
            consoleButton = new JButton();
            consoleButton.setAction(new ConsoleViewAction());
        }
        return consoleButton;
    }
    /**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getPrefButton() {
		if (prefButton == null) {
			prefButton = new JButton();
			prefButton.setAction(new PreferencesAction());
		}
		return prefButton;
	}
	private SBEConfigDialog getConfigDialog(){
		if(dialog == null){
			dialog= new SBEConfigDialog(MiniSBEGui.this);
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            //dialog.setUndecorated(true);
		}
		return dialog;
	}
	/**
	 * This method initializes addPanelButton
	 *
	 * @return javax.swing.JButton
	 */
	private Action getAddPanelAction() {
		if (panelAction == null) {
            panelAction = new AddPanelAction();
		}
		return panelAction;
	}
	/**
	 * This method initializes jButton2
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("Laden");
		}
		return jButton2;
	}
	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getSbepanelsPanel() {
		if (sbepanelsPanel == null) {
			sbepanelsPanel = new JPanel();
			sbepanelsPanel.setLayout(new BoxLayout(sbepanelsPanel, BoxLayout.Y_AXIS));
			for(int i=0; i<sbe_anzahl;i++){
                addSBECandidatePanel(i);
			}
		}
		return sbepanelsPanel;
	}
	/**
     * @param index
     */
    private void addSBECandidatePanel(int index) {
        SBECandidatePanel p = new SBECandidatePanel("ID"+(index+1));
        p.setExpertMode(expertmode);
        if(index%2 == 1)
        	p.setBackgroundColor(new Color(230,230,255));
        sbepanels.add(p);
        sbepanelsPanel.add(p);
    }
    /**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getSbepanelsPanel());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jButton4
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCalcButton() {
		if (calcButton == null) {
			calcButton = new JButton();
			calcButton.setAction(new CalculateAction());

		}
		return calcButton;
	}
	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.add(getCalcButton(), null);
		}
		return jPanel1;
	}
	/**
	 * This method initializes jToggleButton
	 *
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getExpertToggleButton() {
		if (expertToggleButton == null) {
			expertToggleButton = new JToggleButton();
			expertToggleButton.setText("Expertmode");
			expertToggleButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					expertmode = e.getStateChange() == ItemEvent.SELECTED;
                    expertToggleButton.setText(expertmode?"To Standardmode":"To Expertmode");
					for (Iterator it = sbepanels.iterator(); it.hasNext();) {
						SBECandidatePanel sp = (SBECandidatePanel) it.next();
						sp.setExpertMode(expertmode);
					}
				}
			});
		}
		return expertToggleButton;
	}


    /**
     * Modifies the user generated filters for a given SBECandidate id.
     * @param id
     * @param b true, if this filter should be added, false if it shall be removed
     */
    private void modifyUserFilterFor(String id, String filter,boolean b) {
        JToggleButton eb = getExpertToggleButton();
        if(!eb.isSelected())
            eb.doClick();
        for (Iterator it = this.sbepanels.iterator(); it.hasNext();) {
            SBECandidatePanel panel = (SBECandidatePanel) it.next();
            if(panel.getId().equals(id)) {
                String oldfilters = panel.getFilters();
                int pos = oldfilters.indexOf(filter);
                if(pos != -1)
                    oldfilters = oldfilters.substring(0,pos)+oldfilters.substring(pos+filter.length());
                if(b == true)
                    oldfilters=oldfilters+" "+filter;
                oldfilters=oldfilters.replaceAll("   *"," ").trim();//loesche alle mehrfachen leerzeichen
                panel.setFilters(oldfilters);
                return;
            }
        }
    }

 	public static void main(String[] args) {
 	    if(args.length == 0) {
            MiniSBE.initLogfile(".");
            final MiniSBEGui frame=new MiniSBEGui();
 	        frame.pack();
 	        frame.setVisible(true);
 	    }
        else {
            MiniSBE.main(args);
        }
	}

    private class ConsoleViewAction extends MyAction{
        ConsoleWindow console = null;
        /**
         * @param name
         * @param tooltip
         * @param iconurl
         * @param key
         */
        public ConsoleViewAction() {
            super("show logfile","shows the logfile contents", null, KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            if(console == null) {
                PrintStream out = System.out;
                console = new ConsoleWindow(true);
                console.setCopyOutputStream(out);
            }
            ConsoleWindow.showInstalledConsole();
        }

    }
    /**
     * @param sbec
     * @return
     */
    public static JTable createResultTable(final List sbec) {
        final TableModel model = new MiniSBEResultTableModel(sbec);

        //TableSorter sorter = new TableSorter(model);
//        JTableEx table = new JTableEx(sorter) {
        JTableEx table = new JTableEx(model) {
            public String getToolTipText(MouseEvent event) {
                Point p= event.getPoint();
                int row= rowAtPoint(p);
                int col= columnAtPoint(p);
                if(col == 10) {//XXX
                    return getSekStrukTooltipFor((SBECandidate)sbec.get(row));
                }else if(col == 8 || col == 9 ){
                    return splittedHtmlLine(model.getValueAt(row,col).toString());
                }else {
                    return super.getToolTipText(event);
                }
            }

            /**
             * @param candidate
             * @return
             */
            protected String getSekStrukTooltipFor(SBECandidate s) {
                if(!s.hasValidPrimer())
                    return null;
                StringBuffer sb = new StringBuffer("<html>");

                Set sek=s.getSekStrucs();
                for (Iterator it = sek.iterator(); it.hasNext();) {
                    SBESekStruktur struk = (SBESekStruktur) it.next();
                    sb.append(struk.toString().replaceAll("\n","<br>").replaceAll(" ","&nbsp;"));
                    sb.append("<br>");
                    sb.append(struk.getAsciiArt().replaceAll("\n","<br>").replaceAll(" ","&nbsp;"));
                    sb.append("<br>");
                }
                sb.append("</html>");
                return new String(sb);
            }

            /**
             * @param event
             * @return
             */
            protected String splittedHtmlLine(String line) {
                StringBuffer sb= new StringBuffer("<html>");
                StringTokenizer st= new StringTokenizer(line,",");
                while(st.hasMoreTokens()){
                    sb.append(st.nextToken());
                    sb.append("<br>");
                }
                sb.append("</html>");
                return new String(sb);
            }
        };
        table.setPreferredScrollableViewportSize(new Dimension(400,table.getPreferredSize().height));
        //sorter.setTableHeader(table.getTableHeader());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for(int j=0; j <table.getColumnCount();j++){
            TableColumn column = table.getColumnModel().getColumn(j);
            column.setPreferredWidth(100);
        }
        return table;
    }
    /**
     * Saves, if the user clicks yes, returns the value received from  JOptionPane.
     * @return
     */
    protected int askUserForSave() {
        int answer=JOptionPane.showConfirmDialog(null,"Would you like to save?","New assay design",JOptionPane.YES_NO_CANCEL_OPTION);
        if(answer == JOptionPane.YES_OPTION)
            getSavePrimerAction().actionPerformed(new ActionEvent(this,0,"save"));
        return answer;
    }
  }  //  @jve:decl-index=0:visual-constraint="10,102"

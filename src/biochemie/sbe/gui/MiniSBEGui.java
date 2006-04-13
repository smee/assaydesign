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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
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
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import netprimer.cal_Hairpins;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.core.IsNull;

import biochemie.calcdalton.JTableEx;
import biochemie.domspec.SBEPrimer;
import biochemie.domspec.SBESekStruktur;
import biochemie.gui.InfiniteProgressPanel;
import biochemie.sbe.MiniSBE;
import biochemie.sbe.CleavablePrimerFactory;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.gui.actions.SaveResultsAction;
import biochemie.sbe.gui.actions.ShowDiffAction;
import biochemie.sbe.io.MultiKnoten;
import biochemie.sbe.io.SBEPrimerReader;
import biochemie.sbe.multiplex.Multiplexer;
import biochemie.util.ConsoleWindow;
import biochemie.util.FileSelector;
import biochemie.util.GUIHelper;
import biochemie.util.Helper;
import biochemie.util.MyAction;
import biochemie.util.SwingWorker;
import biochemie.util.TableSorter;
import biochemie.util.edges.SecStructureEdge;

/**
 * @author Steffen Dienst
 * TODO NLS
 */
public class MiniSBEGui extends JFrame {

    public class OptimizePLAction extends MyAction {
        private List sbec;

        public OptimizePLAction(List sbec) {
            super("Optimize",
                    "Optimize cleavable linkers",
                    OptimizePLAction.class.getClassLoader().getResource("images/wizard.gif"),
                    null);
            this.sbec=sbec;
        }

        public void actionPerformed(ActionEvent e) {
            final SBEOptions cfg = getConfigDialog().getSBEOptionsFromGui();
            File tempfile = null;
            try {
                tempfile = File.createTempFile("__minisbe",".csv");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            getSavePrimerAction().saveToFile(tempfile);
            final File toLoad = tempfile;
            final Set filter=new HashSet();//edges that should be omitted
            for(int i=0; i < sbec.size(); i++) {
                CleavablePrimerFactory cand = ((CleavablePrimerFactory)sbec.get(i));
                SBECandidatePanel panel = (SBECandidatePanel)sbepanels.get(i);
                boolean setPL = false;
                if(cand.hasValidPrimer()) {
                    Set s=cand.getFavPrimer().getSecStrucs();
                    for (Iterator it = s.iterator(); it.hasNext();) {
                        SBESekStruktur struc = (SBESekStruktur) it.next();
                        filter.add(new SecStructureEdge(null,null,struc).matchString());
                    }
                }
                enterInGUI(cand, panel, setPL);
            }
            System.out.println("Filterset: "+filter);
            //run without secstrucs
            SwingWorker sw=new SwingWorker() {
                public Object construct() {
                    System.out.println("Running step 1 of the pl optimizer....");
                    List sbec2=(List) getCalculationAction().runCalculation("",getCalculationAction().getCompactedSBECandidates(cfg),filter,cfg,false);
                    return sbec2;
                }
                public void finished() {
                    List sbec=(List) get();
                    final SBEOptions cfg=getConfigDialog().getSBEOptionsFromGui();
                    getLoadPrimerAction().loadFromFile(toLoad);
                    for(int i=0; i < sbec.size(); i++) {
                        CleavablePrimerFactory cand = ((CleavablePrimerFactory)sbec.get(i));
                        SBECandidatePanel panel = (SBECandidatePanel)sbepanels.get(i);
                        enterInGUI(cand, panel, true);
                    }
                    SwingWorker sw2= new SwingWorker() {
                        public Object construct() {
                            System.out.println("Running step 2 of the pl optimizer....");
                            return getCalculationAction().runCalculation("PL-optimized results",getCalculationAction().getCompactedSBECandidates(cfg),Collections.EMPTY_SET,cfg,true);
                        }
                        public void finished() {
                            getLoadPrimerAction().loadFromFile(toLoad);
                            if(toLoad != null)
                                toLoad.delete();
                            getCalculationAction().hideProgressIndicator();
                        }
                    };
                    sw2.start();
                }
            };
            getCalculationAction().showProgressIndicator(sw);
            sw.start();
        }

        /**
         * @param cand
         * @param panel
         * @param setPL
         */
        private void enterInGUI(CleavablePrimerFactory cand, SBECandidatePanel panel, boolean setPL) {
            if(cand.hasValidPrimer() == false)
                return;
            if(cand.getType().equals(SBEPrimer._5_)) {
                panel.getSeq3tf().setText("");
                if(setPL)
                    panel.getPlpanel5().setSelectedPL(cand.getBruchstelle());
            }else {
                panel.getSeq5tf().setText("");
                if(setPL)
                    panel.getPlpanel3().setSelectedPL(cand.getBruchstelle());
            }
        }

        private SBESekStruktur hatVerhinderteSekStruc(CleavablePrimerFactory cand) {
            Set sec=cand.getSekStrucs();
            for (Iterator it = sec.iterator(); it.hasNext();) {
                SBESekStruktur struc = (SBESekStruktur) it.next();
                if(struc.isVerhindert())
                    return struc;
            }
            return null;
        }
    }
    public class ExplainPrimerAction extends MyAction {
        private JTable table;
        
        public ExplainPrimerAction(JTable table) {
            super("Show reasoning",
                    "shows the considered primers and the sortings used for the selected row",
                    CalculateAction.class.getClassLoader().getResource("images/question.gif"),
                    null);
            this.table=table;
        }

        public void actionPerformed(ActionEvent e) {
            int row=table.getSelectedRow();
            if(row < 0)
                return;
            String id=(String) table.getValueAt(row,1);
            for (Iterator it = sbepanels.iterator(); it.hasNext();) {
                SBECandidatePanel panel = (SBECandidatePanel) it.next();
                if(panel.getTfId().getText().trim().equals(id)) {
                    showExplanationFrameFor(panel);
                    return;
                }
            }
        }

        private void showExplanationFrameFor(SBECandidatePanel panel) {
            SBEOptions cfg=getConfigDialog().getSBEOptionsFromGui();
            cfg.setDebug(true);
            String output=panel.getSBECandidate(cfg, true).getOutput();
            JFrame frame = new JFrame("Detailed report for "+panel.getTfId().getText());
            frame.getContentPane().setLayout(new BorderLayout());
            JEditorPane ep = new JEditorPane() {
                public Dimension getPreferredScrollableViewportSize() {
                    return new Dimension(500,700);
                }
                public boolean getScrollableTracksViewportWidth() {
                    return false;
                 }
            };
            output="<html><body><font size=\"2\" face=\"Courier\"><pre>"+output+"</pre></font></html></body>";
            ep.setEditable(false);
            ep.setContentType("text/html");
            ep.setText(output);
            ep.setCaretPosition(0);
            //JTextArea ed=new JTextArea(output);
//            ed.setRows(40);
//            ed.setFont(new Font("Courier",Font.PLAIN,11));
//            JScrollPane pane = new JScrollPane(ed);
            JScrollPane pane = new JScrollPane(ep);
            frame.getContentPane().add(pane,BorderLayout.CENTER);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            System.out.println(frame.getBounds());
        }
    }
    public class CalculateAction extends MyAction {
        private JDialog progressdialog;

        public CalculateAction() {
            super("Calculate","Start calculation",
                    CalculateAction.class.getClassLoader().getResource("images/play.gif"),
                    (KeyStroke)null);
        }
        public void actionPerformed(ActionEvent e) {
            if(verifyUserFilters()==false) {
                JOptionPane.showMessageDialog(MiniSBEGui.this,"Invalid primerfilters. Please review the marked fields!");
                return;
            }
            SwingWorker sw=new SwingWorker() {
                public Object construct() {
                    SBEOptions cfg = getConfigDialog().getSBEOptionsFromGui();
                    List sbeccoll = getCompactedSBECandidates(cfg);
                    runCalculation("Results",sbeccoll, Collections.EMPTY_SET,cfg,true);
                    return null;
                }
                public void finished() {
                    hideProgressIndicator();
                }
            };
            showProgressIndicator(sw);
            sw.start();
        }
        /**
         * Tests, if the format of the filters entered by the user are correct.
         * @return
         */
        private boolean verifyUserFilters() {
            Pattern re=Pattern.compile("(3|5|\\\\*)_(\\d+|\\*)_(\\d+|\\*)");
            boolean flag=true;
            for (Iterator it = sbepanels.iterator(); it.hasNext();) {
                SBECandidatePanel p = (SBECandidatePanel) it.next();
                String filter=p.getFiltersPanel().getText();
                StringTokenizer st=new StringTokenizer(filter);
                p.getFiltersPanel().getPBSequenceField().setBorder(BorderFactory.createLineBorder(Color.black,1));
                p.getFiltersPanel().getPBSequenceField().setToolTipText(null);
                while(flag && st.hasMoreTokens()) {
                    String token=st.nextToken();
                    if(!re.matcher(token).matches()) {
                        p.getFiltersPanel().getPBSequenceField().setBorder(BorderFactory.createLineBorder(Color.red,2));
                        p.getFiltersPanel().getPBSequenceField().setToolTipText("Invalid format: "+token);
                        getExpertToggleButton().setSelected(true);
                        flag=false;
                    }
                }
            }
            return flag;
        }
        public List getCompactedSBECandidates(SBEOptions cfg) {
            List sbec= new ArrayList(sbepanels.size());
            for (Iterator it = sbepanels.iterator(); it.hasNext();) {
                SBECandidatePanel p = (SBECandidatePanel) it.next();
                sbec.add(p.getSBECandidate(cfg, false));
            }
            Algorithms.remove(sbec.iterator(),IsNull.instance());
            return SBEPrimerReader.collapseMultiplexes(sbec,cfg);
        }
        public void showProgressIndicator(final SwingWorker sw) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            //dialog um den user zu informieren
            final JDialog dialog = new JDialog(MiniSBEGui.this,"Calculationprogress",false);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            Container pane=dialog.getContentPane();
            pane.setLayout(new BoxLayout(pane,BoxLayout.Y_AXIS));
            JProgressBar bar =new JProgressBar(JProgressBar.HORIZONTAL);
            bar.setIndeterminate(true);
            pane.add(bar);
            pane.add(new JLabel("Please have some patience, this operation might need some time."));
            GUIHelper.center(dialog, MiniSBEGui.this);
            getInfiniteProgressPanel().start();
            JButton stopbutton = new JButton("Cancel");
            stopbutton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Multiplexer.stop(true);
                    sw.interrupt();
                    hideProgressIndicator();
                }
            });
            pane.add(stopbutton);
            dialog.pack();
            dialog.setVisible(true);
            progressdialog=dialog;
        }
        public void hideProgressIndicator() {
            progressdialog.dispose();
            progressdialog=null;
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            getInfiniteProgressPanel().stop();
        }
        /**
         * @param compactsbec
         * @param cfg
         */
        private List runCalculation(final String title, final List compactsbec, final Set filter,final SBEOptions cfg, final boolean showResult) {
            Multiplexer.stop(false);
            MiniSBE m = new MiniSBE(compactsbec,cfg,filter);
            if(Thread.currentThread().isInterrupted())
                return null;
            normalizeSekStruks(getSBECandidatesFromMultiKnotenList(compactsbec));
            if(showResult)
                showResultFrame(title,getSBECandidatesFromMultiKnotenList(compactsbec),cfg);
            return compactsbec;
        }
        /**
         * Liefert List mit SBEcandidates, compactsbec kann SBECs, Multiknoten enthalten.
         * @param compactsbec
         * @return
         */
        protected List getSBECandidatesFromMultiKnotenList(List compactsbec) {
            List ret=new ArrayList();
            for (Iterator it = compactsbec.iterator(); it.hasNext();) {
                Object o = it.next();
                if(o instanceof CleavablePrimerFactory) {
                    ret.add(o);
                    continue;
                }else if(o instanceof MultiKnoten) {
                    ret.addAll(((MultiKnoten)o).getSBECandidates());
                }else
                    throw new IllegalArgumentException("FEHLER im Programm: Liste darf nur SBECandidates oder MultiKnoten enthalten!");
            }
            return ret;
        }
        /**
         * Liste darf nur 
         * @param sbec
         */
        protected void normalizeSekStruks(List sbec) {
            for (Iterator iter = sbec.iterator(); iter.hasNext();) {
                CleavablePrimerFactory sc = (CleavablePrimerFactory) iter.next();
                if(sc.hasValidPrimer())
                    sc.normalizeCrossdimers(new HashSet(sbec));
            }

        }
        /**
         * @param sbec
         */
        private void showResultFrame(String title, List sbec, SBEOptions cfg) {
            JFrame frame = new JFrame(title);
            //JDialog frame = new JDialog(MiniSBEGui.this,true);
            frame.getContentPane().setLayout(new BorderLayout());

            JTable table = createResultTable(sbec);
            JScrollPane scrollpane = new JScrollPane(table);
            frame.getContentPane().add(scrollpane,BorderLayout.CENTER);
            JToolBar toolbar =new JToolBar();
            frame.getContentPane().add(toolbar, BorderLayout.NORTH);

            JButton saveresultsbutton = new JButton(new SaveResultsAction(sbec));
            toolbar.add(saveresultsbutton);

            JButton showdiffs = new JButton(new ShowDiffAction(sbec,cfg,this));
            toolbar.add(showdiffs);
            JButton showexplanation=new JButton(new ExplainPrimerAction(table));
            toolbar.add(showexplanation);
            JButton optimize=new JButton(new OptimizePLAction(sbec));
            toolbar.add(optimize);
            JButton showSekStrucs=new JButton(new ShowSekStrucsAction(table,sbec));
            toolbar.add(showSekStrucs);
            ToolTipManager.sharedInstance().setDismissDelay(100000);
            frame.pack();
            frame.setVisible(true);
        }

        public JTable createResultTable(final List sbec) {
            final TableModel model = new MiniSBEResultTableModel(sbec);

            final TableSorter sorter = new TableSorter(model);
            JTableEx table = new JTableEx(sorter) {

                public String getToolTipText(MouseEvent event) {
                    Point p= event.getPoint();
                    int row= rowAtPoint(p);
                    int col= columnAtPoint(p);
                    if(col == 10) {//XXX
                        return getSekStrukTooltipFor((String) sorter.getValueAt(row,1));
                    }else if(col == 8 || col == 9 ){
                        return splittedHtmlLine(sorter.getValueAt(row,col).toString());
                    }else {
                        return super.getToolTipText(event);
                    }
                }

                /**
                 * @param candidate
                 * @return
                 */
                protected String getSekStrukTooltipFor(String id) {
                    CleavablePrimerFactory s=findSBECandidateWithID(id);
                    if(s==null || !s.hasValidPrimer())
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

                private CleavablePrimerFactory findSBECandidateWithID(String id) {
                    for (Iterator iter = sbec.iterator(); iter.hasNext();) {
                        CleavablePrimerFactory s = (CleavablePrimerFactory) iter.next();
                        if(s.getId().equals(id))
                            return s;
                    }
                    return null;
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
            sorter.setTableHeader(table.getTableHeader());
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            //ColumnResizer.adjustColumnPreferredWidths(table);
            for(int j=0; j <table.getColumnCount();j++){
                TableColumn column = table.getColumnModel().getColumn(j);
                column.setPreferredWidth(100);
            }
            model.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    if(row < 0 || column < 0 ) //structural change, like sorting
                        return;
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
            });
            return table;
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
            File f = null;
            try {
                f = File.createTempFile("__set",".csv");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        	getSavePrimerAction().saveToFile(f);
            for (Iterator it = sbepanels.iterator(); it.hasNext();) {
                SBECandidatePanel panel = (SBECandidatePanel) it.next();
                panel.refreshData(dia.getSBEOptionsFromGui());
            }
            getLoadPrimerAction().loadFromFile(f);
            if(f != null)
                f.delete();
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
            int answer = askUserForSaveIfNeeded();
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
	        //getNewAction().actionPerformed(e);//loesche alle bestehenden primer in der gui
	        int answer = askUserForSaveIfNeeded();
	        if (answer == JOptionPane.CANCEL_OPTION)
	            return;
	        
	        
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
            loadFromFile(file);
	    }
        /**
         * @param file
         */
        public void loadFromFile(File file) {
            if(file !=null){
	            getSbepanelsPanel().removeAll();//XXX eigentlich die Aufgabe von newaction
	            sbepanels.clear();
	            List primerlines = new LinkedList();
	            try {
	                BufferedReader br = new BufferedReader(new FileReader(file));
	                String line=br.readLine().trim();//skip header
                    if(line.charAt(0)=='"')
                        line=line.substring(1);
                    //TODO generisch laden (Format erkennen etc.)
                    final boolean isInputfile=line.startsWith("SBE-ID");
	                while((line=br.readLine())!=null) {
	                    primerlines.add(Helper.clearEmptyCSVEntries(line));
	                }
	                
	                for (int i = 0; i < primerlines.size(); i++) {
	                    addSBECandidatePanel(i);
	                }
	                int i=0;
	                for (Iterator it = primerlines.iterator(); it.hasNext();i++) {
	                    SBECandidatePanel p=((SBECandidatePanel)sbepanels.get(i));
                        if(isInputfile)
                            p.setValuesFromCSVInputLine((String)it.next());
                        else
                            p.setValuesFromCSVOutputLine((String)it.next());
	                    p.setUnchanged();
	                }
	            } catch (IOException e1) {
	                e1.printStackTrace();
	                JOptionPane.showMessageDialog(MiniSBEGui.this,"Error loading file "+file.getName(),"",JOptionPane.ERROR_MESSAGE);
	            } catch (RuntimeException e2) {
	                e2.printStackTrace();
	                JOptionPane.showMessageDialog(MiniSBEGui.this,
                            "Encountered error while parsing input file: "+file.getName()+". Message: "+e2.getMessage(),
                            "Invalid inputfile",
                            JOptionPane.ERROR_MESSAGE);
	            }
                getSbepanelsPanel().revalidate();
                getSbepanelsPanel().repaint();
	        }
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
            saveToFile(file);
        }
        /**
         * @param file
         */
        public void saveToFile(File file) {
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
            final String header = "SBE-ID;" +
                    "5\' Sequenz (in 5\'->3\');" +
                    "Definitiver Hairpin 5\';" +
                    "SNP Variante;" +
                    "3\' Sequenz (in 5\' -> 3\')" +
                    ";Definitiver Hairpin 3\';" +
                    "PCR Produkt;" +
                    "Feste Photolinkerposition (leer, wenn egal);" +
                    "feste MultiplexID;" +
                    "Ausgeschlossene Primer;" +
                    "Primer wird verwendet as-is";
            bw.write(header);
            bw.write("\n");
            for (Iterator it = sbec.iterator(); it.hasNext();) {
                SBECandidatePanel p = (SBECandidatePanel) it.next();
                bw.write(p.getCSVLine());
                p.setUnchanged();
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
    private class ScrollablePanel extends JPanel implements Scrollable{

        public boolean getScrollableTracksViewportHeight() {return false;}
        public boolean getScrollableTracksViewportWidth() {return false;}//true, dann passt sich das an
        public Dimension getPreferredScrollableViewportSize() {return getPreferredSize();}
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return getScrollableUnitIncrement(visibleRect,orientation,direction);
        }
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 30;//TODO jeweils eine Zeile weiter
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
    private LoadPrimerAction loadPrimerAction;
    private SavePrimerAction savePrimerAction;
    private CalculateAction calcAction;
    private Action prefAction;

    private JMenuItem newDesignMenuItem;
    protected boolean expertmode;
    private JMenuItem prefMenuItem;
    private JButton consoleButton;
    private InfiniteProgressPanel infinitePP;
    private JComboBox assayDropdown;


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
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				exitApp();
			}
		});
	}
	private void exitApp() {
//		 Save the state of the window as preferences
        int answer = askUserForSaveIfNeeded();
        if (answer == JOptionPane.CANCEL_OPTION)
            return;
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
        this.setGlassPane(getInfiniteProgressPanel());
        setTitle("MiniSBE (freeze 1) $$$DATE$$$");//wird von ant durch aktuelles Datum ersetzt.
	}
	private InfiniteProgressPanel getInfiniteProgressPanel() {
	    if(infinitePP==null) {
            infinitePP=new InfiniteProgressPanel();
        }
        return infinitePP;
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
			fileMenu.addSeparator();
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
			helpMenu.setText("Help");
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
    private LoadPrimerAction getLoadPrimerAction() {
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
    private SavePrimerAction getSavePrimerAction() {
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
            jToolBar.addSeparator();
			jToolBar.add(getLoadPrimerAction());
			jToolBar.add(getSavePrimerAction());
			jToolBar.addSeparator();
			jToolBar.add(getPrefAction());
			jToolBar.add(getExpertToggleButton());
			jToolBar.add(getConsoleToggleButton());
            JPanel p=new JPanel(new BorderLayout());
            p.add(getAssayTypeDropdownButton(),BorderLayout.WEST);
			jToolBar.add(p);
		}
		return jToolBar;
	}
	private JComboBox getAssayTypeDropdownButton() {
        if (assayDropdown == null) {
            assayDropdown = new JComboBox(new Object[]{"Cleavable linker", "Pinpoint", "Probe"});
            assayDropdown.setToolTipText("Chosen assay type");
            assayDropdown.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent e) {
                    // TODO Auto-generated method stub
                    
                }
            });
        }
        return assayDropdown;
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
	private Action getPrefAction() {
		if (prefAction == null) {
            prefAction = new PreferencesAction();
		}
		return prefAction;
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
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getSbepanelsPanel() {
		if (sbepanelsPanel == null) {
			sbepanelsPanel = new ScrollablePanel();
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
        SBECandidatePanel p = new SBECandidatePanel("ID"+(index+1), getConfigDialog().getSBEOptionsFromGui().getMinCandidateLen(),(index+1));
        p.refreshData(getConfigDialog().getSBEOptionsFromGui());
        p.setUnchanged();
        p.setExpertMode(expertmode);
        if(index%2 == 1)
        	p.setBackground(new Color(230,230,255));
        sbepanels.add(p);
        getSbepanelsPanel().add(p);
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
			calcButton.setAction(getCalculationAction());

		}
		return calcButton;
	}
	private CalculateAction getCalculationAction() {
	    if(calcAction == null)
            calcAction=new CalculateAction();
        return calcAction;
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
            expertToggleButton.setIcon(new ImageIcon(CalculateAction.class.getClassLoader().getResource("images/doktorhut32.gif")));
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
 		try {
			UIManager.setLookAndFeel(new com.jgoodies.looks.plastic.Plastic3DLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
		}
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
            super("show logfile","shows the logfile contents", 
                    ConsoleViewAction.class.getClassLoader().getResource("images/gray.gif"),
                    KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
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
 
    /**
     * Saves, if the user clicks yes, returns the value received from  JOptionPane.
     * @return
     */
    protected int askUserForSaveIfNeeded() {
        boolean dirty=false;
        for (Iterator iter = this.sbepanels.iterator(); iter.hasNext();) {
            SBECandidatePanel panel = (SBECandidatePanel) iter.next();
            dirty = dirty || panel.hasChanged();
        }
        int answer=JOptionPane.NO_OPTION;
        if(dirty) {
            answer=JOptionPane.showConfirmDialog(null,"Would you like to save?","New assay design",JOptionPane.YES_NO_CANCEL_OPTION);
            if(answer == JOptionPane.YES_OPTION)
                getSavePrimerAction().actionPerformed(new ActionEvent(this,0,"save"));
        }
        return answer;
    }
  }  //  @jve:decl-index=0:visual-constraint="10,102"

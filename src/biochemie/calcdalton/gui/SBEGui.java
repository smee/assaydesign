package biochemie.calcdalton.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import biochemie.calcdalton.BerechnungsProgress;
import biochemie.calcdalton.tf_seqDocListener;
import biochemie.util.FileSelector;
import biochemie.util.Helper;
import biochemie.util.SwingWorker;

public class SBEGui extends JFrame{
    JPanel panel;
	private static final class CalculateAction extends AbstractAction {
        Icon icon;
        public CalculateAction(){
            putValue(NAME,"Calculate");
            java.net.URL url=this.getClass().getClassLoader().getResource("images/play.gif");
            if(null != url){
                icon=new ImageIcon(url);
                putValue(Action.SMALL_ICON,icon);
            }
        }
        public void actionPerformed(ActionEvent e){
        	final SwingWorker worker =new SwingWorker() {
        		BerechnungsProgress berechnungsframe;
                public Object construct() {
                    berechnungsframe=new BerechnungsProgress();
        			return berechnungsframe;
        		}
                public void finished() {
                    berechnungsframe.dispose();
                }
        	};
        	worker.start();
        }
    }
    private final class OptionsAction extends AbstractAction {
        Icon icon;
        public OptionsAction(){
            if(!Helper.isJava14()){
                putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
            }else{
                putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
            }
            putValue(NAME,"Preferences");
            putValue(SHORT_DESCRIPTION,"Preferences");
            java.net.URL url=this.getClass().getClassLoader().getResource("images/properti.gif");
            if(null != url){
                icon=new ImageIcon(url);
                putValue(Action.SMALL_ICON,icon);
            }
        }
        public void actionPerformed(ActionEvent e) {
            CDConfig.getInstance().showModalDialog(sbegui);
        }
    }
    private static final class AboutAction extends AbstractAction {
        public AboutAction(){
            putValue(NAME,"About");

        }
        public void actionPerformed(ActionEvent e) {
        	JFrame jf_about=
        		new JFrame("Photolinker-positions in single base extension primers for multiplexing");
        	jf_about.setDefaultCloseOperation(2);
        	jf_about.getContentPane().add(
        		new JScrollPane(
        			new JLabel("<html>Documentation can be found in the subdirectory help/.<br><br>DISCLAIMER<br>" +
        				"THIS SOFTWARE IS PROVIDED BY THE AUTOURS ``AS IS`` AND ANY EXPRESS OR<br>" +
        				"IMPLIED WARANTIES, INCLUDING, BUT NOT LIMITED TO, THE  IMPLIED <br>" +
        				"WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE  ARE <br>" +
        				"DISCLAIMED. IN NO EVENT SHALL THE AUTHORS ARE LIABLE  FOR ANY DIRECT, <br>" +
        				"INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES <br>" +
        				"(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS  OR <br>" +
        				"SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)<br>" +
        				"HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, <br>" +
        				"STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN <br>" +
        				"ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE <br>" +
        				"POSSIBILITY OF SUCH DAMAGE. <br><br>" +
        				"THE COPYRIGHT FOR ANY MATERIAL CREATED BY THE AUTHOR IS RESERVED. ANY <br>" +
        				"DUPLICATION OR USE OF OBJECTS IN OTHER ELECTRONIC OR PRINTED <br>" +
        				"PUBLICATIONS IS NOT PERMITTED WITHOUT THE AUTHOR'S AGREEMENT. <br><br>" +
        				"IF SECTIONS OR INDIVIDUAL TERMS OF THIS STATEMENT ARE NOT LEGAL OR <br>" +
        				"CORRECT, THE CONTENT OR VALIDITY OF THE OTHER PARTS REMAIN UNINFLUENCED <br>" +
        				"BY THIS FACT.<br><br> " +
        				"A tool created by Steffen Dienst and Holger Kirsten.<br>" +
        				"Contact: hkirsten@medizin.uni-leipzig.de</html>")));
        	jf_about.setSize(600, 300);
        	jf_about.setVisible(true);
        }
    }
    private class AddPanelAction extends AbstractAction{
        Icon icon;
        public AddPanelAction(){
            if(!Helper.isJava14()){
                putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
            }else{
                putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
            }
            putValue(NAME,"Add panel");
            putValue(SHORT_DESCRIPTION,"add new SBE panel");
            java.net.URL url=this.getClass().getClassLoader().getResource("images/add.gif");
            if(null != url){
                icon=new ImageIcon(url);
                putValue(Action.SMALL_ICON,icon);
            }
        }
        public void actionPerformed(ActionEvent e) {
            SBEPanel newPanel=new SBEPanel(sbePanelList.size()+1);
            newPanel.tfSequence.getDocument().addDocumentListener(dl_seq);
            newPanel.refreshData();
            sbePanelList.add(newPanel);
            panel.add(newPanel,panel.getComponentCount()-1);
            panel.revalidate();
            panel.repaint();
        }
    }
    private static final class HelpAction extends AbstractAction{
        Icon icon;
        public HelpAction(){
            putValue(NAME,"Help");
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));
            java.net.URL url=this.getClass().getClassLoader().getResource("images/help.gif");
            if(null != url){
                icon=new ImageIcon(url);
                putValue(Action.SMALL_ICON,icon);
            }
        }
        public void actionPerformed(ActionEvent e) {
            URL url=this.getClass().getClassLoader().getResource("help/calcdaltonhelp.htm");
            try {
                JEditorPane editorPane=new JEditorPane(url);
                editorPane.setEditable(false);
                JScrollPane editorScrollPane = new JScrollPane(editorPane);
                editorScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                editorPane.setPreferredSize(new Dimension(600, 300));
                JFrame f=new JFrame("Help");
                f.getContentPane().add(editorScrollPane);
                f.pack();
                f.setVisible(true);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null,"Sorry, couldn't load the help-file.","Error on loading help.",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private class LoadPrimerAction extends AbstractAction{
        Icon icon;
        public LoadPrimerAction(){
            if(!Helper.isJava14()){
                putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
            }else{
                putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
            }
            putValue(NAME,"Load Primer");
            putValue(SHORT_DESCRIPTION,"load primer into gui");
            java.net.URL url=this.getClass().getClassLoader().getResource("images/open.gif");
            if(null != url){
                icon=new ImageIcon(url);
                putValue(Action.SMALL_ICON,icon);
            }
        }
        public void actionPerformed(ActionEvent e) {
            File file = FileSelector.getUserSelectedFile(null,"Load primer...",new FileFilter(){
                public boolean accept(File f) {
                    if(f.isDirectory())
                        return true;
                    if(f.isFile() && (f.getName().toLowerCase().endsWith(".primer")
                            || f.getName().toLowerCase().endsWith(".csv")))
                        return true;
                    return false;
                }
                public String getDescription() {
                    return "textfiles containing primer";
                }
            },JFileChooser.OPEN_DIALOG);
            if(file != null){
                try {
                    /*
                     * FIXME schlampig, erkennt keine unvollstaendigen Ausgabezeilen (also Zeilen ohne "bio" darin)
                     * FIXME erstellt eine Zeile in der GUI zuviel (wegen dem HEader in den CSV-Files)
                     */
                    BufferedReader br=new BufferedReader(new FileReader(file));
                    List lines=new ArrayList();
                    String line="";
                    while(null != (line = br.readLine()))
                        lines.add(line);
                    if(sbePanelList.size()<lines.size()) {
                        int len=lines.size()-sbePanelList.size();
                        for (int i = 0; i < len ; i++) {
                            SBEPanel newPanel=new SBEPanel(sbePanelList.size()+1);
                            newPanel.tfSequence.getDocument().addDocumentListener(dl_seq);
                            newPanel.refreshData();
                            sbePanelList.add(newPanel);
                            panel.add(newPanel);
                        }
                    }
                    for(int i=0;i<lines.size();i++) {
                        String p=(String) lines.get(i);
                        SBEPanel panel=((SBEPanel)sbePanelList.get(i));
                        if(Helper.isSBEPrimer(p)) {//nur ein primer in der Zeile
                            panel.tfSequence.setText(p);
                            panel.tfName.setText("");
                            panel.cmbFest.setSelectedIndex(0);
                        }else {//minisbe-ein/ausgabefile
                            if(i == 0)
                                continue;//header
                            if(p.indexOf("bio")!=-1)
                                loadFromOutputline(Helper.clearEmptyCSVEntries(p),panel);
                            else
                                loadFromInputline(Helper.clearEmptyCSVEntries(p),panel);
                        }
                    }
                    br.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            panel.revalidate();
            panel.repaint();
        }
        private void loadFromInputline(String line, SBEPanel panel) {
            System.out.println("Loading from INputfile: "+line);
            StringTokenizer stok = new StringTokenizer(line,";\"");
            String id = stok.nextToken();
            String l = stok.nextToken();
            stok.nextToken();//bautein5
            String snp = stok.nextToken().toUpperCase();
            loadSNPInto(panel, snp);
            stok.nextToken();//right primer
            stok.nextToken();//bautein5
            stok.nextToken();//PCR-Produktlaenge
            int pl;
            try {
                pl = Integer.parseInt(stok.nextToken());
            } catch (NumberFormatException e) {
                pl = -1;
            }
            panel.setSelectedPL(pl);
            panel.tfName.setText(id);
            panel.tfSequence.setText(l);
        }
        /**
         * @param panel
         * @param snp
         */
        private void loadSNPInto(SBEPanel panel, String snp) {
            for(int j=0;j<snp.length();j++) {
                switch (snp.charAt(j)) {
                case 'A':
                    panel.cb_A.setSelected(true);
                    break;
                case 'C':
                    panel.cb_C.setSelected(true);
                    break;
                case 'G':
                    panel.cb_G.setSelected(true);
                    break;
                case 'T':
                    panel.cb_T.setSelected(true);
                    break;

                default:
                    break;
                }
            }
        }
        private void loadFromOutputline(String p, SBEPanel panel) {
            System.out.println("Loading from OUTputfile: "+p);
            /*
            "Multiplex ID"
            ,"SBE-ID"
            ,"Sequence incl. PL"
            ,"SNP allele"
            ,"Photolinker (=PL): position"
            ,"Primerlength"
            ,"GC contents incl PL"
            ,"Tm incl PL"
            ,"Excluded 5\' Primers"
            ,"Excluded 3\' Primers"
            ,"Sec.struc.: position (3\')"
            ,"Sec.struc.: incorporated nucleotide"
            ,"Sec.struc.: class"
            ,"Sec.struc.: irrelevant due to PL"
            ,"Primer from 3' or 5'"
            ,"Fragment: T-Content"
            ,"Fragment: G-content"
            ,"PCR-Product-length"
            ,"Sequence excl.PL"
            ,"Comment"};
         */
            StringTokenizer stok = new StringTokenizer(p,";\"");
            stok.nextToken();//mid
            String id=stok.nextToken();
            stok.nextToken();//bio seq.
            loadSNPInto(panel,stok.nextToken());//snp
            
            int pl = -1;
            try {
                pl =Integer.parseInt(stok.nextToken());//pl
            }catch(NumberFormatException e) {};
            stok.nextToken();//primerlength
            stok.nextToken();//gc
            stok.nextToken();//tm
            stok.nextToken();//excl. 5
            stok.nextToken();//excl. 3
            stok.nextToken();//sec1
            stok.nextToken();//sec2
            stok.nextToken();//sec3
            stok.nextToken();//sec4
            stok.nextToken();//3 or 5
            stok.nextToken();//t
            stok.nextToken();//g
            stok.nextToken();//pcrprod. length
            String seq=stok.nextToken();//
            panel.tfName.setText(id);
            panel.tfSequence.setText(seq);
            panel.setSelectedPL(pl);
        }
    }
    private class SavePrimerAction extends AbstractAction{
        Icon icon;
        public SavePrimerAction(){
            if(!Helper.isJava14()){
                putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
            }else{
                putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
            }
            putValue(NAME,"Save Primer");
            putValue(SHORT_DESCRIPTION,"save primer into file");
            java.net.URL url=this.getClass().getClassLoader().getResource("images/save.gif");
            if(null != url){
                icon=new ImageIcon(url);
                putValue(Action.SMALL_ICON,icon);
            }
        }
        public void actionPerformed(ActionEvent e) {
            File file = FileSelector.getUserSelectedFile(null,"Save primers as...",new FileFilter(){
                public boolean accept(File f) {
                    if(f.isDirectory())
                        return true;
                    if(f.isFile() && (f.getName().toLowerCase().endsWith(".primer")
                            || f.getName().toLowerCase().endsWith(".csv")))
                        return true;
                    return false;
                }
                public String getDescription() {
                    return "textfiles containing primer";
                }
            },JFileChooser.OPEN_DIALOG);
            if(file != null) {
                    if(!file.getAbsolutePath().toLowerCase().endsWith(".primer") && !file.getAbsolutePath().toLowerCase().endsWith(".csv"))
                        file=new File(file.getAbsolutePath()+".csv");
                    
                    if(file.getAbsolutePath().toLowerCase().endsWith(".primer"))
                        writePrimerFile(file);
                    else
                        writeMiniSBECompatibleFile(file);


            }
            panel.revalidate();
            panel.repaint();
        }
        /**
         * @param file
         */
        private void writeMiniSBECompatibleFile(File file) {
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
            try {
                BufferedWriter bw=new BufferedWriter(new FileWriter(file));
                bw.write(header);
                bw.write('\n');
                for(int i=0;i<sbePanelList.size();i++) {
                    SBEPanel panel=(SBEPanel)sbePanelList.get(i);
                    bw.write(panel.tfName.getText());
                    bw.write(';');
                    bw.write(panel.tfSequence.getText());
                    bw.write(';');
                    //hairpin 5
                    bw.write(';');
                    //SNP
                    String[] temp=panel.getPrimer();
                    for (int j = 1; j < temp.length; j++) {
                        bw.write(temp[j]);
                    }
                    bw.write(';');
                    //3'seq.
                    bw.write(';');
                    //hairpin 3'
                    bw.write(';');
                    //pcr product
                    bw.write(';');
                    int idx=panel.getFestenAnhangIndex();
                    if(idx >-1) {
                        bw.write(Integer.toString(CDConfig.getInstance().getBruchStellenArray()[idx]));
                    }
                    bw.write(';');
                    //multiplexid
                    bw.write(';');
                    //ausschluss
                    bw.write(';');
                    bw.write("true");
                    bw.write(';');
                    bw.write(';');
                    bw.write('\n');
                }
                bw.close(); 
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        /**
         * 
         */
            private void writePrimerFile(File file) {
                try {
                    BufferedWriter br=new BufferedWriter(new FileWriter(file));
                    for(int i=0;i<sbePanelList.size();i++) {
                        SBEPanel panel=(SBEPanel)sbePanelList.get(i);
                        br.write(panel.tfSequence.getText()+'\n');
                    }
                    br.close(); 
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
    }

    private static SBEGui singleton=null;
	JMenuBar menu;
	JMenu help;
	JMenu zeugs;
	JMenuItem prefs;
	JMenuItem about;
	JFrame frame;
	SBEGui sbegui;

	java.util.List sbePanelList;
	public JButton start;
	tf_seqDocListener dl_seq;
	BerechnungsProgress bp;

	int sbe_anzahl=0;

    /**
     * @return
     */
    public int getSbe_anzahl() {
        return sbePanelList.size();
    }

	/**
	 * Implementierung des singletonpatterns
	 */
	public static SBEGui getInstance() {
		try {
			return new SBEGui();
		}catch (SingletonException e) {}
		return SBEGui.singleton;
	}

    private SBEGui()throws SingletonException{
		super("Photolinker-positions in single base extension primers for multiplexing");
       	if(null != singleton)
       		throw new SingletonException();
       	singleton=this;
        ToolTipManager.sharedInstance().setInitialDelay(100);
       	/////////////////////////////////////////
      try
      {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch ( Exception e ) {}

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitApp();
            }
        });
        URL url=this.getClass().getClassLoader().getResource("images/reagenz.gif");
        if(null != url){
            setIconImage(new ImageIcon(url).getImage());
        }
		Action loadaction=new LoadPrimerAction();
		Action saveaction=new SavePrimerAction();
		Action addpanelaction=new AddPanelAction();
		Action helpaction=new HelpAction();
		Action calcaction=new CalculateAction();
		Action optionsaction=new OptionsAction();

		getContentPane().setLayout(new BorderLayout());

		JToolBar toolbar = new JToolBar();
		toolbar.add(addpanelaction);
		toolbar.add(loadaction);
		toolbar.add(saveaction);
		toolbar.add(optionsaction);


		getContentPane().add(toolbar, BorderLayout.NORTH);
		menu= new JMenuBar();
		help= new JMenu("?");
		zeugs=new JMenu("Settings");
        zeugs.setMnemonic(KeyEvent.VK_S);
		menu.add(zeugs);
		menu.add(help);
		setJMenuBar(menu);
		prefs =new JMenuItem(optionsaction);
        prefs.setHorizontalAlignment(SwingConstants.LEFT);
		about= new JMenuItem(new AboutAction());

		JMenuItem loadPrimer=new JMenuItem(loadaction);
		zeugs.add(loadPrimer);
		JMenuItem savePrimer=new JMenuItem(saveaction);
		zeugs.add(savePrimer);

        JMenuItem addPanel=new JMenuItem(addpanelaction);
		zeugs.add(prefs);
        zeugs.add(addPanel);
        JMenuItem helpItem=new JMenuItem(helpaction);
        helpItem.setHorizontalAlignment(SwingConstants.LEFT);
        help.add(helpItem);
		help.add(about);

        ////////////////////////////////////////////
		//Lege SBEPanels an, je nach Usereingabe
		panel= new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		while(1 > sbe_anzahl) {
			try{
                String input=JOptionPane.showInputDialog(null,"Please enter maximum multiplex level (1-?):","1");
                if(null == input)
                    System.exit(0);
				sbe_anzahl = Integer.parseInt(input);

			}
		catch(NumberFormatException nfe){}
		}
        sbePanelList=new ArrayList();

        dl_seq = new tf_seqDocListener(sbePanelList);
       	for(int i=0;i<sbe_anzahl;i++) {
            SBEPanel sbep=new SBEPanel(i+1);
       		sbePanelList.add(sbep);
       		sbep.tfSequence.getDocument().addDocumentListener(dl_seq);
			panel.add(sbep);
       	}


	JScrollPane scrollPane= new JScrollPane(panel);
	getContentPane().add(scrollPane, BorderLayout.CENTER);
		//Startbutton
	JPanel startbuttonpanel=new JPanel();
    start = new JButton(calcaction);
    start.setEnabled(false);
    startbuttonpanel.add(start);
    getContentPane().add(startbuttonpanel, BorderLayout.SOUTH);
    pack();
	show();
    }
	/**
     * 
     */
    public void exitApp() {
        System.exit(0);
    }

    /**
	 * liest die comboboxen für feste bruchstellen neu ein.
	 */
	public void refreshData() {
        int num=sbePanelList.size();
		for(int i=0;i<num;i++)
			((SBEPanel)sbePanelList.get(i)).refreshData();
        dl_seq.check();
	}

    /**
     * @param t
     * @return
     */
    public SBEPanel getPanel(int t) {
        return (SBEPanel)sbePanelList.get(t);
    }

}
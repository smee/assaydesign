package biochemie.calcdalton.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
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
import biochemie.calcdalton.SwingWorker;
import biochemie.calcdalton.tf_seqDocListener;
import biochemie.util.Helper;

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
            File file=new File(".");
            JFileChooser jfc=new JFileChooser(file);
            jfc.setDialogTitle("Load primer...");
            jfc.setFileFilter(new FileFilter(){
                public boolean accept(File f) {
                    if(f.isDirectory())
                        return true;
                    if(f.isFile() && (f.getName().endsWith(".primer") || f.getName().endsWith(".PRIMER")))
                        return true;
                    return false;
                }
                public String getDescription() {
                    return "textfiles containing primer";
                }
            });
            int result=jfc.showOpenDialog(null);
            if(JFileChooser.APPROVE_OPTION == result){
                try {
                    file=jfc.getSelectedFile();
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
                            panel.add(newPanel,panel.getComponentCount()-1);
                        }
                    }
                    for(int i=0;i<sbePanelList.size();i++) {
                        String p=(String) lines.get(i);
                        ((SBEPanel)sbePanelList.get(i)).tfSequence.setText(p);
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
            File file=new File(".");
            JFileChooser jfc=new JFileChooser(file);
            jfc.setDialogTitle("Save primer as ...");
            jfc.setFileFilter(new FileFilter(){
                public boolean accept(File f) {
                    if(f.isDirectory())
                        return true;
                    if(f.isFile() && (f.getName().endsWith(".primer") || f.getName().endsWith(".PRIMER")))
                        return true;
                    return false;
                }
                public String getDescription() {
                    return "(*.primer)";
                }
            });
            int result=jfc.showSaveDialog(null);
            if(JFileChooser.APPROVE_OPTION == result){
                try {
                    file=jfc.getSelectedFile();
                    if(!file.getAbsolutePath().endsWith(".primer") && !file.getAbsolutePath().endsWith(".PRIMER"))
                        file=new File(file.getAbsolutePath()+".primer");
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
            panel.revalidate();
            panel.repaint();
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

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                String input=JOptionPane.showInputDialog(null,"Please enter maximum multiplex level (1-?):","",JOptionPane.INFORMATION_MESSAGE);
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
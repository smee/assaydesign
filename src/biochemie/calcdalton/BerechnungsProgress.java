package biochemie.calcdalton;

import games.Pool;
import games.Slime2P;
import games.TubeBlazerLight;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigInteger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.TableColumn;

import biochemie.calcdalton.gui.CDConfig;
import biochemie.calcdalton.gui.SBEGui;
import biochemie.calcdalton.gui.SBEPanel;

public class BerechnungsProgress extends JFrame{
	JFrame frame;
	JProgressBar progress;
	Timer timer;
	SBEGui sbe=SBEGui.getInstance();
	CalcDalton cd;
    Thread calcthread;
    /**
     * Tabelle mit berechneten Werten
     */
    SBETable sbetable;
    int scale=-1;
    static BigInteger max;//nicht schoen, aber ging nicht anders!

	public BerechnungsProgress() {
		super("Berechnung läuft...");
        calcthread=Thread.currentThread();
		//this.sbe=SBEGui.getInstance();
        getContentPane().setLayout(new BorderLayout());
		progress=new JProgressBar(0);
		progress.setSize(200,50);
        progress.setMaximum(101);
		progress.setStringPainted(true);
		getContentPane().add(progress,BorderLayout.NORTH);
		progress.setValue(0);
		progress.setStringPainted(true);
        JButton cancel=new JButton("Cancel Calculation");
        cancel.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                calcthread.stop();
                dispose();
            }
        });
        java.net.URL url=this.getClass().getClassLoader().getResource("images/stop.gif");
        if(null != url){
            Icon icon=new ImageIcon(url);
            cancel.setIcon(icon);
        }
        getContentPane().add(cancel,BorderLayout.SOUTH);
		//Create a timer.
	   timer = new Timer(1000, new ActionListener() {
		   public void actionPerformed(ActionEvent evt) {
              if(-1 == scale) {
                progress.setMaximum(cd.getMax());
                //System.out.println("max: "+cd.getMax());
                scale=0;
              }
              progress.setValue(cd.getAktuellerWert());
              //System.out.println(cd.getAktuellerWert());
			  repaint();
	        }
       });
       start();
    }

	public void start() {
		JTableEx tabelle;
		JScrollPane scrollPane;
		final JFrame frame;
		JButton jb_next;
		JButton jb_prev;
		JButton showDiffs;
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		/**
		 * alle eingegebenen Daten
		 */
		String[][] paneldata_org;
        String[] sbeNames_org;
		int[] fest_org;
                             
		//jede Zeile enthält die Sequence und die gewählten Anhängsel
		final int anzahl_sbe=sbe.getSbe_anzahl();
        paneldata_org=new String[anzahl_sbe][];
		fest_org=new int[anzahl_sbe];
        sbeNames_org=new String[anzahl_sbe];       
		CDConfig config= CDConfig.getInstance();                
				
		//hole Eingabedaten
		for(int t=0;t<anzahl_sbe;t++) {
            SBEPanel sbep=sbe.getPanel(t);
			paneldata_org[t]=sbep.getPrimer();
			fest_org[t]=sbep.getFestenAnhangIndex();
            sbeNames_org[t]=sbep.getName() ; 		
		}
	    int j=0,k=0	;
		//filtere leere Sequenzen raus
		for(int i=0;i<paneldata_org.length;i++) {
			if(0 != paneldata_org[i][0].length())
				j++;
		}				
		String[][] paneldata=new String[j][];
        final String[] SBENames=new String[j];
        int[] fest=new int[j];
		for(int i=0;i<paneldata_org.length;i++) 
			if(0 != paneldata_org[i][0].length()){
				paneldata[k]=paneldata_org[i];
                SBENames[k]=sbeNames_org[i];
                fest[k]=fest_org[i];
				k++;
			}
		//neues TableModel
        int[] br=config.getBruchStellenArray();
		sbetable=new SBETable(SBENames,br);
//		/////////////////////////////////
			  //alles für den Progressbar
		max=BigInteger.ONE;
        BigInteger brlen=new BigInteger(String.valueOf(br.length));
		for(int x=0;x<paneldata_org.length;x++) {
			max=max.multiply(brlen);
		}
		if(CalcDalton.progress)
			System.out.println("Zu berechnende Varianten : "+max);
        cd=new CalcDalton(br
        				 ,config.getFrom(),config.getTo()
        				 ,config.getPeaks()
        				 ,config.getVerbMassenFrom(), config.getVerbMassenTo()
        				 ,config.allowOverlap());
        pack();
        setVisible(true);
        long starttime=0;
        if(CalcDalton.debug){
            starttime=System.currentTimeMillis();
        }
        timer.start();
        cd.calc(paneldata,sbetable,fest);
		timer.stop();
        if(CalcDalton.debug){
            long endtime=System.currentTimeMillis();
		    System.out.println("Dauer: "+(endtime-starttime)+"ms");
        }
		if(0 == sbetable.getNumberOfSolutions()){
			JOptionPane.showMessageDialog(null, "Sorry, just the first "+(cd.getMaxReachedDepth()+1)+" ones worked together.");
			return;
		}
		frame = new JFrame("Result "+(sbetable.getIndex()+1)+" of "+sbetable.getNumberOfSolutions());
        double p=TableLayoutConstants.PREFERRED;
        double f=TableLayoutConstants.FILL;
        double b=5;
        double[][] sizes={{b,f,b},{b,f,3*b,p,b,25,b}};
        frame.getContentPane().setLayout(new TableLayout(sizes));
        tabelle = new JTableEx(sbetable);
	    tabelle.addKeyListener(new KeyAdapter(){//Eastereggs :-)
	        public void keyPressed(KeyEvent e) {
	            if(KeyEvent.VK_Q == e.getKeyCode()){
	                final SwingWorker worker =new SwingWorker() {
	                    public Object construct() {
	                        return new TubeBlazerLight();
	                    }
	                };
	                worker.start();
	            }if(KeyEvent.VK_P == e.getKeyCode()){
	                final SwingWorker worker =new SwingWorker() {
	                    public Object construct() {
	                        Pool.main(new String[0]);
	                        return null;
	                    }
	                };
	                worker.start();
	            }if(KeyEvent.VK_2 == e.getKeyCode()){
	                final SwingWorker worker =new SwingWorker() {
	                    public Object construct() {
	                        Slime2P.main(new String[0]);
	                        return null;
	                    }
	                };
	                worker.start();
	            } 
	        }
	    });
		scrollPane = new JScrollPane(tabelle);
        tabelle.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumn column = null;
        for (int i = 0; i < tabelle.getColumnModel().getColumnCount(); i++) {
            column = tabelle.getColumnModel().getColumn(i);
            int width=sbetable.getMaxLengthColumn(i);
            if(130 >= width)
                column.setPreferredWidth(width);
            else{
                column.setPreferredWidth(100);
                
            }
        }
        frame.getContentPane().add(scrollPane,"1,1");
        JLabel label=new JLabel("<html>All molecular weights refer to the " +
                                "3'residue after cleavage.<br>They include " +
                                "the residual photolinker weight.</html>");
        frame.getContentPane().add(label,"1,3");
        JPanel buttonpanel=new JPanel();
        buttonpanel.setLayout(new BoxLayout(buttonpanel,BoxLayout.X_AXIS));
        jb_next=new JButton("Next result");
        jb_next.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                sbetable.nextLoesung();
                frame.setTitle("Result "+(sbetable.getIndex()+1)+" of "+sbetable.getNumberOfSolutions());
                frame.repaint();
            }
        });
        jb_prev=new JButton("Previous result");
        jb_prev.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                sbetable.previousLoesung();
                frame.setTitle("Result "+(sbetable.getIndex()+1)+" of "+sbetable.getNumberOfSolutions());
                frame.repaint();
            }
        });
        showDiffs=new JButton("Show mass differences");
        showDiffs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFrame f=new JFrame("Diffs for result "+(sbetable.getIndex()+1));
				DiffTableModel dtm=new DiffTableModel(SBENames,sbetable);
				JTable t=new JTable(dtm);
				JScrollPane sp=new JScrollPane(t);
				f.getContentPane().add(sp);
				f.pack();
				f.setVisible(true);
			}
        });
        buttonpanel.add(jb_prev);
        buttonpanel.add(jb_next);
        buttonpanel.add(Box.createGlue());
        buttonpanel.add(showDiffs);
		frame.getContentPane().add(buttonpanel,"1,5");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
        frame.setVisible(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		sbe.start.setEnabled(true);
	}

}


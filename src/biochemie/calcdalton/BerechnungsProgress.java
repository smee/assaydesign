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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

import org._3pq.jgrapht.UndirectedGraph;
import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.UnaryFunction;

import biochemie.calcdalton.gui.CDConfig;
import biochemie.calcdalton.gui.SBEGui;
import biochemie.calcdalton.gui.SBEPanel;
import biochemie.gui.TaskRunnerDialog;
import biochemie.sbe.calculators.MaximumCliqueFinder;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.GraphHelper;
import biochemie.util.GraphWriter;
import biochemie.util.Helper;
import biochemie.util.SwingWorker;

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
        calcthread.setName("CalcDaltonThread");
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
                calcthread.interrupt();
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
	private void showCDResultTable(final SBETable sbetable) {
        JTableEx tabelle;
        JScrollPane scrollPane;
        final JFrame frame;
        JButton jb_next;
        JButton jb_prev;
        JButton showDiffs;
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
            int width=sbetable.getMaxLengthOfColumn(i);
            if(width <= 130)
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
                DiffTableModel dtm=new DiffTableModel(sbetable);
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
	public void start() {

		
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
        //calcdalton veraendert das array, also muss ichs erst kopieren
        int[] tempfest = new int[fest.length];
        System.arraycopy(fest,0,tempfest,0,fest.length);
        cd.calc(paneldata,sbetable,tempfest);
        if(calcthread.isInterrupted())
            return;
		timer.stop();
        if(CalcDalton.debug){
            long endtime=System.currentTimeMillis();
		    System.out.println("Dauer: "+(endtime-starttime)+"ms");
        }
		if(0 == sbetable.getNumberOfSolutions()){
			int result=JOptionPane.showConfirmDialog(null, "Sorry, just the first "+(cd.getMaxReachedDepth()+1)+" ones worked together." +
                    "Would you like to find the maximal subset of fitting primers?", "Enhanced calculation",JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.NO_OPTION) {
                return;
            }else
                findMaxClique(cd,SBENames,paneldata, fest,br);
            
		}else {
		    showCDResultTable(sbetable);      
        }

	}
    /**
     * @param paneldata
     * @param fest
     */
    private void findMaxClique(final CalcDalton cd,String[] names,final String[][] paneldata, int[] fest, final int[] br) {
        System.out.println("Using fest="+Helper.toString(fest));
        List primer = new ArrayList(paneldata.length * br.length);
        
        for (int i = 0; i < paneldata.length; i++) {
            if(fest[i]!= -1)
                for (int j = 0; j < br.length; j++) {
                    primer.add(new SimplePrimer(cd,names[i],paneldata[i],br[j]));
                }
            else
                primer.add(new SimplePrimer(cd,names[i],paneldata[i],fest[i]));
        }
        final UndirectedGraph graph=GraphHelper.getKomplementaerGraph(GraphHelper.createIncompGraph(primer,true,GraphWriter.TGF));
        final TaskRunnerDialog dialog = new TaskRunnerDialog("Searching for max. clique",null,new SwingWorker() {
            public Object construct() {
                MaximumCliqueFinder mcf = new MaximumCliqueFinder(graph,paneldata.length,true);
                Set max= mcf.maxClique();
                
                String[] cliquenames=(String[]) Algorithms.collect(Algorithms.apply(max.iterator(), new UnaryFunction() {
                    public Object evaluate(Object obj) {
                        return ((SimplePrimer)obj).name;
                    }
                }), new ArrayList(max.size())).toArray(new String[max.size()]);
                
                SBETable sbet= new SBETable(cliquenames, br);
                String[][] sbedata = new String[max.size()][];
                int[] fest = new int[max.size()];
                int i=0;
                for (Iterator iter = max.iterator(); iter.hasNext();i++) {
                    SimplePrimer primer = (SimplePrimer) iter.next();
                    sbedata[i]=primer.datarow;
                    fest[i]=primer.fest;
                }
                
                cd.calc(sbedata,sbet,fest);
                return sbet;
            }
            public void finished() {
                SBETable table=(SBETable)getValue();
                showCDResultTable(table);
            }
        });
        dialog.show();

    }
    private class SimplePrimer implements Multiplexable{
        private CalcDalton cd;
        private String[] datarow;
        int fest;
        String plexid;
        private String name;
        
        public SimplePrimer(CalcDalton cd, String name,String[] row, int f) {
            this.cd=cd;
            this.name=name;
            this.datarow=row;
            this.fest=f;
        }

        public String toString() {
            return name+":"+datarow+"; "+fest;
        }
        public void setPlexID(String s) {
            if(plexid !=null)
                throw new IllegalStateException("Duplicate plexid!");
            this.plexid=s;
        }

        public String getName() {
            return datarow[0];
        }

        public boolean passtMit(Multiplexable other) {
            String[][] sbedata= {datarow
                                ,((SimplePrimer)other).datarow};
            int[] fest=new int[] {this.fest, ((SimplePrimer)other).fest};
            if(cd.calc(sbedata, fest).length == 0) {//keine Loesung
                return false;
            }
            return true;
        
        }

        public int maxPlexSize() {
            return Integer.MAX_VALUE;
        }

        public String getEdgeReason() {
            return "cd";
        }

    }

}


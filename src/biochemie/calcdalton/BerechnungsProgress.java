/*

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package biochemie.calcdalton;

import games.Pool;
import games.Slime2P;
import games.TubeBlazerLight;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
import org.apache.commons.functor.UnaryPredicate;

import biochemie.calcdalton.gui.CDConfig;
import biochemie.calcdalton.gui.SBEGui;
import biochemie.calcdalton.gui.SBEPanel;
import biochemie.gui.TaskRunnerDialog;
import biochemie.sbe.calculators.MaximumCliqueFinder;
import biochemie.sbe.calculators.SBEColorerProxy;
import biochemie.sbe.gui.SpektrometerPreviewFrame;
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
		super("Calculation is running...");
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
	private JFrame showCDResultTable(final SBETable sbetable, String title) {
        JTableEx tabelle;
        JScrollPane scrollPane;
        final JFrame frame;
        JButton jb_next;
        JButton jb_prev;
        JButton showDiffs;
        if(title==null)
            title="Result "+(sbetable.getIndex()+1)+" of "+sbetable.getNumberOfSolutions()+"("+(sbetable.getColumnCount()-1)+" primers)";
        frame = new JFrame(title);
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
                                "3'residue after cleavage.<br>" +
                                "The weight of the cleavable linker after cleavage is included.</html>");
        frame.getContentPane().add(label,"1,3");
        JPanel buttonpanel=new JPanel();
        buttonpanel.setLayout(new BoxLayout(buttonpanel,BoxLayout.X_AXIS));
        jb_next=new JButton("Next result");
        jb_next.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                sbetable.nextLoesung();
                frame.setTitle("Result "+(sbetable.getIndex()+1)+" of "+sbetable.getNumberOfSolutions()+"("+(sbetable.getColumnCount()-1)+" primers)");
                frame.repaint();
            }
        });
        jb_prev=new JButton("Previous result");
        jb_prev.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                sbetable.previousLoesung();
                frame.setTitle("Result "+(sbetable.getIndex()+1)+" of "+sbetable.getNumberOfSolutions()+"("+(sbetable.getColumnCount()-1)+" primers)");
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
        JButton showPreview = new JButton("Show MALDI preview");
        showPreview.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CalcDaltonOptions cfg=CDConfig.getInstance().getConfiguration();
                JFrame preview= new SpektrometerPreviewFrame(sbetable,"MALDI-MS-preview",frame.getTitle(),cfg);
                preview.pack();
                preview.setVisible(true);
            }
        });
        buttonpanel.add(jb_prev);
        buttonpanel.add(jb_next);
        jb_prev.setEnabled(sbetable.getNumberOfSolutions()>1);
        jb_next.setEnabled(sbetable.getNumberOfSolutions()>1);
        buttonpanel.add(Box.createGlue());
        buttonpanel.add(showDiffs);
        buttonpanel.add(showPreview);
        frame.getContentPane().add(buttonpanel,"1,5");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        sbe.start.setEnabled(true);
        return frame;
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
        cd=new CalcDalton(config.getConfiguration());
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
            Object[] choices=new Object[]{"Maxclique - Maximize the number of primers in one multiplex",
                    "Coloring - Minimize the number of multiplexes"};
			String result=(String) JOptionPane.showInputDialog(null, "<html>Sorry, not all primers can be included in one single multiplex according to your settings.<br>" 
                    +"Please choose an algorithm how to proceed:</html>", "Enhanced calculation",JOptionPane.PLAIN_MESSAGE,
                    null,choices,choices[0]);
            if(result==null)
                return;
			if(result.equals(choices[0]))
                findMaxClique(cd,SBENames,paneldata, fest,br);
            else
                doColoring(cd, SBENames, paneldata,fest,br);
            
		}else {
		    showCDResultTable(sbetable,null);      
        }

	}
    private void doColoring(final CalcDalton cd, String[] names, String[][] paneldata, final int[] fest, final int[] br) {
        final List primer = createPrimerList(cd, names, paneldata, fest, br);
        final UndirectedGraph graph = GraphHelper.createIncompGraph(primer,true,GraphWriter.TGF);
        final TaskRunnerDialog dialog = new TaskRunnerDialog("Searching for coloring",null,new SwingWorker() {
            public Object construct() {
                int[] plexsizes=new int[graph.vertexSet().size()];
                Arrays.fill(plexsizes,1);
                //TODO mit threaddingens machen, zeitbeschraenkt
                SBEColorerProxy proxy=new SBEColorerProxy(graph,new HashSet(),primer.size(),CalcDalton.debug);
                proxy.start();
                return proxy.getResult();
            }
            public void finished() {
                List colors=(List) getValue();
                Collections.sort(colors,new Comparator() {//sortieren nach Groesse
                    public int compare(Object arg0, Object arg1) {
                        return ((Collection)arg1).size()-((Collection)arg0).size();
                    }
                });
                //TODO alles in ein fenster
                System.out.println("Got list: "+colors);
                final Set gotThem=new HashSet();
                int count=0;
                for (Iterator it = colors.iterator(); it.hasNext();) {
                    Set mult = (Set) it.next();
                    Algorithms.remove(mult.iterator(),new UnaryPredicate() {
                        public boolean test(Object obj) {
                            return gotThem.contains(((SimplePrimer)obj).getName());
                        }
                    });
                    if(mult.size()==0)
                        continue;
                    else
                        for (Iterator it2 = mult.iterator(); it2.hasNext();) {
                            SimplePrimer p = (SimplePrimer) it2.next();
                            gotThem.add(p.getName());
                        }
                    SBETable table=calculateSBETable(cd,br,mult);
                    count++;
                    JFrame f=showCDResultTable(table,"Result No. "+count+" ("+mult.size()+"/"+fest.length+" primers)");
                    Rectangle rect=f.getBounds();
                    rect.x=rect.x+count*50;
                    f.setBounds(rect);
                }
            }

        });
        dialog.show();
    }
    /**
     * @param paneldata
     * @param fest
     */
    private void findMaxClique(final CalcDalton cd,final String[] names,final String[][] paneldata, final int[] fest, final int[] br) {
        System.out.println("Using fest="+Helper.toString(fest));
        final Set primersToGo=new HashSet(createPrimerList(cd, names, paneldata, fest, br));
        final TaskRunnerDialog dialog = new TaskRunnerDialog("Searching for max. clique",null,new SwingWorker() {
            public Object construct() {
                Set result=new HashSet();
                while(primersToGo.size()>0) {
                    final UndirectedGraph graph = GraphHelper.getKomplementaerGraph(GraphHelper.createIncompGraph(new ArrayList(primersToGo),true,GraphWriter.TGF));
                    MaximumCliqueFinder mcf = new MaximumCliqueFinder(graph,paneldata.length,true);
                    Set max= mcf.maxClique();
                    System.out.println("Found clique of size "+max.size()+": "+max);
                    for (Iterator it = max.iterator(); it.hasNext();) {
                        final SimplePrimer p = (SimplePrimer) it.next();
                        Algorithms.remove(primersToGo.iterator(),new UnaryPredicate() {
                            public boolean test(Object obj) {
                                return p.getName().equals(((SimplePrimer)obj).getName());
                            }
                        });
                    }
                    result.add(calculateSBETable(cd, br, max));
                }
                return result;
            }
            public void finished() {
                Set tables=(Set)getValue();
//                if(table.getNumberOfSolutions()==0){
//                    JOptionPane.showMessageDialog(null,"Sorry, all primers have forbidden masses.","No solution possible",JOptionPane.INFORMATION_MESSAGE);
//                    return;
//                }
                int count=0;
                for (Iterator it = tables.iterator(); it.hasNext();) {
                    SBETable table = (SBETable) it.next();
                    JFrame f=showCDResultTable(table, "Result of biggest possible multiplex ("+(table.getColumnCount()-1)+"/"+fest.length+")");
                    Rectangle rect=f.getBounds();
                    count++;
                    rect.x=rect.x+count*50;
                    f.setBounds(rect);
                }
            }
        });
        dialog.show();

    }
    /**
     * @param cd
     * @param names
     * @param paneldata
     * @param fest
     * @param br
     * @return
     */
    private List createPrimerList(final CalcDalton cd, String[] names, final String[][] paneldata, int[] fest, final int[] br) {
        List primer = new ArrayList(paneldata.length * br.length);
        
        for (int i = 0; i < paneldata.length; i++) {
            if(fest[i]== -1)
                for (int j = 0; j < br.length; j++) {
                    primer.add(new SimplePrimer(cd,names[i],paneldata[i],j));//jeden index einmal als fest verwenden
                }
            else
                primer.add(new SimplePrimer(cd,names[i],paneldata[i],fest[i]));
        }
        return primer;
    }
    /**
     * @param max
     * @return
     */
    private String[] getPrimerNames(Collection max) {
        String[] cliquenames=(String[]) Algorithms.collect(Algorithms.apply(max.iterator(), new UnaryFunction() {
            public Object evaluate(Object obj) {
                return ((SimplePrimer)obj).name;
            }
        }), new ArrayList(max.size())).toArray(new String[max.size()]);
        return cliquenames;
    }
    /**
     * @param cd
     * @param br
     * @param max
     * @return
     */
    private SBETable calculateSBETable(final CalcDalton cd, final int[] br, Set max) {
        String[] cliquenames = getPrimerNames(max);
        
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
            return name+":"+Helper.toString(datarow)+"; "+fest;
        }
        public void setPlexID(String s) {
            if(plexid !=null)
                throw new IllegalStateException("Duplicate plexid!");
            this.plexid=s;
        }

        public String getName() {
            return name;
        }

        public boolean passtMit(Multiplexable other) {
            if(name.equals(((SimplePrimer)other).name))
                return false;
            String[][] sbedata= {datarow
                                ,((SimplePrimer)other).datarow};
            int[] fest=new int[] {this.fest, ((SimplePrimer)other).fest};
            if(cd.calc(sbedata, fest).length == 0) {//keine Loesung
                return false;
            }
            return true;
        
        }


        public String getEdgeReason() {
            return "cd";
        }

        public int realSize() {
            return 1;
        }

        public List getIncludedElements() {
            // TODO Auto-generated method stub
            return null;
        }
    }

}


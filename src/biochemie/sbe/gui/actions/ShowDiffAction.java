/*
 * Created on 18.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.sbe.gui.actions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.lang.ArrayUtils;

import biochemie.calcdalton.CalcDalton;
import biochemie.calcdalton.DiffTableModel;
import biochemie.calcdalton.JTableEx;
import biochemie.calcdalton.SBETable;
import biochemie.sbe.MiniSBE;
import biochemie.sbe.SBECandidate;
import biochemie.sbe.SBEOptionsProvider;
import biochemie.sbe.gui.SpektrometerPreviewFrame;
import biochemie.util.MyAction;


public class ShowDiffAction extends MyAction {
    private int index = 0;
    final TableModel[] diffmodels;
    final TableModel[] cdmodels; 
    final String[] mid;
    private JTable difftable;
    private JFrame frame;
    private List sbecfilt;
    private SBEOptionsProvider cfg;
    private JTableEx cdtable;
    private Set mids;
    
    public ShowDiffAction(List sbec, SBEOptionsProvider cfg) {
        super("Masses / Maldi", "Show mass differences and MALDI previews"
                ,ShowDiffAction.class.getClassLoader().getResource("images/maldi.gif"), null);
        this.cfg = cfg;
        sbecfilt = new ArrayList(sbec);
        Algorithms.remove(sbecfilt.iterator(), new UnaryPredicate() {
            public boolean test(Object obj) {
                return !((SBECandidate)obj).isFoundValidSeq();
            }
        });
        
        mids = new TreeSet();
        //suche alle vorhandenen Multiplexids
        Algorithms.foreach(sbecfilt.iterator(),new UnaryProcedure() {
            public void run(Object obj) {
                mids.add(((SBECandidate)obj).getMultiplexId());
            }
        });
        diffmodels=new TableModel[mids.size()+1];
        cdmodels = new TableModel[mids.size()+1];
        mid=new String[mids.size()+1];
       diffmodels[0]=generateDiffTableModelFor(null,sbecfilt);
       cdmodels[0]=generateCDModelFor(null,sbecfilt);
       mid[0]="All multiplexes";
        int i=1;
       for (Iterator it = mids.iterator(); it.hasNext();i++) {
           String  id = (String ) it.next();
           diffmodels[i]=generateDiffTableModelFor(id,sbecfilt);
           cdmodels[i]=generateCDModelFor(id,sbecfilt);
           mid[i]=id;
       }
    }

    /**
     * @param id
     * @param sbecfilt2
     * @return
     */
    private TableModel generateCDModelFor(String id, List sbec) {
        if(id == null)
            return new DefaultTableModel(0,0);
        
        List mysbec = getFilteredList(id, sbec);
        
        String[] names = new String[mysbec.size()];
        String[][] paneldata = new String[mysbec.size()][];
        int[] fest = new int[mysbec.size()];
        
        int i=0;
        for (Iterator it = mysbec.iterator(); it.hasNext();i++) {
            SBECandidate s = (SBECandidate) it.next();
            names[i]=s.getId();
            paneldata[i]=new String[]{s.getFavSeq(),"A","C","G","T"};
            fest[i]=ArrayUtils.indexOf(cfg.getPhotolinkerPositions(),s.getBruchstelle());
        }
        SBETable sbetable = new SBETable(names,cfg.getPhotolinkerPositions());

        CalcDalton cd = MiniSBE.getCalcDalton(cfg);
        cd.calc(paneldata,sbetable,fest);
        return sbetable;
    }

    /**
     * @param id
     * @param sbecfilt
     * @return
     */
    private TableModel generateDiffTableModelFor(final String id, List sbec) {
        List mysbec = getFilteredList(id, sbec);
        
        double[][] weights = new double[mysbec.size()][];
        String[] sbenames = new String[mysbec.size()];
        int i=0;
        for (Iterator iter = mysbec.iterator(); iter.hasNext();i++) {
            SBECandidate  s = (SBECandidate ) iter.next();
            sbenames[i]=s.getId();
            weights[i]=CalcDalton.calcSBEMass(new String[]{s.getFavSeq(),"A","C","G","T"},s.getBruchstelle());
        }
        return  new DiffTableModel(sbenames,weights);
    }

    /**
     * @param multiplexid
     * @param sbec
     * @return
     */
    private List getFilteredList(final String multiplexid, List sbec) {
        //liste aller sbec, die zu diesem multiplex gehoeren
        List mysbec=(List) Algorithms.collect(Algorithms.select(sbec.iterator(),new UnaryPredicate() {
            public boolean test(Object obj) {
                if(multiplexid == null)
                    return true;
                return ((SBECandidate)obj).getMultiplexId().equals(multiplexid);
            }
        }), new ArrayList());
        return mysbec;
    }

    public void actionPerformed(ActionEvent e) {
       index=1;
        frame = new JFrame("Detailed results");
        frame.getContentPane().setLayout(new BorderLayout());
        difftable = new JTableEx(diffmodels[index]);
        cdtable = new JTableEx(cdmodels[index-1]);
        Dimension dim = new Dimension(500,200);
        difftable.setPreferredScrollableViewportSize(dim);
        cdtable.setPreferredScrollableViewportSize(dim);
        
        JScrollPane sp=new JScrollPane(difftable);
        JScrollPane sp2=new JScrollPane(cdtable);
        
        JSplitPane versplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane horsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        versplit.setTopComponent(sp);
        versplit.setBottomComponent(sp2);
        horsplit.setRightComponent(versplit);
        String[] ids = new String[mids.size() +1];
        ids[0]="All primers";
        int i=1;
        for (Iterator it = mids.iterator(); it.hasNext();) {
            String id = (String) it.next();
            ids[i++]=id;
        }
        final JList idlist = new JList(ids);
        horsplit.setLeftComponent(idlist);
        idlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        idlist.addListSelectionListener(new ListSelectionListener(){

            public void valueChanged(ListSelectionEvent e) {
                int pos = idlist.getSelectedIndex();//XXX sollte da nich drauf zugreifen
                System.out.println("pos "+pos+" selected.");
                index=(pos+mid.length)%mid.length;
                difftable.setModel(diffmodels[index]);
                cdtable.setModel(cdmodels[index]);
                frame.repaint();
            }
            
        });
        frame.getContentPane().add(horsplit,BorderLayout.CENTER);
        JPanel buttonpanel=new JPanel();
        
/*        JButton prev = new JButton("Previous multiplex");
        JButton next = new JButton("Next multiplex");
        prev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                index=(index-1+mid.length)%mid.length;
                difftable.setModel(diffmodels[index]);
                frame.setTitle(mid[index]);
                frame.repaint();
            }
        });
        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                index=(index+1)%mid.length;
                difftable.setModel(diffmodels[index]);
                frame.setTitle(mid[index]);
                frame.repaint();
            }
        });
        buttonpanel.add(prev);
        buttonpanel.add(next);*/
        JButton showspektrometer = new JButton("Show preview");
        buttonpanel.add(showspektrometer);
        showspektrometer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List l = sbecfilt;
                if(index != 0 )
                    l = getFilteredList(mid[index],sbecfilt);
                JFrame f = new SpektrometerPreviewFrame(l,"Spektrometervorschau",mid[index]);
                f.pack();
                f.setVisible(true);
            }
        });
        frame.getContentPane().add(buttonpanel,BorderLayout.SOUTH);   
        idlist.setSelectedIndex(0);
        frame.pack();
        frame.setVisible(true);
    }
}
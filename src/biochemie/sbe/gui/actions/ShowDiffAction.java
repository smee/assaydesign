/*
 * Created on 18.01.2005
 *
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
import biochemie.gui.ColumnResizer;
import biochemie.sbe.CleavablePrimerFactory;
import biochemie.sbe.PrimerFactory;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.gui.MiniSBEGui;
import biochemie.sbe.gui.SpektrometerPreviewFrame;
import biochemie.util.Helper;
import biochemie.util.MyAction;


public class ShowDiffAction extends MyAction {
    private static final Dimension TABLE_DIM = new Dimension(500,200);

    private int index = 0;
    final TableModel[] diffmodels;
    final TableModel[] cdmodels;
    final String[] mid;
    private JFrame frame;
    private List sbecfilt;
    private SBEOptions cfg;
    private Set mids;
    private JTable difftable;
    private JTable cdtable;
    private JTable restable;
    private JTable[] restables;
    private JScrollPane resscrollpane;

    public ShowDiffAction(List sbec, SBEOptions cfg, MiniSBEGui.CalculateAction calcaction) {
        super("Masses / Maldi", "Show mass differences and MALDI previews"
                ,ShowDiffAction.class.getClassLoader().getResource("images/maldi.gif"), null);
        this.cfg = cfg;
        sbecfilt = new ArrayList(sbec);
        Algorithms.remove(sbecfilt.iterator(), new UnaryPredicate() {
            public boolean test(Object obj) {
                return !((PrimerFactory)obj).isFoundValidSeq();
            }
        });

        mids = new TreeSet();
        //suche alle vorhandenen Multiplexids
        Algorithms.foreach(sbecfilt.iterator(),new UnaryProcedure() {
            public void run(Object obj) {
                mids.add(((PrimerFactory)obj).getMultiplexId());
            }
        });
        System.out.println(sbecfilt);
        diffmodels=new TableModel[mids.size()+1];
        cdmodels = new TableModel[mids.size()+1];
        restables = new JTable[mids.size()+1];

        mid=new String[mids.size()+1];
       diffmodels[0]=generateDiffTableModelFor(null,sbecfilt);
       cdmodels[0]=generateCDModelFor(null,sbecfilt);
       restables[0]=calcaction.createResultTable(sbecfilt);
       mid[0]="All multiplexes";
        int i=1;
       for (Iterator it = mids.iterator(); it.hasNext();i++) {
           String  id = (String ) it.next();
           diffmodels[i]=generateDiffTableModelFor(id,sbecfilt);
           cdmodels[i]=generateCDModelFor(id,sbecfilt);
           restables[i]=calcaction.createResultTable(getFilteredList(id, sbecfilt));
           mid[i]=id;
       }
       for (int j = 0; j < restables.length; j++) {
        restables[j].setPreferredScrollableViewportSize(TABLE_DIM);
       }
    }

    /**
     * @param id
     * @param cfg 
     * @param sbecfilt2
     * @return
     */
    private TableModel generateCDModelFor(String id, List sbec) {
        if(id == null || sbec.size()==0)
            return new DefaultTableModel(0,0);

        List mysbec = getFilteredList(id, sbec);

        String[] names = new String[mysbec.size()];
        String[][] paneldata = new String[mysbec.size()][];
        int[] fest = new int[mysbec.size()];

        int i=0;
        for (Iterator it = mysbec.iterator(); it.hasNext();i++) {
            PrimerFactory s = (PrimerFactory) it.next();
            names[i]=s.getId();
            paneldata[i]=createAnhangsData(s);
            if(s instanceof CleavablePrimerFactory)
                fest[i]=ArrayUtils.indexOf(cfg.getPhotolinkerPositions(),((CleavablePrimerFactory)s).getBruchstelle());
//            System.out.print(ArrayUtils.toString(paneldata[i]));
//            System.out.println(" "+fest[i]);
        }
        SBETable sbetable = new SBETable(names,cfg.getPhotolinkerPositions());

        CalcDalton cd = Helper.getCalcDalton(cfg);
        if(sbec.get(0) instanceof CleavablePrimerFactory)
            cd.calc(paneldata,sbetable,fest);
        else
            cd.calc(paneldata,sbetable);
        return sbetable;
    }

    private String[] createAnhangsData(PrimerFactory s) {
//        if(cfg.getCalcDaltonAllExtensions()) {
//            return new String[]{s.getFavSeq(),"A","C","G","T"};
//        }else {
            String[] arr=s.getFavPrimer().getCDParamLine();
            return arr;
//        }
    }

    /**
     * @param id
     * @param sbecfilt
     * @return
     */
    private TableModel generateDiffTableModelFor(final String id, List sbec) {
        if(sbec.size()==0)
            return new DiffTableModel(new String[0],new double[0][]);
        List mysbec = getFilteredList(id, sbec);
        CalcDalton cd=Helper.getCalcDalton(cfg);
        String[][] params=new String[mysbec.size()][];
        int[] br=cfg.getPhotolinkerPositions();
        int[] fest=new int[mysbec.size()];
        
        int i=0;
        SBETable table=new SBETable(getNames(mysbec),br);
        for (Iterator iter = mysbec.iterator(); iter.hasNext();i++) {
            PrimerFactory  s = (PrimerFactory ) iter.next();
            params[i]=s.getFavPrimer().getCDParamLine();
            if(s instanceof CleavablePrimerFactory)
                fest[i]=ArrayUtils.indexOf(br,((CleavablePrimerFactory)s).getBruchstelle());
        }
        if(sbec.get(0) instanceof CleavablePrimerFactory)
            cd.calc(params,table,fest);
        else
            cd.calc(params,table);
        return  new DiffTableModel(table,cfg.isCalcDaltonAllExtensions());
    }
    private String[] getNames(List sbec) {
        String[] sbenames = new String[sbec.size()];
        int i=0;
        for (Iterator iter = sbec.iterator(); iter.hasNext();i++) {
            PrimerFactory  s = (PrimerFactory ) iter.next();
            sbenames[i]=s.getId();
        }
        return sbenames;
    }
    /**
     * liste aller sbec, die zu diesem multiplex gehoeren
     * @param multiplexid
     * @param sbec
     * @return
     */
    private List getFilteredList(final String multiplexid, List sbec) {
        List mysbec=(List) Algorithms.collect(Algorithms.select(sbec.iterator(),new UnaryPredicate() {
            public boolean test(Object obj) {
                if(multiplexid == null)
                    return true;
                return ((PrimerFactory)obj).getMultiplexId().equals(multiplexid);
            }
        }), new ArrayList());
        return mysbec;
    }

    public void actionPerformed(ActionEvent e) {
       index=1;
        frame = new JFrame("Detailed results");
        frame.getContentPane().setLayout(new BorderLayout());
        restable = restables[index];
        difftable = new JTableEx(diffmodels[index]);
        ColumnResizer.adjustColumnPreferredWidths(difftable);
        cdtable = new JTableEx(cdmodels[index-1]);
        ColumnResizer.adjustColumnPreferredWidths(cdtable);
        difftable.setPreferredScrollableViewportSize(TABLE_DIM);
        cdtable.setPreferredScrollableViewportSize(TABLE_DIM);

        JScrollPane sp=new JScrollPane(difftable);
        JScrollPane sp2=new JScrollPane(cdtable);
        resscrollpane=new JScrollPane(restable);
        JSplitPane versplit1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane versplit2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane horsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        versplit1.setTopComponent(resscrollpane);
        versplit1.setBottomComponent(versplit2);
        versplit2.setTopComponent(sp);
        versplit2.setBottomComponent(sp2);
        horsplit.setRightComponent(versplit1);
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
                resscrollpane.setViewportView(restables[index]);
                frame.repaint();
            }

        });
        frame.getContentPane().add(horsplit,BorderLayout.CENTER);
        JPanel buttonpanel=new JPanel();
        JButton showspektrometer = new JButton("Show preview");
        buttonpanel.add(showspektrometer);
        showspektrometer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List l = sbecfilt;
                if(index != 0 )
                    l = getFilteredList(mid[index],sbecfilt);
                JFrame f = new SpektrometerPreviewFrame(l,"MALDI-MS-preview",mid[index],cfg);
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
package biochemie.sbe.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import netprimer.DisplayStructureIcon;
import netprimer.cal_Dimers;
import netprimer.cal_Hairpins;

import biochemie.domspec.SekStruktur;
import biochemie.sbe.SBECandidate;
import biochemie.sbe.gui.MiniSBEGui.OptimizePLAction;
import biochemie.util.MyAction;

public class ShowSekStrucsAction extends MyAction {

    private JTable table;
    private List sbec;

    public ShowSekStrucsAction(JTable table, List sbec) {
        super("Sec.strucs.",
                "Show all secondary structures",
                OptimizePLAction.class.getClassLoader().getResource("images/sec.gif"),
                null);
        this.table=table;
        this.sbec=sbec;
    }

    public void actionPerformed(ActionEvent e) {
        int row=table.getSelectedRow();
        if(row<0)
            return;
        String id=(String) table.getModel().getValueAt(row,1);
        String seq=findSBECandidateWithID(id);
        if(seq==null)
            return;
        JFrame f=new JFrame("Sec.strucs");
        JPanel p=new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        JScrollPane pane=new JScrollPane(p);
        p.add(new JLabel("Hairpins:"));
        p.add(new JLabel(new DisplayStructureIcon(cal_Hairpins.cal_Hairpins(seq),SekStruktur.HAIRPIN)));
        p.add(new JLabel("Homodimers:"));
        p.add(new JLabel(new DisplayStructureIcon(cal_Dimers.cal_Dimers(seq),SekStruktur.HOMODIMER)));
        f.getContentPane().add(pane);
        f.pack();
        f.setSize(new Dimension(500,700));
        f.setVisible(true);
    }
    private String findSBECandidateWithID(String id) {
        for (Iterator iter = sbec.iterator(); iter.hasNext();) {
            SBECandidate s = (SBECandidate) iter.next();
            if(s.getId().equals(id))
                return s.getFavSeq();
        }
        return null;
    }
}

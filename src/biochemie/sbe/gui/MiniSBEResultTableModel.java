/*
 * Created on 20.12.2004
 *
 */
package biochemie.sbe.gui;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.table.AbstractTableModel;

import biochemie.sbe.SBECandidate;
import biochemie.util.Helper;

/**
 * @author sdienst
 *
 */
public class MiniSBEResultTableModel extends AbstractTableModel {
    private final String[] header;
    private final Object[][] data;
    private final List sbec;


    public MiniSBEResultTableModel(List sb) {
        this.sbec = sb;
        String[] origheader = SBECandidate.getCsvheader();
        header  = new String[origheader.length + 2];
        System.arraycopy(origheader,0,header,0,origheader.length);
        header[header.length-2] = "Exclude primer";
        header[header.length-1] = "Exclude pl";
        data = new Object[sbec.size()][];
        int i=0;
        for (Iterator it = sbec.iterator(); it.hasNext();i++) {
            SBECandidate s = (SBECandidate) it.next();
            StringTokenizer st = new StringTokenizer(Helper.clearEmptyCSVEntries(s.getCSVRow()),";");
            Object[] entries = new Object[st.countTokens() + 2];
            int j =0;
            while (st.hasMoreTokens()) {
                entries[j++] = st.nextToken();
            }
            entries[entries.length - 2] = Boolean.FALSE;
            entries[entries.length - 1] = Boolean.FALSE;
            data[i] = entries;
        }
    }
    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return header.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }


    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    public boolean isCellEditable(int row, int col) {
        if(col >= header.length-2 )
            return true;
        return false;
    }
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
    public String getColumnName(int column) {
        return header[column];
    }
    public String getFilterFor(String id) {
        for (Iterator it = sbec.iterator(); it.hasNext();) {
            SBECandidate p = (SBECandidate) it.next();
            if(p.getId().equals(id)) {
                return p.getType()+"_"+p.getFavSeq().length()+"_"+p.getBruchstelle();
            }
        }
        return "";
    }
    public String getPLFilterFor(String id) {
        for (Iterator it = sbec.iterator(); it.hasNext();) {
            SBECandidate p = (SBECandidate) it.next();
            if(p.getId().equals(id)) {
                return "*_*_"+p.getBruchstelle();
            }
        }
        return "";
    }
    public List getSbec() {
        return sbec;
    }
}

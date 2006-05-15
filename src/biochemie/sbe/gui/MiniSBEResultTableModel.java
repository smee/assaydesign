/*
 * Created on 20.12.2004
 *
 */
package biochemie.sbe.gui;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.table.AbstractTableModel;

import biochemie.sbe.PrimerFactory;
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
        if(sb.size()==0){
            data=new Object[0][];
            header=new String[0];
            return;
        }
        String[] origheader = ((PrimerFactory)sb.get(0)).getCsvheader().split(";");
        header  = new String[origheader.length + 2];
        System.arraycopy(origheader,0,header,0,origheader.length);
        header[header.length-2] = "Exclude primer";
        header[header.length-1] = "Exclude complete";
        data = new Object[sbec.size()][];
        int i=0;
        for (Iterator it = sbec.iterator(); it.hasNext();i++) {
            PrimerFactory s = (PrimerFactory) it.next();
            StringTokenizer st = new StringTokenizer(Helper.clearEmptyCSVEntries(s.getCSVRow()),";");
            Object[] entries = new Object[st.countTokens() + 2];
            int j =0;
            while (st.hasMoreTokens()) {
                entries[j++] = st.nextToken().trim();
            }
/*            if(entries[4]!=null && ((String)entries[4]).length()>0)
                entries[4]=new Integer((String) entries[4]);
            if(entries[5]!=null && ((String)entries[5]).length()>0)
                entries[5]=new Integer((String) entries[5]);*/
            //TODO was ist mit leeren feldern? classcastexception beim sortieren!
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
    public Class getColumnClass(int c) {//TODO unsauber, z.b. fuer integer in spalte 4/5, kann ja leer sein! 
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
            PrimerFactory p = (PrimerFactory) it.next();
            if(p.getId().equals(id)) {
                return p.getFavPrimer().getFilter();
            }
        }
        return "";
    }
    public String getPrimerFilterFor(String id) {
        for (Iterator it = sbec.iterator(); it.hasNext();) {
            PrimerFactory p = (PrimerFactory) it.next();
            if(p.getId().equals(id)) {
                return p.getFilter();
            }
        }
        return "";
    }
    public List getSbec() {
        return sbec;
    }
}

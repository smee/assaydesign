package biochemie.calcdalton;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


public class JTableEx extends javax.swing.JTable {
    public JTableEx() {
        this(null, null, null);
    }
    public JTableEx(TableModel dm) {
        this(dm, null, null);
    }
    public JTableEx(TableModel dm, TableColumnModel cm) {
        this(dm, cm, null);
    }
    public JTableEx(TableModel dm, TableColumnModel cm, javax.swing.ListSelectionModel sm) {
        super(dm, cm, sm);
    }
    public JTableEx(int numRows, int numColumns) {
        this(new DefaultTableModel(numRows, numColumns));
    }
    public JTableEx(final Vector rowData, final Vector columnNames) {
        super(rowData, columnNames);
    }
    public JTableEx(final Object[][] rowData, final Object[] columnNames) {
        super(rowData, columnNames);
    }
    public String getToolTipText(MouseEvent event) {
        Point p= event.getPoint();
        int row= rowAtPoint(p);
        int col= columnAtPoint(p);
        Object o= getValueAt(row, col);
        if (null == o)
            return null;
        if (o.toString().equals(""))
            return null;
        return o.toString();
    }
    public Point getToolTipLocation(MouseEvent event) {
        Point p= event.getPoint();
        int row= rowAtPoint(p);
        int col= columnAtPoint(p);
        Object o= getValueAt(row, col);
        if (null == o)
            return null;
        if (o.toString().equals(""))
            return null;
        Point pt= getCellRect(row, col, true).getLocation();
        pt.translate(-1, -2);
        return pt;
    }
    public Dimension getPreferredScrollableViewportSize()
    {
        Dimension size = super.getPreferredScrollableViewportSize();
        return new Dimension(Math.min(getPreferredSize().width, size.width), size.height);
    }
} // End of Class JTableEx
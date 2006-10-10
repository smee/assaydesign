package biochemie.gui;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class CombinedTableModel extends AbstractTableModel {

    private final TableModel[] models;
    
    public CombinedTableModel(TableModel[] models){
        this.models=models;        
    }
    public int getRowCount() {
        int count=0;
        for (int i = 0; i < models.length; i++) 
            count+=models[i].getRowCount();
        return count;
    }

    public int getColumnCount() {
        int count=0;
        for (int i = 0; i < models.length; i++) 
            count+=models[i].getColumnCount();
        return count;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        int idx=0,colcount=models[0].getColumnCount(), oldcount=0;
        while(colcount<=columnIndex){
            idx++;
            oldcount=colcount;
            colcount+=models[idx].getColumnCount();
        }
        if(models[idx].getRowCount()<=rowIndex)
            return null;
        return models[idx].getValueAt(rowIndex,columnIndex-oldcount);
    }

}

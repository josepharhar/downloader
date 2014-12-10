package downloader;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Joseph Arhar
 */
public class DownloadTable extends AbstractTableModel implements Observer{
    
    private static final String[] columns = {"Name", "Size", "State", "Progress"};
    private static final Class[] colClasses = {String.class, String.class, String.class, JProgressBar.class};
    
    private static ArrayList<Download> downloads = new ArrayList();
    
    // Add a new download to the table
    public void addDownload(Download newDownload) {
        //makes sure this table gets updates when the download changes
        newDownload.addObserver(this);
        
        downloads.add(newDownload);
        
        //Tells GUI to update its table
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }
    
    // Returns a download from the specified row
    public Download getDownload(int row) {
        return downloads.get(row);
    }
    
    // Remove a download from the table
    public void removeDownload(int row) {
        downloads.remove(row);
        //Tells GUI to update its table
        fireTableRowsDeleted(row, row);
    }
    
    @Override
    public String getColumnName(int row) {
        return columns[row];
    }

    @Override
    public int getRowCount() {
        return downloads.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }
    
    public Class getColumnClass(int col) {
        return colClasses[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return downloads.get(rowIndex).getFileName();
            case 1:
                return downloads.get(rowIndex).getSize();
            case 2:
                return downloads.get(rowIndex).getStateName();
            case 3:
                return downloads.get(rowIndex).getProgress();
        }
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        int index = downloads.indexOf(arg);
        // Tells GUI to update its table
        fireTableRowsUpdated(index, index);
    }
    
}

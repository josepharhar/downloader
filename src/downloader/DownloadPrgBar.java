package downloader;

import java.awt.Component;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Joseph Arhar
 */
public class DownloadPrgBar extends JProgressBar implements TableCellRenderer {
    
    public DownloadPrgBar(int min, int max) {
        super(min, max);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Integer) {
            setValue((int) value);
        } else {
            setValue((int) ((Float) value).floatValue());
        }
        return this;
    }
    
}

package csheets.ext.db.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Filipe_1110687 & Filipe_1110688
 */
public class TableNamesAdapter implements ComboBoxModel<String> {
    List<String> tables = new ArrayList<String>();
    ListDataListener listener;
    Object item;
    
    @Override
    public int getSize() {
        return tables.size();
    }

    @Override
    public String getElementAt(int index) {
        return tables.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listener = l;
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
    }
    
    public void update(List<String> tables) {
        this.tables = tables;
        if (listener != null) {
	    listener.contentsChanged(new ListDataEvent(this,
		    ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
	}
    }

    @Override
    public void setSelectedItem(Object anItem) {
        item = anItem;
    }

    @Override
    public Object getSelectedItem() {
        return item;
    }
    
}

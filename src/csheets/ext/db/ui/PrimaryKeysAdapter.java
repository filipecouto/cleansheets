package csheets.ext.db.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class PrimaryKeysAdapter implements ListModel<String> {
    
    ListDataListener listener;
    List<String> list = new ArrayList<String>();
    
    @Override
    public void addListDataListener(ListDataListener listener) {
	this.listener = listener;
    }

    @Override
    public String getElementAt(int index) {
	return list.get(index);
    }

    @Override
    public int getSize() {
	return list.size();
    }

    @Override
    public void removeListDataListener(ListDataListener arg0) {
    }

    public void setValues(List<String> list) {
	this.list = list;
        listener.contentsChanged(null);
    }
}

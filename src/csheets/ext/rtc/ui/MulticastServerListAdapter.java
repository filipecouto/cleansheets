package csheets.ext.rtc.ui;

import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import csheets.ext.rtc.ServerInformation;

/**
 * Adapter for list in ConnectionDialog
 * 
 * @author Rita Nogueira
 * 
 */
public class MulticastServerListAdapter implements ListModel {
    ListDataListener listener;
    ArrayList<ServerInformation> information = new ArrayList<ServerInformation>();

    @Override
    public void addListDataListener(ListDataListener arg0) {
	listener = arg0;
    }

    @Override
    public ServerInformation getElementAt(int index) {
	return information.get(index);
    }

    @Override
    public int getSize() {
	return information.size();
    }

    public void addShareInfo(ServerInformation shareInfo) {
	if (!existInArray(shareInfo)) {
	    information.add(shareInfo);
	}
	listener.contentsChanged(null);
    }

    public void clear() {
	information.clear();
	listener.contentsChanged(null);
    }

    private boolean existInArray(ServerInformation shareInfo) {
	for (int i = 0; i < information.size(); i++) {
	    if (shareInfo.getIp().equals(information.get(i).getIp())) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public void removeListDataListener(ListDataListener arg0) {
    }
}

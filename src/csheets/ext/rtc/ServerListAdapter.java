package csheets.ext.rtc;

import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class ServerListAdapter implements ListModel {
    ListDataListener listener;
    ArrayList<ServersInformation> information = new ArrayList<ServersInformation>();

    @Override
    public void addListDataListener(ListDataListener arg0) {
	listener = arg0;
    }

    @Override
    public ServersInformation getElementAt(int index) {
	return information.get(index);
    }

    @Override
    public int getSize() {
	return information.size();
    }

    public void addShareInfo(ServersInformation shareInfo) {
	if (!existInArray(shareInfo)) {
	    information.add(shareInfo);
	}
	listener.contentsChanged(null);
    }

    public void clear() {
	information.clear();
	listener.contentsChanged(null);
    }

    private boolean existInArray(ServersInformation shareInfo) {
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

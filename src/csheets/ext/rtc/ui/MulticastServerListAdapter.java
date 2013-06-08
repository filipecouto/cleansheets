package csheets.ext.rtc.ui;

import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import csheets.ext.rtc.ServerInformation;

/**
 * Adapter to the GUI (to update the list with the names of the shares)
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

    /**
     * Adds a ServerInformation the array if it does not exist (preventing a
     * share appear repeated)
     * 
     * @param shareInfo
     */
    public void addShareInfo(ServerInformation shareInfo) {
	if (!existInArray(shareInfo)) {
	    information.add(shareInfo);
	}
	listener.contentsChanged(null);
    }

    /**
     * Clears the array
     */
    public void clear() {
	information.clear();
	listener.contentsChanged(null);
    }

    /**
     * see if the ServerInformation exists or not in the array
     * 
     * @param shareInfo
     * @return true if exists false if not exists
     */
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

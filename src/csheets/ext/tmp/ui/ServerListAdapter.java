package csheets.ext.tmp.ui;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import csheets.ext.rtc.RtcCommunicator;

/**
 * Adapter to shares list
 * 
 * @author Rita Nogueira
 * 
 */
public class ServerListAdapter implements ListModel<RtcCommunicator> {
    ListDataListener listener;
    private RtcCommunicator[] serverInfos = new RtcCommunicator[0];

    @Override
    public void addListDataListener(ListDataListener l) {
	listener = l;
    }

    @Override
    public RtcCommunicator getElementAt(int index) {
	return serverInfos[index];
    }

    @Override
    public int getSize() {
	return serverInfos == null ? 0 : serverInfos.length;
    }

    @Override
    public void removeListDataListener(ListDataListener arg0) {

    }

    public void update(RtcCommunicator[] info) {
	serverInfos = info;
	if (listener != null) {
	    listener.contentsChanged(new ListDataEvent(this,
		    ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
	}
    }

}

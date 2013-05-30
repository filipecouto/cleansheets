package csheets.ext.rtc.ui;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import csheets.ext.rtc.ClientInfo;

public class ClientsListAdapter implements ListModel<ClientInfo> {
	ListDataListener listener;
	private ClientInfo[] clientInfos = new ClientInfo[0];

	@Override
	public int getSize() {
	    return clientInfos.length; // clients.size();
	}

	@Override
	public ClientInfo getElementAt(int index) {
	    return clientInfos[index]; // clients.get(index).getInfo().getName();
	}

	@Override
	public void addListDataListener(ListDataListener l) {
	    listener = l;
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
	}

	public void update(ClientInfo[] info) {
	    clientInfos = info;
	    if (listener != null) {
		listener.contentsChanged(new ListDataEvent(this,
			ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
	    }
	}
	
}
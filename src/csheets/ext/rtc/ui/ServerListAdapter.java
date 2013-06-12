package csheets.ext.rtc.ui;

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
    
    /**
     * Tells if it's the person who started the share
     * @param index
     * @return 
     */
    public boolean isOwner(int index){
        return serverInfos[index].getSharingProperties().isOwner();
    }
    
    /**
     * Tells if the share is activated
     * @param index
     * @return 
     */
    public boolean isActivated(int index){
        return serverInfos[index].isActivated();
    }
    
    /**
     * Disconnects from a specific share
     * @param index 
     */
    public void disconnect(int index){
        serverInfos[index].onDisconnected(null);
    }
    
    /**
     * Sets the share Activated value
     * @param index
     * @param activated 
     */
    public void setActivated(int index, boolean activated){
        serverInfos[index].setActivated(activated);
    }
    
    /**
     * Sets the share Writable value
     * @param index
     * @param writable 
     */
    public void setWritable(int index, boolean writable){
        serverInfos[index].getSharingProperties().setWritable(writable);
    }
    
    /**
     * Tells if a share is writable or read-only
     * @param index
     * @return 
     */
    public boolean isWritable(int index){
        return serverInfos[index].getSharingProperties().isWritable();
    }

}

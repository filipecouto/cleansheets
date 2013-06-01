package csheets.ext.rtc.ui;

import javax.swing.JComponent;

import csheets.ext.rtc.ClientInfo;
import csheets.ext.rtc.RealTimeCollaboration;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.CellDecorator;
import csheets.ui.ext.UIExtension;

/**
 * This class is the UIExtension of RealTimeCollaboration extension. It provides
 * a side bar and a CellDecorator.
 * 
 * @author gil_1110484
 */
public class RtcUI extends UIExtension {
    private RtcCellDecorator cellDecorator;
    private RtcSidebar sidebar;

    private ShareAction shareAction;
    private ConnectAction connectAction;

    public RtcUI(RealTimeCollaboration realTimeCollaboration,
	    UIController uiController) {
	super(realTimeCollaboration, uiController);
    }

    @Override
    public CellDecorator getCellDecorator() {
	if (cellDecorator == null) {
	    cellDecorator = new RtcCellDecorator(
		    (RealTimeCollaboration) getExtension());
	}
	return cellDecorator;
    }

    @Override
    public JComponent getSideBar() {
	if (sidebar == null) {
	    shareAction = new ShareAction(
		    (RealTimeCollaboration) getExtension(), uiController);
	    shareAction.setListener(new DataListener() {
		@Override
		public void onSendData(ClientInfo info, String address) {
		    // sidebar.onConnection(address);
		}
	    });
	    connectAction = new ConnectAction(
		    (RealTimeCollaboration) getExtension(), uiController);
	    connectAction.setListener(new DataListener() {
		@Override
		public void onSendData(ClientInfo info, String address) {
		    // sidebar.onConnection(address);
		}
	    });
	    sidebar = new RtcSidebar((RealTimeCollaboration) getExtension(),
		    uiController, shareAction, connectAction);
	}
	return sidebar;
    }

    /**
     * Tells the side bar to refresh the list of connected users
     */
    public void updateUsersList(final ClientInfo[] clients) {
	sidebar.updateUsersList(clients);
    }

    /**
     * Called when the connection is stopped, by user request or any other
     * reason
     */
    public void onDisconnected() {
	sidebar.onDisconnected();
    }

    /**
     * Called when connection is established
     */
    public void onConnected(String address) {
	sidebar.onConnection(address);
    }

    /**
     * Called when an exception is thrown while trying to connect, this method
     * will communicate with the UI in order to warn the user about the issue
     * 
     * @param e
     *            the thrown exception
     */
    public void onConnectionFailed(Exception e) {
	sidebar.onConnectionFailed(e);
    }
}
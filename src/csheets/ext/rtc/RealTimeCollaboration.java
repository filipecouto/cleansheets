package csheets.ext.rtc;

import java.io.IOException;

import javax.swing.JComponent;

import csheets.core.Address;
import csheets.ext.Extension;
import csheets.ext.rtc.messages.RemoteCell;
import csheets.ext.rtc.ui.ConnectAction;
import csheets.ext.rtc.ui.DataListener;
import csheets.ext.rtc.ui.RtcCellDecorator;
import csheets.ext.rtc.ui.RtcSidebar;
import csheets.ext.rtc.ui.ShareAction;
import csheets.ui.ctrl.EditEvent;
import csheets.ui.ctrl.EditListener;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.CellDecorator;
import csheets.ui.ext.UIExtension;

/**
 * This extension lets the user share his/her workbook with other CleanSheets
 * users and real-time collaboration (many people can be working on the same
 * workbook at the same time).
 * 
 * Currently, it is able to share one spreadsheet (however the structure is
 * ready to support for any amount of spreadsheets) or a range of cells in one
 * spreadsheet.
 * 
 * Once disconnected, the user can use that shared spreadsheet or piece of it
 * separately from the source.
 * 
 * @author gil_1110484
 */
public class RealTimeCollaboration extends Extension {
    RtcCommunicator communicator;
    RtcEventsResponder responder;
    ClientInfo identity;

    RtcCellDecorator cellDecorator;

    RtcSidebar sidebar;

    ShareAction shareAction;
    ConnectAction connectAction;

    private boolean isOwner;

    public RealTimeCollaboration() {
	super("Real Time Collaboration");
    }

    private RtcEventsResponder getResponder(UIController uiController) {
	if (responder == null) {
	    responder = new RtcEventsResponder(communicator, uiController, this);
	}
	return responder;
    }

    /**
     * Tells the side bar to refresh the list of connected users
     */
    public void updateUsersList() {
	sidebar.updateUsersList(communicator.getConnectedUsers());
    }

    /**
     * Creates a server for sharing on this instance
     * 
     * @param client
     *            the identity of this user
     * @param port
     *            the port where this server will be listening
     * @param properties
     *            sharing options, for now controls whether a cell is shared or
     *            not
     * @param uiController
     *            uiController needed for the RtcResponder in charge of sending
     *            feedback to the GUI
     * @return the identity, may contain the address
     * @throws IOException
     */
    public ClientInfo createServer(ClientInfo client, int port,
	    RtcSharingProperties properties, UIController uiController)
	    throws IOException {
	isOwner = true;
	ServerInterface server = new ServerInterface(client, port, properties,
		uiController);
	identity = server.getServerInfo();
	communicator = server;
	communicator.setListener(getResponder(uiController));
	communicator.start();
	return identity;
    }

    /**
     * Creates a client to connect to a shared workbook
     * 
     * @param client
     *            the identity of this user
     * @param ipAddress
     *            the address of the server to connect to
     * @param port
     *            the port where the server will be listening
     * @param uiController
     *            uiController needed for the RtcResponder in charge of sending
     *            feedback to the GUI
     * @return the identity, may contain the address
     * @throws IOException
     */
    public ClientInfo createClient(ClientInfo client, String ipAddress,
	    int port, UIController uiController) throws IOException {
	isOwner = false;
	identity = client;
	communicator = new ClientInterface(ipAddress, port, identity);
	communicator.setListener(getResponder(uiController));
	communicator.start();
	return identity;
    }

    /**
     * Called from the GUI when the users stops the connection, this method will
     * tell the communication interface to stop (and maybe warn any connected
     * user)
     */
    public void disconnect() {
	communicator.onDisconnected(null);
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
    public void onConnected() {
	sidebar.onConnection(identity.getAddress().getHostAddress());
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

    /**
     * Says whether there is currently a connection (either this is a client or
     * a server)
     * 
     * @return true if it is connected, false otherwise
     */
    public boolean isConnected() {
	return communicator == null ? false : communicator.isConnected();
    }

    /**
     * Says whether this instance represents the real owner of the workbook.
     * Usually determined by who is the server.
     * 
     * @return true if it is, false otherwise
     */
    public boolean isOwner() {
	return isOwner;
    }

    /**
     * Asks the RtcShareProperties if this address is shared
     * 
     * @param address
     *            the address to check
     * @return true if this address is shared, false otherwise
     */
    public boolean isShared(Address address) {
	final RtcSharingProperties props = responder.getShareProperties();
	return props == null ? false : props.isInsideRange(address);
    }

    @Override
    public UIExtension getUIExtension(final UIController uiController) {
	uiController.addEditListener(new EditListener() {
	    @Override
	    public void workbookModified(EditEvent event) {
		if (communicator != null) {
		    if (uiController.getActiveSpreadsheet() == uiController
			    .getActiveWorkbook().getSpreadsheet(0)) {
			communicator.onCellChanged(null, new RemoteCell(
				uiController.getActiveCell()));
		    }
		}
	    }
	});
	// TODO let the others see what's selected
	// uiController.addSelectionListener(new SelectionListener() {
	// @Override
	// public void selectionChanged(SelectionEvent event) {
	// if (uiController.getActiveCell() == null) {
	// return;
	// }
	// if (communicator != null) {
	// communicator.onCellSelected(null, uiController
	// .getActiveCell().getAddress());
	// }
	// }
	// });
	return new UIExtension(this, uiController) {
	    @Override
	    public CellDecorator getCellDecorator() {
		if (cellDecorator == null) {
		    cellDecorator = new RtcCellDecorator(
			    RealTimeCollaboration.this);
		}
		return cellDecorator;
	    }

	    @Override
	    public JComponent getSideBar() {
		if (sidebar == null) {
		    shareAction = new ShareAction(RealTimeCollaboration.this,
			    uiController);
		    shareAction.setListener(new DataListener() {
			@Override
			public void onSendData(ClientInfo info, String address) {
			    // sidebar.onConnection(address);
			}
		    });
		    connectAction = new ConnectAction(
			    RealTimeCollaboration.this, uiController);
		    connectAction.setListener(new DataListener() {
			@Override
			public void onSendData(ClientInfo info, String address) {
			    // sidebar.onConnection(address);
			}
		    });
		    sidebar = new RtcSidebar(RealTimeCollaboration.this,
			    uiController, shareAction, connectAction);
		}
		return sidebar;
	    }
	};
    }
}

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

    private RtcEventsResponder getResponder(UIController uiController,
	    RtcShareProperties properties) {
	if (responder == null) {
	    responder = new RtcEventsResponder(communicator, uiController, this);
	}
	return responder;
    }

    public void updateUsersList() {
	sidebar.updateUsersList(communicator.getConnectedUsers());
    }

    public ClientInfo createServer(ClientInfo client, int port,
	    RtcShareProperties properties, UIController uiController)
	    throws IOException {
	isOwner = true;
	ServerInterface server = new ServerInterface(client, port, properties,
		uiController);
	identity = server.getServerInfo();
	communicator = server;
	communicator.setListener(getResponder(uiController, properties));
	communicator.start();
	return identity;
    }

    public ClientInfo createClient(ClientInfo client, String ipAddress,
	    int port, UIController uiController) throws IOException {
	isOwner = false;
	identity = client;
	communicator = new ClientInterface(ipAddress, port, identity);
	communicator.setListener(getResponder(uiController, null));
	communicator.start();
	return identity;
    }

    public void disconnect() {
	communicator.onDisconnected(null);
    }

    public void onDisconnected() {
	sidebar.onDisconnected();
    }

    public void onConnected() {
	sidebar.onConnection(identity.getAddress().getHostAddress());
    }

    public void onConnectionFailed(Exception e) {
	sidebar.onConnectionFailed(e);
    }

    public boolean isConnected() {
	return communicator == null ? false : communicator.isConnected();
    }

    public boolean isOwner() {
	return isOwner;
    }

    public boolean isShared(Address address) {
	final RtcShareProperties props = responder.getShareProperties();
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

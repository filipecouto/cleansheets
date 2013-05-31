package csheets.ext.rtc;

import java.io.IOException;

import javax.swing.JComponent;

import csheets.core.Address;
import csheets.ext.Extension;
import csheets.ext.rtc.messages.RemoteCell;
import csheets.ext.rtc.ui.ConnectAction;
import csheets.ext.rtc.ui.DataListener;
import csheets.ext.rtc.ui.RtcSidebar;
import csheets.ext.rtc.ui.ShareAction;
import csheets.ui.ctrl.EditEvent;
import csheets.ui.ctrl.EditListener;
import csheets.ui.ctrl.SelectionEvent;
import csheets.ui.ctrl.SelectionListener;
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

    public RealTimeCollaboration() {
	super("Real Time Collaboration");
    }

    private RtcEventsResponder getResponder(UIController uiController) {
	if (responder == null) {
	    responder = new RtcEventsResponder(uiController, this);
	}
	return responder;
    }

    public void updateUsersList() {
	sidebar.updateUsersList(communicator.getConnectedUsers());
    }

    public ClientInfo createServer(ClientInfo client,
	    UIController uiController, Address[] range) throws IOException {
	ServerInterface server = new ServerInterface(client, uiController);
	identity = server.getServerInfo();
	server.setListener(getResponder(uiController));
	communicator = server;
	return identity;
    }

    public ClientInfo createClient(ClientInfo client, String ipAddress,
	    UIController uiController) throws IOException {
	identity = client;
	communicator = new ClientInterface(ipAddress, identity);
	communicator.setListener(getResponder(uiController));
	return identity;
    }

    @Override
    public UIExtension getUIExtension(final UIController uiController) {
	uiController.addEditListener(new EditListener() {
	    @Override
	    public void workbookModified(EditEvent event) {
		if (communicator != null) {
		    communicator.onCellChanged(null, new RemoteCell(
			    uiController.getActiveCell()));
		}
	    }
	});
	uiController.addSelectionListener(new SelectionListener() {
	    @Override
	    public void selectionChanged(SelectionEvent event) {
		if (uiController.getActiveCell() == null) {
		    return;
		}
		if (communicator != null) {
		    communicator.onCellSelected(null, uiController
			    .getActiveCell().getAddress());
		}
	    }
	});
	return new UIExtension(this, uiController) {
	    @Override
	    public CellDecorator getCellDecorator() {
		if (cellDecorator == null) {
		    cellDecorator = new RtcCellDecorator();
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
			}
		    });
		    connectAction = new ConnectAction(
			    RealTimeCollaboration.this, uiController);
		    connectAction.setListener(new DataListener() {
			@Override
			public void onSendData(ClientInfo info, String address) {
			}
		    });
		    sidebar = new RtcSidebar(RealTimeCollaboration.this,
			    uiController, shareAction, connectAction);
		    sidebar.setName("Real Time Collaboration");
		}
		return sidebar;
	    }
	};
    }
}

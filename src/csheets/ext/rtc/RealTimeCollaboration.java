package csheets.ext.rtc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import csheets.ext.Extension;
import csheets.ext.rtc.ui.ClientsListAdapter;
import csheets.ext.rtc.ui.RtcSidebar;
import csheets.ui.ctrl.EditEvent;
import csheets.ui.ctrl.EditListener;
import csheets.ui.ctrl.SelectionEvent;
import csheets.ui.ctrl.SelectionListener;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.CellDecorator;
import csheets.ui.ext.SideBarAction;
import csheets.ui.ext.UIExtension;

public class RealTimeCollaboration extends Extension {
    RtcCommunicator communicator;
    ClientInfo identity;

    RtcCellDecorator cellDecorator;

    ClientsListAdapter adapter;
    RtcSidebar sidebar;

    public RealTimeCollaboration() {
	super("Real Time Collaboration");
    }

    public ClientInfo createServer(ClientInfo client, UIController uiController)
	    throws IOException {
	ServerInterface server = new ServerInterface(client);
	identity = server.getServerInfo();
	server.setListener(new RtcEventsResponder(uiController));
	communicator = server;
	return identity;
    }

    public ClientInfo createClient(ClientInfo client, String ipAddress,
	    UIController uiController) throws IOException {
	identity = client;
	communicator = new ClientInterface(ipAddress, identity);
	communicator.setListener(new RtcEventsResponder(uiController));
	return identity;
    }

    @Override
    public UIExtension getUIExtension(final UIController uiController) {
	uiController.addEditListener(new EditListener() {
	    @Override
	    public void workbookModified(EditEvent event) {
		if (communicator != null) {
		    communicator.onCellChanged(null,
			    uiController.getActiveCell());
		}
	    }
	});
	uiController.addSelectionListener(new SelectionListener() {
	    @Override
	    public void selectionChanged(SelectionEvent event) {
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
		    sidebar = new RtcSidebar(RealTimeCollaboration.this,
			    uiController);
		    sidebar.setName("Real Time Collaboration");
		}
		return sidebar;
	    }
	};
    }
}

package csheets.ext.rtc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import csheets.ext.Extension;
import csheets.ui.ctrl.EditEvent;
import csheets.ui.ctrl.EditListener;
import csheets.ui.ctrl.SelectionEvent;
import csheets.ui.ctrl.SelectionListener;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.CellDecorator;
import csheets.ui.ext.UIExtension;

public class RealTimeCollaboration extends Extension {
    private class ClientsListAdapter implements ListModel<String> {
	ListDataListener listener;

	@Override
	public int getSize() {
	    return 0; // clients.size();
	}

	@Override
	public String getElementAt(int index) {
	    return ""; // clients.get(index).getInfo().getName();
	}

	@Override
	public void addListDataListener(ListDataListener l) {
	    listener = l;
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
	}

	public void update() {
	    if (listener != null) {
		listener.contentsChanged(new ListDataEvent(this,
			ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
	    }
	}
    }

    RtcInterface communicator;
    ClientInfo identity;

    RtcCellDecorator cellDecorator;

    ClientsListAdapter adapter;

    // ArrayList<Client> clients = new ArrayList<Client>();
    // private ServerSocket server;

    public RealTimeCollaboration() {
	super("Real Time Collaboration");
    }

    @Override
    public UIExtension getUIExtension(final UIController uiController) {
	uiController.addEditListener(new EditListener() {
	    @Override
	    public void workbookModified(EditEvent event) {
		if (communicator != null) {
		    communicator.onCellChanged(identity,
			    uiController.getActiveCell());
		}
	    }
	});
	uiController.addSelectionListener(new SelectionListener() {
	    @Override
	    public void selectionChanged(SelectionEvent event) {
		if (communicator != null) {
		    communicator.onCellSelected(identity, uiController
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
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		final JButton bShare = new JButton("Share");
		bShare.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		panel.add(bShare);
		bShare.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
			try {
			    ServerInterface server = new ServerInterface(
				    new ClientInfo("Servidor"));
			    identity = server.getServerInfo();
			    server.setListener(new RtcEventsResponder(
				    uiController));
			    communicator = server;
			} catch (IOException e1) {
			    e1.printStackTrace();
			}
		    }
		});
		final JButton bConnect = new JButton("Connect");
		bConnect.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		panel.add(bConnect);
		bConnect.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
			try {
			    identity = new ClientInfo("Gil");
			    communicator = new ClientInterface("localhost",
				    identity);
			    communicator.setListener(new RtcEventsResponder(
				    uiController));
			} catch (UnknownHostException e1) {
			    e1.printStackTrace();
			} catch (IOException e1) {
			    e1.printStackTrace();
			}
		    }
		});
		adapter = new ClientsListAdapter();
		JList<String> list = new JList<String>(adapter);
		panel.add(list);
		return panel;
	    }
	};
    }
}

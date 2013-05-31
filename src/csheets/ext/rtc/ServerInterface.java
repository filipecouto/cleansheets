package csheets.ext.rtc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import csheets.core.Address;
import csheets.core.Spreadsheet;
import csheets.ext.rtc.messages.RemoteCell;
import csheets.ext.rtc.messages.RemoteSpreadsheet;
import csheets.ext.rtc.messages.RemoteWorkbook;
import csheets.ui.ctrl.UIController;

public class ServerInterface implements RtcCommunicator {
    private ClientInfo info;
    private ServerSocket server;
    private RtcListener listener;

    private boolean connected;

    private RtcShareProperties properties;

    private UIController uiController;

    private ArrayList<Client> clients;

    public ServerInterface(ClientInfo clientInfo,
	    RtcShareProperties properties, UIController uiController) {
	this.properties = properties;
	this.uiController = uiController;
	this.info = clientInfo;
	clients = new ArrayList<Client>();
    }

    @Override
    public void start() {
	try {
	    server = new ServerSocket(PORT);
	    connected = true;
	    info.addConnectionInfo(server.getInetAddress());
	    listener.onConnected(info);
	    new Thread(new Runnable() {
		public void run() {
		    try {
			Socket socket;
			while ((socket = server.accept()) != null) {
			    onClientConnected(socket);
			}
			server.close();
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
	    }).start();
	} catch (IOException e) {
	    e.printStackTrace();
	    connected = false;
	    listener.onDisconnected(info);
	}
    }

    public RemoteCell[] getCellsToSend(int spreadsheet, Address[] range) {
	int xOffset = range[0].getColumn();
	int yOffset = range[0].getRow();
	int columnCount = range[1].getColumn() - xOffset + 1;
	int rowCount = range[1].getRow() - yOffset + 1;
	int i = 0;

	RemoteCell[] cells = new RemoteCell[columnCount * rowCount];
	final Spreadsheet sheet = uiController.getActiveWorkbook()
		.getSpreadsheet(spreadsheet);
	Address address;

	for (int y = yOffset; y < yOffset + rowCount; y++) {
	    for (int x = xOffset; x < xOffset + columnCount; x++) {
		address = new Address(x, y);
		if (properties.isInsideRange(address)) {
		    cells[i++] = new RemoteCell(sheet.getCell(address));
		}
	    }
	}

	return cells;
    }

    public RemoteWorkbook getWorkbookToSend() {
	return new RemoteWorkbook(uiController.getActiveWorkbook());
    }

    public RemoteSpreadsheet getSpreadsheetToSend(int index) {
	return new RemoteSpreadsheet(uiController.getActiveWorkbook()
		.getSpreadsheet(index), index);
    }

    public ClientInfo getServerInfo() {
	return info;
    }

    @Override
    public synchronized ClientInfo[] getConnectedUsers() {
	synchronized (clients) {
	    final int len = clients.size();
	    ClientInfo[] info = new ClientInfo[len + 1];
	    for (int i = 0; i < len; i++) {
		info[i] = clients.get(i).getInfo();
	    }
	    info[len] = this.info;
	    return info;
	}
    }

    public void onClientConnected(Socket client) {
	try {
	    Client newClient = new Client(this, client);
	    synchronized (clients) {
		clients.add(newClient);
		newClient.run();
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void onConnected(ClientInfo client) {
	if (client == null) {
	    client = info;
	} else {
	    listener.onConnected(client);
	}
	synchronized (clients) {
	    for (Client c : clients) {
		c.onConnected(client);
	    }
	}
    }

    @Override
    public void onCellSelected(ClientInfo source, Address address) {
	if (source == null) {
	    source = info;
	} else {
	    listener.onCellSelected(source, address);
	}
	synchronized (clients) {
	    for (Client c : clients) {
		c.onCellSelected(source, address);
	    }
	}
    }

    @Override
    public void onCellChanged(ClientInfo source, RemoteCell cell) {
	if (source == null) {
	    source = info;
	} else {
	    listener.onCellChanged(source, cell);
	}
	synchronized (clients) {
	    for (Client c : clients) {
		c.onCellChanged(source, cell);
	    }
	}
    }

    @Override
    public void onDisconnected(ClientInfo client) {
	if (client == null) {
	    client = info;
	} else {
	    listener.onDisconnected(client);
	}
	synchronized (clients) {
	    for (Client c : clients) {
		c.onDisconnected(client);
	    }
	}
    }

    private void removeUser(ClientInfo id) {
	synchronized (clients) {
	    final int len = clients.size();
	    for (int i = 0; i < len; i++) {
		if (clients.get(i).getInfo() == id) {
		    clients.remove(i);
		    return;
		}
	    }
	}
    }

    @Override
    public void onUserAction(ClientInfo source, Object action) {
	if (action instanceof Boolean) {
	    if ((Boolean) action == false) {
		removeUser(source);
	    }
	}
	listener.onUserAction(source, action);
	synchronized (clients) {
	    for (Client c : clients) {
		c.onUserAction(source, action);
	    }
	}
    }

    @Override
    public void setListener(RtcListener listener) {
	this.listener = listener;
    }

    @Override
    public RtcShareProperties getShareProperties() {
	return properties;
    }

    @Override
    public boolean isConnected() {
	return connected;
    }
}

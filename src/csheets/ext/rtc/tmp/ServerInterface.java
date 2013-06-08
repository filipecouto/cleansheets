package csheets.ext.rtc.tmp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import csheets.core.Address;
import csheets.core.Spreadsheet;
import csheets.ext.rtc.Client;
import csheets.ext.rtc.ClientInfo;
import csheets.ext.rtc.MulticastServer;
import csheets.ext.rtc.RtcCommunicator;
import csheets.ext.rtc.RtcListener;
import csheets.ext.rtc.messages.RemoteCell;
import csheets.ext.rtc.messages.RemoteSpreadsheet;
import csheets.ext.rtc.messages.RemoteWorkbook;
import csheets.ext.rtc.ui.RtcSharingProperties;
import csheets.ui.ctrl.UIController;

/**
 * This class creates the server, listens to incoming connections and sends them
 * to a Client instance in order to manage them.
 * 
 * @author gil_1110484; Rita Nogueira
 */
public class ServerInterface implements RtcCommunicator {
    private ClientInfo info;
    private ServerSocket server;
    private RtcListener listener;

    private boolean connected;
    private int port;

    private RtcSharingProperties properties;

    private UIController uiController;

    private ArrayList<Client> clients;
    private MulticastServer multserver;

    public ServerInterface(ClientInfo clientInfo, int port,
	    RtcSharingProperties properties, UIController uiController) {
	this.properties = properties;
	this.uiController = uiController;
	this.info = clientInfo;
	this.port = port;
	clients = new ArrayList<Client>();
    }

    @Override
    public void start() {
	multserver = new MulticastServer(port, info.getShareName(), clients.size());
	try {
	    server = new ServerSocket(port);
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
		    } catch (IOException e) {
			// server was disconnected
		    }
		}
	    }).start();
	} catch (IOException e) {
	    connected = false;
	    listener.onConnectionFailed(e);
	}
    }

    /**
     * Closes the server and warns the listener
     */
    private void close() {
	try {
	    connected = false;
	    server.close();
	    multserver.stop();
	    listener.onDisconnected(info);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Creates an array of RemoteCells to send to a client by asking
     * RtcSharingOptions whether that cell can be shared or not
     * 
     * @param spreadsheet
     *            the index of the requested spreadsheet
     * @param range
     *            the range of the cells to get
     * @return the array with the cells ready to send
     */
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

    /**
     * Instantiates a RemoteWorkbook to send to clients
     * 
     * @return the RemoteWorkbook ready to send
     */
    public RemoteWorkbook getWorkbookToSend() {
	return new RemoteWorkbook(uiController.getActiveWorkbook());
    }

    /**
     * Instantiates a RemoteSpreadsheet to send to clients, it may not contain
     * any filled in cell
     * 
     * @param index
     *            the index of the requested spreadsheet
     * @return the RemoteSpreadsheet ready to send
     */
    public RemoteSpreadsheet getSpreadsheetToSend(int index) {
	return new RemoteSpreadsheet(uiController.getActiveWorkbook()
		.getSpreadsheet(index), index);
    }

    /**
     * Gets the identity of the user who started this server
     * 
     * @return his/her identity
     */
    public ClientInfo getServerInfo() {
	return info;
    }

    @Override
    public synchronized ClientInfo[] getConnectedUsers() {
	if (connected) {
	    synchronized (clients) {
		final int len = clients.size();
		ClientInfo[] info = new ClientInfo[len + 1];
		for (int i = 0; i < len; i++) {
		    info[i] = clients.get(i).getInfo();
		}
		info[len] = this.info;

		return info;
	    }
	} else {
	    return null;
	}
    }

    /**
     * Called by the server when a user attempts to connect to the server,
     * creates a Client instance to communicate with it and set the connection
     * up
     * 
     * @param client
     *            the socket that connects to the client
     */
    private void onClientConnected(Socket client) {
	try {
	    Client newClient = new Client(this, client);
	    synchronized (clients) {
		clients.add(newClient);
		newClient.run();
		multserver.serverNrClient(clients.size());
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
	try {
	    connected = false;
	    ArrayList<Client> auxClients;
	    synchronized (clients) {
		auxClients = new ArrayList<Client>(clients.size());
		for (Client c : clients) {
		    auxClients.add(c);
		}
	    }
	    if (auxClients.size() == 0) {
		close();
	    } else {
		for (Client c : auxClients) {
		    c.onDisconnected(client);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Called to remove a Client from the list when a user disconnects
     * 
     * @param id
     *            its identity
     */
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

		synchronized (clients) {
		    if (!connected && clients.size() == 0) {
			close();
		    }
		}
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
    public RtcSharingProperties getSharingProperties() {
	return properties;
    }

    @Override
    public boolean isConnected() {
	return connected;
    }
    
    public String toString() {
	return info.getShareName();
    }
}

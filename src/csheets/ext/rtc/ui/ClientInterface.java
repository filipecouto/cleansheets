package csheets.ext.rtc.ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import csheets.core.Address;
import csheets.core.Workbook;
import csheets.ext.rtc.Communicator;
import csheets.ext.rtc.RtcCommunicator;
import csheets.ext.rtc.RtcListener;
import csheets.ext.rtc.messages.MessageTypes;
import csheets.ext.rtc.messages.RemoteCell;
import csheets.ext.rtc.messages.RemoteSpreadsheet;
import csheets.ext.rtc.messages.RemoteWorkbook;
import csheets.ext.rtc.messages.RtcMessage;

/**
 * This class will be the client-side bridge of the connection (therefore the
 * name "Interface")
 * 
 * @author gil_1110484
 */
public class ClientInterface extends Communicator implements RtcCommunicator {
    private ClientInfo info;
    private Socket server;
    private RtcListener listener;
    private RtcSharingProperties properties;

    private boolean connected = false;
    private String address;
    private int port;

    private Workbook workbook;

    private ClientInfo[] otherUsers;

    public ClientInterface(String address, int port, ClientInfo clientInfo)
	    throws UnknownHostException, IOException {
	this.address = address;
	this.port = port;
	info = clientInfo;
    }

    @Override
    public void start() {
	try {
	    server = new Socket(address, port);
	    setSocket(server);
	    info.addConnectionInfo(server);
	    new Thread(new Runnable() {
		@Override
		public void run() {
		    try {
			RtcMessage message;

			// the server needs to know who we are
			sendMessage(new RtcMessage(info.getAddress(),
				MessageTypes.info, info));

			// wait for the list of already connected users
			if ((message = getMessageOrFail(MessageTypes.infoList)) != null) {
			    Serializable[] response = (Serializable[]) message
				    .getArgument();
			    otherUsers = (ClientInfo[]) response[0];
			    properties = (RtcSharingProperties) response[1];
			    listener.onUserAction(info, null);
			} else {
			    return;
			}

			if ((message = getMessageOrFail(MessageTypes.workbook)) != null) {
			    workbook = ((RemoteWorkbook) message.getArgument())
				    .getWorkbook();
			    if (workbook.getSpreadsheetCount() > 0) {
				sendMessage(new RtcMessage(info.getAddress(),
					MessageTypes.getSpreadsheet, 0));
			    } else {
				// TODO support more than one spreadsheet,
				// protocol
				// and interfaces are ready for it
				// TODO what if the workbook is empty?
			    }
			} else {
			    return;
			}

			connected = true;
			listener.onConnected(info);

			while (true) {
			    message = getMessage();
			    if (message == null) {
				// server disconnected
				close();
			    } else {
				switch (message.getMessageType()) {
				case eventCellChanged:
				    final RemoteCell c = (RemoteCell) message
					    .getArgument();
				    listener.onCellChanged(null, c);
				    break;
				case eventCellSelected:
				    final Address a = (Address) message
					    .getArgument();
				    listener.onCellSelected(null, a);
				    break;
				case cells:
				    final RemoteCell[] cells = (RemoteCell[]) message
					    .getArgument();
				    listener.onCellsReceived(null, cells);
				    break;
				case spreadsheet:
				    RemoteSpreadsheet sheet = (RemoteSpreadsheet) message
					    .getArgument();
				    sheet.getSpreadsheet(workbook);
				    sendMessage(new RtcMessage(
					    info.getAddress(),
					    MessageTypes.getCells,
					    new Address[] {
						    new Address(0, 0),
						    new Address(sheet
							    .getColumnCount(),
							    sheet.getRowCount()) }));
				    listener.onWorkbookReceived(info, workbook);
				    break;
				case infoList:
				    otherUsers = (ClientInfo[]) message
					    .getArgument();
				    listener.onUserAction(info, null);
				    break;
				case disconnect:
				    close();
				    return;
				}
			    }
			}
		    } catch (IOException e) {
			// server disconnected
			close();
		    } catch (ClassNotFoundException e) {
			e.printStackTrace();
			close();
		    }
		}
	    }).start();
	} catch (UnknownHostException e) {
	    connected = false;
	    listener.onConnectionFailed(e);
	} catch (SocketException e) {
	    connected = false;
	    listener.onConnectionFailed(e);
	} catch (IOException e) {
	    e.printStackTrace();
	    connected = false;
	    listener.onDisconnected(info);
	}
    }

    @Override
    public void onConnected(ClientInfo client) {

    }

    @Override
    public void onCellSelected(ClientInfo source, Address address) {
	sendMessage(new RtcMessage(server.getInetAddress(),
		MessageTypes.eventCellSelected, address));
    }

    @Override
    public void onCellChanged(ClientInfo source, RemoteCell cell) {
	sendMessage(new RtcMessage(server.getInetAddress(),
		MessageTypes.eventCellChanged, cell));
    }

    @Override
    public void onDisconnected(ClientInfo client) {
	close();
    }

    @Override
    public void setListener(RtcListener listener) {
	this.listener = listener;
    }

    @Override
    public ClientInfo[] getConnectedUsers() {
	return connected ? otherUsers : null;
    }

    @Override
    public void onUserAction(ClientInfo source, Object action) {
	listener.onUserAction(source, action);
    }

    @Override
    protected void close() {
	try {
	    connected = false;
	    server.close();
	    listener.onDisconnected(null);
	} catch (IOException e) {
	    e.printStackTrace();
	}
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

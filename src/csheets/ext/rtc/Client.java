package csheets.ext.rtc;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

import csheets.core.Address;
import csheets.ext.rtc.messages.RemoteCell;

public class Client extends Communicator implements RtcInterface {
    private ClientInfo info;
    private ServerInterface server;
    private Socket client;

    public Client(ServerInterface server, Socket client) throws IOException {
	super(client);
	this.server = server;
	this.client = client;
	info = new ClientInfo(client.getInetAddress());
    }

    public void run() {
	new Thread(new Runnable() {
	    @Override
	    public void run() {
		try {
		    RtcMessage message;

		    // wait for its identity
		    if ((message = getMessageOrFail(MessageTypes.info)) != null) {
			info = (ClientInfo) message.getArgument();
		    } else {
			return;
		    }

		    // let this client know who is already connected
		    sendMessage(new RtcMessage(server.getServerInfo()
			    .getAddress(), MessageTypes.infoList,
			    new Serializable[] { server.getConnectedUsers(),
				    server.getShareProperties() }));

		    // send our workbook
		    sendMessage(new RtcMessage(server.getServerInfo()
			    .getAddress(), MessageTypes.workbook, server
			    .getWorkbookToSend()));

		    server.onUserAction(info, null);

		    while (true) {
			message = getMessage();
			switch (message.getMessage()) {
			case eventCellChanged:
			    RemoteCell c = (RemoteCell) message.getArgument();
			    server.onCellChanged(info, c);
			    break;
			case eventCellSelected:
			    Address a = (Address) message.getArgument();
			    server.onCellSelected(info, a);
			    break;
			case getSpreadsheet:
			    sendMessage(new RtcMessage(
				    server.getServerInfo().getAddress(),
				    MessageTypes.spreadsheet,
				    server.getSpreadsheetToSend((Integer) message
					    .getArgument())));
			    break;
			case getCells:
			    Address[] range = (Address[]) message.getArgument();
			    sendMessage(new RtcMessage(server.getServerInfo()
				    .getAddress(), MessageTypes.cells, server
				    .getCellsToSend(0, range)));
			    break;
			case disconnect:
			    close();
			    return;
			}
		    }
		} catch (IOException e) {
		    // Client disconnected
		    close();
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		    close();
		}
	    }
	}).start();
    }

    public boolean isSameClient(ClientInfo info) {
	return this.info.isSameIP(info.getAddress());
    }

    public ClientInfo getInfo() {
	return info;
    }

    @Override
    public void close() {
	try {
	    client.close();
	    server.onUserAction(info, false);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void onConnected(ClientInfo client) {
    }

    @Override
    public void onCellSelected(ClientInfo source, Address address) {
	sendMessage(new RtcMessage(source.getAddress(),
		MessageTypes.eventCellSelected, address));
    }

    @Override
    public void onCellChanged(ClientInfo source, RemoteCell cell) {
	sendMessage(new RtcMessage(source.getAddress(),
		MessageTypes.eventCellChanged, cell));
    }

    @Override
    public void onUserAction(ClientInfo source, Object action) {
	sendMessage(new RtcMessage(source.getAddress(), MessageTypes.infoList,
		server.getConnectedUsers()));
    }

    @Override
    public void onDisconnected(ClientInfo client) {
	sendMessage(new RtcMessage(info.getAddress(), MessageTypes.disconnect,
		null));
	close();
    }
}

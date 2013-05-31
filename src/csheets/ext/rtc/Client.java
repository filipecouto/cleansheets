package csheets.ext.rtc;

import java.io.IOException;
import java.net.Socket;

import csheets.core.Address;
import csheets.core.Cell;
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

		    // let this client know who is already connected
		    sendMessage(new RtcMessage(server.getServerInfo()
			    .getAddress(), MessageTypes.infoList, server
			    .getConnectedUsers()));

		    // wait for its identity
		    if ((message = getMessageOrFail(MessageTypes.info)) != null) {
			info = (ClientInfo) message.getArgument();
			// System.out.println(info.getName() +
			// " just connected");
		    } else {
			return;
		    }

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
			    server.onUserAction(info, null);
			    client.close();
			    return;
			}
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
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
	    onDisconnected(info);
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
    public void onCellChanged(ClientInfo source, Cell cell) {
	sendMessage(new RtcMessage(source.getAddress(),
		MessageTypes.eventCellChanged, new RemoteCell(cell)));
    }

    @Override
    public void onUserAction(ClientInfo source, Object action) {
	sendMessage(new RtcMessage(source.getAddress(), MessageTypes.infoList,
		server.getConnectedUsers()));
    }

    @Override
    public void onDisconnected(ClientInfo client) {
    }
}

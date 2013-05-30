package csheets.ext.rtc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.Workbook;
import csheets.ext.rtc.messages.RemoteCell;
import csheets.ext.rtc.messages.RemoteWorkbook;

/**
 * This class will be the client-side bridge of the connection (therefore the
 * name "Interface")
 * 
 * @author gil_1110484
 */
public class ClientInterface implements RtcCommunicator {
    private ClientInfo info;
    private Socket server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private RtcListener listener;

    private ClientInfo[] otherUsers;

    public ClientInterface(String address, ClientInfo clientInfo)
	    throws UnknownHostException, IOException {
	server = new Socket(address, PORT);
	info = clientInfo;
	out = new ObjectOutputStream(server.getOutputStream());

	new Thread(new Runnable() {
	    @Override
	    public void run() {
		try {
		    RtcMessage message;
		    in = new ObjectInputStream(server.getInputStream());

		    // wait for the list of already connected users
		    if ((message = getMessageOrFail(MessageTypes.infoList)) != null) {
			otherUsers = (ClientInfo[]) message.getArgument();
			System.out.println("There are " + otherUsers.length + " users connected");
		    } else {
			return;
		    }

		    // the server needs to know who we are
		    sendMessage(new RtcMessage(info.getAddress(),
			    MessageTypes.info, info));

		    if ((message = getMessageOrFail(MessageTypes.workbook)) != null) {
			Workbook wb = ((RemoteWorkbook) message.getArgument())
				.getWorkbook();
			System.out.println("The shared workbook has " + otherUsers.length + " spreadsheets");
			if(wb.getSpreadsheetCount() > 0) {
			    sendMessage(new RtcMessage(info.getAddress(), MessageTypes.getSpreadsheet, 0));
			}
		    } else {
			return;
		    }

		    while (true) {
			message = getMessage();
			switch (message.getMessage()) {
			case eventCellChanged:
			    RemoteCell c = (RemoteCell) message.getArgument();
			    listener.onCellChanged(null, c);
			    break;
			case eventCellSelected:
			    Address a = (Address) message.getArgument();
			    listener.onCellSelected(null, a);
			    break;
			case disconnect:
			    listener.onDisconnected(info);
			    server.close();
			    return;
			}
		    }

		    // in.close();
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		}
	    }
	}).start();
    }

    private RtcMessage getMessage() throws IOException, ClassNotFoundException {
	Object o = in.readObject();
	return (RtcMessage) o;
    }

    private RtcMessage getMessageOrFail(MessageTypes type) throws IOException,
	    ClassNotFoundException {
	Object o = in.readObject();
	final RtcMessage message = (RtcMessage) o;
	if (message.getMessage() == type) {
	    return message;
	} else {
	    // something is wrong, we better disconnect
	    listener.onDisconnected(null);
	    server.close();
	    return null;
	}
    }

    private void sendMessage(RtcMessage message) {
	try {
	    out.writeObject(message);
	} catch (IOException e) {
	    e.printStackTrace();
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
    public void onCellChanged(ClientInfo source, Cell cell) {
	sendMessage(new RtcMessage(server.getInetAddress(),
		MessageTypes.eventCellChanged, new RemoteCell(cell)));
    }

    @Override
    public void onDisconnected(ClientInfo client) {
    }

    @Override
    public void setListener(RtcListener listener) {
	this.listener = listener;
    }

    @Override
    public ClientInfo[] getConnectedUsers() {
	return otherUsers;
    }
}

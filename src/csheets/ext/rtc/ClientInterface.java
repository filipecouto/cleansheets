package csheets.ext.rtc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import csheets.core.Address;
import csheets.core.Cell;

/**
 * This class will be the client-side bridge of the connection (therefore the
 * name "Interface")
 * 
 * @author gil_1110484
 */
public class ClientInterface implements RtcInterface {
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
		    in = new ObjectInputStream(server.getInputStream());
		    RtcMessage message;
		    message = getMessage();
		    if (message.getMessage() == MessageTypes.infoList) {
			otherUsers = (ClientInfo[]) message.getArgument();
		    } else {
			// ERROR server didn't send what it should,
			// diconnecting...
			listener.onDisconnected(null);
			server.close();
		    }
		    sendMessage(new RtcMessage(info.getAddress(),
			    MessageTypes.info, info));
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

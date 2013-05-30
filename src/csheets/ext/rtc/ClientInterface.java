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
    private RtcListener listener;

    public ClientInterface(String address, ClientInfo clientInfo)
	    throws UnknownHostException, IOException {
	server = new Socket(address, PORT);
	info = clientInfo;
	out = new ObjectOutputStream(server.getOutputStream());

	new Thread(new Runnable() {
	    @Override
	    public void run() {
		try {
		    ObjectInputStream in = new ObjectInputStream(
			    server.getInputStream());
		    Object o;
		    RtcMessage message;
		    while (true) {
			o = in.readObject();
			message = (RtcMessage) o;
			switch (message.getMessage()) {
			case RtcMessage.MESSAGE_EVENT_CELLCHANGED:
			    RemoteCell c = (RemoteCell) message.getArgument();
			    listener.onCellChanged(null, c);
			    break;
			case RtcMessage.MESSAGE_EVENT_CELLSELECTED:
			    Address a = (Address) message.getArgument();
			    listener.onCellSelected(null, a);
			    break;
			case RtcMessage.MESSAGE_DISCONNECT:
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
		RtcMessage.MESSAGE_EVENT_CELLSELECTED, address));
    }

    @Override
    public void onCellChanged(ClientInfo source, Cell cell) {
	sendMessage(new RtcMessage(server.getInetAddress(),
		RtcMessage.MESSAGE_EVENT_CELLCHANGED, new RemoteCell(cell)));
    }

    @Override
    public void onDisconnected(ClientInfo client) {
    }

    @Override
    public void setListener(RtcListener listener) {
	this.listener = listener;
    }
}

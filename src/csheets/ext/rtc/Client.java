package csheets.ext.rtc;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import csheets.core.Address;
import csheets.core.Cell;

public class Client implements RtcInterface {
    private ClientInfo info;
    private Socket client;
    private ObjectOutputStream out;
    private RtcListener listener;

    public Client(Socket client) {
	this.client = client;
	info = new ClientInfo(client.getInetAddress());
	try {
	    out = new ObjectOutputStream(client.getOutputStream());
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void run() {
	new Thread(new Runnable() {
	    @Override
	    public void run() {
		try {
		    InputStream in = client.getInputStream();
		    ObjectInputStream oin = new ObjectInputStream(in);
		    Object o;
		    RtcMessage message;
		    while (true) {
			o = oin.readObject();
			message = (RtcMessage) o;
			switch (message.getMessage()) {
			case RtcMessage.MESSAGE_EVENT_CELLCHANGED:
			    RemoteCell c = (RemoteCell) message.getArgument();
			    listener.onCellChanged(info, c);
			    break;
			case RtcMessage.MESSAGE_EVENT_CELLSELECTED:
			    Address a = (Address) message.getArgument();
			    listener.onCellSelected(info, a);
			    break;
			case RtcMessage.MESSAGE_DISCONNECT:
			    listener.onDisconnected(info);
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

    private void sendMessage(RtcMessage message) {
	try {
	    out.writeObject(message);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void close() {
	try {
	    out.close();
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
		RtcMessage.MESSAGE_EVENT_CELLSELECTED, address));
    }

    @Override
    public void onCellChanged(ClientInfo source, Cell cell) {
	sendMessage(new RtcMessage(source.getAddress(),
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

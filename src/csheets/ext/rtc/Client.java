package csheets.ext.rtc;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import csheets.core.Address;
import csheets.core.Cell;

public class Client implements RtcListener {
    private ClientInfo info;
    private ServerInterface server;
    private Socket client;
    private ObjectOutputStream out;
    //private RtcListener listener;

    public Client(ServerInterface server, Socket client) {
	this.server = server;
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
		    sendMessage(new RtcMessage(server.getServerInfo()
			    .getAddress(), MessageTypes.infoList,
			    server.getConnectedUsers()));
		    Object o;
		    RtcMessage message;
		    while (true) {
			o = oin.readObject();
			message = (RtcMessage) o;
			switch (message.getMessage()) {
			case eventCellChanged:
			    RemoteCell c = (RemoteCell) message.getArgument();
			    server.onCellChanged(info, c);
			    break;
			case eventCellSelected:
			    Address a = (Address) message.getArgument();
			    server.onCellSelected(info, a);
			    break;
			case disconnect:
			    server.onDisconnected(info);
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
		MessageTypes.eventCellSelected, address));
    }

    @Override
    public void onCellChanged(ClientInfo source, Cell cell) {
	sendMessage(new RtcMessage(source.getAddress(),
		MessageTypes.eventCellChanged, new RemoteCell(cell)));
    }

    @Override
    public void onDisconnected(ClientInfo client) {
    }

//    @Override
//    public void setListener(RtcListener listener) {
//	this.listener = listener;
//    }
}

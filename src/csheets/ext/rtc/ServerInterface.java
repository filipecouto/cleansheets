package csheets.ext.rtc;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import csheets.core.Address;
import csheets.core.Cell;

public class ServerInterface implements RtcInterface {
    private ClientInfo info;
    private ServerSocket server;
    private ObjectOutputStream out;
    private RtcListener listener;

    private ArrayList<Client> clients;

    public ServerInterface(ClientInfo clientInfo) throws IOException {
	clients = new ArrayList<Client>();
	server = new ServerSocket(PORT);
	info = new ClientInfo(server.getInetAddress());
	info.addRemoteInfo(clientInfo);
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
    }

    public ClientInfo getServerInfo() {
	return info;
    }

    @Override
    public synchronized ClientInfo[] getConnectedUsers() {
	synchronized (clients) {
	    final int len = clients.size();
	    ClientInfo[] info = new ClientInfo[len];
	    for (int i = 0; i < len; i++) {
		info[i] = clients.get(i).getInfo();
	    }
	    return info;
	}
    }

    public void onClientConnected(Socket client) {
	final Client newClient = new Client(this, client);
	// newClient.setListener(clientsListener);
	synchronized (clients) {
	    clients.add(newClient);
	    newClient.run();
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
    public void onCellChanged(ClientInfo source, Cell cell) {
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

    @Override
    public void setListener(RtcListener listener) {
	this.listener = listener;
    }

//    private RtcListener clientsListener = new RtcListener() {
//	@Override
//	public void onConnected(ClientInfo client) {
//	    listener.onConnected(client);
//	    synchronized (clients) {
//		for (Client c : clients) {
//		    // if (!c.isSameClient(client)) {
//		    c.onConnected(client);
//		    // }
//		}
//	    }
//	}
//
//	@Override
//	public void onCellSelected(ClientInfo source, Address address) {
//	    listener.onCellSelected(source, address);
//	    synchronized (clients) {
//		for (Client c : clients) {
//		    // if (!c.isSameClient(source)) {
//		    c.onCellSelected(source, address);
//		    // }
//		}
//	    }
//	}
//
//	@Override
//	public void onCellChanged(ClientInfo source, Cell cell) {
//	    listener.onCellChanged(source, cell);
//	    synchronized (clients) {
//		for (Client c : clients) {
//		    // if (!c.isSameClient(source)) {
//		    c.onCellChanged(source, cell);
//		    // }
//		}
//	    }
//	}
//
//	@Override
//	public void onDisconnected(ClientInfo client) {
//	    listener.onDisconnected(client);
//	    synchronized (clients) {
//		for (Client c : clients) {
//		    // if (!c.isSameClient(client)) {
//		    c.onDisconnected(client);
//		    // }
//		}
//	    }
//	}
//    };
}

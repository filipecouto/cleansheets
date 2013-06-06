package csheets.ext.rtc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MulticastServer {
    private DatagramSocket socket;
    private DatagramPacket outPacket;
    private Runnable searcher;
    private String serverName;
    private int serverNrClients;

    public MulticastServer(final int port, final String username,
	    final int nrClients) {
	this.serverName = username;
	this.serverNrClients = nrClients;
	searcher = new Runnable() {

	    @Override
	    public void run() {
		try {
		    socket = new DatagramSocket();
		    String info = setMsg();
		    while (true) {
			byte[] outBuf = info.getBytes();

			// Send to multicast IP address and port
			InetAddress address = InetAddress
				.getByName("224.2.2.3");
			outPacket = new DatagramPacket(outBuf, outBuf.length,
				address, port);
			socket.send(outPacket);
			try {
			    Thread.sleep(1000);
			} catch (InterruptedException ie) {
			}
		    }
		} catch (IOException ioe) {
		    System.out.println(ioe);
		}
	    }

	};
	startSearching();
    }

    private String setMsg() {
	return (serverNrClients + ";" + serverName);
    }

    public void serverNrClient(int nrClient) throws UnknownHostException,
	    IOException {
	stop();
	this.serverNrClients = nrClient;
	startSearching();
    }

    private void startSearching() {
	new Thread(searcher).start();
    }

    public void stop() {
	if (socket != null && !socket.isClosed()) {
	    socket.close();
	}
    }
}

package csheets.ext.rtc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Random;

public class MulticastClient {
    private MulticastSocket socket;
    private DatagramPacket inPacket;
    private byte[] inBuf = new byte[256];
    private int port = 33334;
    private Runnable searcher;

    public MulticastClient(final OnShareFoundListener onShareFoundListener) {

	searcher = new Runnable() {
	    @Override
	    public void run() {
		try {
		    // Prepare to join multicast group
		    createConnection();
		    while (true) {
			socket.receive(inPacket);
			String msg = new String(inBuf, 0, inPacket.getLength());
			String[] info = splitMsg(msg);
			onShareFoundListener
				.onShareFound(new ServerInformation(info[1],
					Integer.parseInt(info[0]), inPacket
						.getAddress()));
		    }
		} catch (IOException ioe) {
		}
	    }
	};
	startSearching();
    }

    private void startSearching() {
	new Thread(searcher).start();
    }

    private static String[] splitMsg(String msg) {
	int pos = msg.indexOf(";");
	String[] tmp = new String[2];
	tmp[0] = (String) msg.subSequence(0, (pos));
	tmp[1] = (String) msg.subSequence(pos + 1, msg.length());
	return tmp;
    }

    public void setPort(int port) throws UnknownHostException, IOException {
	stop();
	this.port = port;
	startSearching();
    }

    public void stop() {
	if (socket != null && !socket.isClosed()) {
	    socket.close();
	}
    }

    private void createConnection() throws IOException, UnknownHostException {
	socket = new MulticastSocket(port);
	InetAddress address = InetAddress.getByName("224.2.2.3");
	socket.joinGroup(address);
	inPacket = new DatagramPacket(inBuf, inBuf.length);
    }
}

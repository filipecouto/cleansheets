package csheets.ext.rtc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Multicast client for receiving all the information from multicast servers
 * 
 * @author Rita Nogueira; Gil Castro (gil_1110484); Filipe Silva
 * 
 */
public class MulticastClient {
	private OnShareFoundListener onShareFoundListener;
	private MulticastSocket socket;
	private DatagramPacket inPacket;
	private byte[] inBuf = new byte[256];
	private Runnable searcher;

	public MulticastClient(OnShareFoundListener onShareFoundListener) {
		this.onShareFoundListener = onShareFoundListener;
		searcher = new Runnable() {
			@Override
			public void run() {
				try {
					createConnection();
					while (true) {
						socket.receive(inPacket);
						String msg = new String(inBuf, 0, inPacket.getLength());
						processMessage(msg);
					}
				} catch (IOException ioe) {
				}
			}
		};
		start();
	}

	/**
	 * Starts the thread
	 */
	public void start() {
		new Thread(searcher).start();
	}

	private void processMessage(String msg) {
		String[] parts = msg.split(";");
		if ((parts.length % 3) == 0) {
			for (int i = 0; i < parts.length; i += 3) {
				try {
					onShareFoundListener.onShareFound(new ServerInformation(parts[i]
							.replace("_<_SEMI_>_", ";"), Integer.parseInt(parts[i + 1]),
							Integer.parseInt(parts[i + 2]), inPacket.getAddress()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Got wrong message (" + msg + ") with "
					+ parts.length + " parts");
		}
	}

	/**
	 * Stops the thread
	 */
	public void stop() {
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}

	/**
	 * Creates the connection to the server
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	private void createConnection() throws IOException, UnknownHostException {
		socket = new MulticastSocket(MulticastServer.PORT);
		InetAddress address = InetAddress.getByName("224.2.2.3");
		socket.joinGroup(address);
		inPacket = new DatagramPacket(inBuf, inBuf.length);
	}
}

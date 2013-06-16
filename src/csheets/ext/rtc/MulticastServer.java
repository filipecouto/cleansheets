package csheets.ext.rtc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

/**
 * Multicast server for propagating tpc server's ip address
 * 
 * @author Rita Nogueira; Gil Castro (gil_1110484); Filipe Silva
 * 
 */
public class MulticastServer {
	public static final int PORT = 33334;

	private DatagramSocket socket;
	private DatagramPacket outPacket;
	private Runnable searcher;
	private List<RtcCommunicator> communicators;

	public MulticastServer(List<RtcCommunicator> communicators) {
		this.communicators = communicators;
		searcher = new Runnable() {
			@Override
			public void run() {
				try {
					socket = new DatagramSocket();
					while (true) {
						try {
							byte[] outBuf = getMessage().getBytes();

							// Send to multicast IP address and port
							InetAddress address = InetAddress.getByName("224.2.2.3");
							outPacket = new DatagramPacket(outBuf, outBuf.length,
									address, PORT);
							socket.send(outPacket);
							Thread.sleep(1500);
						} catch (Exception e) {
						}
					}
				} catch (IOException ioe) {
				}
			}

		};
		start();
	}

	/**
	 * Creates the message with the name of the share and the number of persons
	 * connected to it
	 * 
	 * @return message
	 */
	private String getMessage() {
		String message = "";
		for (RtcCommunicator com : communicators) {
			if (com.getSharingProperties().isOwner()) {
				message += com.getClientInfo().getShareName()
						.replace(";", "_<_SEMI_>_")
						+ ";"
						+ com.getConnectedUsers().length
						+ ";"
						+ com.getPort()
						+ ";";
			}
		}
		return message.substring(0, message.length() - 1);
	}

	/**
	 * Starts the thread
	 */
	private void start() {
		new Thread(searcher).start();
	}

	/**
	 * Stops the thread
	 */
	public void stop() {
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}
}

package csheets.ext.rtc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Multicast server for propagating tpc server's ip address
 * 
 * @author Rita Nogueira
 * 
 */
public class MulticastServer {
    private static int PORT = 33334;
    private DatagramSocket socket;
    private DatagramPacket outPacket;
    private Runnable searcher;
    private String serverName;
    private int serverNrClients;
    private List<RtcCommunicator> communicators;

    public MulticastServer(List<RtcCommunicator> communicators) {
        this.communicators = communicators;
	searcher = new Runnable() {

	    @Override
	    public void run() {
		try {
		    socket = new DatagramSocket();
		    String info = setMessage();
		    while (true) {
			byte[] outBuf = info.getBytes();

			// Send to multicast IP address and port
			InetAddress address = InetAddress
				.getByName("224.2.2.3");
			outPacket = new DatagramPacket(outBuf, outBuf.length,
				address, PORT);
			socket.send(outPacket);
			try {
			    Thread.sleep(1000);
			} catch (InterruptedException ie) {
			}
		    }
		} catch (IOException ioe) {
		}
	    }

	};
	startSearching();
    }

    /**
     * Creates the message with the name of the share and the number of persons
     * connected to it
     * 
     * @return message
     */
    private String setMessage() {
        int count=0;
        String message="";
        System.out.println("Communicators Number: "+communicators.size());
        for(RtcCommunicator com : communicators){
            if(com.getSharingProperties().isOwner()){
                System.out.println("owner?");
                count++;
                message+=com.getClientInfo().getShareName()+";"+com.getConnectedUsers().length+";"+com.getPort();
            }
        }
	return (count + ";" + message);
    }

    /**
     * method to stop the thread of the server, modify the value of the variable
     * nrClients and restart the thread
     * 
     * @param nrClient
     * @throws UnknownHostException
     * @throws IOException
     */
    public void serverNrClient(int nrClient) throws UnknownHostException,
	    IOException {
	stop();
	this.serverNrClients = nrClient;
	startSearching();
    }

    /**
     * Starts the thread
     */
    private void startSearching() {
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

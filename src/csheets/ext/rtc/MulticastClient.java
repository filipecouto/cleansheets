package csheets.ext.rtc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


/**
 * Multicast client for receiving all the information from multicast servers
 * 
 * @author Rita Nogueira
 * 
 */
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
		    createConnection();
		    while (true) {
			socket.receive(inPacket);
			String msg = new String(inBuf, 0, inPacket.getLength());
                        System.out.println("MSG:"+msg);
			String[] info = splitMessage(msg);
                        int count=Integer.parseInt(info[0]);
                        //System.out.println("Count= "+count);
                        for(int i=0;i<count;i++){
                            onShareFoundListener
                                    .onShareFound(new ServerInformation(info[1+i],
                                            Integer.parseInt(info[2+i]), Integer.parseInt(info[3+i])
                                            ,inPacket.getAddress()));
                            //System.out.println(info[i]+" "+info[1+i]+" "+info[2+i]+" "+inPacket.getAddress());
                        }
                        System.out.println("-IN-");
		    }
		} catch (IOException ioe) {
		}
	    }
	};
	startSearching();
    }

    /**
     * Starts the thread
     */
    private void startSearching() {
	new Thread(searcher).start();
    }

    /**
     * Decomposes the message
     * 
     * @return message
     */
    private static String[] splitMessage(String msg) {
	String[] tmp = msg.split(";");
	return tmp;
    }

    /**
     * * method to stop the thread of the server, modify the value of the
     * variable port and restart the thread
     * 
     * @param port
     * @throws UnknownHostException
     * @throws IOException
     */
    public void setPort(int port) throws UnknownHostException, IOException {
	stop();
	this.port = port;
	startSearching();
        System.out.println("Searching.................................");
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
	socket = new MulticastSocket(port);
	InetAddress address = InetAddress.getByName("224.2.2.3");
	socket.joinGroup(address);
	inPacket = new DatagramPacket(inBuf, inBuf.length);
        System.out.println("Porta: "+port);
    }
}

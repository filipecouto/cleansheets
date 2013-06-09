package csheets.ext.rtc;

import java.awt.Color;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This class holds the identification of a host
 * 
 * @author gil_1110484; Rita Nogueira
 */
public class ClientInfo implements Serializable {
    private static final long serialVersionUID = 6614659918137265454L;

    private InetAddress address;
    private String userName;
    private String shareName;
    private Color color;

    /**
     * Instantiates a ClientInfo with an address
     * 
     * @param address
     */
    public ClientInfo(InetAddress address) {
	this.address = address;
    }

    /**
     * Instantiates a ClientInfo with the user's (nick)name and share's name
     * 
     * @param shareName
     * @param userName
     */
    public ClientInfo(String shareName, String userName) {
	this.userName = userName;
	color = Color.getHSBColor((float) Math.random() * 0.8f,
		(float) (Math.random() * 0.4f + 0.6f),
		(float) (Math.random() * 0.2f + 0.8f));
	this.shareName = shareName;
    }

    /**
     * Syncs this instance with remote info
     * 
     * @param info
     *            remote info
     */
    void addRemoteInfo(ClientInfo info) {
	userName = info.userName;
    }

    /**
     * Adds the address to this instance
     * 
     * @param socket
     *            a socket containing the address
     */
    void addConnectionInfo(Socket socket) {
	address = socket.getLocalAddress();
    }

    /**
     * Adds the address to this instance
     * 
     * @param address
     *            the address
     */
    void addConnectionInfo(InetAddress address) {
	this.address = address;
    }

    /**
     * Gets the address of this user
     * 
     * @return a InetAddress
     */
    public InetAddress getAddress() {
	return address;
    }

    /**
     * Gets the color associated with this user, this color is generated
     * automatically, at least for now
     * 
     * @return
     */
    public Color getColor() {
	return color;
    }

    /**
     * Gets the (nick)name of this user
     * 
     * @return the name specified by the user
     */
    public String getUserName() {
	return userName;
    }

    /**
     * Gets the (share)name of this user
     * 
     * @return the share's name specified by the user
     */
    public String getShareName() {
	return shareName;
    }

    public boolean isSameIP(InetAddress address) {
	return this.address.equals(address);
    }
}

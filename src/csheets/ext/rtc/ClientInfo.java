package csheets.ext.rtc;

import java.awt.Color;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This class holds the identification of a host
 * 
 * @author gil_1110484
 */
public class ClientInfo implements Serializable {
    private static final long serialVersionUID = 6614659918137265454L;

    private InetAddress address;
    private String name;
    private Color color;

    public ClientInfo(InetAddress address) {
	this.address = address;
    }

    public ClientInfo(String name) {
	this.name = name;
	color = Color.getHSBColor((float) Math.random() * 0.8f,
		(float) (Math.random() * 0.4f + 0.6f),
		(float) (Math.random() * 0.2f + 0.8f));
    }

    void addRemoteInfo(ClientInfo info) {
	name = info.name;
    }

    void addConnectionInfo(Socket socket) {
	address = socket.getLocalAddress();
    }

    void addConnectionInfo(InetAddress address) {
	this.address = address;
    }

    public InetAddress getAddress() {
	return address;
    }

    public Color getColor() {
	return color;
    }

    public String getName() {
	return name;
    }

    public boolean isSameIP(InetAddress address) {
	return this.address.equals(address);
    }
}

package csheets.ext.rtc;

import java.awt.Color;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

public class ClientInfo implements Serializable {
    private static final long serialVersionUID = 6614659918137265454L;

    private InetAddress address;
    private String name;
    private Color color;

    public ClientInfo(InetAddress address) {
	this.address = address;
	color = Color.black;
    }

    public ClientInfo(String name) {
	this.name = name;
    }

    void addRemoteInfo(ClientInfo info) {
	name = info.name;
	color = Color.getHSBColor((int) (Math.random() * 300), 0, 0);
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

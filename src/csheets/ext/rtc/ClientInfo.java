package csheets.ext.rtc;

import java.awt.Color;
import java.io.Serializable;
import java.net.InetAddress;

public class ClientInfo implements Serializable {
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

package csheets.ext.rtc;

import java.net.InetAddress;

/**
 * Class for saving information from the message from MulticastServer
 * 
 * @author Rita Nogueira
 * 
 */
public class ServerInformation {

    private String shareName;
    private int nrPersons;
    private InetAddress ip;

    public ServerInformation(String shareName, int nrPersons, InetAddress ip) {
	this.shareName = shareName;
	this.nrPersons = nrPersons;
	this.ip = ip;
    }

    public String toString() {
	return (shareName + " - " + getIp() + " - " + nrPersons + " pessoa(s)");
    }

    public String getShareName() {
	return shareName;
    }

    public String getIp() {
	String address = ip.toString();
	while (address.startsWith("/")) {
	    address = address.substring(1);
	}
	return address;
    }

    public int getnrPersons() {
	return nrPersons;
    }
}

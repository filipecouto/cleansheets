package csheets.ext.rtc;

import java.net.InetAddress;

/**
 * Class to store all information received and processed by the class
 * MulticastClient
 * 
 * @author Rita Nogueira
 * 
 */
public class ServerInformation {

    private String shareName;
    private int nrPersons;
    private InetAddress ip;

    /**
     * 
     * @param shareName
     *            Share name to which the client is trying to connect
     * @param nrPersons
     *            Number of people in the share
     * @param ip
     *            Share's ip address
     */
    public ServerInformation(String shareName, int nrPersons, InetAddress ip) {
	this.shareName = shareName;
	this.nrPersons = nrPersons;
	this.ip = ip;
    }

    public String toString() {
	return (shareName + " - " + getIp() + " - " + (nrPersons + 1) + " persons(s)");
    }

    /**
     * 
     * @return the share's name
     */
    public String getShareName() {
	return shareName;
    }

    /**
     * 
     * @return a string with the ip address
     */
    public String getIp() {
	String address = ip.toString();
	while (address.startsWith("/")) {
	    address = address.substring(1);
	}
	return address;
    }

    /**
     * 
     * @return number of persons in the share
     */
    public int getNrPersons() {
	return nrPersons;
    }
}

package csheets.ext.rtc;

import csheets.ext.rtc.ui.RtcSharingProperties;

/**
 * This interface is the bridge between clients and server
 * 
 * @author gil_1110484
 */
public interface RtcCommunicator extends RtcInterface {
	/**
	 * Used by each side of the communication, another RtcListener must be
	 * implemented in order to react to events on the other side
	 * 
	 * @param listener
	 *           another implementation of RtcListener
	 */
	public void setListener(RtcListener listener);

	/**
	 * Gets all connected users. A server would simply return the list of
	 * connected clients, a client would return a list provided by the server
	 * (which may not be up-to-date)
	 * 
	 * @return an array of ClientInfo's with their name and color
	 */
	public ClientInfo[] getConnectedUsers();

	/**
	 * Gets how many users are connected.
	 * 
	 * @return the number of connected users
	 */
	public int getConnectedUsersCount();

	/**
	 * Tells whether this server or client is online
	 * 
	 * @return true if it is
	 */
	public boolean isConnected();

	/**
	 * Gets some properties such as what cells are shared
	 * 
	 * @return a RtcSharingProperties containing all needed properties
	 */
	public RtcSharingProperties getSharingProperties();

	/**
	 * Starts this server/client
	 */
	public void start();

	/**
	 * Sets the share Activated value
	 * 
	 * @param isActivated
	 */
	public void setActivated(boolean isActivated);

	/**
	 * Tells if the share is activated
	 * 
	 * @return
	 */
	public boolean isActivated();

	/**
	 * 
	 * @return the port number
	 */
	public abstract int getPort();

	/**
	 * 
	 * @return clientInfo
	 */
	public abstract ClientInfo getClientInfo();

}

package csheets.ext.rtc;

import csheets.core.Address;
import csheets.ext.rtc.messages.RemoteCell;

/**
 * This interface contains methods needed for both ends to communicate and let
 * the UI know about events
 * 
 * @author gil_1110484
 */
public interface RtcInterface {
    /**
     * Called when a connection was established
     * 
     * @param client
     *            the identity
     */
    public void onConnected(ClientInfo client);

    /**
     * Called when another user selects a cell
     * 
     * @param source
     *            the identity of that user
     * @param address
     *            the address of his/her selection
     */
    public void onCellSelected(ClientInfo source, Address address);

    /**
     * Called when another user changes a cell (content, or formatting)
     * 
     * @param source
     *            the identity of that user
     * @param cell
     *            a RemoteCell containing the needed info in order to replicate
     *            the new cell
     */
    public void onCellChanged(ClientInfo source, RemoteCell cell);

    /**
     * Called when another user joins on leaves this share
     * 
     * @param source
     *            the identity of that user
     * @param action
     *            currently only tells a user disconnected if it is a false
     *            Boolean, if a user connects it will be null
     */
    public void onUserAction(ClientInfo source, Object action);

    /**
     * Called when the connection is closed
     * 
     * @param client
     *            the identity
     */
    public void onDisconnected(ClientInfo client);
}

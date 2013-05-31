package csheets.ext.rtc;

import csheets.core.Address;
import csheets.core.Cell;

/**
 * This interface contains all the methods needed for both ends to communicate
 * and let the UI know about events
 * 
 * @author gil_1110484
 */
public interface RtcInterface {
    public void onConnected(ClientInfo client);

    public void onCellSelected(ClientInfo source, Address address);

    public void onCellChanged(ClientInfo source, Cell cell);

    public void onUserAction(ClientInfo source, Object action);

    public void onDisconnected(ClientInfo client);
}

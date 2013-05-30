package csheets.ext.rtc;

import csheets.core.Address;
import csheets.core.Cell;

public interface RtcListener {
    public void onConnected(ClientInfo client);

    public void onCellSelected(ClientInfo source, Address address);

    public void onCellChanged(ClientInfo source, Cell cell);

    public void onDisconnected(ClientInfo client);
}

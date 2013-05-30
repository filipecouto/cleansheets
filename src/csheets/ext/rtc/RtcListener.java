package csheets.ext.rtc;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.Workbook;

public interface RtcListener {
    public void onConnected(ClientInfo client);

    //public void onWoorkbookReceived(ClientInfo source, Workbook workbook);

    public void onCellSelected(ClientInfo source, Address address);

    public void onCellChanged(ClientInfo source, Cell cell);

    public void onDisconnected(ClientInfo client);
}

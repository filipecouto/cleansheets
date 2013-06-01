package csheets.ext.rtc;

import csheets.core.Workbook;
import csheets.ext.rtc.messages.RemoteCell;

/**
 * This interface contains all the methods (including those from its
 * super-interface) needed to make the UI show remote data
 * 
 * @author gil_1110484
 */
public interface RtcListener extends RtcInterface {
    /**
     * Called when a workbook is received
     * 
     * @param source
     *            who sent it
     * @param workbook
     *            the workbook, may contain spreadsheets already
     */
    public void onWorkbookReceived(ClientInfo source, Workbook workbook);

    // public void onSpreadsheetReceived(ClientInfo source, Spreadsheet
    // spreadsheet);

    /**
     * Called when an array of cells is received
     * 
     * @param source
     *            who sent it
     * @param cells
     *            the array of cells
     */
    public void onCellsReceived(ClientInfo source, RemoteCell[] cells);

    /**
     * Called when an attempt to connect fails
     * 
     * @param e
     *            the exception throw during connection
     */
    public void onConnectionFailed(Exception e);
}

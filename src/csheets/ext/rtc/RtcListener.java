package csheets.ext.rtc;

import csheets.core.Cell;
import csheets.core.Spreadsheet;
import csheets.core.Workbook;

/**
 * This interface contains all the methods (including those from its
 * super-interface) needed to make the UI show remote data
 * 
 * @author gil_1110484
 */
public interface RtcListener extends RtcInterface {
    //public void onWorkbookReceived(ClientInfo source, Workbook workbook);

    public void onSpreadsheetReceived(ClientInfo source, Spreadsheet spreadsheet);

    public void onCellReceived(ClientInfo source, Cell cell);
}

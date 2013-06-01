package csheets.ext.rtc.messages;

import java.io.Serializable;

import csheets.core.Workbook;

/**
 * This class is in charge of storing any data needed to build a workbook
 * (without its dependencies) and rebuilding in in the other end
 * 
 * @author gil_1110484
 */
public class RemoteWorkbook implements Serializable {
    private static final long serialVersionUID = 4730582636660279033L;

    private int spreadsheetCount;

    /**
     * Constructor, requires a Workbook.
     * 
     * @param workbook
     *            the workbook to send
     */
    public RemoteWorkbook(Workbook workbook) {
	spreadsheetCount = workbook.getSpreadsheetCount();
    }

    /**
     * Builds the workbook using as much of the data it can hold.
     * 
     * @return the workbook, ready to pass to the application
     */
    public Workbook getWorkbook() {
	Workbook wb = new Workbook(spreadsheetCount);

	return wb;
    }
}

package csheets.ext.rtc.messages;

import java.io.Serializable;

import csheets.core.Spreadsheet;
import csheets.core.Workbook;

/**
 * This class holds data from Spreadsheet instances in order to send them across
 * the network
 * 
 * @author gil_1110484
 */
public class RemoteSpreadsheet implements Serializable {
    private static final long serialVersionUID = -5519246036922362083L;

    private String title;
    private int index;
    private int columnCount;
    private int rowCount;

    /**
     * Constructor, requires a Spreadsheet and its index.
     * 
     * @param spreadsheet
     *            the spreadsheet to send
     * @param index
     *            the index of this spreadsheet in its workbook
     */
    public RemoteSpreadsheet(Spreadsheet spreadsheet, int index) {
	this.index = index;
	title = spreadsheet.getTitle();
	columnCount = spreadsheet.getColumnCount();
	rowCount = spreadsheet.getRowCount();
    }

    /**
     * Puts the held Spreadsheet in its right place. The workbook must contain a
     * place for this spreadsheet.
     * 
     * @param workbook
     *            the workbook where to put the spreadsheet
     */
    public Spreadsheet getSpreadsheet(Workbook workbook) {
	Spreadsheet sheet = workbook.getSpreadsheet(index);
	sheet.setTitle(title);
	return sheet;
    }

    /**
     * Returns the current number of columns of this spreadsheet
     * 
     * @return the current number of columns of this spreadsheet
     */
    public int getColumnCount() {
	return columnCount;
    }

    /**
     * Returns the current number of rows of this spreadsheet
     * 
     * @return the current number of rows of this spreadsheet
     */
    public int getRowCount() {
	return rowCount;
    }
}

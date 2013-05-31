package csheets.ext.rtc.messages;

import java.io.Serializable;

import csheets.core.Spreadsheet;
import csheets.core.Workbook;

public class RemoteSpreadsheet implements Serializable {
    private static final long serialVersionUID = -5519246036922362083L;

    private String title;
    private int index;
    private int columnCount;
    private int rowCount;

    public RemoteSpreadsheet(Spreadsheet spreadsheet, int index) {
	this.index = index;
	title = spreadsheet.getTitle();
	columnCount = spreadsheet.getColumnCount();
	rowCount = spreadsheet.getRowCount();
    }

    public Spreadsheet getSpreadsheet(Workbook workbook) {
	Spreadsheet sheet = workbook.getSpreadsheet(index);
	sheet.setTitle(title);
	return sheet;
    }

    public int getColumnCount() {
	return columnCount;
    }

    public int getRowCount() {
	return rowCount;
    }
}

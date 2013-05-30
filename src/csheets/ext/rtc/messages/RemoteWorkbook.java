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

    public RemoteWorkbook(Workbook workbook) {
	workbook.getSpreadsheetCount();
    }

    public Workbook getWorkbook() {
	Workbook wb = new Workbook(spreadsheetCount);

	return wb;
    }
}

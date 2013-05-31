package csheets.ext.rtc;

import csheets.core.Address;
import csheets.ext.rtc.messages.RemoteCell;

public class RtcShareProperties {
    private int spreadsheet;

    private boolean wholeSpreadsheet = false;

    private Address start;
    private Address end;

    public RtcShareProperties() {
	spreadsheet = 0;
	start = new Address(1, 1);
	end = new Address(5, 8);
    }

    public void setAcceptWholeSpreadsheet(boolean accept) {
	wholeSpreadsheet = accept;
    }

    public void setRange(Address start, Address end) {
	this.start = start;
	this.end = end;
    }

    public void setSpreadsheet(int spreadsheet) {
	this.spreadsheet = spreadsheet;
    }

    public int getSpreadsheet() {
	return spreadsheet;
    }

    /**
     * Tells whether address is inside the specified range
     * 
     * @param address
     * @return true if it is
     */
    public boolean isInsideRange(Address address) {
	if (wholeSpreadsheet)
	    return true;
	return start.getColumn() >= address.getColumn()
		&& start.getRow() >= start.getRow()
		&& end.getColumn() <= address.getColumn()
		&& end.getRow() <= address.getRow();
    }

    /**
     * Tells whether this remote change is permitted by current share properties
     * 
     * @param cell
     * @return
     */
    public boolean isValid(RemoteCell cell) {
	return cell.isValid(this);
    }
}

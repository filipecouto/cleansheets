package csheets.ext.rtc;

import java.io.Serializable;

import csheets.core.Address;
import csheets.ext.rtc.messages.RemoteCell;

/**
 * This class controls the access to the workbook in accordance to user's
 * choice.
 * 
 * @author gil_1110484
 */
public class RtcSharingProperties implements Serializable {
    private static final long serialVersionUID = 8740372311265893270L;

    private int spreadsheet;

    private boolean wholeSpreadsheet = true;

    private Address start;
    private Address end;

    public RtcSharingProperties() {
	spreadsheet = 0;
    }

    /**
     * Creates another instance to send to clients. This may be useful if there
     * is any need to hide some properties that only matter to the server.
     * 
     * @return the new instance
     */
    public RtcSharingProperties getPropertiesForClients() {
	RtcSharingProperties props = new RtcSharingProperties();
	props.start = start;
	props.end = end;
	props.wholeSpreadsheet = wholeSpreadsheet;
	return props;
    }

    /**
     * Sets whether all cells in this spreadsheet (currently, RtcSharingOptions
     * only control one spreadsheet) should be shared
     * 
     * @param accept
     */
    public void setAcceptWholeSpreadsheet(boolean accept) {
	wholeSpreadsheet = accept;
    }

    /**
     * Sets the range of the shared cells
     * 
     * @param start
     *            the top left corner of the range
     * @param end
     *            the bottom right corner of the range
     */
    public void setRange(Address start, Address end) {
	this.start = start;
	this.end = end;
    }

    /**
     * Sets the shared spreadsheet
     * 
     * @param spreadsheet
     *            the index of the spreadsheet in its workbook
     */
    public void setSpreadsheet(int spreadsheet) {
	this.spreadsheet = spreadsheet;
    }

    /**
     * Gets the index of the shared spreadsheet
     * 
     * @return
     */
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
	if (wholeSpreadsheet) {
	    return true;
	} else {
	    return start.getColumn() <= address.getColumn()
		    && start.getRow() <= address.getRow()
		    && end.getColumn() >= address.getColumn()
		    && end.getRow() >= address.getRow();
	}
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

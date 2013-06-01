package csheets.ext.rtc;

import javax.swing.SwingUtilities;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.CellListener;
import csheets.core.Workbook;
import csheets.ext.rtc.messages.RemoteCell;
import csheets.ui.ctrl.UIController;

public class RtcEventsResponder implements RtcListener {
    private UIController uiController;
    private RtcCommunicator communicator;
    private RtcShareProperties properties;
    private RealTimeCollaboration extension;

    public RtcEventsResponder(RtcCommunicator communicator,
	    UIController uiController, RealTimeCollaboration extension) {
	this.uiController = uiController;
	this.extension = extension;
	this.communicator = communicator;
    }

    @Override
    public void onDisconnected(ClientInfo client) {
	extension.onDisconnected();
	extension.updateUsersList();
    }

    @Override
    public void onConnected(ClientInfo client) {
	properties = communicator.getShareProperties();
	extension.onConnected();
	if (extension.isOwner()) {
	    extension.updateUsersList();
	}
    }

    public RtcShareProperties getShareProperties() {
	return properties;
    }

    @Override
    public void onCellSelected(ClientInfo source, Address address) {
	if (properties != null && !properties.isInsideRange(address)) {
	    return;
	}
	synchronized (uiController) {
	    final Cell cell = uiController.getActiveSpreadsheet().getCell(
		    address);
	    for (CellListener l : cell.getCellListeners()) {
		l.contentChanged(cell);
	    }
	}
    }

    @Override
    public void onCellChanged(ClientInfo source, final RemoteCell cell) {
	if (properties != null && !properties.isValid(cell)) {
	    return;
	}
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		cell.getCell(uiController.getActiveWorkbook());
	    }
	});
    }

    @Override
    public void onWorkbookReceived(ClientInfo source, final Workbook workbook) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		uiController.setActiveWorkbook(workbook);
	    }
	});
    }

    // @Override
    // public void onSpreadsheetReceived(ClientInfo source, Spreadsheet
    // spreadsheet) {
    // uiController.setActiveSpreadsheet(spreadsheet);
    // }

    @Override
    public void onCellsReceived(ClientInfo source, final RemoteCell[] cells) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		for (RemoteCell cell : cells) {
		    if (cell != null) {
			cell.getCell(uiController.getActiveWorkbook());
		    }
		}
	    }
	});
    }

    @Override
    public void onUserAction(ClientInfo source, Object action) {
	extension.updateUsersList();
    }

    @Override
    public void onConnectionFailed(Exception e) {
	extension.onConnectionFailed(e);
    }
}
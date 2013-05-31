package csheets.ext.rtc;

import javax.swing.SwingUtilities;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.CellListener;
import csheets.core.Workbook;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.ext.rtc.messages.RemoteCell;
import csheets.ui.ctrl.UIController;

public class RtcEventsResponder implements RtcListener {
    UIController uiController;
    RealTimeCollaboration extension;

    public RtcEventsResponder(UIController uiController, RealTimeCollaboration extension) {
	this.uiController = uiController;
	this.extension = extension;
    }

    @Override
    public void onDisconnected(ClientInfo client) {
    }

    @Override
    public void onConnected(ClientInfo client) {
    }

    @Override
    public void onCellSelected(ClientInfo source, Address address) {
	synchronized (uiController) {
	    final Cell cell = uiController.getActiveSpreadsheet().getCell(
		    address);
	    for (CellListener l : cell.getCellListeners()) {
		l.contentChanged(cell);
	    }
	}
    }

    @Override
    public void onCellChanged(ClientInfo source, final Cell cell) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		try {
		    uiController.getActiveSpreadsheet()
			    .getCell(cell.getAddress())
			    .setContent(cell.getContent());
		} catch (FormulaCompilationException e) {
		    e.printStackTrace();
		}
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
		try {
		    for (RemoteCell cell : cells) {
			uiController.getActiveSpreadsheet()
				.getCell(cell.getAddress())
				.setContent(cell.getContent());
		    }
		} catch (FormulaCompilationException e) {
		}
	    }
	});
    }

    @Override
    public void onUserAction(ClientInfo source, Object action) {
	extension.updateUsersList();
    }
}
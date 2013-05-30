package csheets.ext.rtc;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.CellListener;
import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.ui.ctrl.UIController;

public class RtcEventsResponder implements RtcListener {
    UIController uiController;

    public RtcEventsResponder(UIController uiController) {
	this.uiController = uiController;
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
    public void onCellChanged(ClientInfo source, Cell cell) {
	synchronized (uiController) {
	    try {
		uiController.getActiveSpreadsheet().getCell(cell.getAddress())
			.setContent(cell.getContent());
	    } catch (FormulaCompilationException e) {
		e.printStackTrace();
	    }
	}
    }

//    @Override
//    public void onWorkbookReceived(ClientInfo source, Workbook workbook) {
//	uiController.setActiveWorkbook(workbook);
//    }

    @Override
    public void onSpreadsheetReceived(ClientInfo source, Spreadsheet spreadsheet) {
	uiController.setActiveSpreadsheet(spreadsheet);
    }

    @Override
    public void onCellReceived(ClientInfo source, Cell cell) {
	// TODO Auto-generated method stub
	
    }
}
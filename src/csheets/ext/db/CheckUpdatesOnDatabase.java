package csheets.ext.db;

import java.sql.Connection;

import javax.print.attribute.standard.SheetCollate;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.ext.db.ui.DatabaseUIExtension;
import csheets.ext.db.ui.OnDatabaseInteractionListener;
import csheets.ui.ctrl.UIController;

public class CheckUpdatesOnDatabase {

    private DatabaseSharedArea sharedArea;
    private Runnable updater;
    private DatabaseInterface databaseInterface;
    private Connection connection;
    private UIController controller;

    public CheckUpdatesOnDatabase(final UIController controller,
	    final DatabaseSharedArea area) {
	this.controller = controller;
	this.sharedArea = area;
	System.out.println("hello" + sharedArea.getDatabaseName());
	databaseInterface = sharedArea.getDatabase();
	connection = databaseInterface.openDatabase(sharedArea
		.getDatabaseName());
	updater = new Runnable() {

	    @Override
	    public void run() {
		while (true) {
		    System.out.println("thread");
		    String[][] data = databaseInterface.getData(sharedArea
			    .getTableName());
		    setCellsContent(data);
		    try {
			Thread.sleep(30000);
		    } catch (InterruptedException ie) {
		    }
		}
	    }

	    private void setCellsContent(String[][] data) {

		Spreadsheet sheet = controller.getActiveSpreadsheet();
		int beginColumn = sharedArea.getInitialCell().getColumn();
		int beginRow = sharedArea.getInitialCell().getRow();
		int endColumn = sharedArea.getFinalCell().getColumn();
		int endRow = sharedArea.getFinalCell().getRow();
		if ((endColumn - beginColumn) < data.length) {
		    if ((endRow - beginRow) < data[0].length) {
			int sumColumn = endColumn - data.length;
			sumColumn += beginColumn;
			int sumRow = endRow - data[0].length;
			sumRow += beginRow;
			sharedArea.setFinalCell(new Address(sumColumn, sumRow));
		    } else {
			int sumColumn = endColumn - data.length;
			sumColumn += beginColumn;
			int sumRow = sharedArea.getFinalCell().getRow();
			sharedArea.setFinalCell(new Address(sumColumn, sumRow));
		    }
		} else {
		    int sumColumn = sharedArea.getFinalCell().getColumn();
		    int sumRow = sharedArea.getFinalCell().getRow();
		    sharedArea.setFinalCell(new Address(sumColumn, sumRow));
		}
		beginColumn = sharedArea.getInitialCell().getColumn();
		beginRow = sharedArea.getInitialCell().getRow();
		endColumn = sharedArea.getFinalCell().getColumn();
		endRow = sharedArea.getFinalCell().getRow();
		try {
		    for (int i = beginColumn; i < endColumn; i++) {
			for (int j = beginRow; j < endRow; j++) {

			    sheet.getCell(j, i).setContent(
				    data[i][j].toString());

			}
		    }

		    DatabaseUIExtension.setSharedArea(sharedArea);
		} catch (FormulaCompilationException e) {
		    e.printStackTrace();
		}
	    }
	};
	startSearching();
    }

    public void startSearching() {
	new Thread(updater).start();
    }

    public void setSharedArea(DatabaseSharedArea sharedArea) {
	System.out.println("parou");
	stop();
	this.sharedArea = sharedArea;
	startSearching();
    }

    @SuppressWarnings("deprecation")
    public void stop() {
	databaseInterface.closeDatabase();
	new Thread(updater).stop();
    }

}

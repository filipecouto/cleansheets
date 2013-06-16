package csheets.ext.db;

import java.sql.Connection;

import csheets.core.Spreadsheet;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.ext.db.ui.DatabaseUIExtension;
import csheets.ui.ctrl.UIController;

/**
 * Class for updating the sheet with alterations ont the database
 * 
 * @author Rita Nogueira & Filipe Couto
 * 
 */
public class CheckUpdatesOnDatabase {

    private DatabaseSharedArea sharedArea;
    private Runnable updater;
    private DatabaseInterface databaseInterface;
    private Connection connection;
    private UIController UIcontroller;

    public CheckUpdatesOnDatabase(final UIController controller,
	    final DatabaseSharedArea area) {
	this.UIcontroller = controller;
	this.sharedArea = area;
	databaseInterface = sharedArea.getDatabase();
	connection = databaseInterface.openDatabase(sharedArea
		.getDatabaseName());
	updater = new Runnable() {

	    @Override
	    public void run() {
		while (true) {
		    String[][] data = databaseInterface.getData(sharedArea
			    .getTableName());
		    setCellsContent(data);
		    try {
			Thread.sleep(30000);
		    } catch (InterruptedException ie) {
		    }
		}
	    }

	    /**
	     * Method for setting the information from the database on the sheet
	     * 
	     * @param data
	     */
	    private void setCellsContent(String[][] data) {

		Spreadsheet sheet = UIcontroller.getActiveWorkbook()
			.getSpreadsheet(sharedArea.getSpreadsheetNumber());
		int beginColumn = sharedArea.getInitialCell().getColumn();
		int beginRow = sharedArea.getInitialCell().getRow();
		int endColumn = sharedArea.getFinalCell().getColumn();
		int endRow = sharedArea.getFinalCell().getRow();

		int verticalDimension = endRow - beginRow;
		int verticalDimensionOffSet = data.length - verticalDimension
			- 1;
		endRow += verticalDimensionOffSet;

		try {
		    for (int i = beginColumn; i < endColumn; i++) {
			for (int j = beginRow; j < endRow; j++) {
			    sheet.getCell(i, j).setContent(
				    data[j][i].toString());

			}
		    }
		    DatabaseUIExtension.setSharedArea(sharedArea);
		} catch (FormulaCompilationException e) {
		    e.printStackTrace();
		}
	    }
	};
	startUpdating();
    }

    /**
     * Method for starting the thread
     */
    public void startUpdating() {
	new Thread(updater).start();
    }

    /**
     * Method for change the sharedArea
     * 
     * @param sharedArea
     */
    public void setSharedArea(DatabaseSharedArea sharedArea) {
	stop();
	this.sharedArea = sharedArea;
	startUpdating();
    }

    /**
     * Method for stoping the thread
     */
    @SuppressWarnings("deprecation")
    public void stop() {
	new Thread(updater).stop();
    }

}

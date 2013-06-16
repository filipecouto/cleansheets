package csheets.ext.db;

import java.sql.Connection;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.Spreadsheet;
import csheets.ui.ctrl.UIController;

public class CheckUpdatesOnSheet {

    private static DatabaseSharedArea sharedArea;
    private DatabaseInterface databaseInterface;
    private Connection connection;
    private UIController uiController;
    private CheckUpdatesOnDatabase updatesOnDatabase;

    public CheckUpdatesOnSheet(UIController controller,
	    DatabaseSharedArea area, CheckUpdatesOnDatabase updatesOnDatabase) {
	this.uiController = controller;
	this.sharedArea = area;
	this.updatesOnDatabase=updatesOnDatabase;
    }

    public void startChecking() {
	
	    Cell c = uiController.getActiveCell();
	    databaseInterface = getSharedArea().getDatabase();
	    connection = databaseInterface.openDatabase(getSharedArea()
		    .getDatabaseName());
	    if (getSharedArea().isInSharedArea(c.getAddress())) {
		databaseInterface.update(getSharedArea().getTableName(),
			getColumnsName(), getColumnsContent(c), c.getAddress()
				.getColumn());
	    } else {
		if (getSharedArea().isNextToSharedArea(c.getAddress())) {
		    System.out.println("next");
		    insertNewCell(c);
		}
	    }
	

    }

    private String[] getColumnsName() {
	Spreadsheet sheet = uiController.getActiveSpreadsheet();
	int beginColumn = getSharedArea().getInitialCell().getColumn();
	int endColumn = getSharedArea().getFinalCell().getColumn();
	String[] cellsNames = new String[endColumn - beginColumn + 1];

	for (int i = beginColumn; i <= endColumn; i++) {

	    cellsNames[i] = sheet.getCell(i, 0).getContent();
	    System.out.println("Hi , I'm creating this cell: " + cellsNames[i]);
	}
	return cellsNames;
    }

    private String getColumnsName(Cell c) {
	Spreadsheet sheet = uiController.getActiveSpreadsheet();
	int column = c.getAddress().getColumn();
	return sheet.getCell(column, 0).getContent();
    }

    private String[] getColumnsContent(Cell c) {
	Spreadsheet sheet = uiController.getActiveSpreadsheet();
	int beginColumn = getSharedArea().getInitialCell().getColumn();
	int endColumn = getSharedArea().getFinalCell().getColumn();
	int row = c.getAddress().getRow();

	String[] cellsNames = new String[endColumn - beginColumn + 1];
	for (int i = beginColumn; i <= endColumn; i++) {
	    cellsNames[i] = sheet.getCell(i, row).getContent();
	}
	return cellsNames;
    }

    private void insertNewCell(Cell c) {
	String info[][] = databaseInterface.getData(getSharedArea()
		.getTableName());
	int i = info.length;
	int j = info[0].length;
	String tmp[] = new String[j + 1];
	Address addr;
	switch (getSharedArea().whereIsCell(c.getAddress())) {
	case 2:
	    databaseInterface.insert(getSharedArea().getTableName(),
		    getColumnsName(c), c.getContent(), getColumnsName());
	    addr = new Address(getSharedArea().getFinalCell().getColumn(),
		    getSharedArea().getFinalCell().getRow() + 1);
	    sharedArea.setFinalCell(addr);
	    updatesOnDatabase.setSharedArea(sharedArea);
	    break;
	default:
	    break;
	}

    }

    public static DatabaseSharedArea getSharedArea() {
	return sharedArea;
    }

    public static void setSharedArea(DatabaseSharedArea sharedArea) {
	CheckUpdatesOnSheet.sharedArea = sharedArea;
    }
}

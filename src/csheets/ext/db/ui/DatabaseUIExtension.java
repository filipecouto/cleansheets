package csheets.ext.db.ui;

import java.sql.Connection;
import javax.swing.JMenu;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.Spreadsheet;
import csheets.core.SpreadsheetImpl;
import csheets.ext.db.DatabaseCellExtension;
import csheets.ext.db.DatabaseExtension;
import csheets.ext.db.DatabaseInterface;
import csheets.ext.db.DatabaseSharedArea;
import csheets.ui.ctrl.EditEvent;
import csheets.ui.ctrl.EditListener;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

public class DatabaseUIExtension extends UIExtension implements
	OnDatabaseInteractionListener {
    private DatabaseSharedArea sharedArea;
    private DatabaseInterface databaseInterface;
    private Connection connection;

    public DatabaseUIExtension(DatabaseExtension extension,
	    final UIController uiController) {
	super(extension, uiController);
	uiController.addEditListener(new EditListener() {

	    @Override
	    public void workbookModified(EditEvent event) {
		if (sharedArea != null) {
		    Cell c = uiController.getActiveCell();
		    databaseInterface = sharedArea.getDatabase();
		    connection = databaseInterface.openDatabase(sharedArea
			    .getDatabaseName());
		    if (sharedArea.isInSharedArea(c.getAddress())) {
			if (!isAllRowEmty(c)) {
			    databaseInterface.update(sharedArea.getTableName(),
				    getColumnsName(), getColumnsContent(c), c
					    .getAddress().getColumn());
			} else {
			    insertNewCell(c);
			}
		    } else {

			if (sharedArea.isNextToSharedArea(c.getAddress())) {
			    insertNewCell(c);
			}
		    }
		}
	    }

	    private boolean isAllRowEmty(Cell c) {
		Spreadsheet sheet = uiController.getActiveSpreadsheet();
		int beginColumn = sharedArea.getInitialCell().getColumn();
		int endColumn = sharedArea.getFinalCell().getColumn();
		int row = c.getAddress().getRow();

		for (int i = beginColumn; i < endColumn; i++) {
		    if (sheet.getCell(i, row).getContent().isEmpty()) {
			return true;
		    }
		}
		return false;
	    }

	    private String[] getColumnsName() {
		Spreadsheet sheet = uiController.getActiveSpreadsheet();
		int beginColumn = sharedArea.getInitialCell().getColumn();
		int endColumn = sharedArea.getFinalCell().getColumn();
		String[] cellsNames = new String[endColumn - beginColumn];

		for (int i = beginColumn; i < endColumn; i++) {
		    cellsNames[i] = sheet.getCell(i, 0).getContent();
		}
		return cellsNames;
	    }

	    private String[] getColumnsContent(Cell c) {
		Spreadsheet sheet = uiController.getActiveSpreadsheet();
		int beginColumn = sharedArea.getInitialCell().getColumn();
		int endColumn = sharedArea.getFinalCell().getColumn();
		int row = c.getAddress().getRow();

		String[] cellsNames = new String[endColumn - beginColumn];
		for (int i = beginColumn; i < endColumn; i++) {
		    cellsNames[i] = sheet.getCell(i, row).getContent();
		}
		return cellsNames;
	    }

	    private void insertNewCell(Cell c) {
		String info[][] = databaseInterface.getData(sharedArea
			.getTableName());
		int i = info.length;
		int j = info[0].length;
		System.out.println("i: " + i + ", j: " + j);
		String tmp[];

		switch (sharedArea.whereIsCell(c.getAddress())) {
		case 1:
		    tmp = new String[i];
		    fillArray(0, c.getAddress().getColumn(), tmp);
		    tmp[c.getAddress().getRow()] = c.getContent();
		    fillArray((c.getAddress().getColumn() + 1), i, tmp);
		    databaseInterface.addLine(sharedArea.getTableName(), tmp);
		    break;
		case 2:
		    tmp = new String[j];
		    fillArray(0, c.getAddress().getColumn(), tmp);
		    tmp[c.getAddress().getColumn()] = c.getContent();
		    fillArray((c.getAddress().getColumn() + 1), j, tmp);
		    databaseInterface.addLine(sharedArea.getTableName(), tmp);
		    break;
		default:
		    break;
		}

	    }

	    private void fillArray(int i, int end, String[] array) {
		for (; i < end; i++) {
		    array[i] = "";
		}
	    }
	});
    }

    @Override
    public JMenu getMenu() {
	return new DatabaseUIMenu((DatabaseExtension) extension, this);
    }

    @Override
    public void onDatabaseInteraction(DatabaseInterface database,
	    String databaseName, Address initialCell, Address finalCell,
	    String tableName, int spreadsheetNumber) {
	sharedArea = new DatabaseSharedArea(initialCell, finalCell, tableName,
		database, databaseName, spreadsheetNumber);
    }
}

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
			Cell initialCell = c.getSpreadsheet().getCell(
				c.getAddress().getColumn(), 0);
			if (c.getContent().isEmpty()) {
			    databaseInterface.delete(sharedArea.getTableName(),
				    initialCell.getContent(), c.getContent(), c
					    .getAddress().getRow());
			} else {
			    int id = ((DatabaseCellExtension) c
				    .getExtension("DB")).getId();
			    databaseInterface.update(sharedArea.getTableName(),
				    initialCell.getContent(), c.getContent(),
				    id);
			}
		    } else {

			if (sharedArea.isNextToSharedArea(c.getAddress())) {
			    insertNewCell(c);

			}
		    }
		}
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
	databaseInterface = database;
	connection = databaseInterface.openDatabase(databaseName);
	Spreadsheet sheet = uiController.getActiveWorkbook().getSpreadsheet(
		spreadsheetNumber);
	Cell[][] cells = new Cell[finalCell.getColumn()
		- initialCell.getColumn()][finalCell.getRow()
		- initialCell.getRow()];
	String tmp[][] = new String[finalCell.getColumn()
		- initialCell.getColumn()][finalCell.getRow()
		- initialCell.getRow()];

	for (int i = initialCell.getColumn(); i < finalCell.getColumn(); i++) {
	    for (int j = initialCell.getRow(); j < finalCell.getRow(); j++) {
		cells[i][j] = sheet.getCell(i, j);

	    }
	}
	String[][] data = databaseInterface.getData(tableName);
	for (int i = 0; i < cells.length; i++) {
	    for (int j = 0; j < cells[0].length; j++) {
		((DatabaseCellExtension) cells[i][j].getExtension("DB"))
			.setId(Integer.parseInt(data[i][data[0].length - 1]));
	    }
	}
	databaseInterface.closeDatabase();
    }
}

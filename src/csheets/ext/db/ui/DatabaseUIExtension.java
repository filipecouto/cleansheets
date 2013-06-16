package csheets.ext.db.ui;

import java.sql.Connection;
import javax.swing.JMenu;

import csheets.SpreadsheetAppEvent;
import csheets.SpreadsheetAppListener;
import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.Spreadsheet;
import csheets.core.SpreadsheetImpl;
import csheets.ext.db.CheckUpdatesOnDatabase;
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
    private CheckUpdatesOnDatabase updatesOnDatabase;

    public DatabaseUIExtension(DatabaseExtension extension,
	    final UIController uiController) {
	super(extension, uiController);
	uiController.addWorkbookListener(new SpreadsheetAppListener() {

	    @Override
	    public void workbookUnloaded(SpreadsheetAppEvent event) {
		sharedArea = null;
	    }

	    @Override
	    public void workbookSaved(SpreadsheetAppEvent event) {

	    }

	    @Override
	    public void workbookLoaded(SpreadsheetAppEvent event) {

	    }

	    @Override
	    public void workbookCreated(SpreadsheetAppEvent event) {

	    }
	});
	uiController.addEditListener(new EditListener() {

	    @Override
	    public void workbookModified(EditEvent event) {
		if (sharedArea != null) {
		    Cell c = uiController.getActiveCell();
		    databaseInterface = sharedArea.getDatabase();
		    connection = databaseInterface.openDatabase(sharedArea
			    .getDatabaseName());
		    if (sharedArea.isInSharedArea(c.getAddress())) {
			databaseInterface.update(sharedArea.getTableName(),
				getColumnsName(), getColumnsContent(c), c
					.getAddress().getColumn());
		    } else {
			if (sharedArea.isNextToSharedArea(c.getAddress())) {
			    System.out.println("next");
			    insertNewCell(c);
			}
		    }
		}
	    }

	    private String[] getColumnsName() {
		Spreadsheet sheet = uiController.getActiveSpreadsheet();
		int beginColumn = sharedArea.getInitialCell().getColumn();
		int endColumn = sharedArea.getFinalCell().getColumn();
		String[] cellsNames = new String[endColumn - beginColumn + 1];

		for (int i = beginColumn; i <= endColumn; i++) {

		    cellsNames[i] = sheet.getCell(i, 0).getContent();
		    System.out.println("Hi , I'm creating this cell: "
			    + cellsNames[i]);
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
		int beginColumn = sharedArea.getInitialCell().getColumn();
		int endColumn = sharedArea.getFinalCell().getColumn();
		int row = c.getAddress().getRow();

		String[] cellsNames = new String[endColumn - beginColumn + 1];
		for (int i = beginColumn; i <= endColumn; i++) {
		    cellsNames[i] = sheet.getCell(i, row).getContent();
		}
		return cellsNames;
	    }

	    private void insertNewCell(Cell c) {
		String info[][] = databaseInterface.getData(sharedArea
			.getTableName());
		int i = info.length;
		int j = info[0].length;
		String tmp[] = new String[j + 1];
		Address addr;
		switch (sharedArea.whereIsCell(c.getAddress())) {
		case 1:
		    System.out.println(c.getAddress().getRow());
		    if ((c.getAddress().getRow() != 0)) {
			databaseInterface.insertColumn(sharedArea
				.getTableName(), (sharedArea.getFinalCell()
				.getColumn() + 1), c.getContent(), "Column"
				+ c.getAddress().getColumn());
			tmp = copyDatabaseRow(info, c.getAddress().getRow());
			databaseInterface.update(sharedArea.getTableName(),
				getColumnsContent(c), tmp, (sharedArea
					.getFinalCell().getColumn() + 1));
		    } else {
			databaseInterface.insertColumn(sharedArea
				.getTableName(), (sharedArea.getFinalCell()
				.getColumn() + 1), c.getContent(), c
				.getContent());
		    }
		    addr = new Address(
			    sharedArea.getFinalCell().getColumn() + 1,
			    sharedArea.getFinalCell().getRow());
		    sharedArea.setFinalCell(addr);
		    updatesOnDatabase.setSharedArea(sharedArea);
		    break;
		case 2:
		    databaseInterface.insert(sharedArea.getTableName(),
			    getColumnsName(c), c.getContent(), getColumnsName());
		    addr = new Address(sharedArea.getFinalCell().getColumn(),
			    sharedArea.getFinalCell().getRow() + 1);
		    sharedArea.setFinalCell(addr);
		    break;
		default:
		    System.out.println("Default");
		    break;
		}

	    }

	    private String[] copyDatabaseRow(String[][] info, int row) {
		String tmp[] = new String[info[0].length + 1];
		for (int i = 0; i < info.length; i++) {
		    System.out.println(info[i][row]);
		    tmp[i] = info[i][row];
		}
		return tmp;
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
	//updatesOnDatabase = new CheckUpdatesOnDatabase(uiController, sharedArea);
    }
    public static void setSharedArea(DatabaseSharedArea sharedArea) {
	// TODO Auto-generated method stub
	
    }
}

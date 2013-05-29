package csheets.ext.db;

import csheets.core.Cell;

public class DatabaseExportController {
    private DatabaseExportInterface driver; // database driver
    private String tableName; // table name
    private String database; // database name
    private Cell[][] cells; // cells
    private boolean createTable;
    private boolean justSelection;

    public DatabaseExportController() {

    }

    public void setDriver(DatabaseExportInterface driver) {
	this.driver = driver;
    }

    public void setTableName(String tableName) {
	this.tableName = tableName;
    }

    public void setDatabase(String database) {
	this.database = database;
    }

    public void setCells(Cell[][] cells) {
	this.cells = cells;
    }

    public void setCreateTable(boolean createTable) {
	this.createTable = createTable;
    }

    public void setJustSelection(boolean justSelection) {
	this.justSelection = justSelection;
    }

    public DatabaseExportInterface getDriver() {
	return driver;
    }

    public String getTableName() {
	return tableName;
    }

    public String getDatabase() {
	return database;
    }

    public Cell[][] getCells() {
	return cells;
    }

    public boolean getCreateTable() {
	return createTable;
    }

    public boolean getJustSelection() {
	return justSelection;
    }

    public void export() {
	DatabaseExportBuilder exportBuilder = new DatabaseExportBuilder(getDriver());
	exportBuilder.setCreateTable(getCreateTable());
	exportBuilder.setDatabase(getDatabase());
	exportBuilder.setTableName(getTableName());
	final Cell[][] selectedCells = getCells();
	final int rowCount = selectedCells.length - 1;
	if (rowCount < 1)
	    return;
	final int columnCount = selectedCells[0].length;
	String[] columns = new String[columnCount];
	for (int i = 0; i < columnCount; i++) {
	    final String columnName = selectedCells[0][i].getValue().toString();
	    columns[i] = columnName.length() == 0 ? "col" + (i + 1)
		    : columnName;
	}
	exportBuilder.setColumns(columns);
	String[][] values = new String[rowCount][columnCount];
	for (int y = 0; y < rowCount; y++) {
	    for (int x = 0; x < columnCount; x++) {
		values[y][x] = selectedCells[y + 1][x].getValue().toString();
	    }
	}
	exportBuilder.setValues(values);
	exportBuilder.export();

    }

}

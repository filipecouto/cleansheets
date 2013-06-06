package csheets.ext.db;


import csheets.core.Cell;
import csheets.core.Spreadsheet;
import java.util.List;

public class DatabaseExportController {
    private DatabaseInterface driver; // database driver
    private String tableName; // table name
    private String database; // database name
    private String[] columns;
    private String[][] values;
    private boolean createTable;
    private boolean justSelection;
    private List<String> primaryKeys;

    public DatabaseExportController() {

    }

    public void setDriver(DatabaseInterface driver) {
	this.driver = driver;
    }

    public void setTableName(String tableName) {
	this.tableName = tableName;
    }

    public void setDatabase(String database) {
	this.database = database;
    }

    public void setCells(Cell[][] selectedCells) {
	final int rowCount = selectedCells.length - 1;
	if (rowCount < 1)
	    return;
	final int columnCount = selectedCells[0].length;
	String[] columns = new String[columnCount];
	for (int i = 0; i < columnCount; i++) {
	    final String columnName = selectedCells[0][i].getValue().toString();
	    columns[i] = columnName.length() == 0 ? "Column" + (i + 1)
		    : columnName;
	}
	setColumns(columns);
	String[][] values = new String[rowCount][columnCount];
	for (int y = 0; y < rowCount; y++) {
	    for (int x = 0; x < columnCount; x++) {
		values[y][x] = selectedCells[y + 1][x].getValue().toString();
	    }
	}
	setValues(values);
    }
    
    public void setCells(Spreadsheet sheet) {
	final int rowCount = sheet.getRowCount() - 1;
	if(rowCount < 1) {
	    return;
	}
	final int columnCount = sheet.getColumnCount() + 1;
	String [] columns = new String[columnCount];
	String [][] values = new String[rowCount][columnCount];
	for(int i=0;i < columnCount; i++) {
	    final String columnName = sheet.getCell(i, 0).getValue().toString();
	    columns[i] = columnName.length() == 0 ? "Column" + (i + 1)
		    : columnName;
	}
	setColumns(columns);
	for(int y = 0; y < rowCount; y++) {
	    for(int x = 0; x < columnCount; x++) {
		values[y][x] = sheet.getCell(x, y+1).getValue().toString();
	    }
	}
	setValues(values);
    }
    
    public void setValues(String[][] values) {
	this.values = values;
    }
    
    public void setColumns(String[] columns) {
	this.columns = columns;
    }

    public void setCreateTable(boolean createTable) {
	this.createTable = createTable;
    }

    public void setJustSelection(boolean justSelection) {
	this.justSelection = justSelection;
    }
    
    public void setPrimaryKeys(List<String> primaryKeys){
        this.primaryKeys=primaryKeys;
    }

    public DatabaseInterface getDriver() {
	return driver;
    }

    public String getTableName() {
	return tableName;
    }

    public String getDatabase() {
	return database;
    }
    
    public String[] getColumns() {
	return columns;
    }

    public String[][] getValues() {
	return values;
    }

    public boolean getCreateTable() {
	return createTable;
    }

    public boolean getJustSelection() {
	return justSelection;
    }

    public void export() {
	DatabaseExportBuilder exportBuilder = new DatabaseExportBuilder(driver);
	exportBuilder.setCreateTable(createTable);
	exportBuilder.setDatabase(database);
	exportBuilder.setTableName(tableName);
	exportBuilder.setColumns(columns);
	exportBuilder.setValues(values);
        exportBuilder.setPrimaryKeys(primaryKeys);
	exportBuilder.export();
    }
}

package csheets.ext.db;

import java.util.List;

/**
 * Class in charge of communicating with the DatabaseInterface drivers in
 * order to export data according to its attributes.
 * 
 * @author gil_1110484 & Filipe_1110688
 */
public class DatabaseExportBuilder {
    private DatabaseInterface driver; // database driver
    private String tableName; // table name
    private String database; // database name
    private String[] columns; // columns for the table
    private String[][] values; // values for the table
    private boolean createTable;
    private List<String> primaryKeys;

    public DatabaseExportBuilder(DatabaseInterface driver) {
	setDriver(driver);
    }

    // getters & setters
    public DatabaseInterface getDriver() {
	return driver;
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

    public String getTableName() {
	return tableName;
    }

    public boolean getCreateTable() {
	return createTable;
    }

    public void setDriver(DatabaseInterface driver) {
	this.driver = driver;
    }

    public void setDatabase(String database) {
	if (database == null || database.length() == 0) {
	    throw new RuntimeException("Database not specified");
	}
	this.database = database;
    }

    public void setColumns(String[] columns) {
	this.columns = columns;
    }

    public void setValues(String[][] values) {
	this.values = values;
    }

    public void setTableName(String tableName) {
	if (tableName == null || tableName.length() == 0) {
	    throw new RuntimeException("Table name not specified");
	}
	this.tableName = tableName;
    }

    public void setCreateTable(boolean createTable) {
	this.createTable = createTable;
    }
    
    public void setPrimaryKeys(List<String> primaryKeys){
        this.primaryKeys = primaryKeys;
    }

    /**
     * Proceeds to the exportation to the database, checks if creation of table
     * is required
     */
    public void export() {
	// TODO maybe check if everything is ready to export, throw a
	// RuntimeException if not
	driver.openDatabase(database);
	if (createTable) {
	    if (!driver.createTable(tableName, columns, primaryKeys)) {
		throw new RuntimeException("Error inserting");
	    }
	}
	for (String[] line : values)
	    driver.addLine(tableName, line);
	driver.closeDatabase();
    }
}

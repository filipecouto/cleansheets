package csheets.ext.db;

/**
 * Class that is in charge of building the database driver on which the information will be stored
 * 
 * @author gil_1110484 & Filipe_1110688
 */

public class DatabaseExportBuilder {
	// variables
	private DatabaseExportInterface driver; // database driver
	private String tableName; // table name
	private String database; // database name
	private String[] columns; // columns for the table
	private String[][] values; // values for the table

	public DatabaseExportBuilder(DatabaseExportInterface driver) {
		setDriver(driver);
	}

	// getters & setters
	public DatabaseExportInterface getDriver() {
		return this.driver;
	}

	public String getDatabase() {
		return this.database;
	}

	public String[] getColumns() {
		return this.columns;
	}

	public String[][] getValues() {
		return this.values;
	}

	public String getTableName() {
		return tableName;
	}

	public void setDriver(DatabaseExportInterface driver) {
		this.driver = driver;
	}

	public void setDatabase(String database) {
		if (database == null || database.length() == 0) { throw new RuntimeException("Database not specified"); }
		this.database = database;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public void setValues(String[][] values) {
		this.values = values;
	}

	public void setTableName(String tableName) {
		if (tableName == null || tableName.length() == 0) { throw new RuntimeException("Table name not specified"); }
		this.tableName = tableName;
	}

	public void export() {
		driver.openDatabase(database);
		driver.createTable(tableName, columns);
		for (String[] line : values)
			driver.addLine(tableName, line);
		driver.closeDatabase();
	}
}

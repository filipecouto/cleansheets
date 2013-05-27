package csheets.ext.db;

/**
 * Class that is in charge of building the database driver on which the information will be stored
 * @author gil_1110484 & Filipe_1110688
 */

public class DatabaseExportBuilder {

	// variables
	private String driver; //database driver
	private String database; //database name
	private String[] columns; //columns for the table
	private String[][] values; //values for the table
	
	// constructors
	public DatabaseExportBuilder(String driver, String database, String[] columns, String[][] values) {
		setDriver(driver);
		setDatabase(database);
		setColumns(columns);
		setValues(values);
	}
	
	public DatabaseExportBuilder(String driver) {
		setDriver(driver);
	}
	
	//getters & setters
	public String getDriver() {
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
	
	public void setDriver(String driver) {
		this.driver = driver;
	}
	
	public void setDatabase(String database) {
		this.database = database;
	}
	
	public void setColumns(String[] columns) {
		this.columns = columns;
	}
	
	public void setValues(String[][] values) {
		this.values = values;
	}
	
	public void export() {
	
	}
	
}

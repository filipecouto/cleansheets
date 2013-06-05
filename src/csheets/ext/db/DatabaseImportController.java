import csheets.core.Cell;
import csheets.core.Spreadsheet;

public class DatabaseImportController {
    private DatabaseInterface driver; // database driver
    private String tableName; // table name
    private String database; // database name
  

    public DatabaseImportController() {

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

    public DatabaseInterface getDriver() {
	return driver;
    }

    public String getTableName() {
	return tableName;
    }

    public String getDatabase() {
	return database;
    }
    
    /*public void export() {
	DatabaseExportBuilder exportBuilder = new DatabaseExportBuilder(driver);
	exportBuilder.setCreateTable(createTable);
	exportBuilder.setDatabase(database);
	exportBuilder.setTableName(tableName);
	exportBuilder.setColumns(columns);
	exportBuilder.setValues(values);
	exportBuilder.export();
    }*/
    
    public String[] getTables() {
	driver.openDatabase(database);
	String [] tables = driver.getTables();
	return tables;
    }
}

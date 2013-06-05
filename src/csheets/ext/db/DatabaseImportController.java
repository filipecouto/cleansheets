package csheets.ext.db;

import java.util.List;

/*
 * 
 * @author Filipe_1110687 & Filipe_1110688
 */

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
    
    public void importM() {
        driver.openDatabase(database);
    }
    
    public List<String> getTables() {
	driver.openDatabase(database);
	List<String> tables = driver.getTables();
	return tables;
    }
}

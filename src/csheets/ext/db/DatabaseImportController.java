package csheets.ext.db;

import java.util.List;

import csheets.core.Cell;
import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.core.formula.compiler.FormulaCompilationException;

/*
 * 
 * @author Filipe_1110687 & Filipe_1110688
 */

public class DatabaseImportController {
    private DatabaseInterface driver; // database driver
    private String tableName; // table name
    private String database; // database name
    private Spreadsheet table;
    private Cell cell;
    private boolean importToCurrentSheet;

    public DatabaseImportController() {

    }
    
    public void setCell(Cell cell) {
	this.cell = cell;
    }
    
    public void setSpreadsheet(Spreadsheet table) {
	this.table = table;
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
    
    public void setImportToCurrentSheet(boolean importToCurrentSheet){
        this.importToCurrentSheet=importToCurrentSheet;
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
    
    public String [][] importM() {
        driver.openDatabase(database);
        String [][] info = driver.getData(tableName);
        driver.closeDatabase();
        return info;
    }
    
    public List<String> getTables() {
	driver.openDatabase(database);
	List<String> tables = driver.getTables();
	return tables;
    }
}

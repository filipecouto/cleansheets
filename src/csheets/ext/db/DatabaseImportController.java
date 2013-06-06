package csheets.ext.db;

import java.util.List;

import csheets.core.Cell;
import csheets.core.Spreadsheet;
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
        String [][] info = driver.getData(tableName);
        int i=0,j=0;
        int cellCol,cellRow;
        cellCol=cell.getAddress().getColumn();
        cellRow=cell.getAddress().getRow();
        try{
            for(i=0;i<info.length;i++){
                for(j=0;j<info[0].length;j++){
                    table.getCell(cellCol+j, cellRow+i).setContent(info[i][j].toString());
                }
            }
        }catch(FormulaCompilationException e){
            System.out.println(e);
        }
        driver.closeDatabase();
    }
    
    public List<String> getTables() {
	driver.openDatabase(database);
	List<String> tables = driver.getTables();
	return tables;
    }
}

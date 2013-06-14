package csheets.ext.db;

import java.sql.Connection;

import org.dbunit.database.DatabaseConnection;

import csheets.core.Address;
import csheets.ext.db.ui.OnDatabaseInteractionListener;
import csheets.ext.rtc.OnShareFoundListener;

/**
 * @author Rita Nogueira
 * 
 */
public class DatabaseSharedArea {

    private Address initialCell;
    private Address finalCell;
    private String tableName;
    private DatabaseInterface database;
    private String databaseName;
    private int spreadsheetNumber;

    public DatabaseSharedArea(Address initialCell, Address finalCell,
	    String tableName, DatabaseInterface database, String databaseName,
	    int spreadsheetNumber) {
	this.initialCell = initialCell;
	this.finalCell = finalCell;
	this.databaseName = databaseName;
	this.database = database;
	this.tableName = tableName;
	this.spreadsheetNumber = spreadsheetNumber;
    }

    public Address getInitialCell() {
	return initialCell;
    }

    public void setInitialCell(Address initialCell) {
	this.initialCell = initialCell;
    }

    public Address getFinalCell() {
	return finalCell;
    }

    public void setFinalCell(Address finalCell) {
	this.finalCell = finalCell;
    }

    public boolean isInSharedArea(Address address) {
	return initialCell.getColumn() <= address.getColumn()
		&& initialCell.getRow() <= address.getRow()
		&& finalCell.getColumn() >= address.getColumn()
		&& finalCell.getRow() >= address.getRow();

    }

    public boolean isNextToSharedArea(Address address) {
	System.out.println("Coluna: " + address.getColumn() + " Linha: "
		+ address.getRow());
	System.out.println("Coluna: " + (finalCell.getColumn() + 1)
		+ " Linha: " + (finalCell.getRow() + 1));
	return (finalCell.getColumn() + 1) == address.getColumn()
		|| (finalCell.getRow() + 1) == address.getRow();
    }

    public String getTableName() {
	return tableName;
    }

    public void setTableName(String tableName) {
	this.tableName = tableName;
    }

    public int whereIsCell(Address address) {
	if ((finalCell.getColumn() + 1) == address.getColumn()) {
	    return 1;
	}
	if ((finalCell.getRow() + 1) == address.getRow()) {
	    return 2;
	}
	return 0;

    }

    public DatabaseInterface getDatabase() {
	return database;
    }

    public void setDatabase(DatabaseInterface database) {
	this.database = database;
    }

    public String getDatabaseName() {
	return databaseName;
    }

    public void setDatabaseName(String databaseName) {
	this.databaseName = databaseName;
    }

    public int getSpreadsheetNumber() {
	return spreadsheetNumber;
    }

    public void setSpreadsheetNumber(int spreadsheetNumber) {
	this.spreadsheetNumber = spreadsheetNumber;
    }

}

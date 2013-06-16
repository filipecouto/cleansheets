package csheets.ext.db;

import java.sql.Connection;

import org.dbunit.database.DatabaseConnection;

import csheets.core.Address;
import csheets.ext.db.ui.OnDatabaseInteractionListener;
import csheets.ext.rtc.OnShareFoundListener;
import csheets.ui.ctrl.UIController;

/**
 * Class to store information about the sheet and the base data that is stored
 * 
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

    /**
     * Method for determining whether the cell is within the area stored in the
     * database
     * 
     * @param address
     * @return
     */
    public boolean isInSharedArea(Address address) {
	return initialCell.getColumn() <= address.getColumn()
		&& initialCell.getRow() <= address.getRow()
		&& finalCell.getColumn() >= address.getColumn()
		&& finalCell.getRow() >= address.getRow();

    }

    /**
     * Method for testing if the cell is next to the area
     * 
     * @param address
     * @return
     */
    public boolean isNextToSharedArea(Address address) {
	return (finalCell.getColumn() + 1) == address.getColumn()
		|| (finalCell.getRow() + 1) == address.getRow();
    }

    public String getTableName() {
	return tableName;
    }

    public void setTableName(String tableName) {
	this.tableName = tableName;
    }

    /**
     * Method for test if the cell was added in the next row or the next column
     * 
     * @param address
     * @return
     */
    public int whereIsCell(Address address) {
	/*
	 * After a group discussion and a thorough analysis of the statement,
	 * was decided that the creation of new columns would not be considered
	 * because the statement speaks of records. Records are the contents of
	 * a column and not a new column.This method is prepared to determine
	 * the need to create a new column, but this functionality is not
	 * implemented.
	 */
	if ((finalCell.getRow() + 1) == address.getRow()) {
	    return 1;
	}
	if ((finalCell.getColumn() + 1) == address.getColumn()) {
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

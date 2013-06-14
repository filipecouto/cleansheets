package csheets.ext.db.ui;

import csheets.core.Address;
import csheets.ext.db.DatabaseInterface;

public interface OnDatabaseInteractionListener {
    public void onDatabaseInteraction(DatabaseInterface database,
	    String databaseName, Address initialCell, Address finalCell,
	    String tableName, int spreadsheetNumber);

}

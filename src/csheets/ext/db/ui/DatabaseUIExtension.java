package csheets.ext.db.ui;

import javax.swing.JMenu;

import csheets.SpreadsheetAppEvent;
import csheets.SpreadsheetAppListener;
import csheets.core.Address;
import csheets.ext.db.CheckUpdatesOnDatabase;
import csheets.ext.db.CheckUpdatesOnSheet;
import csheets.ext.db.DatabaseExtension;
import csheets.ext.db.DatabaseInterface;
import csheets.ext.db.DatabaseSharedArea;
import csheets.ui.ctrl.EditEvent;
import csheets.ui.ctrl.EditListener;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

public class DatabaseUIExtension extends UIExtension implements
	OnDatabaseInteractionListener {
    private static DatabaseSharedArea sharedArea;
    private CheckUpdatesOnDatabase updatesOnDatabase;
    private ListenerOfEdition editListener;
    private CheckUpdatesOnSheet updatesOnSheet;
    private UIController uiController;

    private class ListenerOfEdition implements EditListener {

	@Override
	public void workbookModified(EditEvent event) {
	    if (sharedArea != null) {
		updatesOnSheet.startChecking();
	    }
	}

    }

    public DatabaseUIExtension(DatabaseExtension extension,
	    final UIController uiController) {
	super(extension, uiController);
	this.uiController = uiController;
	uiController.addWorkbookListener(new SpreadsheetAppListener() {

	    @Override
	    public void workbookUnloaded(SpreadsheetAppEvent event) {
		sharedArea = null;
	    }

	    @Override
	    public void workbookSaved(SpreadsheetAppEvent event) {

	    }

	    @Override
	    public void workbookLoaded(SpreadsheetAppEvent event) {

	    }

	    @Override
	    public void workbookCreated(SpreadsheetAppEvent event) {

	    }
	});
	editListener = new ListenerOfEdition();
	uiController.addEditListener(editListener);
    }

    @Override
    public JMenu getMenu() {
	return new DatabaseUIMenu((DatabaseExtension) extension, this);
    }

    @Override
    public void onDatabaseInteraction(DatabaseInterface database,
	    String databaseName, Address initialCell, Address finalCell,
	    String tableName, int spreadsheetNumber) {
	sharedArea = new DatabaseSharedArea(initialCell, finalCell, tableName,
		database, databaseName, spreadsheetNumber);
	uiController.removeEditListener(editListener);
	updatesOnDatabase = new CheckUpdatesOnDatabase(uiController, sharedArea);
	updatesOnSheet = new CheckUpdatesOnSheet(uiController, sharedArea,updatesOnDatabase);
	uiController.addEditListener(editListener);
    }

    public static void setSharedArea(DatabaseSharedArea area) {
	CheckUpdatesOnSheet.setSharedArea(area);
    }
}

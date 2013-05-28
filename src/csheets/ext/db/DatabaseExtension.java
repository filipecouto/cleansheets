package csheets.ext.db;

import java.util.List;

import csheets.ext.Extension;
import csheets.ext.db.ui.DatabaseUIExtension;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

/**
 * This extension allows the user to export a range of cells in the active spreadsheet.
 * 
 * @author gil_1110484
 */
public class DatabaseExtension extends Extension {
    public DatabaseExtension() {
	super("Database Export");
    }

    /**
     * Gets all available export drivers (DatabaseExportInterface's) gathering them from the DatabaseDriverManager
     * @return A list containing all available drivers
     */
    public List<DatabaseExportInterface> getAvailableDrivers() {
	return DatabaseDriverManager.getInstance().getAvailableDrivers();
    }

    @Override
    public UIExtension getUIExtension(UIController uiController) {
	return new DatabaseUIExtension(this, uiController);
    }
}

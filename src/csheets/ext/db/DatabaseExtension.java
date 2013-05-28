package csheets.ext.db;

import java.util.List;

import csheets.ext.Extension;
import csheets.ext.db.ui.DatabaseUIExtension;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

public class DatabaseExtension extends Extension {
    public DatabaseExtension() {
	super("Database Export");
    }

    public List<DatabaseExportInterface> getAvailableDrivers() {
	return DatabaseDriverManager.getInstance().getAvailableDrivers();
    }

    @Override
    public UIExtension getUIExtension(UIController uiController) {
	return new DatabaseUIExtension(this, uiController);
    }
}

package csheets.ext.db;

import csheets.ext.Extension;
import csheets.ext.db.ui.DatabaseUIExtension;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

public class DatabaseExtension extends Extension {
	private DatabaseExportInterface[] availableDrivers;

	public DatabaseExtension() {
		super("Database Export");
	}

	public DatabaseExportInterface[] getAvailableDrivers() {
		if (availableDrivers == null) {
			// lazily load the drivers list
			availableDrivers = new DatabaseExportInterface[] { new H2Exporter() };
		}
		return availableDrivers;
	}

	@Override
	public UIExtension getUIExtension(UIController uiController) {
		return new DatabaseUIExtension(this, uiController);
	}
}

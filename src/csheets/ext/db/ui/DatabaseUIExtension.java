package csheets.ext.db.ui;

import javax.swing.JMenu;

import csheets.ext.db.DatabaseExtension;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

public class DatabaseUIExtension extends UIExtension {
    public DatabaseUIExtension(DatabaseExtension extension,
	    UIController uiController) {
	super(extension, uiController);
    }

    @Override
    public JMenu getMenu() {
	return new DatabaseUIMenu((DatabaseExtension) extension);
    }
}

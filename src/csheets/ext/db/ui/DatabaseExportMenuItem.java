package csheets.ext.db.ui;

import java.awt.event.ActionEvent;

import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.ext.db.DatabaseExtension;
import csheets.ui.ctrl.FocusOwnerAction;

public class DatabaseExportMenuItem extends FocusOwnerAction {
    private DatabaseExtension extension;

    private DatabaseExportDialog dialog;

    public DatabaseExportMenuItem(DatabaseExtension extension) {
	this.extension = extension;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	if (dialog == null) {
	    dialog = new DatabaseExportDialog(extension);
	}
	dialog.prepareDialog(focusOwner);
	dialog.setVisible(true);
    }

    @Override
    protected String getName() {
	return "Export";
    }

}

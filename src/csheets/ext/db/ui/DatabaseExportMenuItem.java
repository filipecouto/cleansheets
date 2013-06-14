package csheets.ext.db.ui;

import java.awt.event.ActionEvent;

import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.ext.db.DatabaseExtension;
import csheets.ui.ctrl.FocusOwnerAction;

public class DatabaseExportMenuItem extends FocusOwnerAction {
    private DatabaseExtension extension;

    private DatabaseExportDialog dialog;

    private OnDatabaseInteractionListener interactionListener ;

    public DatabaseExportMenuItem(DatabaseExtension extension, OnDatabaseInteractionListener interactionListener) {
	this.extension = extension;
	this.interactionListener =interactionListener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	//TODO 
	//make sure if grid is not empty
	if (dialog == null) {
	    dialog = new DatabaseExportDialog(extension);
	    dialog.setListener(interactionListener);	
	}
	dialog.prepareDialog(focusOwner);
	dialog.setVisible(true);
    }

    @Override
    protected String getName() {
	return "Export";
    }

}

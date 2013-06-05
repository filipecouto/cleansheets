package csheets.ext.db.ui;

import java.awt.event.ActionEvent;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import csheets.ext.db.DatabaseExtension;
import csheets.ui.ctrl.FocusOwnerAction;

/*
 * 
 * @author Filipe Silva
 */

public class DatabaseImportMenuItem extends FocusOwnerAction {
    private DatabaseExtension extension;

    private DatabaseImportDialog dialog;

    public DatabaseImportMenuItem(DatabaseExtension extension) {
	this.extension = extension;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	if (dialog == null) {
	    dialog = new DatabaseImportDialog(extension, focusOwner);
	}
	dialog.prepareDialog(focusOwner);
	dialog.setVisible(true);
    }

    @Override
    protected String getName() {
	return "Import";
    }

}

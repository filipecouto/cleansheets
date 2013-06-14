package csheets.ext.db.ui;

import java.awt.event.ActionEvent;
import java.sql.Connection;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import csheets.core.Address;
import csheets.ext.db.DatabaseExtension;
import csheets.ext.db.DatabaseSharedArea;
import csheets.ui.ctrl.FocusOwnerAction;

/*
 * 
 * @author Filipe Silva
 */

public class DatabaseImportMenuItem extends FocusOwnerAction {
    private DatabaseExtension extension;

    private DatabaseImportDialog dialog;

    private OnDatabaseInteractionListener interactionListener;

    public DatabaseImportMenuItem(DatabaseExtension extension,
	    OnDatabaseInteractionListener interactionListener) {
	this.extension = extension;
	this.interactionListener = interactionListener;
	
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	if (dialog == null) {
	    dialog = new DatabaseImportDialog(extension, focusOwner);
	    dialog.setListener(interactionListener);	    
	}
	dialog.prepareDialog(focusOwner);
	dialog.setVisible(true);
    }

    @Override
    protected String getName() {
	return "Import";
    }

}

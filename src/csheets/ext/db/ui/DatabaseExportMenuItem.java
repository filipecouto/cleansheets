package csheets.ext.db.ui;

import java.awt.event.ActionEvent;

import csheets.ui.ctrl.FocusOwnerAction;
import csheets.ui.ctrl.UIController;

public class DatabaseExportMenuItem extends FocusOwnerAction {
	private UIController uiController;

	private DatabaseExportDialog dialog;

	public DatabaseExportMenuItem(UIController uiController) {
		this.uiController = uiController;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (dialog == null) dialog = new DatabaseExportDialog(uiController);
		dialog.prepareDialog(focusOwner);
		dialog.setVisible(true);
	}

	@Override
	protected String getName() {
		return "Export";
	}

}

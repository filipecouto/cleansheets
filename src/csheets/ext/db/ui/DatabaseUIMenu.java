package csheets.ext.db.ui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import csheets.ui.ctrl.UIController;

public class DatabaseUIMenu extends JMenu {
	private UIController uiController;

	private JMenuItem itemExport;

	public DatabaseUIMenu(UIController uiController) {
		super("Database");

		this.uiController = uiController;

		itemExport = new JMenuItem(new DatabaseExportMenuItem(uiController));
		add(itemExport);
	}
}

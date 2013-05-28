package csheets.ext.db.ui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import csheets.ext.db.DatabaseExtension;

public class DatabaseUIMenu extends JMenu {
    private static final long serialVersionUID = 2854862500115559859L;

    private JMenuItem itemExport;

    public DatabaseUIMenu(DatabaseExtension extension) {
	super("Database");

	// this.extension = extension;

	itemExport = new JMenuItem(new DatabaseExportMenuItem(extension));
	add(itemExport);
    }
}

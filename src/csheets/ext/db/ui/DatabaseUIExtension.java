package csheets.ext.db.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import csheets.ext.Extension;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

public class DatabaseUIExtension extends UIExtension {
	public DatabaseUIExtension(Extension extension, UIController uiController) {
		super(extension, uiController);
	}

	@Override
	public JMenu getMenu() {
		JMenu menu = new JMenu("Database");
		menu.add(new JMenuItem("Export"));
		menu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		return menu;
	}
}

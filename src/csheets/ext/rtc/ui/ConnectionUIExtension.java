package csheets.ext.rtc.ui;

import javax.swing.JMenu;

import csheets.ext.Extension;
import csheets.ext.db.DatabaseExtension;
import csheets.ext.db.ui.DatabaseUIMenu;
import csheets.ext.rtc.RealTimeCollaboration;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

public class ConnectionUIExtension extends UIExtension {

    public ConnectionUIExtension(Extension extension, UIController uiController) {
	super(extension, uiController);
	// TODO Auto-generated constructor stub
    }

    @Override
    public JMenu getMenu() {
	return new ConnectionUIMenu((RealTimeCollaboration) extension, uiController);
    }
}

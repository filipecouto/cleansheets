package csheets.ext.rtc.ui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import csheets.ext.rtc.RealTimeCollaboration;
import csheets.ui.ctrl.UIController;

public class ConnectionUIMenu extends JMenu {

    private JMenuItem shareMenuItem;
    private JMenuItem connectMenuItem;

    public ConnectionUIMenu(RealTimeCollaboration extension,
	    UIController uiController) {
	super("Real Time Collaboration");

	shareMenuItem = new JMenuItem(new ShareAction(extension, uiController));
	add(shareMenuItem);

	connectMenuItem = new JMenuItem(new ConnectAction(extension,
		uiController));
	add(connectMenuItem);
    }

}

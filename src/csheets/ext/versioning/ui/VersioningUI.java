package csheets.ext.versioning.ui;

import javax.swing.JComponent;

import csheets.ext.Extension;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

/**
 * VersioningExtension provides a side bar to present the available versions and
 * allow to manage them.
 * 
 * @author Gil Castro (gil_1110484)
 */
public class VersioningUI extends UIExtension {
	public VersioningUI(Extension extension, UIController uiController) {
		super(extension, uiController);
	}

	@Override
	public JComponent getSideBar() {
		return new VersioningSideBar(uiController);
	}
}

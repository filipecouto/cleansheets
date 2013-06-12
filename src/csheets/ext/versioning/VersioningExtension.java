package csheets.ext.versioning;

import csheets.ext.Extension;
import csheets.ext.versioning.ui.VersioningUI;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

public class VersioningExtension extends Extension {
	public VersioningExtension() {
		super("Versioning");
	}

	@Override
	public UIExtension getUIExtension(UIController uiController) {
		return new VersioningUI(this, uiController);
	}
}

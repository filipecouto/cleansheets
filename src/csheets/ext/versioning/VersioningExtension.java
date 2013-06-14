package csheets.ext.versioning;

import csheets.ext.Extension;
import csheets.ext.versioning.ui.VersioningUI;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

/**
 * This extension provides a GUI to let the user consult different versions of a
 * Workbook and revert to those. The extension relies on VersionControllerCodec
 * which is currently supported by the XMLCodec and can be easily extended to
 * other Codecs.
 * 
 * @author Gil Castro (gil_1110484)
 */
public class VersioningExtension extends Extension {
	public VersioningExtension() {
		super("Versioning");
	}

	@Override
	public UIExtension getUIExtension(UIController uiController) {
		return new VersioningUI(this, uiController);
	}
}

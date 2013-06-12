package csheets.ext.versioning.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import csheets.io.versioning.VersionControllerCodec;
import csheets.io.versioning.VersionInfo;

public class VersionsListAdapter implements ListModel<String> {
	private VersionControllerCodec versionController;
	private ListDataListener listener;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("d/MMM H:mm");

	private List<String> items = new ArrayList<String>();
	private List<VersionInfo> versions = new ArrayList<VersionInfo>();

	@Override
	public void addListDataListener(ListDataListener l) {
		listener = l;
	}

	@Override
	public String getElementAt(int index) {
		return items.get(index);
	}
	
	public VersionInfo getVersionAt(int index) {
		return versions.get(index);
	}

	@Override
	public int getSize() {
		return items.size();
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listener = null;
	}

	public void setVersionController(VersionControllerCodec controller) {
		versionController = controller;
		items.clear();
		if (versionController != null) {
			versions = versionController.getVersions();
			if (versions != null) {
				for (VersionInfo version : versions) {
					items.add(dateFormat.format(version.getDate()) + " - " + version.getSpreadsheetCount() + " sheet(s)");
				}
			}
		}
		if (listener != null) {
			listener.contentsChanged(null);
		}
	}
}

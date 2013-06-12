package csheets.ext.versioning.ui;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import csheets.SpreadsheetAppEvent;
import csheets.SpreadsheetAppListener;
import csheets.core.Workbook;
import csheets.ext.Extension;
import csheets.ui.ctrl.SelectionEvent;
import csheets.ui.ctrl.SelectionListener;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

public class VersioningUI extends UIExtension implements SpreadsheetAppListener {
	private UIController uiController;

	private JPanel sideBar;
	private JScrollPane pane;
	private JList<String> versionsList;
	private JLabel info;

	private VersionsListAdapter adapter;

	private Workbook currentBook;

	public VersioningUI(Extension extension, UIController uiController) {
		super(extension, uiController);
		this.uiController = uiController;
		uiController.addWorkbookListener(this);
		uiController.addSelectionListener(new SelectionListener() {
			@Override
			public void selectionChanged(SelectionEvent event) {
				if (currentBook != event.getWorkbook()) {
					currentBook = event.getWorkbook();
					loadVersion(currentBook);
				}
			}
		});
	}

	@Override
	public JComponent getSideBar() {
		if (sideBar == null) {
			sideBar = new JPanel();
			sideBar.setName("Version Control");
			sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));

			adapter = new VersionsListAdapter();

			versionsList = new JList<String>(adapter);
			versionsList.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent event) {
					if (!event.getValueIsAdjusting()) {
						uiController.setActiveWorkbook(adapter.getVersionAt(event.getFirstIndex()).loadVersion(null));
					}
				}
			});
			pane = new JScrollPane(versionsList);

			info = new JLabel(
					"<html><center>This format doesn't support version control, please save this file in a format that supports it (XML for instance) in order to control its versions.</center></html>");

			sideBar.add(info);// , BorderLayout.NORTH);
			sideBar.add(pane);// , BorderLayout.CENTER);

			loadVersion(null);
		}

		return sideBar;
	}

	private void loadVersion(Workbook book) {
		final boolean supported = book != null && book.getVersionController() != null;
		if (supported) {
			adapter.setVersionController(book.getVersionController());
		}
		info.setVisible(!supported);
		pane.setVisible(supported);
	}

	@Override
	public void workbookCreated(SpreadsheetAppEvent event) {
		loadVersion(event.getWorkbook());
	}

	@Override
	public void workbookLoaded(SpreadsheetAppEvent event) {
		loadVersion(event.getWorkbook());
	}

	@Override
	public void workbookUnloaded(SpreadsheetAppEvent event) {
		loadVersion(uiController.getActiveWorkbook());
	}

	@Override
	public void workbookSaved(SpreadsheetAppEvent event) {
		loadVersion(event.getWorkbook());
	}
}

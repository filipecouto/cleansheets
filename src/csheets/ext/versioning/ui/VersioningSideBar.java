package csheets.ext.versioning.ui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import csheets.SpreadsheetAppEvent;
import csheets.SpreadsheetAppListener;
import csheets.core.Workbook;
import csheets.ext.versioning.VersioningController;
import csheets.ui.ctrl.EditEvent;
import csheets.ui.ctrl.EditListener;
import csheets.ui.ctrl.SelectionEvent;
import csheets.ui.ctrl.SelectionListener;
import csheets.ui.ctrl.UIController;

public class VersioningSideBar extends JPanel implements
		SpreadsheetAppListener {
	private static final long serialVersionUID = 7065800787321449295L;

	private UIController controller;

	private JScrollPane pane;
	private JList<String> versionsList;
	private JLabel info;

	private VersionsListAdapter adapter;

	private Workbook currentBook;

	private boolean shownInfo = false;
	private boolean justOpenedVersion = false;

	public VersioningSideBar(UIController uiController) {
		this.controller = uiController;
		uiController.addWorkbookListener(this);
		uiController.addSelectionListener(new SelectionListener() {
			@Override
			public void selectionChanged(SelectionEvent event) {
				if (currentBook != event.getWorkbook()) {
					currentBook = event.getWorkbook();
					loadVersions(currentBook);
				}
			}
		});
		uiController.addEditListener(new EditListener() {
			@Override
			public void workbookModified(EditEvent event) {
				if (justOpenedVersion
						&& event.getWorkbook().getVersionController() != null) {
					int option = JOptionPane
							.showConfirmDialog(
									null,
									"Would you like to set this version as the current one?\nYou still can come back to the current version after replacing it.\n\nIf you choose not to replace it, it will still be replaced if you save this file.",
									"Replace version", JOptionPane.YES_NO_OPTION);
					if (option == 0) {
						VersioningController.addVersion(event.getWorkbook(), null,
								event.getWorkbook());
						loadVersions(event.getWorkbook());
					}
					versionsList.clearSelection();
					justOpenedVersion = false;
				}
			}
		});
		setName("Version Control");
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		adapter = new VersionsListAdapter();

		versionsList = new JList<String>(adapter);
		versionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		versionsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting()) {
					final int selectedIndex = versionsList.getSelectedIndex();
					if (selectedIndex == -1) {
						return;
					}
					if (!shownInfo) {
						JOptionPane
								.showMessageDialog(
										null,
										"You're about to load another version of this file.\n"
												+ "If you want to revert to this version, please save the file.\n"
												+ "You can select the current version if you want to come back to it.",
										"Loading another version",
										JOptionPane.INFORMATION_MESSAGE);
						shownInfo = true;
					}
					justOpenedVersion = false;
					controller.setActiveWorkbook(VersioningController.getVersion(
							currentBook, adapter.getVersionAt(selectedIndex)));
					justOpenedVersion = selectedIndex != 0;
				}
			}
		});
		versionsList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					final int index = versionsList.locationToIndex(e.getPoint());

					JPopupMenu menu = new JPopupMenu();
					menu.add("Remove version").addActionListener(
							new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									int option = JOptionPane
											.showConfirmDialog(
													null,
													"Are you sure you want to remove this version?",
													"Remove Version",
													JOptionPane.YES_NO_OPTION);
									if (option == 0) {
										VersioningController.removeVersion(adapter
												.getVersionAt(index));
										loadVersions(currentBook);
										controller.setWorkbookModified(currentBook);
										justOpenedVersion = false;
									}
								}
							});

					Rectangle cellBounds = versionsList.getCellBounds(index, index);
					menu.show(versionsList, 0,
							(int) (cellBounds.y + cellBounds.getHeight()));
				}
			}
		});
		pane = new JScrollPane(versionsList);

		info = new JLabel(
				"<html><center>This format doesn't support version control, please save this file in a format that supports it (XML for instance) in order to control its versions.</center></html>");

		add(info);
		add(pane);

		loadVersions(null);
	}

	private void loadVersions(Workbook book) {
		final boolean supported = book != null
				&& book.getVersionController() != null;
		if (supported) {
			adapter.setVersionController(book.getVersionController());
		}
		info.setVisible(!supported);
		pane.setVisible(supported);
	}

	@Override
	public void workbookCreated(SpreadsheetAppEvent event) {
		loadVersions(event.getWorkbook());
	}

	@Override
	public void workbookLoaded(SpreadsheetAppEvent event) {
		loadVersions(event.getWorkbook());
	}

	@Override
	public void workbookUnloaded(SpreadsheetAppEvent event) {
		loadVersions(controller.getActiveWorkbook());
	}

	@Override
	public void workbookSaved(SpreadsheetAppEvent event) {
		loadVersions(event.getWorkbook());
	}
}

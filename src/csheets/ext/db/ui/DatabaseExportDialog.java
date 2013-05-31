package csheets.ext.db.ui;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import csheets.core.Cell;
import csheets.ext.db.DatabaseExportBuilder;
import csheets.ext.db.DatabaseExportController;
import csheets.ext.db.DatabaseExportInterface;
import csheets.ext.db.DatabaseExtension;
import csheets.ui.sheet.SpreadsheetTable;

public class DatabaseExportDialog extends JDialog {
    private JFileChooser fileChooser;
    private JTextField url;
    private JTextField tableName;
    private JComboBox<String> format;
    private JRadioButton exportWhole;
    private JRadioButton exportSelected;
    private JTextField username;
    private JTextField password;

    private JPanel panelButtons;

    private DatabaseExtension extension;

    private SpreadsheetTable table;

    public DatabaseExportDialog(DatabaseExtension extension) {
	super((JFrame) null, "Export to Database", true);

	this.extension = extension;

	getContentPane().setLayout(
		new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

	fileChooser = new JFileChooser();
	fileChooser.remove(fileChooser.getComponentCount() - 1);
	add(fileChooser);

	add(createOptionsPanel());
	add(createButtonsPanel());

	pack();
	setLocationRelativeTo(null);
    }

    private JPanel createButtonsPanel() {
	panelButtons = new JPanel();
	panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));
	JButton cancel = new JButton("Cancel");
	cancel.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
	    }
	});
	panelButtons.add(cancel);

	JButton ok = new JButton("Export");
	ok.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		export();
	    }
	});
	panelButtons.add(ok);
	return panelButtons;
    }

    /**
     * enables buttons depending on parameter
     * 
     * @param value
     */
    public void enableButtons(boolean value) {
	panelButtons.getComponent(0).setEnabled(value);
	panelButtons.getComponent(1).setEnabled(value);
    }

    /**
     * Creates a Dialog that is used to retrieve the exportation data from the
     * user in order to proceed to the exportation
     */
    private void export() {
	panelButtons.getComponent(0).setEnabled(false);
	panelButtons.getComponent(1).setEnabled(false);
	// force the GUI to redraw the window so the label can be seen
	revalidate();
	repaint();
	Thread exportThread = new Thread(new Runnable() {
	    DatabaseExportController exportController;

	    @Override
	    public void run() {
		if (exportController == null) {
		    exportController = new DatabaseExportController();
		    exportController.setDriver(extension.getAvailableDrivers()
			    .get(format.getSelectedIndex()));
		    if (exportWhole.isSelected()) {
			exportController.setCells(table.getSpreadsheet());
		    } else if (exportSelected.isSelected()) {
			exportController.setCells(table.getSelectedCells());
		    }
		    exportController.setCreateTable(true);
		    String dbUrl = url.getText();
		    if (!dbUrl.contains("/") && !dbUrl.contains("/"))
			dbUrl = fileChooser.getCurrentDirectory()
				.getAbsolutePath() + "/" + dbUrl;
		    exportController.setDatabase(dbUrl.length() == 0 ? fileChooser
			    .getSelectedFile().getAbsolutePath() : dbUrl);
		    exportController.setTableName(tableName.getText());
		}
		try {
		    exportController.export();
		} catch (Exception e) {
		    if (e.getMessage() != null
			    && e.getMessage().equals("Table already exists")) {
			Object[] options = { "Yes", "No" };
			int n = JOptionPane
				.showOptionDialog(
					getContentPane(),
					"Table already exists, would you like to append your data to the table? ",
					"Table exists",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null,
					options, options[1]);
			switch (n) {
			case 0:
			    exportController.setCreateTable(false);
			    run();
			    return;
			case 1:
			    enableButtons(true);
			    return;
			}
		    } else {
			e.printStackTrace();
			JOptionPane.showMessageDialog(getContentPane(),
				"An error has occured while exporting your table: "
					+ e.getMessage(), "Error",
				JOptionPane.PLAIN_MESSAGE);
		    }
		}
		enableButtons(true);
		setVisible(false);
	    }
	});
	exportThread.start();
    }

    private JPanel createOptionsPanel() {
	JPanel options = new JPanel();
	GroupLayout layout = new GroupLayout(options);
	options.setLayout(layout);
	layout.setAutoCreateGaps(true);
	layout.setAutoCreateContainerGaps(true);

	final JLabel lUrl = new JLabel("URL");
	final JLabel lTableName = new JLabel("Table name");
	final JLabel lFormat = new JLabel("Format");
	final JLabel lExport = new JLabel("Export");
	final JLabel lUserName = new JLabel("User name");
	final JLabel lPassword = new JLabel("Password");

	url = new JTextField();
	url.setText("Choose a file from above or type here a new file or the URL of the database");
	url.addFocusListener(new FocusListener() {
	    @Override
	    public void focusLost(FocusEvent arg0) {
	    }

	    @Override
	    public void focusGained(FocusEvent arg0) {
		url.selectAll();
	    }
	});
	tableName = new JTextField();

	final List<DatabaseExportInterface> availableDrivers = extension
		.getAvailableDrivers();

	String[] availableDriverNames = new String[availableDrivers.size()];
	for (int i = 0; i < availableDriverNames.length; i++) {
	    availableDriverNames[i] = availableDrivers.get(i).getName();
	}

	format = new JComboBox<String>(availableDriverNames);
	format.addItemListener(new ItemListener() {
	    @Override
	    public void itemStateChanged(ItemEvent e) {
		onFormatSelected(extension.getAvailableDrivers().get(
			format.getSelectedIndex()));
	    }
	});

	JPanel export = new JPanel();
	export.setLayout(new BoxLayout(export, BoxLayout.Y_AXIS));

	exportWhole = new JRadioButton("Whole current sheet");
	export.add(exportWhole);
	exportSelected = new JRadioButton("Selected area");
	export.add(exportSelected);

	username = new JTextField();
	username.setEnabled(false);

	password = new JTextField();
	password.setEnabled(false);

	layout.setHorizontalGroup(layout
		.createParallelGroup(GroupLayout.Alignment.LEADING)
		.addGroup(
			layout.createSequentialGroup().addComponent(lUrl)
				.addComponent(url))
		.addGroup(
			layout.createSequentialGroup().addComponent(lTableName)
				.addComponent(tableName))
		.addGroup(
			layout.createSequentialGroup().addComponent(lFormat)
				.addComponent(format))
		.addGroup(
			layout.createSequentialGroup().addComponent(lExport)
				.addComponent(export))
		.addGroup(
			layout.createSequentialGroup().addComponent(lUserName)
				.addComponent(username))
		.addGroup(
			layout.createSequentialGroup().addComponent(lPassword)
				.addComponent(password)));
	layout.setVerticalGroup(layout
		.createSequentialGroup()
		.addGroup(
			layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE)
				.addComponent(lUrl).addComponent(url))
		.addGroup(
			layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE)
				.addComponent(lTableName)
				.addComponent(tableName))
		.addGroup(
			layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE)
				.addComponent(lFormat).addComponent(format))
		.addGroup(
			layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE)
				.addComponent(lExport).addComponent(export))
		.addGroup(
			layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE)
				.addComponent(lUserName).addComponent(username))
		.addGroup(
			layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE)
				.addComponent(lPassword).addComponent(password)));

	ButtonGroup group = new ButtonGroup();
	group.add(exportSelected);
	group.add(exportWhole);

	// prepare GUI for this driver
	if (availableDrivers.size() != 0) {
	    onFormatSelected(availableDrivers.get(0));
	}

	return options;
    }

    private void onFormatSelected(DatabaseExportInterface driver) {
	username.setEnabled(driver.requiresUsername());
	password.setEnabled(driver.requiresUsername());
    }

    public void prepareDialog(SpreadsheetTable table) {
	this.table = table;

	// check whether the user selected a range of cells
	// if he/she did: exportSelected will be checked
	// if he/she didn't: exportWhile will be checked and exportSelected will
	// be disabled
	final Cell[][] selectedCells = table.getSelectedCells();
	final int rowCount = selectedCells.length;
	boolean hasInterestingSelection = true;

	if (rowCount != 0) {
	    final int columnCount = selectedCells[0].length;
	    if (rowCount == 1 && columnCount == 1)
		hasInterestingSelection = false;
	} else
	    hasInterestingSelection = false;

	exportSelected.setEnabled(hasInterestingSelection);
	exportSelected.setSelected(hasInterestingSelection);
	exportWhole.setSelected(!hasInterestingSelection);
    }
}

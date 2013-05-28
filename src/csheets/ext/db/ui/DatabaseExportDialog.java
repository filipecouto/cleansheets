package csheets.ext.db.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import csheets.core.Cell;
import csheets.ext.db.DatabaseExportBuilder;
import csheets.ext.db.DatabaseExportInterface;
import csheets.ext.db.DatabaseExtension;
import csheets.ui.sheet.SpreadsheetTable;

public class DatabaseExportDialog extends JFrame {
    private JFileChooser fileChooser;
    private JTextField url;
    private JTextField tableName;
    private JComboBox<String> format;
    private JRadioButton exportWhole;
    private JRadioButton exportSelected;
    private JTextField username;
    private JTextField password;

    private DatabaseExtension extension;

    private SpreadsheetTable table;

    public DatabaseExportDialog(DatabaseExtension extension) {
	super("Export to Database");

	this.extension = extension;

	getContentPane().setLayout(
		new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

	fileChooser = new JFileChooser();
	fileChooser.remove(fileChooser.getComponentCount() - 1);
	add(fileChooser);

	add(createOptionsPanel());
	add(createButtonsPanel());

	pack();
    }

    private JPanel createButtonsPanel() {
	JPanel buttons = new JPanel();
	buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
	JButton cancel = new JButton("Cancel");
	cancel.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
	    }
	});
	buttons.add(cancel);

	JButton ok = new JButton("Export");
	ok.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		export();
	    }
	});
	buttons.add(ok);
	return buttons;
    }

    private void export() {
	DatabaseExportBuilder exportBuilder = new DatabaseExportBuilder(
		DatabaseExportDialog.this.extension.getAvailableDrivers().get(
			format.getSelectedIndex()));
	exportBuilder.setDatabase(url.getText().length() == 0 ? fileChooser
		.getSelectedFile().getAbsolutePath() : url.getText());
	exportBuilder.setTableName(tableName.getText());
	final Cell[][] selectedCells = table.getSelectedCells();
	final int rowCount = selectedCells.length - 1;
	if (rowCount < 1)
	    return;
	final int columnCount = selectedCells[0].length;
	String[] columns = new String[columnCount];
	for (int i = 0; i < columnCount; i++) {
	    columns[i] = selectedCells[0][i].getValue().toString();
	}
	exportBuilder.setColumns(columns);
	String[][] values = new String[rowCount][columnCount];
	for (int y = 0; y < rowCount; y++) {
	    for (int x = 0; x < columnCount; x++) {
		values[y][x] = selectedCells[y + 1][x].getValue().toString();
	    }
	}
	exportBuilder.setValues(values);
	exportBuilder.export();
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
		onFormatSelected((DatabaseExportInterface) e.getSource());
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

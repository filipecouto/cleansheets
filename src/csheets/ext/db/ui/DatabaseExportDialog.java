package csheets.ext.db.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import csheets.core.Cell;
import csheets.ext.db.DatabaseExportControllerContainer;
import csheets.ext.db.DatabaseExportBuilder;
import csheets.ext.db.DatabaseExportController;
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

	url = getTextField();
	tableName = getTextField();

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
		url.setText(e.getItem().toString());
	    }
	});

	JPanel export = new JPanel();
	export.setLayout(new BoxLayout(export, BoxLayout.Y_AXIS));

	exportWhole = new JRadioButton("Whole current sheet");
	export.add(exportWhole);
	exportSelected = new JRadioButton("Selected area");
	export.add(exportSelected);

	username = getTextField();
	username.setEnabled(false);

	password = getTextField();
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

	add(options);

	JButton ok = new JButton("Export");
	ok.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		DatabaseExportController controller = new DatabaseExportController();
		// controller.export();
	    }
	});
	add(ok);

	pack();
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

	// BUG swing does not repaint the radio buttons unless some other event
	// updates them
    }

    private JTextField getTextField() {
	final JTextField field = new JTextField();
	// field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
	return field;
    }

}

package csheets.ext.db.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import csheets.core.Cell;
import csheets.ext.db.DatabaseExportBuilder;
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

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		fileChooser = new JFileChooser();
		fileChooser.remove(fileChooser.getComponentCount() - 1);
		add(fileChooser);

		JPanel options = new JPanel();
		options.setLayout(new GridLayout(6, 2));

		options.add(new JLabel("URL"));
		url = getTextField();
		options.add(url);

		options.add(new JLabel("Table name"));
		tableName = getTextField();
		options.add(tableName);

		options.add(new JLabel("Format"));
		// TODO show available formats
		String[] availableDrivers = new String[extension.getAvailableDrivers().length];
		for (int i = 0; i < availableDrivers.length; i++) {
			availableDrivers[i] = extension.getAvailableDrivers()[i].getName();
		}
		format = new JComboBox<String>(availableDrivers);
		format.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				url.setText(e.getItem().toString());
			}
		});
		options.add(format);

		options.add(new JLabel("Export"));
		JPanel export = new JPanel();
		export.setLayout(new BoxLayout(export, BoxLayout.Y_AXIS));

		exportWhole = new JRadioButton("Whole current sheet");
		export.add(exportWhole);
		exportSelected = new JRadioButton("Selected area");
		export.add(exportSelected);
		options.add(export);

		ButtonGroup group = new ButtonGroup();
		group.add(exportSelected);
		group.add(exportWhole);

		options.add(new JLabel("Username"));
		username = getTextField();
		username.setEnabled(false);
		options.add(username);

		options.add(new JLabel("Password"));
		password = getTextField();
		password.setEnabled(false);
		options.add(password);

		add(options);

		JButton ok = new JButton("Export");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});
		add(ok);

		pack();
	}

	public void prepareDialog(SpreadsheetTable table) {
		this.table = table;

		// check whether the user selected a range of cells
		// if he/she did: exportSelected will be checked
		// if he/she didn't: exportWhile will be checked and exportSelected will be disabled
		final Cell[][] selectedCells = table.getSelectedCells();
		final int rowCount = selectedCells.length;
		boolean hasInterestingSelection = true;

		if (rowCount != 0) {
			final int columnCount = selectedCells[0].length;
			if (rowCount == 1 && columnCount == 1) hasInterestingSelection = false;
		} else
			hasInterestingSelection = false;

		exportSelected.setEnabled(hasInterestingSelection);
		exportSelected.setSelected(hasInterestingSelection);
		exportWhole.setSelected(!hasInterestingSelection);

		// BUG swing does not repaint the radio buttons unless some other event updates them
	}

	private JTextField getTextField() {
		final JTextField field = new JTextField();
		// field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
		return field;
	}

	private void export() {
		try {
			DatabaseExportBuilder exportBuilder = new DatabaseExportBuilder(extension.getAvailableDrivers()[format.getSelectedIndex()]);
			exportBuilder.setDatabase(url.getText().length() != 0 ? url.getText() : fileChooser.getSelectedFile().getAbsolutePath());
			exportBuilder.setTableName(tableName.getText());
			final Cell[][] selectedCells = table.getSelectedCells();
			final int rowCount = selectedCells.length - 1;
			if (rowCount < 1) return;
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
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(getContentPane(), "There was an error while exporting your cells: " + e1.getMessage(), "Error while exporting",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}

package csheets.ext.db.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import csheets.ui.ctrl.SelectionEvent;
import csheets.ui.ctrl.SelectionListener;
import csheets.ui.ctrl.UIController;
import csheets.ui.sheet.SpreadsheetTable;

public class DatabaseExportDialog extends JFrame {
	private JTextField url;
	private JComboBox<String> format;
	private JRadioButton exportWhole;
	private JRadioButton exportSelected;
	private JTextField username;
	private JTextField password;

	private UIController uiController;

	public DatabaseExportDialog(UIController uiController) {
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.remove(fileChooser.getComponentCount() - 1);
		add(fileChooser);

		JPanel options = new JPanel();
		options.setLayout(new GridLayout(5, 2));

		options.add(new JLabel("URL"));
		url = getTextField();
		options.add(url);

		options.add(new JLabel("Format"));
		format = new JComboBox<String>(new String[] { "H2", "SQLite", "MySQL" });
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

		pack();
	}

	public void prepareDialog(SpreadsheetTable table) {
		if (table.getSelectedCells().length > 1) {
			exportSelected.setSelected(true);
		} else {
			exportWhole.setSelected(true);
		}
	}

	private JTextField getTextField() {
		final JTextField field = new JTextField();
		// field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
		return field;
	}
}

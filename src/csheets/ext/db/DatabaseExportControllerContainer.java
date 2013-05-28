package csheets.ext.db;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import csheets.ext.db.ui.DatabaseExportDialog;
import csheets.ui.sheet.SpreadsheetTable;

public class DatabaseExportControllerContainer {
	
	private DatabaseExtension extension;
	private JComboBox<String> format;
	private JTextField url;
	private JFileChooser fileChooser;
	private JTextField tableName;
	private SpreadsheetTable table;
	
	public DatabaseExportControllerContainer(DatabaseExtension extensin, JComboBox<String> format, JTextField url, JFileChooser fileChooser, JTextField tableName, SpreadsheetTable table) {
		this.extension = extension;
		this.format = format;
		this.url = url;
		this.fileChooser = fileChooser;
		this.tableName = tableName;
		this.table = table;
	}
	
	public DatabaseExtension getExtension() {
		return extension;
	}
	
	public JComboBox<String> getFormat() {
		return format;
	}
	
	public JTextField getUrl() {
		return url;
	}
	
	public JFileChooser getFileChooser() {
		return fileChooser;
	}
	
	public JTextField getTableName() {
		return tableName;
	}
	
	public SpreadsheetTable getTable() {
		return table;
	}
	
}

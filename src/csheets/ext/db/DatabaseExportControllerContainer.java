package csheets.ext.db;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import csheets.core.Cell;
import csheets.ext.db.ui.DatabaseExportDialog;
import csheets.ui.sheet.SpreadsheetTable;

public class DatabaseExportControllerContainer {
	
	private DatabaseExtension extension;
	private int comboOption;
	private String url;
	private String file;
	private String tableName;
	private String[] columns;
	private String[][] values;
	private String username;
	private String password;
	
	public DatabaseExportControllerContainer() {
	}
	
	public void setExtension(DatabaseExtension extension) {
		this.extension = extension;
	}
	
	public void setComboOption(int comboOption) {
		this.comboOption = comboOption;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setFile(String file) {
		this.file = file;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void setColumns(String[] columns) {
		this.columns = columns;
	}
	
	public void setValues(String[][] values) {
		this.values = values;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public DatabaseExtension getExtension() {
		return extension;
	}
	
	public int getComboOption() {
		return comboOption;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getFile() {
		return file;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public String[] getColumns() {
		return columns;
	}
	
	public String[][] getValues() {
		return values;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
}

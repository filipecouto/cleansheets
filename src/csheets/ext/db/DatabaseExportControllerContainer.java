package csheets.ext.db;

public class DatabaseExportControllerContainer {
    private DatabaseExtension extension;
    private String url;
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

    public void setUrl(String url) {
	this.url = url;
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

    public String getUrl() {
	return url;
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

package csheets.ext.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class responsible for working with HSQL Databases
 * 
 * @author Filipe_1110688
 * 
 */

public class HSQLdbExporter implements DatabaseExportInterface {

    private Connection databaseConnection;

    @Override
    public void openDatabase(String database) {
	try {
	    Class.forName("org.hsqldb.jdbcDriver");
	    databaseConnection = DriverManager.getConnection("jdbc:hsqldb:"
		    + database);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public boolean createTable(String name, String[] columns) {
	try {
	    String Statement = "CREATE TABLE " + name + "(";
	    for (int i = 0; i < columns.length; i++) {
		Statement += columns[i] + " varchar(512)";
		if ((i + 1) != columns.length) {
		    Statement += ",";
		}
	    }
	    Statement += ")";
	    databaseConnection.prepareStatement(Statement).execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    @Override
    public boolean addLine(String table, String[] values) {
	try {
	    String Statement = "INSERT INTO " + table + " VALUES(";
	    for (int i = 0; i < values.length; i++) {
		Statement += "'" + values[i] + "'";
		if ((i + 1) != values.length) {
		    Statement += ",";
		}
	    }
	    Statement += ")";
	    databaseConnection.prepareStatement(Statement).execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    @Override
    public void closeDatabase() {
	try {
	    databaseConnection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public String getName() {
	return "HSQL";
    }

    @Override
    public boolean requiresUsername() {
	return false;
    }

    @Override
    public boolean requiresPassword() {
	return false;
    }

}

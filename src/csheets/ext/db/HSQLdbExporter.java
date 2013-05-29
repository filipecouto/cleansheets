package csheets.ext.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
	    PreparedStatement preparedStatement = null;
	    String Statement = "CREATE TABLE " + name + "(";
	    for (int i = 0; i < columns.length; i++) {
		Statement += " ? varchar(512)";
		if ((i + 1) != columns.length) {
		    Statement += ",";
		}
	    }
	    Statement += ")";
	    preparedStatement = databaseConnection.prepareStatement(Statement);
	    for (int i = 0; i < columns.length; i++) {
		preparedStatement.setString((i + 1), columns[i]);
	    }
	    preparedStatement.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    @Override
    public boolean addLine(String table, String[] values) {
	try {
	    PreparedStatement preparedStatement = null;
	    String Statement = "INSERT INTO " + table + " VALUES(";
	    for (int i = 0; i < values.length; i++) {
		Statement += "?";
		if ((i + 1) != values.length) {
		    Statement += ",";
		}
	    }
	    Statement += ")";
	    preparedStatement = databaseConnection.prepareStatement(Statement);
	    for (int i = 0; i < values.length; i++) {
		preparedStatement.setString((i + 1), values[i]);
	    }
	    preparedStatement.execute();
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

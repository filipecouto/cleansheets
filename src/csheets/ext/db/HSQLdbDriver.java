package csheets.ext.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

/**
 * Class responsible for working with HSQL Databases
 * 
 * @author Filipe_1110688
 * 
 */

public class HSQLdbDriver implements DatabaseInterface {

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
		Statement += DatabaseExportHelper.PrepareColumnName(columns[i],i) + " varchar(512)";
		if ((i + 1) != columns.length) {
		    Statement += ",";
		}
	    }
	    Statement += ")";
	    databaseConnection.prepareStatement(Statement).execute();
	} catch (SQLException e) {
	    if(e.getMessage().contains("object name already exists")) {
		throw new RuntimeException("Table name already exists");
	    }
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    @Override
    public boolean addLine(String table, String[] values) {
	try {
	    PreparedStatement preparedStatement;
	    String Statement = "INSERT INTO " + table + " VALUES(";
	    for (int i = 0; i < values.length; i++) {
		Statement += "?";
		if ((i + 1) != values.length) {
		    Statement += ",";
		}
	    }
	    Statement += ")";
	    preparedStatement = databaseConnection.prepareStatement(Statement);
	    for (int i = 1; i <= values.length; i++) {
		preparedStatement.setString(i, values[i-1]);
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

    @Override
    public boolean requiresFile() {
	return true;
    }

    @Override
    public List<String> getTables() {
        List<String> tables = new ArrayList<String>();
        try{
            ResultSet rs = databaseConnection.getMetaData().getTables(null, "PUBLIC", "%",
		    null);
            while (rs.next()) {
                tables.add(rs.getString(3));
                System.out.println(rs.getString(3));
            }
            rs.close();
            databaseConnection.close();
        } catch(SQLException e){
            System.out.println(e);
        }
        return tables;
    }

}

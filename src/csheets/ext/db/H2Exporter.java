package csheets.ext.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class responsible for working with H2 databases
 * 
 * @author FILIPE
 */

public class H2Exporter implements DatabaseExportInterface {
	private Connection databaseConnection;

	@Override
	public void openDatabase(String database) {
		try {
			Class.forName("org.h2.Driver");
			if (database.endsWith("h2.db")) database = database.substring(0, database.indexOf("h2.db") - 1);
			databaseConnection = DriverManager.getConnection("jdbc:h2:" + database);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean createTable(String name, String[] columns) {
		try {
			String Statement = "CREATE TABLE IF NOT EXISTS " + name + "(";
			for (int i = 0; i < columns.length; i++) {
				Statement += columns[i] + " text";
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
		return "H2";
	}

	@Override
	public boolean requiresUsername() {
		return false;
	}
}

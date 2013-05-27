package csheets.ext.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class responsible for working with H2 type databases
 * @author FILIPE
 *
 */

public class H2Exporter implements DatabaseExportInterface {

	private Connection DatabaseConnection;
	
	public H2Exporter(String database) {
		try {
			Class.forName("org.h2.Driver");
			DatabaseConnection = DriverManager.getConnection("jdbc:h2:~/" + database);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean createTable(String name, String[] columns) {
		try {
			String Statement = "CREATE TABLE IF NOT EXISTS " + name +"(id int AUTO_INCREMENT PRIMARY KEY";
			for(String Column : columns) {
				Statement += ", " + Column + " text";
			}
			Statement += ")";
			DatabaseConnection.prepareStatement(Statement).execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean addLine(String table, String[] values) {
		try {
			String Statement = "INSERT INTO TABLE " + table + "(";
			for(int i=0;i<values.length;i++) {
				Statement += "'" + values[i] + "'";
				if((i+1) != values.length) {
					Statement +=",";
				}
			}
			Statement += ")";
			DatabaseConnection.prepareStatement(Statement).execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}

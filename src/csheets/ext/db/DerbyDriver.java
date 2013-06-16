package csheets.ext.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for working with Derby databases
 * 
 * @author Filipe Silva
 */
public class DerbyDriver implements DatabaseInterface {

    private Connection databaseConnection;
    private List<String> columnsNames = new ArrayList<String>();
    private List<String> primaryKeysNames;

    @Override
    public boolean createTable(String name, String[] columns,
	    List<String> primaryKeys) {
	try {
	    columnsNames.clear();
	    primaryKeysNames = primaryKeys;
	    String Statement = "CREATE TABLE " + name + "(";

	    for (int i = 0; i < columns.length; i++) {
		columnsNames.add(DatabaseExportHelper.PrepareColumnName(
			columns[i], i));
		boolean check = false;
		for (String key : primaryKeys) {
		    if (DatabaseExportHelper.PrepareColumnName(columns[i], i)
			    .compareTo(key) == 0) {
			check = true;
		    }
		}
		if (check) {
		    Statement += DatabaseExportHelper.PrepareColumnName(
			    columns[i], i) + " varchar(255) not null";
		} else {
		    Statement += DatabaseExportHelper.PrepareColumnName(
			    columns[i], i) + " varchar(255)";
		}
		if ((i + 1) != columns.length) {
		    Statement += ",";
		}

	    }

	    if (!primaryKeys.isEmpty()) {
		Statement += ", PRIMARY KEY (";
		for (String key : primaryKeys) {
		    Statement += key + ",";
		}
		Statement = Statement.substring(0, Statement.length() - 1);
		Statement += ")";
	    }
	    Statement += ")";
	    databaseConnection.prepareStatement(Statement).execute();
	} catch (Exception e) {
	    if (e.getMessage().contains("already exists")) {
		throw new RuntimeException("Table already exists");
	    }
	    e.printStackTrace();
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
		preparedStatement.setString(i, values[i - 1]);
	    }
	    preparedStatement.execute();
	} catch (Exception e) {
	    if (e.getMessage() != null
		    && e.getMessage().contains("duplicate key value")) {
		String sql = "Update " + table + " Set ";
		int data = 0;
		boolean check, comma = false;
		for (String col : columnsNames) {
		    check = false;
		    for (String prim : primaryKeysNames) {
			if (col.compareTo(prim) == 0) {
			    check = true;
			}
		    }
		    if (!check) {
			if (!comma) {
			    sql += col + "='" + values[data] + "' ";
			    comma = true;
			} else {
			    sql += ", " + col + "='" + values[data] + "' ";
			}

		    }
		    data++;

		}
		sql += " Where ";
		int pos = 0;
		comma = false;
		for (String prim : primaryKeysNames) {
		    data = 0;
		    for (String col : columnsNames) {
			if (prim.compareTo(col) == 0) {
			    pos = data;
			}
			data++;
		    }
		    if (!comma) {
			sql += prim + "='" + values[pos] + "' ";
			comma = true;
		    } else {
			sql += ", " + prim + "='" + values[pos] + "' ";
		    }
		}
		try {
		    PreparedStatement statement = databaseConnection
			    .prepareStatement(sql);
		    statement.execute();
		    databaseConnection.commit();
		    return true;
		} catch (SQLException sqle) {
		    System.out.println("Exception: " + sqle);
		}
	    }
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    @Override
    public Connection openDatabase(String database) {
	try {
	    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	    databaseConnection = DriverManager.getConnection("jdbc:derby:"
		    + database + ";create=true");
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return databaseConnection;
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
	return "Derby";
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
    public List<String> getTables() {
	List<String> tables = new ArrayList<String>();
	try {
	    ResultSet rs = databaseConnection.getMetaData().getTables(null,
		    "APP", "%", null);
	    while (rs.next()) {
		tables.add(rs.getString(3));
		System.out.println(rs.getString(3));
	    }
	    rs.close();
	    databaseConnection.close();
	} catch (SQLException e) {
	    System.out.println(e);
	}
	return tables;
    }

    @Override
    public boolean requiresFile() {
	return false;
    }

    @Override
    public String[][] getData(String table) {
	String sql = "Select * from " + table;
	String[][] info = null;
	int columnsNumber = 0, rowsNumber = 0, i = 0, j = 0;
	try {
	    PreparedStatement statement = databaseConnection.prepareStatement(
		    sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
		    ResultSet.CONCUR_READ_ONLY);
	    ResultSet rs = statement.executeQuery();
	    ResultSetMetaData rsmd = rs.getMetaData();
	    columnsNumber = rsmd.getColumnCount();
	    rs.last();
	    rowsNumber = rs.getRow() + 1;
	    rs.beforeFirst();
	    info = new String[rowsNumber][columnsNumber];
	    for (j = 1; j <= columnsNumber; j++) {
		info[i][j - 1] = rsmd.getColumnName(j);
	    }
	    i++;
	    while (rs.next()) {
		for (j = 1; j <= columnsNumber; j++) {
		    info[i][j - 1] = rs.getString(j);
		}
		i++;
	    }
	} catch (SQLException e) {
	    System.out.println(e);
	}
	return info;
    }

    @Override
    public void dropTable(String table) {
	String sql = "Drop table " + table;
	try {
	    PreparedStatement statement = databaseConnection
		    .prepareStatement(sql);
	    statement.execute();
	} catch (SQLException e) {
	    System.out.println(e);
	}
    }

    @Override
    public void update(String table, String columns[], String values[],
	    int positionInArray) {
	PreparedStatement prepareStatement;
	String sql = "UPDATE " + table + " SET " + columns[positionInArray]
		+ "= '" + values[positionInArray] + "'  WHERE ";
	List<String> columnsList = new ArrayList<String>();
	List<String> valuesList = new ArrayList<String>();
	for (int i = 0; i < columns.length; i++) {

	    if (i != positionInArray) {
		columnsList.add(columns[i]);
		valuesList.add(values[i]);
	    }
	}
	int columnsListSize = columnsList.size();
	for (int i = 0; i < columnsListSize; i++) {

	    sql += columnsList.get(i) + "='" + valuesList.get(i) + "'";
	    if ((i + 1) != columnsListSize) {
		sql += " AND ";

	    }
	}
	try {
	    System.out.println("Statement = " + sql);
	    prepareStatement = databaseConnection.prepareStatement(sql);
	    prepareStatement.executeUpdate();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void insert(String table, String column, String value,
	    String[] columnNames) {
	PreparedStatement preparedStatement;
	try {
	    int position = 0;
	    String sql = "INSERT INTO " + table + " values(";
	    for (int i = 0; i < columnNames.length; i++) {
		if (columnNames[i].equals(column)) {
		    position = i;
		}
		sql += "?";
		if ((i + 1) != columnNames.length) {
		    sql += ", ";
		}
	    }
	    sql += ")";
	    preparedStatement = databaseConnection.prepareStatement(sql);
	    for (int i = 1; i <= columnNames.length; i++) {
		if ((position + 1) == i) {
		    preparedStatement.setString(i, value);
		} else {
		    preparedStatement.setString(i, " ");
		}
	    }
	    preparedStatement.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void insertColumn(String table, int position, String value,
	    String columnName) {
	PreparedStatement prepareStatement;
	String Statement = "ALTER TABLE " + table + " ADD ";
	Statement += DatabaseExportHelper.PrepareColumnName(columnName,
		position) + " varchar(255)";

	try {
	    prepareStatement = databaseConnection.prepareStatement(Statement);
	    prepareStatement.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }
}
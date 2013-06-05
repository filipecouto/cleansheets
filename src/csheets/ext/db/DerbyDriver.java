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
public class DerbyDriver implements DatabaseInterface{
    
    private Connection databaseConnection;

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
    public void openDatabase(String database) {
        try {
	    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	    databaseConnection = DriverManager.getConnection("jdbc:derby:"
		    + database + ";create=true");
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
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
    
    @Override
    public boolean requiresFile() {
	return false;
    }
    
    @Override
    public String[][] getData(String table) {
        String sql = "Select * from "+table;
        String [][] info=null;
        int columnsNumber=0,rowsNumber=0,i=0,j=0;
        try{
            PreparedStatement statement = databaseConnection.prepareStatement(sql, 
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            columnsNumber = rsmd.getColumnCount();
            rs.last();
            rowsNumber = rs.getRow() + 1;
            rs.beforeFirst();
            System.out.println("Linhas = "+rowsNumber+" Colunas = "+columnsNumber);
            info = new String[rowsNumber][columnsNumber];
            for(j=1;j<=columnsNumber;j++){
                info[i][j-1]=rsmd.getColumnName(j);
            }
            i++;
            while(rs.next()){
                for(j=1;j<=columnsNumber;j++){
                    info[i][j-1]=rs.getString(j);
                }
                i++;
            }
            System.out.println("done");
        }catch(SQLException e){
            System.out.println(e);
        }
	return info;
    }
}

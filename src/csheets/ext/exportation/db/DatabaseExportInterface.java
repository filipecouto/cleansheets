package csheets.ext.exportation.db;


/**
 * An interface required by different database drivers in order to establish a communication
 * with the application
 * @author Filipe_1110688
 */

public interface DatabaseExportInterface {

	/**
	 * Invoked by the application to create a new table in the determined database driver
	 * @param name the name of the table to be created
	 * @param column an array of strings setting the table columns names
	 * @return true if successful , false if not
	 */
	public boolean createTable(String name, String[] column);
	
	/**
	 * Invoked by the application to insert a line in a table in the determined database driver
	 * @param table the name of the table on which the data will be inserted
	 * @param values an array of strings setting the column values
	 * @return true if successful , false if not
	 */
	public boolean addLine(String table, String[] values);

}

package csheets.ext.db;

import java.util.List;

/**
 * An interface to be implemented by many database drivers in order to establish
 * a communication with the application
 * 
 * @author Filipe_1110688 & gil_1110484
 */

public interface DatabaseInterface {

    /**
     * Invoked by the application to create a new table in the determined
     * database driver
     * 
     * @param name
     *            the name of the table to be created
     * @param column
     *            an array of strings setting the table columns names
     * @return true if successful , false if not
     */
    public boolean createTable(String name, String[] column, List<String> primaryKeys);

    /**
     * Invoked by the application to insert a line in a table in the determined
     * database driver
     * 
     * @param table
     *            the name of the table on which the data will be inserted
     * @param values
     *            an array of strings setting the column values
     * @return true if successful , false if not
     */
    public boolean addLine(String table, String[] values);

    /**
     * Invoked by the application to create a connection to the specified
     * database
     * 
     * @param database
     *            database URL
     */
    public void openDatabase(String database);

    /**
     * Closes the database connection
     */
    public void closeDatabase();

    /**
     * Invoked by the user interface in order to show the user the name of this
     * driver
     * 
     * @return the name of this driver in a user-friendly mode
     */
    public String getName();

    /**
     * Invoked by the user interface to find if this exporter requires an
     * username to login
     * 
     * @return true if it requires username false if it does not
     */
    public boolean requiresUsername();

    /**
     * Invoked by the user interface to find if this exporter requires an
     * password
     * 
     * @return true if it requires password false if it does not
     */
    public boolean requiresPassword();
    
    /**
     * Invoked by the Import Controller to find the tables
     * @return List<String> name of tables
     */
    
    public List<String> getTables();
    
    /**
     * Specifies if the required database requires a directory or a file
     * 
     */
     
    public boolean requiresFile();
    
    
    /**
     * Invoked by the controller to get the table content
     * @return String[][] table content
     */
    
    public String[][] getData(String table);
}

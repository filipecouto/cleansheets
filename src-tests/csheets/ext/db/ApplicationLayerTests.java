package csheets.ext.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.Statement;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import csheets.core.Cell;
import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.io.CLSCodec;

public class ApplicationLayerTests {
    private static final String TABLE_NAME = "testTable";
    private static final String DATABASE_NAME = "testDatabase";
    private static Workbook workbook;
    private static Spreadsheet importSpreadsheet;
    private static Spreadsheet generatedSpreadsheet;

    private static DatabaseInterface dbDriver;

    private static int columns;
    private static int rows;
    private static int xOffset;
    private static int yOffset;

    /**
     * Starts up the tests, creating a random spreadsheet.
     * @throws Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
	// remove old test stuff just in case

	// let's choose to create our random data in a range of up to 99 rows
	// and columns, starting on a row from 1 to 10 and a column from A to E
	columns = (int) (Math.random() * 60);
	rows = (int) (Math.random() * 100);
	xOffset = (int) (Math.random() * 5);
	yOffset = (int) (Math.random() * 10);

	// let's create our workbook
	workbook = new Workbook(3);
	generatedSpreadsheet = workbook.getSpreadsheet(0);
	importSpreadsheet = workbook.getSpreadsheet(1);
	generateData(generatedSpreadsheet);

	// let's save this workbook, it may contain precious info
	saveTestWorkbook();

	dbDriver = new DerbyDriver();

	System.out.println("Done creating a " + columns + "x" + rows
		+ " table to export using " + dbDriver.getName() + " driver!");

	export();
        importDatabase();
    }

    /**
     * Exports to database
     */
    public static void export() {
	try {
	    DatabaseExportController controller = new DatabaseExportController();
	    controller.setDriver(dbDriver);
	    controller.setDatabase(DATABASE_NAME);
	    controller.setTableName(TABLE_NAME);
	    controller.setCreateTable(true);
            controller.setDropTable(false);
            controller.setPrimaryKeys(new ArrayList<String>());
	    Cell[][] cells = new Cell[rows][columns];
	    for (int y = 0; y < rows; y++) {
		for (int x = 0; x < columns; x++) {
		    cells[y][x] = generatedSpreadsheet.getCell(x + xOffset, y + yOffset);
		}
	    }
	    controller.setCells(cells);
	    controller.export();
	    System.out.println("Table was exported successfully");
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Table failed to export");
	}
    }
    
    /*
     * Imports the Database
     */
    public static void importDatabase(){
        DatabaseImportController controller = new DatabaseImportController();
        controller.setDriver(dbDriver);
        controller.setDatabase(DATABASE_NAME);
        controller.setTableName("testTable");
        controller.setSpreadsheet(importSpreadsheet);
        Cell c1 = importSpreadsheet.getCell(0, 0);
        controller.setCell(c1);
        controller.setImportToCurrentSheet(false);
        String [][] values = controller.importDatabase();
        //Read matrix and put data into spreadsheet
        int i=0,j=0;
        int cellCol,cellRow;
        // Import into a new sheet
        cellCol=c1.getAddress().getColumn();
        cellRow=c1.getAddress().getRow();
        try{
            for(i=0;i<values.length;i++){
                for(j=0;j<values[0].length;j++){
                    importSpreadsheet.getCell(cellCol+j, cellRow+i).setContent(values[i][j].toString());
                }
            }
        }catch(FormulaCompilationException e){
            System.out.println(e);
        }
    }
    
    /*
     * Verifies if the content of a matrix matches the content of a spreadsheet starting in the cell A1
     */
    public static boolean compareSpreadsheet(){
        for(int i = 1; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
        	/*System.out.println("Generated : " + generatedSpreadsheet.getCell(j + xOffset, i + yOffset).getContent());
        	System.out.println("Imported : " + importSpreadsheet.getCell(j, i).getContent());*/
        	if(!generatedSpreadsheet.getCell(j + xOffset, i + yOffset).getContent().equals(importSpreadsheet.getCell(j, i).getContent())) {
        	    return false;
        	}
            }
        }
        return true;
    }

    /**
     * Saves this test workbook just in case
     */
    private static void saveTestWorkbook() {
	try {
	    FileOutputStream out = new FileOutputStream("CleanSheets_Test.cls");
	    CLSCodec codec = new CLSCodec();
	    codec.write(workbook, out);
	    out.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * generates random data
     * @param sheet the spreadsheet
     */
    private static void generateData(Spreadsheet sheet) {
        //System.out.println((rows-yOffset) + " - " + (columns-xOffset));
        String value;
        //System.out.println(yOffset + " = " + (rows+yOffset));
        //System.out.println(xOffset + " = " + (columns+xOffset));
	for (int y = yOffset; y < rows + yOffset; y++) {
	    for (int x = xOffset; x < columns + xOffset; x++) {
		if (Math.random() > 0.08f) {
		    // 8 on 100 cells will be empty
		    if (Math.random() > 0) {
			// 6 on 10 cells will contain text
			try {
                            value=makeRandomString();
			    sheet.getCell(x, y).setContent(value);
			} catch (FormulaCompilationException e) {
			    e.printStackTrace();
			}
		    } else {
			// 4 on 10 cells will contain numbers
			try {
                            value=String.valueOf(Math.random() * 1000000);
			    sheet.getCell(x, y).setContent(value);
			} catch (FormulaCompilationException e) {
			    e.printStackTrace();
			}
		    }
		}
	    }
	}
    }

    /**
     * generate random strings
     * @return a random string
     */
    private static String makeRandomString() {
	String result = new String();
	final int len = (int) (Math.random() * 100 + 1);
	for (int i = 0; i < len; i++) {
	    result += (char) ((Math.random() * (0x7e - 0x20)) + 0x20);
	}
	if (result.startsWith("=") || result.startsWith("#")) {
	    // avoid creating a bad formula
	    result = "a" + result;
	}
	return result;
    }

    /**
     * Tests the creation of the table
     */
    
    public void testTableCreation() {
	try {
	    Class.forName("org.hsqldb.jdbcDriver");
	    Connection conn = DriverManager.getConnection("jdbc:derby:"
		    + DATABASE_NAME);
	    ResultSet rs = conn.getMetaData().getTables(null, "PUBLIC", "%",
		    null);
	    while (rs.next()) {
		if (TABLE_NAME.equalsIgnoreCase(rs.getString(3))) {
		    assertTrue("Table was created successfully", true);
		    rs.close();
		    conn.close();
		    return;
		}
	    }
	    assertTrue("Table was created successfully", false);
	    rs.close();
	    conn.close();
	    return;
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	assertTrue("Table was created successfully", false);
    }

    @Test
    /**
     * Compares the two spreadsheets
     */
    public void compareSpreadsheets() {
	assertTrue("Spreadsheets comparison", compareSpreadsheet());
    }

    /**
     * Compares the number of rows in the table
     */
    @Test
    public void compareNumberOfRows() {
	int rowCount = ApplicationLayerTests.rows - 1;
	int row = 0;
	try {
	    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	    Connection conn = DriverManager.getConnection("jdbc:derby:"
		    + DATABASE_NAME);
	    String statement = "SELECT * FROM " + TABLE_NAME;
	    ResultSet res = conn.prepareStatement(statement).executeQuery();

	    while (res.next()) {
		row++;
	    }
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	assertEquals("Row comparison", row, rowCount);
    }

    /**
     * Cleans the database
     */
    @AfterClass
    public static void cleanUp() {
	// TODO maybe remove database or table?
	try {
	    // connects to the database with the username and password
	    Connection conn = DriverManager.getConnection("jdbc:derby:"
		    + DATABASE_NAME);
	    conn.prepareStatement("DROP TABLE testTable").execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

}

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
    private static Spreadsheet spreadsheet;

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
	cleanUp();

	// let's choose to create our random data in a range of up to 99 rows
	// and columns, starting on a row from 1 to 10 and a column from A to E
	columns = (int) (Math.random() * 60);
	rows = (int) (Math.random() * 100);
	xOffset = (int) (Math.random() * 5);
	yOffset = (int) (Math.random() * 10);

	// let's create our workbook
	workbook = new Workbook(3);
	spreadsheet = workbook.getSpreadsheet(0);
	generateData(spreadsheet);

	// let's save this workbook, it may contain precious info
	saveTestWorkbook();

	dbDriver = new HSQLdbDriver();

	System.out.println("Done creating a " + columns + "x" + rows
		+ " table to export using " + dbDriver.getName() + " driver!");

	export();
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
	    Cell[][] cells = new Cell[rows][columns];
	    for (int y = 0; y < rows; y++) {
		for (int x = 0; x < columns; x++) {
		    cells[y][x] = spreadsheet.getCell(x + xOffset, y + yOffset);
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
	for (int y = yOffset; y < rows + yOffset; y++) {
	    for (int x = xOffset; x < columns + xOffset; x++) {
		if (Math.random() > 0.08f) {
		    // 8 on 100 cells will be empty
		    if (Math.random() > 0) {
			// 6 on 10 cells will contain text
			try {
			    sheet.getCell(x, y).setContent(makeRandomString());
			} catch (FormulaCompilationException e) {
			    e.printStackTrace();
			}
		    } else {
			// 4 on 10 cells will contain numbers
			try {
			    sheet.getCell(x, y).setContent(
				    String.valueOf(Math.random() * 1000000));
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
	if (result.startsWith("=")) {
	    // avoid creating a bad formula
	    result = "a" + result;
	}
	return result;
    }

    /**
     * Tests the creation of the table
     */
    @Test
    public void testTableCreation() {
	try {
	    Class.forName("org.hsqldb.jdbcDriver");
	    Connection conn = DriverManager.getConnection("jdbc:hsqldb:"
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

    /**
     * Compares the rows from the spreadsheet with the rows from the table
     */
    @Test
    public void compareAllRows() {
	try {
	    Class.forName("org.hsqldb.jdbcDriver");
	    Connection conn = DriverManager.getConnection("jdbc:hsqldb:"
		    + DATABASE_NAME);
	    String statement = "SELECT * FROM " + TABLE_NAME;
	    ResultSet res = conn.prepareStatement(statement).executeQuery();
	    int columnCount = columns;
	    int row = yOffset + 1;
	    while (res.next()) {
		for (int column = 0; column < columnCount; column++) {
		    if (!(spreadsheet.getCell(column + xOffset + 1, row).getValue()
			    .toString().equals(res.getString(column + 1)))) {
			assertEquals(spreadsheet.getCell(column + xOffset, row).getValue()
			    .toString(), res.getString(column + 1));
		    }
		}
		row++;
	    }
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	assertTrue("All \"cells\" in the database match the worksheet", true);
    }

    /**
     * Compares the number of rows in the table
     */
    @Test
    public void compareNumberOfRows() {
	int rowCount = ApplicationLayerTests.rows - 1;
	int row = 0;
	try {
	    Class.forName("org.hsqldb.jdbcDriver");
	    Connection conn = DriverManager.getConnection("jdbc:hsqldb:"
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
	    Connection conn = DriverManager.getConnection("jdbc:hsqldb:"
		    + DATABASE_NAME, "SA", "");
	    conn.prepareStatement("DROP TABLE testTable").execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

}

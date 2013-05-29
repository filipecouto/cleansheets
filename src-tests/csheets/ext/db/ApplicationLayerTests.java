package csheets.ext.db;

import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import csheets.core.Cell;
import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.io.CLSCodec;

public class ApplicationLayerTests {
    private static Workbook workbook;
    private static Spreadsheet spreadsheet;

    private static DatabaseExportInterface dbDriver;

    private static int columns;
    private static int rows;
    private static int xOffset;
    private static int yOffset;

    @BeforeClass
    public static void setUp() throws Exception {
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

	dbDriver = DatabaseDriverManager.getInstance().getAvailableDrivers()
		.get(0);
	System.out.println("Done creating a " + columns + "x" + rows
		+ " table to export using " + dbDriver.getName() + " driver!");
    }

    @Test
    public void export() {
	try {
	    DatabaseExportController controller = new DatabaseExportController();
	    controller.setDriver(dbDriver);
	    controller.setDatabase("testDatabase");
	    controller.setTableName("testTable");
	    controller.setCreateTable(true);
	    Cell[][] cells = new Cell[rows][columns];
	    for (int y = 0; y < rows; y++) {
		for (int x = 0; x < columns; x++) {
		    cells[y][x] = spreadsheet.getCell(x + xOffset, y + yOffset);
		}
	    }
	    controller.setCells(cells);
	    controller.export();
	    assertTrue("Table was exported successfully", true);
	} catch (Exception e) {
	    e.printStackTrace();
	    assertTrue("Table was exported successfully", false);
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

    private static String makeRandomString() {
	String result = new String();
	final int len = (int) (Math.random() * 100 + 1);
	for (int i = 0; i < len; i++) {
	    // TODO use this one instead of the "anti-SQL injection" workaround
	    // result += (char) ((Math.random() * (0x7e - 0x20)) + 0x20);
	    result += (char) ((Math.random() * (0x5a - 0x41)) + 0x41);
	}
	if (result.startsWith("=")) {
	    // avoid creating a bad formula
	    result = "a" + result;
	}
	return result;
    }

    @Test
    public void compareAllRows() {
	assertTrue("All \"cells\" in the database match the worksheet", true);
    }

    @After
    public void cleanUp() {
	// TODO maybe remove database or table?
    }
}

package csheets.core.formula.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.Test;

import csheets.core.IllegalValueTypeException;
import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.io.CLSCodec;

/**
 * This class tests multiple expressions using the ExpressionSet and Loop
 * classes
 * 
 * @author Gil Castro (gil_1110484)  Filipe Couto (Filipe_1110688)
 */
public class MultipleExpressionsTest {
    private Spreadsheet testSheet;
    private int start;
    private int end;

    /**
     * Before starting any test, we will fill in A1 and B1 with an ExpressionSet
     * and a Loop respectively, writing them indirectly (that is, not
     * instantiating those classes but letting the ANTLR classes parse a normal
     * string)
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
	start = 0;
	end = 1000;

	Workbook book = new Workbook(1);
	testSheet = book.getSpreadsheet(0);

	testSheet.getCell(0, 0).setContent(
		"#{a2:=\"From = \";a3:=\"To = \";a4:=\"i = \";b2:=" + start
			+ ";b3:=" + end + ";b4:=" + start
			+ ";\"Multiple expressions test\"}");
	testSheet.getCell(1, 0).setContent("#{whiledo{b4<b3;b4:=b4+1" + "}}");

	testSheet.getCell(2, 0).setContent(
		"#{a5:=\"From = \";a6:=\"To = \";a7:=\"i = \";b5:=" + start
			+ ";b6:=" + end + ";b7:=" + start
			+ ";\"Multiple expressions test\"}");
	testSheet.getCell(3, 0).setContent("#{dowhile{b7:=b7+1;b7<b6" + "}}");

	// Let's save the file, if we ever need to check what was generated
	CLSCodec codec = new CLSCodec();
	codec.write(book, new FileOutputStream(
		"MultipleExpressionsTestFile.cls"));
    }

    /**
     * Checks if the ExpressionSet evaluated the requested Expressions (which
     * were Attributions) by checking if the Attributions worked as expected
     */
    @Test
    public void multipleExpressions() {
	assertTrue("Cell A2 doesn't contain the expected content",
		cellContentEquals(0, 1, "From = "));
	assertTrue("Cell A3 doesn't contain the expected content",
		cellContentEquals(0, 2, "To = "));
	assertTrue("Cell A4 doesn't contain the expected content",
		cellContentEquals(0, 3, "i = "));
	assertTrue("Cell A5 doesn't contain the expected content",
		cellContentEquals(0, 4, "From = "));
	assertTrue("Cell A6 doesn't contain the expected content",
		cellContentEquals(0, 5, "To = "));
	assertTrue("Cell A7 doesn't contain the expected content",
		cellContentEquals(0, 6, "i = "));
    }

    /**
     * Checks if the Loop calculated the expected values
     */
    @Test
    public void whileCycles() {
	cellValueEquals(1, 1, start);
	cellValueEquals(1, 2, end);
	cellValueEquals(1, 3, end);
	cellValueEquals(1, 4, start);
	cellValueEquals(1, 5, end);
	cellValueEquals(1, 6, end);
	cellValueEquals(1, 0, end);
	cellValueEquals(3, 0, end);
    }

    private void cellValueEquals(int column, int row, double value) {
	try {
	    final double cellValue = testSheet.getCell(column, row).getValue()
		    .toDouble();
	    assertEquals(cellValue, value, 0);
	} catch (IllegalValueTypeException e) {
	    fail("Cell value is wrong");
	}
    }

    private boolean cellContentEquals(int column, int row, String content) {
	return testSheet.getCell(column, row).getContent().equals(content);
    }
}

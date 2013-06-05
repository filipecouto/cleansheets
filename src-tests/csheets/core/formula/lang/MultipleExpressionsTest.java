package csheets.core.formula.lang;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import csheets.core.IllegalValueTypeException;
import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.io.XMLCodec;

public class MultipleExpressionsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private Spreadsheet testSheet;
	private int start;
	private int end;

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

		XMLCodec codec = new XMLCodec();
		codec.write(book, new FileOutputStream("MultipleExpressionsTestFile.xml"));
	}

	@Test
	public void multipleExpressions() {
		assertTrue("Cell A2 doesn't contain the expected content",
				cellContentEquals(0, 1, "From = "));
		assertTrue("Cell A3 doesn't contain the expected content",
				cellContentEquals(0, 2, "To = "));
		assertTrue("Cell A4 doesn't contain the expected content",
				cellContentEquals(0, 3, "i = "));
	}

	@Test
	public void whileCycles() {
		cellValueEquals(1, 1, start);
		cellValueEquals(1, 2, end);
		cellValueEquals(1, 3, end);
		cellValueEquals(1, 0, end);
	}

	public void cellValueEquals(int column, int row, double value) {
		try {
			final double cellValue = testSheet.getCell(column, row).getValue()
					.toDouble();
			assertEquals(cellValue, value, 0);
		} catch (IllegalValueTypeException e) {
			fail("Cell value is wrong");
		}
	}

	public boolean cellContentEquals(int column, int row, String content) {
		return testSheet.getCell(column, row).getContent().equals(content);
	}
}

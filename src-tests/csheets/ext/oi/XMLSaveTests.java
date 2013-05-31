package csheets.ext.oi;

import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.beans.Statement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import csheets.core.Cell;
import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.ext.style.StylableCell;
import csheets.ext.style.StyleExtension;
import csheets.io.CLSCodec;
import csheets.io.XMLCodec;

public class XMLSaveTests {
    private static final String TABLE_NAME = "testTable";
    private static final String DATABASE_NAME = "testDatabase";
    private static Workbook workbook;
    private static Spreadsheet spreadsheet;

    private static String[] font = { Font.MONOSPACED, Font.SANS_SERIF,
	    Font.SERIF };
    private static Random r = new Random();
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
    }

    /**
     * Saves this test workbook just in case
     */
    private static void saveTestWorkbook() {
	try {
	    FileOutputStream out = new FileOutputStream("XmlTestOut.xml");
	    XMLCodec codec = new XMLCodec();
	    codec.write(workbook, out);
	    out.close();
	} catch (ParserConfigurationException e) {
	    System.err.println("ParserConfigurationException");
	    e.printStackTrace();
	} catch (IOException e) {
	    System.err.println("IOException");
	    e.printStackTrace();
	} catch (DOMException e) {
	    System.err.println("DOMException");
	    e.printStackTrace();
	} catch (TransformerException e) {
	    System.err.println("TransformerException");
	    e.printStackTrace();
	}
    }

    private static void generateData(Spreadsheet sheet) {
	int countRow = rows;
	int countColumn = columns;
	for (int row = 0; row < countRow; row++) {
	    for (int column = 0; column < countColumn; column++) {
		try {
		    StylableCell sc = (StylableCell) sheet.getCell(column
			    + xOffset, row + yOffset).getExtension(StyleExtension.NAME);
		    sc.setContent(makeRandomString());
		    sc.setBackgroundColor(makeRandomColor());
		    sc.setFont(makeRandomFont());
		    sc.setForegroundColor(makeRandomColor());
		    sc.setHorizontalAlignment(makeRandomTextAlignment(r
			    .nextInt(2)));
		    sc.setVerticalAlignment(makeRandomCellAlignment(r
			    .nextInt(2)));
		} catch (FormulaCompilationException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    private static int makeRandomCellAlignment(int gerar) {
	switch (gerar) {
	case 0:
	    return SwingConstants.CENTER;
	case 1:
	    return SwingConstants.BOTTOM;
	case 2:
	    return SwingConstants.TOP;
	default:
	    return SwingConstants.CENTER;
	}
    }

    private static int makeRandomTextAlignment(int gerar) {
	switch (gerar) {
	case 0:
	    return SwingConstants.CENTER;
	case 1:
	    return SwingConstants.LEFT;
	case 2:
	    return SwingConstants.RIGHT;
	default:
	    return SwingConstants.LEFT;
	}
    }

    private static Font makeRandomFont() {
	return new Font(font[r.nextInt(2)],
		makeRandomBoldAndItalic(r.nextInt(3)), r.nextInt(50));
    }

    private static int makeRandomBoldAndItalic(int gerar) {
	switch (gerar) {
	case 0:
	    return Font.BOLD;
	case 1:
	    return Font.ITALIC;
	case 2:
	    return Font.BOLD | Font.ITALIC;
	case 3:
	    return 0;
	default:
	    return 0;
	}
    }

    private static Color makeRandomColor() {
	return new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }

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

    @Test
    public void compareAllRows() {
	try {
	    FileOutputStream stream = new FileOutputStream(new File(
		    "XmlTestOut.xml"));
	    XMLCodec xml = new XMLCodec();
	    xml.write(workbook, stream);
	    if (testNumberSheetsAndCells("XmlTestOut.xml")) {
		assertTrue("Number of SpreadSheets and Cells correct", true);
	    } else {
		assertTrue("Number of SpreadSheets and Cells incorrect", false);
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (TransformerException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ParserConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    private boolean testNumberSheetsAndCells(String file) {
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

	try {
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(new File(file));

	    NodeList nList = doc.getElementsByTagName("SpreadSheet");
	    int totalSheets = nList.getLength();
	    int nSheets = workbook.getSpreadsheetCount();
	    if (totalSheets == nSheets) {
		for (int i = 0; i < totalSheets; i++) {
		    int nCells = (workbook.getSpreadsheet(i).getColumnCount())
			    * (workbook.getSpreadsheet(i).getRowCount());
		    NodeList nListCells = doc.getElementsByTagName("Cell");
		    int totalCells = nListCells.getLength();
		    if (nCells != totalCells) {
			return false;
		    }
		    return true;
		}
	    }
	    return false;

	} catch (SAXException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ParserConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return false;
    }

    @Test
    public void compareNumberOfRows() {

    }

    @AfterClass
    public static void cleanUp() {
    }

}

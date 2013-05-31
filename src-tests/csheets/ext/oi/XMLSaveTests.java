package csheets.ext.oi;

import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.ext.style.StylableCell;
import csheets.ext.style.StyleExtension;
import csheets.io.XMLCodec;

public class XMLSaveTests {
    private static final String TABLE_NAME = "testTable";
    private static final String DATABASE_NAME = "testDatabase";
    private static Workbook workbook;
    private static Spreadsheet spreadsheet;

    private static String[] font = { Font.MONOSPACED, Font.SANS_SERIF,
	    Font.SERIF };
    private static Random r = new Random();
    private static File outFile = new File("XmlTextOut.xml");
    private static int columns;
    private static int rows;
    private static int xOffset;
    private static int yOffset;
    private static int totalColumns, totalRows;

    @BeforeClass
    public static void setUp() throws Exception {
	// let's choose to create our random data in a range of up to 99 rows
	// and columns, starting on a row from 1 to 10 and a column from A to E
	columns = (int) (Math.random() * 100);
	rows = (int) (Math.random() * 10);
	xOffset = (int) (Math.random() * 50);
	yOffset = (int) (Math.random() * 10);

	// let's create our workbook

	generateData();

	// let's save this workbook, it may contain precious info
	saveTestWorkbook();
    }

    /**
     * Saves this test workbook just in case
     */
    private static void saveTestWorkbook() {
	try {
	    FileOutputStream out = new FileOutputStream(outFile);
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

    private static void generateData() {
	workbook = new Workbook(1);
	spreadsheet = workbook.getSpreadsheet(0);
	int countRow = rows;
	int countColumn = columns;
	if (Math.random() > 0.08f) {
	    for (int row = 0; row < countRow; row++) {
		for (int column = 0; column < countColumn; column++) {

		    try {
			StylableCell sc = (StylableCell) spreadsheet.getCell(
				column + xOffset, row + yOffset).getExtension(
				StyleExtension.NAME);
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

	} else {
	    for (int row = 0; row < countRow; row++) {
		for (int column = 0; column < countColumn; column++) {
		    try {
			StylableCell sc = (StylableCell) spreadsheet.getCell(
				column + xOffset, row + yOffset).getExtension(
				StyleExtension.NAME);
			sc.setContent(String.valueOf(Math.random() * 1000000));
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
	totalColumns += countColumn;
	totalRows += countRow;
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
	    FileOutputStream stream = new FileOutputStream(outFile);
	    XMLCodec xml = new XMLCodec();
	    xml.write(workbook, stream);
	    if (testNumberSheetsAndCells()) {
		assertTrue("Number of SpreadSheets and Cells correct", true);
	    } else {
		assertTrue("Number of SpreadSheets and Cells incorrect", false);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (TransformerException e) {
	    e.printStackTrace();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}

    }

    private boolean testNumberSheetsAndCells() {
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

	try {
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(outFile);

	    NodeList nList = doc.getElementsByTagName("SpreadSheet");
	    int totalSheets = nList.getLength();
	    int nSheets = workbook.getSpreadsheetCount();
	    if (totalSheets == nSheets) {
		int nCells = totalColumns * totalRows;
		NodeList nListCells = nList.item(0).getChildNodes();
		int totalCells = nListCells.getLength();
		if (nCells != totalCells) {
		    return false;
		}
		return true;
	    }

	    return false;

	} catch (SAXException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}
	return false;
    }

    @Test
    public void compareNumberOfRows() {
	try {
	    FileOutputStream stream = new FileOutputStream(outFile);
	    XMLCodec xml = new XMLCodec();
	    xml.write(workbook, stream);
	    if (testSheetsAndCellsContent()) {
		assertTrue("The content of SpreadSheets and Cells correct",
			true);
	    } else {
		assertTrue("The content of SpreadSheets and Cells incorrect",
			false);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (TransformerException e) {
	    e.printStackTrace();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}
    }

    private boolean testSheetsAndCellsContent() {
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	int column = 0;
	int row = 0;
	try {
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(outFile);

	    NodeList nList = doc.getElementsByTagName("SpreadSheet");
	    int totalSheets = nList.getLength();
	    Workbook wb = new Workbook(totalSheets);
	    NodeList nListCells = doc.getElementsByTagName("Cell");
	    int totalCells = nListCells.getLength();
	    Spreadsheet sheet = wb.getSpreadsheet(0);
	    for (int i = 0; i < totalCells; i++) {
		Node nNode = nListCells.item(i);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    Element eElement = (Element) nNode;
		    column = Integer.parseInt(eElement.getAttribute("Column"));
		    row = Integer.parseInt(eElement.getAttribute("Row"));
		    StylableCell scXML = (StylableCell) sheet.getCell(column,
			    row).getExtension(StyleExtension.NAME);
		    scXML.setBackgroundColor(new Color(Integer
			    .parseInt(eElement.getAttribute("BackgroundColor"))));
		    scXML.setForegroundColor(new Color(Integer
			    .parseInt(eElement.getAttribute("ForegroundColor"))));
		    scXML.setVerticalAlignment(getIntCellAlign(eElement
			    .getAttribute("CellAlign")));
		    scXML.setHorizontalAlignment(getIntTextAlign(eElement
			    .getAttribute("TextAlign")));
		    scXML.setFont(new Font(eElement.getAttribute("FontFamily"),
			    getBoldAndItalic(eElement.getAttribute("Bold"),
				    eElement.getAttribute("Italic")),
			    Integer.parseInt(eElement.getAttribute("FontSize"))));
		    scXML.setBorder(BorderFactory.createMatteBorder(
			    getIntBorder(eElement.getAttribute("BorderTop")),
			    getIntBorder(eElement.getAttribute("BorderLeft")),
			    getIntBorder(eElement.getAttribute("BorderBottom")),
			    getIntBorder(eElement.getAttribute("BorderRight")),
			    new Color(Integer.parseInt(eElement
				    .getAttribute("BorderColor")))));

		    scXML.setContent(eElement.getTextContent());

		    StylableCell scWorkbook = (StylableCell) workbook
			    .getSpreadsheet(0).getCell(column, row)
			    .getExtension(StyleExtension.NAME);

		    if (!testOneCell(scWorkbook, scXML)) {
			return false;
		    }
		}
	    }
	    return true;

	} catch (SAXException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	} catch (DOMException e) {
	    e.printStackTrace();
	} catch (FormulaCompilationException e) {
	    e.printStackTrace();
	}
	return false;
    }

    private int getIntBorder(String border) {
	if (border.compareTo("True") == 0) {
	    return 1;
	}
	return 0;
    }

    private int getBoldAndItalic(String bold, String italic) {
	if ((bold.compareTo("True") == 0) && (italic.compareTo("True") == 0)) {
	    return (Font.BOLD | Font.ITALIC);
	}

	if (bold.compareTo("True") == 0) {
	    return (Font.BOLD);
	}

	if (italic.compareTo("True") == 0) {
	    return (Font.ITALIC);
	}
	return 0;
    }

    private int getIntTextAlign(String textAlign) {
	if (textAlign.compareTo("Center") == 0) {
	    return SwingConstants.CENTER;
	}
	if (textAlign.compareTo("Left") == 0) {
	    return SwingConstants.LEFT;
	}
	if (textAlign.compareTo("Right") == 0) {
	    return SwingConstants.RIGHT;
	}
	return 2;
    }

    private int getIntCellAlign(String cellAlign) {

	if (cellAlign.compareTo("Center") == 0) {
	    return SwingConstants.CENTER;
	}
	if (cellAlign.compareTo("Top") == 0) {
	    return SwingConstants.TOP;
	}
	if (cellAlign.compareTo("Bottom") == 0) {
	    return SwingConstants.BOTTOM;
	}
	return 0;
    }

    private boolean testOneCell(StylableCell scWorkbook, StylableCell scXML) {
	if (scWorkbook.getBackgroundColor().getRGB() != scXML
		.getBackgroundColor().getRGB()) {
	    return false;
	}

	if (scWorkbook.getForegroundColor().getRGB() != scXML
		.getForegroundColor().getRGB()) {
	    return false;
	}

	if (scWorkbook.getVerticalAlignment() != scXML.getVerticalAlignment()) {
	    return false;
	}

	if (scWorkbook.getHorizontalAlignment() != scXML
		.getHorizontalAlignment()) {
	    return false;
	}

	if (!scWorkbook.getFont().getFamily()
		.equals(scXML.getFont().getFamily())) {
	    return false;
	}

	if (scWorkbook.getFont().getSize() != scXML.getFont().getSize()) {
	    return false;
	}

	if (scWorkbook.getFont().isBold() != scXML.getFont().isBold()) {
	    return false;
	}

	if (scWorkbook.getFont().isItalic() != scXML.getFont().isItalic()) {
	    return false;
	}

	return true;
    }

}

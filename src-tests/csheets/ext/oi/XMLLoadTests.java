package csheets.ext.oi;

import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Attr;
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

public class XMLLoadTests {
    private static DocumentBuilderFactory dbFactory;
    private static DocumentBuilder dBuilder;
    private static Document doc;

    private static String[] font = { Font.MONOSPACED, Font.SANS_SERIF,
	    Font.SERIF };
    private static Random r = new Random();
    private static File inFile = new File("XmlTextIn.xml");
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
	rows = (int) (Math.random() * 100);
	xOffset = (int) (Math.random() * 50);
	yOffset = (int) (Math.random() * 10);

	// let's create our workbook

	generateData();

	// let's save this workbook, it may contain precious info
	loadTestWorkbook();
    }

    /**
     * Saves this test workbook just in case
     */
    private static void loadTestWorkbook() {

	FileInputStream in;
	try {
	    in = new FileInputStream(inFile);
	    XMLCodec codec = new XMLCodec();
	    codec.read(in);
	    in.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (DOMException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	} catch (SAXException e) {
	    e.printStackTrace();
	} catch (FormulaCompilationException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private static void generateData() {
	try {
	    FileOutputStream stream = new FileOutputStream(inFile);
	    dbFactory = DocumentBuilderFactory.newInstance();
	    dBuilder = dbFactory.newDocumentBuilder();

	    doc = dBuilder.newDocument();
	    createXMLContent();

	    TransformerFactory transformerFactory = TransformerFactory
		    .newInstance();
	    Transformer transformer;

	    transformer = transformerFactory.newTransformer();

	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(stream);

	    transformer.transform(source, result);
	    stream.close();
	} catch (TransformerConfigurationException e) {
	    e.printStackTrace();
	} catch (TransformerException e) {
	    e.printStackTrace();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}

    }

    private static void createXMLContent() {
	int countRow = rows;
	int countColumn = columns;
	Element workBook = (Element) doc.createElement("Workbook");
	doc.appendChild(workBook);
	// set attribute to workBook element
	Attr attr = doc.createAttribute("name");
	attr.setValue("Ficheiro");
	workBook.setAttributeNode(attr);
	attr = doc.createAttribute("xmlns:xsi");
	attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
	workBook.setAttributeNode(attr);
	attr = doc.createAttribute("xsi:noNamespaceSchemaLocation");
	attr.setValue("XMLSchema.xsd");
	workBook.setAttributeNode(attr);
	Element spreadSheet = doc.createElement("SpreadSheet");
	workBook.appendChild(spreadSheet);
	// set attribute to spreadSheet element
	attr = doc.createAttribute("name");
	attr.setValue(makeRandomString());
	spreadSheet.setAttributeNode(attr);

	for (int row = 0; row <= countRow; row++) {
	    for (int column = 0; column <= countColumn; column++) {

		Element cell = doc.createElement("Cell");
		spreadSheet.appendChild(cell);
		// set attributes to cell element
		attr = doc.createAttribute("Column");
		attr.setValue("" + column);
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("Row");
		attr.setValue("" + row);
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("BackgroundColor");
		attr.setValue("" + makeRandomColor().getRGB());
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("TextAlign");
		attr.setValue("" + makeRandomStringTextAlignment(r.nextInt(2)));
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("CellAlign");
		attr.setValue("" + makeRandomStringCellAlignment(r.nextInt(2)));
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("ForegroundColor");
		attr.setValue("" + makeRandomColor().getRGB());
		cell.setAttributeNode(attr);
		Font f = makeRandomFont();
		attr = doc.createAttribute("FontFamily");
		attr.setValue(f.getFamily());
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("FontSize");
		attr.setValue("" + f.getSize());
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("BorderTop");
		attr.setValue("False");
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("BorderBottom");
		attr.setValue("False");
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("BorderLeft");
		attr.setValue("False");
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("BorderRight");
		attr.setValue("False");
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("BorderColor");
		attr.setValue("000000");
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("Bold");
		attr.setValue(makeRandomStringItalicBold());
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("Italic");
		attr.setValue(makeRandomStringItalicBold());
		cell.setAttributeNode(attr);
		attr = doc.createAttribute("Underline");
		attr.setValue("False");
		cell.setAttributeNode(attr);
		cell.appendChild(doc.createTextNode(makeRandomString()));
	    }
	}

	totalColumns += countColumn;
	totalRows += countRow;
    }

    private static String makeRandomStringItalicBold() {
	return ((r.nextInt() * 1) == 0 ? "True" : "False");
    }

    private static String makeRandomStringCellAlignment(int gerar) {
	switch (gerar) {
	case 1:
	    return "Center";
	case 2:
	    return "Bottom";
	case 3:
	    return "Top";
	default:
	    return "Center";
	}
    }

    private static String makeRandomStringTextAlignment(int gerar) {
	switch (gerar) {
	case 1:
	    return "Center";
	case 2:
	    return "Left";
	case 3:
	    return "Right";
	default:
	    return "Left";
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
	    FileInputStream stream = new FileInputStream(inFile);
	    XMLCodec xml = new XMLCodec();
	    Workbook wb = xml.read(stream);
	    if (testNumberSheetsAndCells(wb)) {
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
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (DOMException e) {
	    e.printStackTrace();
	} catch (SAXException e) {
	    e.printStackTrace();
	} catch (FormulaCompilationException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private boolean testNumberSheetsAndCells(Workbook workbook) {

	NodeList nList = doc.getElementsByTagName("SpreadSheet");
	int totalSheets = nList.getLength();
	int nSheets = workbook.getSpreadsheetCount();
	if (totalSheets == nSheets) {
	    int nCells = totalColumns * totalRows;
	    int totalCells = workbook.getSpreadsheet(0).getRowCount()
		    * workbook.getSpreadsheet(0).getColumnCount();
	    if (nCells != totalCells) {
		return false;
	    }
	    return true;
	}
	return false;
    }

    @Test
    public void compareNumberOfRows() {
	try {
	    FileInputStream stream = new FileInputStream(inFile);
	    XMLCodec xml = new XMLCodec();
	    Workbook wb = xml.read(stream);
	    if (testSheetsAndCellsContent(wb)) {
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
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (DOMException e) {
	    e.printStackTrace();
	} catch (SAXException e) {
	    e.printStackTrace();
	} catch (FormulaCompilationException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private boolean testSheetsAndCellsContent(Workbook workbook) {
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	int column = 0;
	int row = 0;
	try {
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inFile);

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

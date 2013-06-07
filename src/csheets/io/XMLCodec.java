package csheets.io;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
import csheets.io.mapping.Hibernate;
import csheets.io.mapping.MappedWorkbook;

/**
 * A codec for read and write XML files.
 * 
 * @author Filipe Silva & Rita Nogueira & Filipe Couto
 */
public class XMLCodec implements Codec {

	/**
	 * Creates a new XML codec.
	 */
	public XMLCodec() {
	}

	@Override
	public Workbook read(InputStream stream) throws IOException,
			ClassNotFoundException, ParserConfigurationException, SAXException,
			DOMException, FormulaCompilationException, Exception {
		int totalCells = 0;
		int totalSheets = 0;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(stream);

		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("SpreadSheet");
		totalSheets = nList.getLength();
		Workbook wb = new Workbook(totalSheets);

		for (int i = 0; i < totalSheets; i++) {
			Spreadsheet sheet = wb.getSpreadsheet(i);

			NodeList nCells = nList.item(i).getChildNodes();
			totalCells = nCells.getLength();

			for (int j = 0; j < totalCells; j++) {
				try {
					Node nNode = nCells.item(j);
					getStylabeCell(nNode, sheet);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return wb;
	}

	/**
	 * Method to get the stored information in an XML file for a StylabeCell
	 * 
	 * @param nNode
	 * @param sheet
	 * @return
	 * @throws DOMException
	 * @throws FormulaCompilationException
	 * 
	 */
	private StylableCell getStylabeCell(Node nNode, Spreadsheet sheet)
			throws DOMException, FormulaCompilationException {
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;
			StylableCell sc = (StylableCell) sheet.getCell(
					Integer.parseInt(eElement.getAttribute("Column")),
					Integer.parseInt(eElement.getAttribute("Row"))).getExtension(
					StyleExtension.NAME);

			sc.setBackgroundColor(new Color(Integer.parseInt(eElement
					.getAttribute("BackgroundColor"))));
			sc.setForegroundColor(new Color(Integer.parseInt(eElement
					.getAttribute("ForegroundColor"))));
			sc.setVerticalAlignment(getIntCellAlign(eElement
					.getAttribute("CellAlign")));
			sc.setHorizontalAlignment(getIntTextAlign(eElement
					.getAttribute("TextAlign")));
			sc.setFont(new Font(eElement.getAttribute("FontFamily"),
					getBoldAndItalic(eElement.getAttribute("Bold"),
							eElement.getAttribute("Italic")), Integer
							.parseInt(eElement.getAttribute("FontSize"))));
			sc.setBorder(BorderFactory.createMatteBorder(
					getIntBorder(eElement.getAttribute("BorderTop")),
					getIntBorder(eElement.getAttribute("BorderLeft")),
					getIntBorder(eElement.getAttribute("BorderBottom")),
					getIntBorder(eElement.getAttribute("BorderRight")),
					new Color(Integer.parseInt(eElement.getAttribute("BorderColor")))));

			sc.setContent(eElement.getTextContent());
			return sc;
		}
		return null;

	}

	/**
	 * Method for testing if there is a border lign
	 * 
	 * @param border
	 * @return
	 */
	private int getIntBorder(String border) {
		if (border.compareTo("True") == 0) {
			return 1;
		}
		return 0;
	}

	/**
	 * Method to test if text is bold and italic
	 * 
	 * @param bold
	 * @param italic
	 * @return
	 */
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

	/**
	 * Method for testing the alignment of the text
	 * 
	 * @param textAlign
	 * @return
	 */
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

	/**
	 * Method for testing the alignment of the cell
	 * 
	 * @param cellAlign
	 * @return
	 */
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

	/**
	 * Border is not fully supported in XML because CleanSheets doesn't have
	 * access methods to it
	 */
	@Override
	public void write(Workbook workbook, OutputStream stream)
			throws IOException, TransformerException, ParserConfigurationException {
	    	// creates the database in memory
	    	Transaction transaction = null;
	    	SessionFactory sessionFactory = Hibernate.getSessionFactory();
	    	Session session = sessionFactory.openSession();
	    	transaction = session.beginTransaction();
	    	session.persist(new MappedWorkbook(workbook));
	    	transaction.commit();
	    	
	    	// exports the same database to an xml files
	    	try {
        	    	Class driverClass = Class.forName("org.hsqldb.jdbcDriver");
        	        Connection jdbcConnection = DriverManager.getConnection(
        	                "jdbc:hsqldb:mem:DBNAME", "sa", "");
        	        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        	    	IDataSet fullSet = connection.createDataSet();
        	    	FlatXmlDataSet.write(fullSet, stream);
	    	} catch (Exception e) {
	    	    e.printStackTrace();
	    	}
	    	
	    	
	    		/*
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		// root element
		Document doc = docBuilder.newDocument();
		createXMLContent(doc, workbook);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(stream);

		transformer.transform(source, result);
		// Frees resources
		stream.close();*/
	}

	/**
	 * Method to create the location and the variables to receive information to
	 * be stored
	 * 
	 * @param doc
	 * @param workbook
	 */
	private void createXMLContent(Document doc, Workbook workbook) {
		int iS = 0;
		int countSpreadsheet;
		int countRow;
		int countColumn;

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
		// going through the spreadsheets
		// for (Spreadsheet sheet : workbook) {
		countSpreadsheet = workbook.getSpreadsheetCount();

		for (iS = 0; iS < countSpreadsheet; iS++) {
			Spreadsheet sheet = workbook.getSpreadsheet(iS);
			// spreadSheet element
			Element spreadSheet = doc.createElement("SpreadSheet");
			workBook.appendChild(spreadSheet);
			// set attribute to spreadSheet element
			attr = doc.createAttribute("name");
			attr.setValue(sheet.getTitle());
			spreadSheet.setAttributeNode(attr);

			// going through the cells
			countRow = sheet.getRowCount();
			countColumn = sheet.getColumnCount();
			for (int row = 0; row <= countRow; row++) {
				for (int column = 0; column <= countColumn; column++) {
					setXMLContent(sheet, column, row, doc, spreadSheet);

				}
			}
		}

	}

	/**
	 * Method to put the information in the document
	 * 
	 * @param sheet
	 * @param column
	 * @param row
	 * @param doc
	 * @param spreadSheet
	 */
	private void setXMLContent(Spreadsheet sheet, int column, int row,
			Document doc, Element spreadSheet) {
		String textAlignString;
		String cellAlignString;
		String styleBold = "False";
		String styleItalic = "False";
		Attr attr = null;

		// testing the background
		if ((((((StylableCell) (sheet.getCell(column, row))
				.getExtension(StyleExtension.NAME)).getBackgroundColor().getRGB()) != (Color.WHITE
				.getRGB())) && ((((StylableCell) (sheet.getCell(column, row))
				.getExtension(StyleExtension.NAME)).getForegroundColor().getRGB()) != (Color.BLACK
				.getRGB())))
				|| (sheet.getCell(column, row).getContent()).length() != 0) {
			// create StylableCell to access cell styles
			StylableCell stylableCell = (StylableCell) sheet.getCell(column, row)
					.getExtension(StyleExtension.NAME);
			// get the horizontal alignment value and define a
			// string to it
			textAlignString = getStringTextAlign(stylableCell
					.getHorizontalAlignment());

			// get the vertical alignment value and define a
			// string to it
			cellAlignString = getStringCellAlign(stylableCell
					.getVerticalAlignment());

			// define the use of bold and/or italic
			styleBold = getBold(stylableCell.getFont());
			styleItalic = getItalic(stylableCell.getFont());

			// cell element
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
			attr.setValue("" + stylableCell.getBackgroundColor().getRGB());
			cell.setAttributeNode(attr);
			attr = doc.createAttribute("TextAlign");
			attr.setValue(textAlignString);
			cell.setAttributeNode(attr);
			attr = doc.createAttribute("CellAlign");
			attr.setValue(cellAlignString);
			cell.setAttributeNode(attr);
			attr = doc.createAttribute("ForegroundColor");
			attr.setValue("" + stylableCell.getForegroundColor().getRGB());
			cell.setAttributeNode(attr);
			attr = doc.createAttribute("FontFamily");
			attr.setValue("" + stylableCell.getFont().getFamily());
			cell.setAttributeNode(attr);
			attr = doc.createAttribute("FontSize");
			attr.setValue("" + stylableCell.getFont().getSize());
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
			attr.setValue(styleBold);
			cell.setAttributeNode(attr);
			attr = doc.createAttribute("Italic");
			attr.setValue(styleItalic);
			cell.setAttributeNode(attr);
			attr = doc.createAttribute("Underline");
			attr.setValue("False");
			cell.setAttributeNode(attr);
			cell.appendChild(doc.createTextNode(sheet.getCell(column, row)
					.getContent()));
		}

	}

	/**
	 * Method to determine whether the cell is in bold and prepare the
	 * information for the file
	 * 
	 * @param font
	 * @return
	 */
	private String getBold(Font font) {
		return (font.isBold() ? "True" : "False");
	}

	/**
	 * Method to determine whether the cell is in italic and prepare the
	 * information for the file
	 * 
	 * @param font
	 * @return
	 */
	private String getItalic(Font font) {
		return (font.isItalic() ? "True" : "False");
	}

	/**
	 * Method for determining the alignment of the cell and prepare the
	 * information for the file
	 * 
	 * @param cellAlign
	 * @return
	 */
	private String getStringCellAlign(int cellAlign) {
		switch (cellAlign) {
			case SwingConstants.CENTER:
				return "Center";
			case SwingConstants.TOP:
				return "Top";
			case SwingConstants.BOTTOM:
				return "Bottom";
			default:
				return "Center";
		}
	}

	/**
	 * Method for determining the alignment of the text and prepare the
	 * information for the file
	 * 
	 * @param textAlign
	 * @return
	 */
	private String getStringTextAlign(int textAlign) {
		switch (textAlign) {
			case SwingConstants.CENTER:
				return "Center";
			case SwingConstants.LEFT:
				return "Left";
			case SwingConstants.RIGHT:
				return "Right";
			default:
				return "Left";
		}
	}
}

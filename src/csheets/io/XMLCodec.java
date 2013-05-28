package csheets.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import csheets.core.Spreadsheet;
import csheets.core.Workbook;
import csheets.ext.style.StylableCell;
import csheets.ext.style.StyleExtension;

/**
 * A codec for xml files.
 * 
 * @author Filipe Silva & Rita Nogueira
 */
public class XMLCodec implements Codec {

    /**
    * Creates a new XML codec.
    */
    public XMLCodec() {}
    
    
    @Override
    public Workbook read(InputStream stream) throws IOException,
	    ClassNotFoundException {
	Workbook workbook= new Workbook();
	try {

	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory
		    .newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(stream);
	    
	    doc.getDocumentElement().normalize();

	    System.out.println("Root element :"
		    + doc.getDocumentElement().getNodeName());

	    NodeList nList = doc.getElementsByTagName("SpreadSheet");
            int totalSheets = nList.getLength();
	    System.out.println("----------------------------");

	    for (int i = 0; i < totalSheets; i++) {

		Node nNode = nList.item(i);

		System.out.println("\nCurrent Element :" + nNode.getNodeName());

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

		    Element eElement = (Element) nNode;

		    System.out.println("Staff id : "
			    + eElement.getAttribute("Column"));
		    System.out.println("Salary : "
			    + eElement.getElementsByTagName("salary").item(0)
				    .getTextContent());

		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;

    }

    @Override
    public void write(Workbook workbook, OutputStream stream)
	    throws IOException {
	int textAlign, cellAlign;
	String textAlignString, cellAlignString, styleBold = "False", styleItalic = "False";

	try {
	    DocumentBuilderFactory docFactory = DocumentBuilderFactory
		    .newInstance();
	    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	    // root element
	    Document doc = docBuilder.newDocument();
	    Element workBook = (Element) doc.createElement("Workbook");
	    doc.appendChild(workBook);
	    // set attribute to workBook element
	    Attr attr = doc.createAttribute("name");
	    attr.setValue("Workbook Name");
	    workBook.setAttributeNode(attr);
	    // going through the spreadsheets
	    for (Spreadsheet sheet : workbook) {
		// spreadSheet element
		Element spreadSheet = doc.createElement("SpreadSheet");
		workBook.appendChild(spreadSheet);
		// set attribute to spreadSheet element
		attr = doc.createAttribute("name");
		attr.setValue("SpreadSheet Name");
		spreadSheet.setAttributeNode(attr);

		// going through the cells
		for (int row = 0; row < sheet.getRowCount(); row++) {
		    for (int column = 0; column < sheet.getColumnCount(); column++) {
			if (column + 1 < sheet.getColumnCount()) {
			    // create StylableCell to access cell styles
			    StylableCell stylableCell = (StylableCell) sheet
				    .getCell(column, row).getExtension(
					    StyleExtension.NAME);
			    // get the horizontal alignment value and define a
			    // string to it
			    textAlign = stylableCell.getHorizontalAlignment();
			    switch (textAlign) {
			    case 0:
				textAlignString = "Center";
				break;
			    case 2:
				textAlignString = "Left";
				break;
			    case 4:
				textAlignString = "Right";
				break;
			    default:
				textAlignString = "Left";
				break;
			    }
			    // get the vertical alignment value and define a
			    // string to it
			    cellAlign = stylableCell.getVerticalAlignment();
			    switch (cellAlign) {
			    case 0:
				cellAlignString = "Center";
				break;
			    case 1:
				cellAlignString = "Top";
				break;
			    case 3:
				cellAlignString = "Bottom";
				break;
			    default:
				cellAlignString = "Center";
				break;
			    }
			    // define the use of bold and/or italic
			    if (stylableCell.getFont().isPlain()) {
				styleBold = "False";
				styleItalic = "False";
			    } else if (stylableCell.getFont().isBold()
				    && stylableCell.getFont().isItalic()) {
				styleBold = "True";
				styleItalic = "True";
			    } else if (stylableCell.getFont().isBold()) {
				styleBold = "True";
				styleItalic = "False";
			    } else if (stylableCell.getFont().isItalic()) {
				styleBold = "False";
				styleItalic = "True";
			    }

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
			    attr.setValue(""
				    + stylableCell.getBackgroundColor()
					    .getRGB());
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
			    attr = doc.createAttribute("TextAlign");
			    attr.setValue(textAlignString);
			    cell.setAttributeNode(attr);
			    attr = doc.createAttribute("CellAlign");
			    attr.setValue(cellAlignString);
			    cell.setAttributeNode(attr);
			    attr = doc.createAttribute("ForegroundColor");
			    attr.setValue(""
				    + stylableCell.getForegroundColor()
					    .getRGB());
			    cell.setAttributeNode(attr);
			    attr = doc.createAttribute("FontFamily");
			    attr.setValue(""
				    + stylableCell.getFont().getFamily());
			    cell.setAttributeNode(attr);
			    attr = doc.createAttribute("FontSize");
			    attr.setValue("" + stylableCell.getFont().getSize());
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
			    cell.appendChild(doc.createTextNode(sheet.getCell(
				    column, row).getContent()));
			    // output
			    TransformerFactory transformerFactory = TransformerFactory
				    .newInstance();
			    Transformer transformer = transformerFactory
				    .newTransformer();
			    DOMSource source = new DOMSource(doc);
			    StreamResult result = new StreamResult(stream);

			    // Output to console for testing
			    // StreamResult result = new
			    // StreamResult(System.out);

			    transformer.transform(source, result);

			}
		    }
		}
	    }
	} catch (ParserConfigurationException pce) {
	    pce.printStackTrace();
	} catch (TransformerException tfe) {
	    tfe.printStackTrace();
	}
	// Frees resources
	stream.close();
    }
}

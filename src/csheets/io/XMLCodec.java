package csheets.io;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import javax.swing.BorderFactory;
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
import org.xml.sax.InputSource;

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
    public XMLCodec() {
    }

    @Override
    public Workbook read(InputStream stream) throws IOException,
	    ClassNotFoundException {
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
	    Workbook wb = new Workbook(totalSheets);

	    for (int i = 0; i < totalSheets; i++) {
		Spreadsheet sheet = wb.getSpreadsheet(i);
		Node nNode = nList.item(i);

		System.out.println("\nCurrent Element :" + nNode.getNodeName());

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    Element eElement = (Element) nNode;
		    StylableCell sc = (StylableCell) sheet.getCell(
			    Integer.parseInt(eElement.getAttribute("Column")),
			    Integer.parseInt(eElement.getAttribute("Row")));

		    sc.setBackgroundColor(new Color(Integer.parseInt(eElement
			    .getAttribute("BackgroundColor"))));
		    sc.setForegroundColor(new Color(Integer.parseInt(eElement
			    .getAttribute("ForegroundColor"))));
		    sc.setHorizontalAlignment(getIntCellAlign(eElement
			    .getAttribute("CellAlign")));
		    sc.setVerticalAlignment(getIntTextAlign(eElement
			    .getAttribute("TextAlign")));
		    sc.setFont(new Font(eElement.getAttribute("FontFamily"),
			    getBoldAndItalic(eElement.getAttribute("Bold"),
				    eElement.getAttribute("Italic")), Integer
				    .parseInt(eElement.getAttribute("Size"))));
		    sc.setBorder(BorderFactory.createMatteBorder(
			    getIntBorder(eElement.getAttribute("BorderTop")),
			    getIntBorder(eElement.getAttribute("BorderLeft")),
			    getIntBorder(eElement.getAttribute("BorderBottom")),
			    getIntBorder(eElement.getAttribute("BorderRight")),
			    new Color(Integer.parseInt(eElement
				    .getAttribute("BorderColor")))));
		}
	    }
	    return wb;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
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
	    return 0;
	}
	if (textAlign.compareTo("Left") == 0) {
	    return 2;
	}
	if (textAlign.compareTo("Right") == 0) {
	    return 4;
	}
	return 2;
    }

    private int getIntCellAlign(String cellAlign) {

	if (cellAlign.compareTo("Center") == 0) {
	    return 0;
	}
	if (cellAlign.compareTo("Top") == 0) {
	    return 1;
	}
	if (cellAlign.compareTo("Bottom") == 0) {
	    return 3;
	}
	return 0;
    }

    @Override
    public void write(Workbook workbook, OutputStream stream)
	    throws IOException {
	int textAlign, cellAlign;
	String textAlignString, cellAlignString, styleBold = "False", styleItalic = "False";
	DocumentBuilderFactory docFactory = DocumentBuilderFactory
		.newInstance();

	try {
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

    private String getBold(Font font) {
	return (font.isBold() ? "True" : "False");
    }

    private String getItalic(Font font) {
	return (font.isItalic() ? "True" : "False");
    }

    private String getStringCellAlign(int cellAlign) {
	switch (cellAlign) {
	case 0:
	    return "Center";
	case 1:
	    return "Top";
	case 3:
	    return "Bottom";
	default:
	    return "Center";
	}
    }

    private String getStringTextAlign(int textAlign) {
	switch (textAlign) {
	case 0:
	    return "Center";
	case 2:
	    return "Left";
	case 4:
	    return "Right";
	default:
	    return "Left";
	}
    }
}

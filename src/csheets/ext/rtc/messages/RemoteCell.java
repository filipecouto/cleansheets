package csheets.ext.rtc.messages;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;

import javax.swing.border.Border;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.Workbook;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.ext.rtc.RtcSharingProperties;
import csheets.ext.style.StylableCell;
import csheets.ext.style.StyleExtension;

/**
 * This class holds data from Cell instances in order to send them across the
 * network
 * 
 * @author gil_1110484
 */
public class RemoteCell implements Serializable {
    private static final long serialVersionUID = 7156793484407606025L;

    int spreadsheet;

    Address address;
    String content;

    Color backgroundColor;
    Border border;
    Font font;
    Color foregroundColor;
    int horizontalAlignment;
    int verticalAlignment;

    /**
     * Constructor, requires a Cell.
     * 
     * @param cell
     *            the cell to send
     */
    public RemoteCell(Cell cell) {
	spreadsheet = 0;

	this.address = cell.getAddress();
	this.content = cell.getContent();

	StylableCell sc = (StylableCell) cell.getExtension(StyleExtension.NAME);
	backgroundColor = sc.getBackgroundColor();
	border = sc.getBorder();
	font = sc.getFont();
	foregroundColor = sc.getForegroundColor();
	horizontalAlignment = sc.getHorizontalAlignment();
	verticalAlignment = sc.getVerticalAlignment();
    }

    /**
     * Puts the held Cell in its right place
     * 
     * @param workbook
     *            the workbook where to put the cell
     */
    public void getCell(Workbook workbook) {
	final Cell cell = workbook.getSpreadsheet(spreadsheet).getCell(address);
	try {
	    cell.setContent(content);
	} catch (FormulaCompilationException e) {
	}

	StylableCell sc = (StylableCell) cell.getExtension(StyleExtension.NAME);
	sc.setBackgroundColor(backgroundColor);
	sc.setBorder(border);
	sc.setFont(font);
	sc.setForegroundColor(foregroundColor);
	sc.setHorizontalAlignment(horizontalAlignment);
	sc.setVerticalAlignment(verticalAlignment);
    }

    /**
     * Says whether this cell is in a shared place, accordingly to
     * RtcSharingProperties
     * 
     * @param properties
     *            the sharing properties
     * @return true if it is in a shared place, false otherwise
     */
    public boolean isValid(RtcSharingProperties properties) {
	return properties.isInsideRange(address)
		&& properties.getSpreadsheet() == spreadsheet;
    }
}

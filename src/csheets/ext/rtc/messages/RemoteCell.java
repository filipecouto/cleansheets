package csheets.ext.rtc.messages;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.SortedSet;

import javax.swing.border.Border;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.CellListener;
import csheets.core.Spreadsheet;
import csheets.core.Value;
import csheets.core.Workbook;
import csheets.core.formula.Formula;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.ext.rtc.RtcShareProperties;
import csheets.ext.style.StylableCell;
import csheets.ext.style.StyleExtension;

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

    public boolean isValid(RtcShareProperties properties) {
	return properties.isInsideRange(address)
		&& properties.getSpreadsheet() == spreadsheet;
    }
}

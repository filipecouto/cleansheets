package csheets.io.mapping;

import java.awt.Color;
import java.awt.Font;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.swing.border.Border;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.ext.style.StylableCell;
import csheets.ext.style.StyleExtension;

@Entity(name = "Cell")
public class MappedCell {
	@Id
	private int id;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	private MappedSpreadsheet spreadsheet;

	private Address address;

	private String content;

	private Color backgroundColor;
	private Border border;
	private Font font;
	private Color foregroundColor;
	private int horizontalAlignment;
	private int verticalAlignment;

	private MappedCell() {
		// empty
	}

	MappedCell(MappedSpreadsheet spreadsheet, Cell cell) {
		this.spreadsheet = spreadsheet;

		address = cell.getAddress();
		content = cell.getContent();

		final Cell extension = cell.getExtension(StyleExtension.NAME);
		if (extension != null) {
			final StylableCell style = (StylableCell) extension;
			backgroundColor = style.getBackgroundColor();
			border = style.getBorder();
			font = style.getFont();
			foregroundColor = style.getForegroundColor();
			horizontalAlignment = style.getHorizontalAlignment();
			verticalAlignment = style.getVerticalAlignment();
		}
	}

	void makeCell(Cell cell) {
		try {
			cell.setContent(content);
		} catch (FormulaCompilationException e) {
			// we shouldn't keep the user from seeing his/her file just because
			// there's a wrong formula
		}

		final Cell extension = cell.getExtension(StyleExtension.NAME);
		if (extension != null) {
			final StylableCell style = (StylableCell) extension;
			style.setBackgroundColor(backgroundColor);
			style.setBorder(border);
			style.setFont(font);
			style.setForegroundColor(foregroundColor);
			style.setHorizontalAlignment(horizontalAlignment);
			style.setVerticalAlignment(verticalAlignment);
		}
	}
	
	Address getAddress() {
		return address;
	}
}

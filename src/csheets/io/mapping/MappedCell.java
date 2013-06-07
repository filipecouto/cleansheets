package csheets.io.mapping;

import java.awt.Color;
import java.awt.Font;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.swing.border.Border;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.ext.style.StylableCell;
import csheets.ext.style.StyleExtension;

/**
 * This class holds all the data needed to store a Cell in the database and also
 * builds a new one from its contained data
 * 
 * @author Gil Castro (gil_1110484)
 */
@Entity(name = "Cell")
public class MappedCell {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	private MappedSpreadsheet spreadsheet;

	private Address address;

	private String content;

	private Color backgroundColor;
	//private Border border;
	private Font font;
	private Color foregroundColor;
	private int horizontalAlignment;
	private int verticalAlignment;

	/**
	 * Constructor needed to rebuild the data from a persistent source
	 */
	MappedCell() {
		// empty
	}

	/**
	 * Constructor that stores all the needed data.
	 * 
	 * This constructor should be used when the user wishes to save his/her
	 * workbook and then pass this instance to the persistence target.
	 * 
	 * @param spreadsheet
	 *           the parent of this Cell representation
	 * @param cell
	 *           the Cell to store
	 */
	MappedCell(MappedSpreadsheet spreadsheet, Cell cell) {
		this.spreadsheet = spreadsheet;

		address = cell.getAddress();
		content = cell.getContent();

		final Cell extension = cell.getExtension(StyleExtension.NAME);
		if (extension != null) {
			final StylableCell style = (StylableCell) extension;
			backgroundColor = style.getBackgroundColor();
			//border = style.getBorder();
			font = style.getFont();
			foregroundColor = style.getForegroundColor();
			horizontalAlignment = style.getHorizontalAlignment();
			verticalAlignment = style.getVerticalAlignment();
		}
	}

	/**
	 * Restores all the stored data into this <code>cell</code>.
	 * 
	 * This should be used when the user wishes to open his/her workbook.
	 * 
	 * @param cell
	 *           the Cell where to restore this data into
	 */
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
			//style.setBorder(border);
			style.setFont(font);
			style.setForegroundColor(foregroundColor);
			style.setHorizontalAlignment(horizontalAlignment);
			style.setVerticalAlignment(verticalAlignment);
		}
	}

	/**
	 * Gets the address of this cell
	 * 
	 * @return the address
	 */
	Address getAddress() {
		return address;
	}
}

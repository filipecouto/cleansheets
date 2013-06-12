package csheets.io.mapping;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import csheets.core.Cell;
import csheets.core.Spreadsheet;

/**
 * This class holds all the data needed to store a Spreadsheet in the database
 * and also builds a new one from its contained data
 * 
 * @author Gil Castro (gil_1110484)
 */
@Entity(name = "Spreadsheet")
class MappedSpreadsheet {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	private MappedWorkbook workbook;

	private String title;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "spreadsheet")
	private List<MappedCell> cells;

	/**
	 * Constructor needed to rebuild the data from a persistent source
	 */
	MappedSpreadsheet() {
		// empty
	}

	/**
	 * Constructor that stores all the needed data.
	 * 
	 * This constructor should be used when the user wishes to save his/her
	 * workbook and then pass this instance to the persistence target.
	 * 
	 * @param workbook
	 *           the parent of this Spreadsheet representation
	 * @param sheet
	 *           the Spreadsheet to store
	 */
	MappedSpreadsheet(MappedWorkbook workbook, Spreadsheet sheet) {
		this.workbook = workbook;

		cells = new ArrayList<MappedCell>();

		title = sheet.getTitle();

		for (Cell cell : sheet) {
			cells.add(new MappedCell(this, cell));
		}
	}

	/**
	 * Returns the title of this MappedSpreadsheet, this title will be used when
	 * making the spreadsheet.
	 * 
	 * @return the title of this MappedSpreadsheet
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Restores all the stored data into this <code>sheet</code>.
	 * 
	 * This should be used when the user wishes to open his/her workbook.
	 * 
	 * @param sheet
	 *           the Spreadsheet where to restore this data into
	 */
	void makeSpreadsheet(Spreadsheet sheet) {
		sheet.setTitle(title);

		for (MappedCell cell : cells) {
			cell.makeCell(sheet.getCell(cell.getAddress()));
		}
	}
}

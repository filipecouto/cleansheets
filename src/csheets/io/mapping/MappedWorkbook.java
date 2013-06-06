package csheets.io.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import csheets.core.Spreadsheet;
import csheets.core.Workbook;

/**
 * This class holds all the data needed to store a Workbook in the database and
 * also builds a new one from its contained data.
 * 
 * As requested, this class also holds the date of its creation/modification.
 * 
 * @author Gil Castro (gil_1110484)
 */
@Entity(name = "Workbook")
public class MappedWorkbook {
	@Id
	private int id;

	private Date version;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "workbook")
	private List<MappedSpreadsheet> spreadsheets;

	/**
	 * Constructor needed to rebuild the data from a persistent source
	 */
	MappedWorkbook() {
		// empty
	}

	/**
	 * Constructor that stores all the needed data.
	 * 
	 * This constructor should be used when the user wishes to save his/her
	 * workbook and then pass this instance to the persistence target.
	 * 
	 * @param workbook
	 *           the Workbook to store
	 */
	public MappedWorkbook(Workbook workbook) {
		version = new Date();
		spreadsheets = new ArrayList<MappedSpreadsheet>();
		for (Spreadsheet sheet : workbook) {
			spreadsheets.add(new MappedSpreadsheet(this, sheet));
		}
	}

	/**
	 * Restores all the stored data and returns it.
	 * 
	 * This should be used when the user wishes to open his/her workbook.
	 * 
	 * @return a Workbook, rebuilt from this stored data
	 */
	public Workbook makeWorkbook() {
		final int len = spreadsheets.size();
		Workbook book = new Workbook(len);

		for (int i = 0; i < len; i++) {
			spreadsheets.get(i).makeSpreadsheet(book.getSpreadsheet(i));
		}

		return book;
	}
}

package csheets.io.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.AUTO)
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
		Workbook book = new Workbook(spreadsheets.size());

		rebuildWorkbook(book);

		return book;
	}

	public Workbook makeWorkbook(Workbook target) {
		while (target.getSpreadsheetCount() != 0) {
			target.removeSpreadsheet(target.getSpreadsheet(0));
		}

		final int len = spreadsheets.size();
		for (int i = 0; i < len; i++) {
			target.addSpreadsheet(spreadsheets.get(i).getTitle());
		}

		rebuildWorkbook(target);

		return target;
	}

	private void rebuildWorkbook(Workbook target) {
		final int len = spreadsheets.size();

		for (int i = 0; i < len; i++) {
			spreadsheets.get(i).makeSpreadsheet(target.getSpreadsheet(i));
		}
	}
}

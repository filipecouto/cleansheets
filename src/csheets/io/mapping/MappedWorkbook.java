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

@Entity(name = "Workbook")
public class MappedWorkbook {
	@Id
	private int id;

	private Date version;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "workbook")
	private List<MappedSpreadsheet> spreadsheets;

	private MappedWorkbook() {
		// empty
	}

	public MappedWorkbook(Workbook workbook) {
		version = new Date();
		spreadsheets = new ArrayList<MappedSpreadsheet>();
		for (Spreadsheet sheet : workbook) {
			spreadsheets.add(new MappedSpreadsheet(this, sheet));
		}
	}

	public Workbook makeWorkbook() {
		final int len = spreadsheets.size();
		Workbook book = new Workbook(len);

		for (int i = 0; i < len; i++) {
			spreadsheets.get(i).makeSpreadsheet(book.getSpreadsheet(i));
		}

		return book;
	}
}

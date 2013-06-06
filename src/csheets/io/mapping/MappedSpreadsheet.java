package csheets.io.mapping;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import csheets.core.Cell;
import csheets.core.Spreadsheet;

@Entity(name = "Spreadsheet")
class MappedSpreadsheet {
	@Id
	private int id;

	private MappedWorkbook workbook;

	private String title;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "spreadsheet")
	private List<MappedCell> cells;

	private MappedSpreadsheet() {
		// empty
	}

	MappedSpreadsheet(MappedWorkbook workbook, Spreadsheet sheet) {
		this.workbook = workbook;

		title = sheet.getTitle();

		for (Cell cell : sheet) {
			cells.add(new MappedCell(this, cell));
		}
	}

	void makeSpreadsheet(Spreadsheet sheet) {
		sheet.setTitle(title);

		for (MappedCell cell : cells) {
			cell.makeCell(sheet.getCell(cell.getAddress()));
		}
	}
}

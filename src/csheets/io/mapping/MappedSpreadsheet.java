package csheets.io.mapping;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "Spreadsheet")
public class MappedSpreadsheet {
	@Id
	private int id;
	
	private MappedWorkbook workbook;
	
	private String name;
}

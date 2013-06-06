package csheets.io.mapping;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "Workbook")
public class MappedWorkbook {
	@Id
	private int id;
	
	private Date version;
}

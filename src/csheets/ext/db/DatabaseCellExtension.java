package csheets.ext.db;

import csheets.core.Cell;
import csheets.ext.CellExtension;

public class DatabaseCellExtension extends CellExtension {
    private int id;

    public DatabaseCellExtension(Cell delegate, String name) {
	super(delegate, name);
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }
}
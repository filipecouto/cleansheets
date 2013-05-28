package csheets.ext.db;

import javax.swing.JOptionPane;

import csheets.core.Cell;

public class DatabaseExportController {

	public DatabaseExportController() {
		
	}
	
	public void export(DatabaseExportControllerContainer container) {
		DatabaseExportBuilder exportBuilder = new DatabaseExportBuilder(container.getExtension().getAvailableDrivers()[container.getFormat().getSelectedIndex()]);
		exportBuilder.setDatabase(container.getUrl().getText().length() != 0 ? container.getUrl().getText() : container.getFileChooser().getSelectedFile().getAbsolutePath());
		exportBuilder.setTableName(container.getTableName().getText());
		final Cell[][] selectedCells = container.getTable().getSelectedCells();
		final int rowCount = selectedCells.length - 1;
		if (rowCount < 1) return;
		final int columnCount = selectedCells[0].length;
		String[] columns = new String[columnCount];
		for (int i = 0; i < columnCount; i++) {
			columns[i] = selectedCells[0][i].getValue().toString();
		}
		exportBuilder.setColumns(columns);
		String[][] values = new String[rowCount][columnCount];
		for (int y = 0; y < rowCount; y++) {
			for (int x = 0; x < columnCount; x++) {
				values[y][x] = selectedCells[y + 1][x].getValue().toString();
			}
		}
		exportBuilder.setValues(values);
		exportBuilder.export();
	}
	
}

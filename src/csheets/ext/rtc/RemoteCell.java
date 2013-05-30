package csheets.ext.rtc;

import java.util.SortedSet;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.CellListener;
import csheets.core.Spreadsheet;
import csheets.core.Value;
import csheets.core.formula.Formula;
import csheets.core.formula.compiler.FormulaCompilationException;

public class RemoteCell implements Cell {
    Address address;
    // Value value;
    String content;

    // Formula formula;

    public RemoteCell(Cell cell) {
	this.address = cell.getAddress();
	// this.value = cell.getValue();
	this.content = cell.getContent();
	// this.formula = cell.getFormula().;
    }

    @Override
    public int compareTo(Cell o) {
	return 0;
    }

    @Override
    public Cell getExtension(String name) {
	return null;
    }

    @Override
    public Spreadsheet getSpreadsheet() {
	return null;
    }

    @Override
    public Address getAddress() {
	return address;
    }

    @Override
    public Value getValue() {
	return null;
    }

    @Override
    public String getContent() {
	return content;
    }

    @Override
    public Formula getFormula() {
	return null;
    }

    @Override
    public void setContent(String content) throws FormulaCompilationException {
    }

    @Override
    public void clear() {
    }

    @Override
    public SortedSet<Cell> getPrecedents() {
	return null;
    }

    @Override
    public SortedSet<Cell> getDependents() {
	return null;
    }

    @Override
    public void copyFrom(Cell source) {
    }

    @Override
    public void moveFrom(Cell source) {
    }

    @Override
    public void addCellListener(CellListener listener) {
    }

    @Override
    public void removeCellListener(CellListener listener) {
    }

    @Override
    public CellListener[] getCellListeners() {
	return null;
    }

}

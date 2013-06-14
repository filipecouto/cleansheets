/*
 * Copyright (c) 2005 Einar Pehrson <einar@pehrson.nu>.
 * 
 * This file is part of CleanSheets - a spreadsheet application for the Java platform.
 * 
 * CleanSheets is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * CleanSheets is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with CleanSheets; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package csheets.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import csheets.io.versioning.VersionControllerCodec;

/**
 * A workbook which can contain several spreadsheets.
 * 
 * @author Einar Pehrson
 */
public class Workbook implements Iterable<Spreadsheet>, Serializable {

	/** The unique version identifier used for serialization */
	private static final long serialVersionUID = -6324252462576447242L;

	/** The spreadsheets of which the workbook consists */
	private List<Spreadsheet> spreadsheets = new ArrayList<Spreadsheet>();

	/** The cell listeners that have been registered on the cell */
	private transient List<WorkbookListener> listeners = new ArrayList<WorkbookListener>();

	/** The number of spreadsheets that have been created in the workbook */
	private int createdSpreadsheets;

	/**
	 * A VersionControllerCodec in charge of retrieving and setting different
	 * versions for the Workbook
	 */
	private VersionControllerCodec versionController;

	/**
	 * Creates a new empty workbook.
	 */
	public Workbook() {
	}

	/**
	 * Creates a new workbook, which initially contains the given number of blank
	 * spreadsheets.
	 * 
	 * @param sheets
	 *           the number of sheets to create initially
	 */
	public Workbook(int sheets) {
		for (int i = 0; i < sheets; i++)
			spreadsheets.add(new SpreadsheetImpl(this, getNextSpreadsheetTitle()));
	}

	/**
	 * Creates a new workbook, using the given content matrix to create
	 * spreadsheets initially.
	 * 
	 * @param contents
	 *           the content matrices to use when creating spreadsheets
	 */
	public Workbook(String[][]... contents) {
		for (String[][] content : contents)
			spreadsheets.add(new SpreadsheetImpl(this, getNextSpreadsheetTitle(),
					content));
	}

	/**
	 * Gets a version controller, if available
	 * 
	 * @return a VersionControllerCodec or null
	 * @since 1.5
	 */
	public VersionControllerCodec getVersionController() {
		return versionController;
	}

	/**
	 * Sets a VersionControllerCodec in order to allow the app to load and set
	 * different versions of this Workbook. This is usually useful when a Codec
	 * is loading the Workbook.
	 * 
	 * @param versionController
	 *           the VersionControllerCodec to set
	 * @since 1.5
	 */
	public void setVersionController(VersionControllerCodec versionController) {
		this.versionController = versionController;
	}

	/**
	 * Adds a blank spreadsheet to the end of the workbook.
	 */
	public void addSpreadsheet() {
		Spreadsheet spreadsheet = new SpreadsheetImpl(this,
				getNextSpreadsheetTitle());
		spreadsheets.add(spreadsheet);
		fireSpreadsheetInserted(spreadsheet, spreadsheets.size() - 1);
	}

	/**
	 * Adds a blank spreadsheet to the end of the workbook. This method was
	 * created in version 1.5 as a workaround for a bug in the current GUI (GUI
	 * won't update when the title of a spreadsheet changes).
	 * 
	 * @param title
	 *           the title of the spreadsheet to add
	 * @since 1.5
	 */
	public void addSpreadsheet(String title) {
		Spreadsheet spreadsheet = new SpreadsheetImpl(this, title);
		spreadsheets.add(spreadsheet);
		fireSpreadsheetInserted(spreadsheet, spreadsheets.size() - 1);
	}

	/**
	 * Adds a new spreadsheet to the workbook, in which cells are initialized
	 * with data from the given content matrix.
	 * 
	 * @param content
	 *           the contents of the cells in the spreadsheet
	 */
	public void addSpreadsheet(String[][] content) {
		Spreadsheet spreadsheet = new SpreadsheetImpl(this,
				getNextSpreadsheetTitle(), content);
		spreadsheets.add(spreadsheet);
		fireSpreadsheetInserted(spreadsheet, spreadsheets.size() - 1);
	}

	/**
	 * Returns the title to be used for the next spreadsheet added.
	 * 
	 * @return the title to be used for the next spreadsheet added
	 */
	private String getNextSpreadsheetTitle() {
		return SpreadsheetImpl.BASE_TITLE + " " + (createdSpreadsheets++ + 1);
	}

	/**
	 * Adds a new blank spreadsheet to the workbook.
	 */
	public void removeSpreadsheet(Spreadsheet spreadsheet) {
		spreadsheets.remove(spreadsheet);
		// Remove references to the spreadsheet in remaining spreadsheets!
		fireSpreadsheetRemoved(spreadsheet);
	}

	/**
	 * Returns the spreadsheet at the given index.
	 * 
	 * @param index
	 *           the index of the spreadsheet in the workbook
	 * @return the spreadsheet at the given index
	 * @throws IndexOutOfBoundsException
	 *            if the index is out of range (index < 0 || index >=
	 *            |spreadsheets|)
	 */
	public Spreadsheet getSpreadsheet(int index)
			throws IndexOutOfBoundsException {
		return spreadsheets.get(index);
	}

	/**
	 * Returns the number of spreadsheets in the the workbook.
	 * 
	 * @return the number of spreadsheets in the the workbook
	 */
	public int getSpreadsheetCount() {
		return spreadsheets.size();
	}

	/**
	 * Returns an iterator over the spreadsheets in the workbook.
	 * 
	 * @return an iterator over the spreadsheets in the workbook
	 */
	public Iterator<Spreadsheet> iterator() {
		return spreadsheets.iterator();
	}

	/*
	 * EVENT HANDLING
	 */

	/**
	 * Registers the given listener on the workbook.
	 * 
	 * @param listener
	 *           the listener to be added
	 */
	public void addWorkbookListener(WorkbookListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the given listener from the workbook.
	 * 
	 * @param listener
	 *           the listener to be removed
	 */
	public void removeWorkbookListener(WorkbookListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Returns the listeners that have been registered on the workbook.
	 * 
	 * @return the listeners that have been registered on the workbook
	 */
	public WorkbookListener[] getWorkbookListeners() {
		return listeners.toArray(new WorkbookListener[listeners.size()]);
	}

	/**
	 * Notifies all registered listeners that a spreadsheet has been inserted.
	 * 
	 * @param spreadsheet
	 *           the spreadsheet that was inserted
	 * @param index
	 *           the index at which the spreadsheet was inserted
	 */
	private void fireSpreadsheetInserted(Spreadsheet spreadsheet, int index) {
		for (WorkbookListener listener : listeners)
			listener.spreadsheetInserted(spreadsheet, index);
	}

	/**
	 * Notifies all registered listeners that a spreadsheet has been removed.
	 * 
	 * @param spreadsheet
	 *           the spreadsheet that was removed
	 */
	private void fireSpreadsheetRemoved(Spreadsheet spreadsheet) {
		for (WorkbookListener listener : listeners)
			listener.spreadsheetRemoved(spreadsheet);
	}

	/**
	 * Notifies all registered listeners that a spreadsheet has been renamed.
	 * 
	 * @param spreadsheet
	 *           the spreadsheet that was renamed
	 */
	@SuppressWarnings("unused")
	private void fireSpreadsheetRenamed(Spreadsheet spreadsheet) {
		for (WorkbookListener listener : listeners)
			listener.spreadsheetRenamed(spreadsheet);
	}

	/*
	 * GENERAL
	 */

	/**
	 * Customizes deserialization by recreating the listener list.
	 * 
	 * @param stream
	 *           the object input stream from which the object is to be read
	 * @throws IOException
	 *            If any of the usual Input/Output related exceptions occur
	 * @throws ClassNotFoundException
	 *            If the class of a serialized object cannot be found.
	 */
	private void readObject(ObjectInputStream stream) throws IOException,
			ClassNotFoundException {
		stream.defaultReadObject();
		listeners = new ArrayList<WorkbookListener>();
	}
}
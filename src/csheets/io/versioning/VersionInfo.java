package csheets.io.versioning;

import java.util.Date;

import csheets.core.Workbook;

/**
 * This class stores some information about a version of a Workbook. This class
 * can also load itself into a Workbook and remove itself.
 * 
 * @author Gil Castro (gil_1110484)
 */
public class VersionInfo {
	private Object id;
	private String name;
	private Date date;
	private int spreadsheetCount;

	private VersionControllerCodec codec;

	/**
	 * VersionInfo constructor
	 * 
	 * @param id
	 *           the id of this version, used when the codec is requested to
	 *           perform certain operations with this version
	 * @param name
	 *           the name of this version (optional)
	 * @param date
	 *           the date of this version
	 * @param spreadsheetCount
	 *           the number of spreadsheets in this version
	 * @param codec
	 *           the VersionControllerCodec in charge of performing certain
	 *           operations
	 */
	public VersionInfo(Object id, String name, Date date, int spreadsheetCount,
			VersionControllerCodec codec) {
		this.id = id;
		this.name = name;
		this.date = date;
		this.spreadsheetCount = spreadsheetCount;
		this.codec = codec;
	}

	/**
	 * Gets the name of this version
	 * 
	 * @return the name of this version
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the date of this version
	 * 
	 * @return the date of this version
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Gets how many spreadsheets are in this version
	 * 
	 * @return the number of spreadsheets
	 */
	public int getSpreadsheetCount() {
		return spreadsheetCount;
	}

	/**
	 * Loads this version into the target
	 * 
	 * @param target
	 *           a Workbook, preferably empty, where all the data will be loaded
	 *           into
	 * @return the same target Workbook
	 */
	public Workbook loadVersion(Workbook target) {
		return codec.loadVersion(id, target);
	}

	/**
	 * Removes this version from the file
	 */
	public void removeVersion() {
		codec.removeVersion(id);
	}
}

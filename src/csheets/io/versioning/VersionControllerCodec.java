package csheets.io.versioning;

import java.util.List;

import csheets.core.Workbook;

/**
 * This interface is used to let CleanSheets load, save, remove and consult the
 * available Workbook versions in a file. Codecs should implement this interface
 * in order to support version control.
 * 
 * @author Gil Castro (gil_1110484)
 */
public interface VersionControllerCodec {
	/**
	 * Gets all the available versions in this file. It should sort the versions
	 * by data from the newest to the oldest.
	 * 
	 * @return a list with all the available versions
	 */
	public List<VersionInfo> getVersions();

	/**
	 * Loads a version in a Workbook
	 * 
	 * @param versionId
	 *           the id of the version to load
	 * @param target
	 *           where the version will be loaded into
	 * @return the same target Workbook
	 */
	public Workbook loadVersion(Object versionId, Workbook target);

	/**
	 * Adds a version in a Workbook
	 * 
	 * @param version
	 *           information about the version, may be null
	 * @param book
	 *           the version to save
	 */
	public void saveVersion(VersionInfo version, Workbook book);

	/**
	 * Removes a version
	 * 
	 * @param versionId
	 *           the id of the version to remove
	 */
	public void removeVersion(Object versionId);
}

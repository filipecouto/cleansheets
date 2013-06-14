package csheets.ext.versioning;

import csheets.core.Workbook;
import csheets.io.versioning.VersionInfo;

/**
 * This controller contains helper methods to add, remove and get versions from
 * a Workbook.
 * 
 * @author Gil Castro (gil_1110484)
 */
public class VersioningController {
	/**
	 * Adds a new version to a Workbook
	 * 
	 * @param workbook
	 *           the Workbook where this new version will be stored
	 * @param versionInfo
	 *           info about this version, may be null
	 * @param newVersion
	 *           the new version to save into workbook
	 */
	public static void addVersion(Workbook workbook, VersionInfo versionInfo,
			Workbook newVersion) {
		workbook.getVersionController().saveVersion(versionInfo, newVersion);
	}

	/**
	 * Removes a version from a Workbook
	 * 
	 * @param version
	 *           the version to remove
	 */
	public static void removeVersion(VersionInfo version) {
		version.removeVersion();
	}

	/**
	 * Gets and builds a version into a Workbook
	 * 
	 * @param workbook
	 *           the Workbook where this version will be loaded
	 * @param versionInfo
	 *           the version to load
	 * @return the built Workbook with the specified version
	 */
	public static Workbook getVersion(Workbook workbook, VersionInfo versionInfo) {
		return versionInfo.loadVersion(workbook);
	}
}

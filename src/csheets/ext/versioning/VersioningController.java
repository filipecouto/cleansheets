package csheets.ext.versioning;

import csheets.core.Workbook;
import csheets.io.versioning.VersionInfo;

public class VersioningController {
	public static void addVersion(Workbook workbook, VersionInfo versionInfo,
			Workbook newVersion) {
		workbook.getVersionController().saveVersion(versionInfo, newVersion);
	}

	public static void removeVersion(VersionInfo version) {
		version.removeVersion();
	}

	public static Workbook getVersion(Workbook workbook, VersionInfo versionInfo) {
		return versionInfo.loadVersion(workbook);
	}
}

package csheets.io.versioning;

import java.util.List;

import csheets.core.Workbook;

public interface VersionControllerCodec {
	public void saveVersion(VersionInfo version, Workbook book);

	public Workbook loadVersion(Object versionId, Workbook target);

	public List<VersionInfo> getVersions();
}

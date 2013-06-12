package csheets.io.versioning;

import java.util.List;

import csheets.core.Workbook;

public interface VersionControllerCodec {
	public List<VersionInfo> getVersions();

	public Workbook loadVersion(Object versionId, Workbook target);

	public void saveVersion(VersionInfo version, Workbook book);
	
	public void removeVersion(Object versionId);
}

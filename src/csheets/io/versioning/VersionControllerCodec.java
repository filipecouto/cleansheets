package csheets.io.versioning;

import java.util.List;

import csheets.core.Workbook;

public interface VersionControllerCodec {
	public Workbook loadVersion(Object versionId, Workbook target);

	public List<VersionInfo> getVersions();
}

/**
@startuml
class Workbook
interface VersionControllerCodec {
	List<VersionInfo> getVersions()
	Workbook loadVersion(Object versionId, Workbook target)
	void saveVersion(VersionInfo version, Workbook book)
	void removeVersion(Object versionId)
}
class VersionInfo {
	+VersionInfo(id, name, date, spreadsheetCount, codec)
	+Workbook loadVersion(Workbook target)
	+void removeVersion()
}
class VersioningController {
	+{static}void addVersion(workbook, versionInfo, newVersion)
	+{static}void removeVersion(version)
	+{static}Workbook getVersion(workbook, versionInfo)
}
class XMLCodec
VersionInfo					-->		VersionControllerCodec
VersionInfo					-->		Workbook
VersionControllerCodec	<|--		XMLCodec
VersioningController		-->		VersionInfo
Workbook						<-			VersioningController
@enduml
*/
/**
 * @author Gil Castro (gil_1110484)
 */
package csheets.io.versioning;
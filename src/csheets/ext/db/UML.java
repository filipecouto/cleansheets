package csheets.ext.db;

public class UML {
/**
 * @startuml
 * Interface->"DatabaseExportBuilder": instantiate(driver:DatabaseExportInterface)
 * Interface->"DatabaseExportBuilder": setDatabase()
 * Interface->"DatabaseExportBuilder": setTableName()
 * Interface->"DatabaseExportBuilder": setColumns()
 * Interface->"DatabaseExportBuilder": setValues()
 * Interface->"DatabaseExportBuilder": export()
 * "DatabaseExportBuilder"->"DatabaseExportInterface":openDatabase()
 * "DatabaseExportBuilder"->"DatabaseExportInterface":createTable()
 * loop for each row in the spreadsheet selection
 * "DatabaseExportBuilder"->"DatabaseExportInterface":addLine()
 * end
 * "DatabaseExportBuilder"->"DatabaseExportInterface":closeDatabase()
 * "DatabaseExportBuilder"->Interface:export()
 * @enduml
 * 
 * @startuml
 * activate "DatabaseExtension" 
 * Interface-> "DatabaseExtension":getUIExtension()
 * activate "DatabaseUIExtension" 
 * "DatabaseExtension"-> "DatabaseUIExtension": instantiate()
 * activate "DatabaseMenuExtension" 
 * "DatabaseUIExtension"-> "DatabaseMenuExtension":instantiate()
 * activate "DatabaseExtension" 
 * "DatabaseMenuExtension"-> "DatabaseExtension":instantiate()
 * deactivate "DatabaseExtension" 
 * "DatabaseUIExtension"-> "DatabaseExtension":instantiate()
 * deactivate "DatabaseExtension" 
 * Interface-> "DatabaseExtension":getUIExtension()
 * activate "DatabaseUIExtension" 
 * Interface-> "DatabaseUIExtension": getMenu()
 * activate "DatabaseUIMenu"
 * "DatabaseUIExtension"-> "DatabaseUIMenu":instantiate()
 * activate "DatabaseExportMenuItem"
 * "DatabaseUIMenu"-> "DatabaseExportMenuItem":instantiate()
 * deactivate "DatabaseUIMenu"
 * "DatabaseExportMenuItem"-> DatabaseUIMenu:instantiate()
 * deactivate "DatabaseUIExtension"
 * "DatabaseUIMenu"-> "DatabaseUIExtension":instantiate()
 * deactivate "DatabaseUIExtension" 
 * Interface -> "DatabaseUIExtension": getMenu()
 * activate "DatabaseExportMenuItem"
 * Interface-> "DatabaseExportMenuItem":actionPerformed()
 * opt if dialog not instantiated
 * "DatabaseExportMenuItem"->"DatabaseExportDialog":instantiate()
 * end
 * "DatabaseExportMenuItem"->"DatabaseExportDialog":prepareDialog()
 * "DatabaseExportMenuItem"->"DatabaseExportDialog":setVisible(true)
 * activate Interface
 * "DatabaseExportMenuItem"-->Interface: actionPerformed()
 * @enduml
 */
}

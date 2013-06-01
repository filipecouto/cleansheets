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
 * 
 * @startuml
 * class DatabaseExportBuilder
 * class DatabaseDriverManager {
 *  +getAvailableDrivers()
 * }
 * class DatabaseExportInterface {
 *  createTable()
 *  addLine()
 *  openDatabase()
 *  closeDatabase()
 *  getName()
 *  requiresUsername()
 *  requiresPassword()
 * }
 * class H2Exporter
 * class HSQLdbExporter
 * class DatabaseExportController
 * class DatabaseExtension {
 *  +getAvailableDrivers()
 *  +getUIExtension()
 * }
 * class DatabaseExportDialog {
 *  -export()
 * }
 * class DatabaseUIExtension
 * class DatabaseExportMenuItem
 * 
 * DatabaseExportInterface <|-- H2Exporter
 * DatabaseExportInterface <|-- HSQLdbExporter
 * DatabaseExportInterface <-- DatabaseExportBuilder
 * DatabaseExportInterface <-- DatabaseDriverManager
 * DatabaseExportBuilder <-- DatabaseExportController
 * DatabaseDriverManager <-- DatabaseExtension
 * DatabaseExtension <-- DatabaseExportDialog
 * DatabaseExportDialog <-- DatabaseExportController
 * DatabaseExportDialog <-- DatabaseExportMenuItem
 * DatabaseExportMenuItem <-- DatabaseUIMenu
 * DatabaseUIMenu <-- DatabaseUIExtension
 * DatabaseUIExtension <-- DatabaseExtension
 * 
 * 
 * @enduml
 */
}

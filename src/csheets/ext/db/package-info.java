/**
  @startuml
  Interface->"DatabaseExportBuilder": instantiate(driver:DatabaseExportInterface)
  Interface->"DatabaseExportBuilder": setDatabase()
  Interface->"DatabaseExportBuilder": setTableName()
  Interface->"DatabaseExportBuilder": setColumns()
  Interface->"DatabaseExportBuilder": setValues()
  Interface->"DatabaseExportBuilder": export()
  "DatabaseExportBuilder"->"DatabaseExportInterface":openDatabase()
  "DatabaseExportBuilder"->"DatabaseExportInterface":createTable()
  loop for each row in the spreadsheet selection
  "DatabaseExportBuilder"->"DatabaseExportInterface":addLine()
  end
  "DatabaseExportBuilder"->"DatabaseExportInterface":closeDatabase()
  "DatabaseExportBuilder"->Interface:export()
  @enduml
  
  @startuml
  activate "DatabaseExtension" 
  Interface-> "DatabaseExtension":getUIExtension()
  activate "DatabaseUIExtension" 
  "DatabaseExtension"-> "DatabaseUIExtension": instantiate()
  activate "DatabaseMenuExtension" 
  "DatabaseUIExtension"-> "DatabaseMenuExtension":instantiate()
  activate "DatabaseExtension" 
  "DatabaseMenuExtension"-> "DatabaseExtension":instantiate()
  deactivate "DatabaseExtension" 
  "DatabaseUIExtension"-> "DatabaseExtension":instantiate()
  deactivate "DatabaseExtension" 
  Interface-> "DatabaseExtension":getUIExtension()
  activate "DatabaseUIExtension" 
  Interface-> "DatabaseUIExtension": getMenu()
  activate "DatabaseUIMenu"
  "DatabaseUIExtension"-> "DatabaseUIMenu":instantiate()
  activate "DatabaseExportMenuItem"
  "DatabaseUIMenu"-> "DatabaseExportMenuItem":instantiate()
  deactivate "DatabaseUIMenu"
  "DatabaseExportMenuItem"-> DatabaseUIMenu:instantiate()
  deactivate "DatabaseUIExtension"
  "DatabaseUIMenu"-> "DatabaseUIExtension":instantiate()
  deactivate "DatabaseUIExtension" 
  Interface -> "DatabaseUIExtension": getMenu()
  activate "DatabaseExportMenuItem"
  Interface-> "DatabaseExportMenuItem":actionPerformed()
  opt if dialog not instantiated
  "DatabaseExportMenuItem"->"DatabaseExportDialog":instantiate()
  end
  "DatabaseExportMenuItem"->"DatabaseExportDialog":prepareDialog()
  "DatabaseExportMenuItem"->"DatabaseExportDialog":setVisible(true)
  activate Interface
  "DatabaseExportMenuItem"-->Interface: actionPerformed()
  @enduml
  
  @startuml
  class DatabaseExportBuilder
  class DatabaseDriverManager {
   +getAvailableDrivers()
  }
  class DatabaseExportInterface {
   createTable()
   addLine()
   openDatabase()
   closeDatabase()
   getName()
   requiresUsername()
   requiresPassword()
  }
  class H2Exporter
  class HSQLdbExporter
  class DatabaseExportController
  class DatabaseExtension {
   +getAvailableDrivers()
   +getUIExtension()
  }
  class DatabaseExportDialog {
   -export()
  }
  class DatabaseUIExtension
  class DatabaseExportMenuItem



  DatabaseExportInterface <|-- H2Exporter
  DatabaseExportInterface <|-- HSQLdbExporter
  DatabaseExportInterface <-- DatabaseExportBuilder
  DatabaseExportInterface <-- DatabaseDriverManager
  DatabaseExportBuilder <-- DatabaseExportController
  DatabaseDriverManager <-- DatabaseExtension
  DatabaseExtension <-- DatabaseExportDialog
  DatabaseExportDialog <-- DatabaseExportController
  DatabaseExportDialog <-- DatabaseExportMenuItem
  DatabaseExportMenuItem <-- DatabaseUIMenu
  DatabaseUIMenu <-- DatabaseUIExtension
  DatabaseUIExtension <-- DatabaseExtension
  @enduml
 
   @startuml 
   DatabaseImportDialog-> DatabaseImportController : new()
   DatabaseImportDialog-> DatabaseImportController : setDriver()
   DatabaseImportDialog-> DatabaseImportController : setDatabase()
   DatabaseImportDialog-> DatabaseImportController : setTableName()
   DatabaseImportDialog-> DatabaseImportController : setSpreadsheet()
   DatabaseImportDialog-> DatabaseImportController : setCell()
   DatabaseImportDialog-> DatabaseImportController : setImportToCurrentSheet()
   DatabaseImportDialog-> DatabaseImportController : importDatabase()
   DatabaseImportDialog-> DatabaseUIExtention : onDatabaseInteraction(DatabaseInterface,String, Address, Address,String, int)
   
   DatabaseUIExtention-> DatabaseOnSharedArea : new (...)
   DatabaseUIExtention-> CheckUpdatesOnDatabase : new (...)
   DatabaseUIExtention-> CheckUpdatesOnDatabase : new (...)
   CheckUpdatesOnDatabase-> CheckUpdatesOnDatabase : startUpdating()
   activate "CheckUpdatesOnDatabase" 
   DatabaseImportController -> Driver : openDatabase()
   DatabaseImportController <- Driver : getData()
   DatabaseImportController -> Driver : closeDatabase()
   DatabaseImportController -> DatabaseImportDialog: return
   ...
   CleanSheet->DatabaseUIExtention : worbookModified(Event)
   DatabaseUIExtention->CheckUpdatesOnSheet:  startChecking()
   activate "CheckUpdatesOnSheet" 
   CheckUpdatesOnSheet->DatabaseInterface :insert(...)
   CheckUpdatesOnSheet ->CheckUpdatesOnDatabase: setSharedArea(...)
   deactivate "CheckUpdatesOnSheet"
   CheckUpdatesOnDatabase ->CheckUpdatesOnDatabase: stop()
   CheckUpdatesOnDatabase ->CheckUpdatesOnDatabase: setSharedArea(...)
   CheckUpdatesOnDatabase ->CheckUpdatesOnDatabase: startUpdating()
   ...
   CleanSheet->DatabaseUIExtention : worbookModified(Event)
   DatabaseUIExtention->CheckUpdatesOnSheet:  startChecking()
   activate "CheckUpdatesOnSheet" 
   CheckUpdatesOnSheet->DatabaseInterface :update(...)
   deactivate "CheckUpdatesOnSheet"
   ...
   CheckUpdatesOnDatabase->CheckUpdatesOnDatabase :setCellsContent(...)
   CheckUpdatesOnDatabase-> CheckUpdatesOnSheet: setSharedArea(...)
   @enduml
  
   @startuml
   class DatabaseDriverManager {
    +getAvailableDrivers()
   }
   class DatabaseInterface {
    createTable()
    addLine()
    openDatabase()
    closeDatabase()
    getName()
    requiresUsername()
    requiresPassword()
    getTables()
    requiresFile()
    getData()
    dropTable()
   }
   class H2Driver
   class HSQLdbDriver
   class DerbyDriver
   class DatabaseImportController
   class DatabaseExtension {
    +getAvailableDrivers()
    +getUIExtension()
   }
   class DatabaseImportDialog {
    -importDatabase()
   }
class DatabaseImportDialog {
	-JFileChooser fileChooser
	-JTextField url
	-JTextField username
	-JTextField password
	-JComboBox<String> format
	-JComboBox<String> tables
	-JRadioButton currentSheet
	-JRadioButton newSheet
	-JButton browse
	-String databaseName
	-JPanel panelButtons
	-DatabaseExtension extension
	-SpreadsheetTable table
	-boolean importToCurrentSheet
	-OnDatabaseInteractionListener listener
	+DatabaseImportDialog(DatabaseExtension extension, SpreadsheetTable table)
	-JPanel createButtonsPanel()
	+void enableButtons(boolean value)
	-void importDatabase()
	-JPanel createOptionsPanel()
	-void onFormatSelected(DatabaseInterface driver)
	-void updateComboBox(String database)
	+void prepareDialog(SpreadsheetTable table)
	+void setListener(OnDatabaseInteractionListener interactionListener)
}
class JDialog {
}
JDialog <|-- DatabaseImportDialog

class DatabaseUIExtension {
	-{static}DatabaseSharedArea sharedArea
	-CheckUpdatesOnDatabase updatesOnDatabase
	-ListenerOfEdition editListener
	-CheckUpdatesOnSheet updatesOnSheet
	-UIController uiController
	+DatabaseUIExtension(DatabaseExtension extension, UIController uiController)
	+JMenu getMenu()
	+void onDatabaseInteraction(DatabaseInterface database, String databaseName, Address initialCell, Address finalCell, String tableName, int spreadsheetNumber)
	+{static}void setSharedArea(DatabaseSharedArea area)
}
abstract class UIExtension {
}
class CheckUpdatesOnDatabase {
	-DatabaseSharedArea sharedArea
	-Runnable updater
	-DatabaseInterface databaseInterface
	-Connection connection
	-UIController UIcontroller
	+CheckUpdatesOnDatabase(UIController controller, DatabaseSharedArea area)
	+void startSearching()
	+void setSharedArea(DatabaseSharedArea sharedArea)
	+void stop()
}
class CheckUpdatesOnSheet {
	-{static}DatabaseSharedArea sharedArea
	-DatabaseInterface databaseInterface
	-Connection connection
	-UIController uiController
	-CheckUpdatesOnDatabase updatesOnDatabase
	+CheckUpdatesOnSheet(UIController controller, DatabaseSharedArea area, CheckUpdatesOnDatabase updatesOnDatabase)
	+void startChecking()
	-String[] getColumnsName()
	-String getColumnsName(Cell c)
	-String[] getColumnsContent(Cell c)
	-void insertNewCell(Cell c)
	+{static}DatabaseSharedArea getSharedArea()
	+{static}void setSharedArea(DatabaseSharedArea sharedArea)
}
class DatabaseSharedArea {
	-Address initialCell
	-Address finalCell
	-String tableName
	-DatabaseInterface database
	-String databaseName
	-int spreadsheetNumber
	+DatabaseSharedArea(Address initialCell, Address finalCell, String tableName, DatabaseInterface database, String databaseName, int spreadsheetNumber)
	+Address getInitialCell()
	+void setInitialCell(Address initialCell)
	+Address getFinalCell()
	+void setFinalCell(Address finalCell)
	+boolean isInSharedArea(Address address)
	+boolean isNextToSharedArea(Address address)
	+String getTableName()
	+void setTableName(String tableName)
	+int whereIsCell(Address address)
	+DatabaseInterface getDatabase()
	+void setDatabase(DatabaseInterface database)
	+String getDatabaseName()
	+void setDatabaseName(String databaseName)
	+int getSpreadsheetNumber()
	+void setSpreadsheetNumber(int spreadsheetNumber)
}

class DatabaseImportMenuItem {
	-DatabaseExtension extension
	-DatabaseImportDialog dialog
	-OnDatabaseInteractionListener interactionListener
	+DatabaseImportMenuItem(DatabaseExtension extension, OnDatabaseInteractionListener interactionListener)
	+void actionPerformed(ActionEvent e)
	#String getName()
}
abstract class FocusOwnerAction {
}
FocusOwnerAction <|-- DatabaseImportMenuItem

UIExtension <|-- DatabaseUIExtension
interface OnDatabaseInteractionListener {
}
OnDatabaseInteractionListener <|.. DatabaseUIExtension
DatabaseSharedArea <-- DatabaseUIExtension
CheckUpdatesOnSheet <-- DatabaseUIExtension
 CheckUpdatesOnDatabase <-- DatabaseUIExtension
   DatabaseInterface <|-- H2Driver
   DatabaseInterface <|-- HSQLdbDriver
   DatabaseInterface <|-- DerbyDriver
   DatabaseInterface <-- DatabaseImportController
   DatabaseInterface <-- DatabaseDriverManager
   DatabaseDriverManager <-- DatabaseExtension
   DatabaseExtension <-- DatabaseImportDialog 
   DatabaseImportDialog <-- DatabaseImportController
   DatabaseImportDialog <-- DatabaseImportMenuItem
   DatabaseImportMenuItem <-- DatabaseUIMenu
   DatabaseUIMenu <-- DatabaseUIExtension
   DatabaseUIExtension <-- DatabaseExtension
   @enduml
 **/

package csheets.ext.db;
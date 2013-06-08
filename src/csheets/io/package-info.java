/**

<p>Provides support for importing and exporting workbooks to and from various
file formats. Classes that can read and/or write <code>Workbook</code>s
to/from files of a certain format should implement the <code>Codec</code>
interface. <code>Codec</code>s must be named <code>nnnCodec</code> where
<code>nnn</code> is the extension of files of that format.

<p>Also provided is a factory for accessing codecs, and some I/O related
utility classes.

*/
/**
 * iteração 1
 @startuml
 class Frame
 class CleanSheets {
 +save()
 +saveAs()
 +load(
 }
 class Codec {
 }
 class OpenAction {
 }
 class SaveAction {
 }
 class XMLCodec {
 +read(InputStream)
 +write(Workbook, OutputStream)
 +getStylabeCell(Node, Spreadsheet)
 +getIntBorder(String)
 +getBoldAndItalic(String, String)
 +getIntTextAlign(String)
 +getIntCellAlign(String)
 +createXMLContent(Document, Workbook) 
 +setXMLContent(Spreadsheet, int, int,Document, Element)
 +getBold(Font)
 +getItalic(Font)
 +getStringCellAlign(int)
 +getStringTextAlign(int)
 }
 class XMLValidator {
 +validate(InputStream)
 }
 Codec <|-- XMLCodec
 OpenAction  <-- Frame
 SaveAction <-- Frame
 CleanSheets <--OpenAction
 CleanSheets <--SaveAction
 XMLCodec   <--CleanSheets
 XMLValidator  <--CleanSheets
 @enduml

iteração 2
@startuml
 class Frame
 class CleanSheets {
 +save()
 +saveAs()
 +load(
 }
 class Codec {
 }
 class OpenAction {
 }
 class SaveAction {
 }
 class MappedWorkbook {
 +Workbook makeWorkbook()
 }
 class MappedCell {
 }
 class MappedSpreadsheet {
 }
 class XMLCodec {
 +read(InputStream)
 +write(Workbook, OutputStream)
 +getStylabeCell(Node, Spreadsheet)
 +getIntBorder(String)
 +getBoldAndItalic(String, String)
 +getIntTextAlign(String)
 +getIntCellAlign(String)
 +createXMLContent(Document, Workbook) 
 +setXMLContent(Spreadsheet, int, int,Document, Element)
 +getBold(Font)
 +getItalic(Font)
 +getStringCellAlign(int)
 +getStringTextAlign(int)
 }
 MappedWorkbook <-- MappedSpreadsheet
 MappedSpreadsheet <-- MappedCell
 Codec <|-- XMLCodec
 OpenAction  <-- Frame
 SaveAction <-- Frame
 XMLCodec <-- MappedWorkbook
 CleanSheets <--OpenAction
 CleanSheets <--SaveAction
 XMLCodec   <--CleanSheets
 @enduml

 iteração 1
 @startuml 
 Interface->"CleanSheets":save()
 CleanSheets->"CleanSheets":saveAs()
 CleanSheets ->XMLCodec:write(Workbook, FileOutputStream)
 @enduml

 @startuml 
 Interface->"CleanSheets":load()
 CleanSheets->XMLValidator:validate(FileInputStream)
 CleanSheets ->XMLCodec:read(FileInputStream)
 XMLCodec-->CleanSheets : Workbook
 @enduml
 
 iteração 2
  @startuml 
 Interface->"CleanSheets":save()
 CleanSheets->"CleanSheets":saveAs()
 CleanSheets ->XMLCodec:write(Workbook, FileOutputStream)
 @enduml

 @startuml 
 Interface->"CleanSheets":load()
 CleanSheets ->XMLCodec:read(FileInputStream)
 XMLCodec-->CleanSheets : Workbook
 @enduml
 
 **/
	 
package csheets.io;
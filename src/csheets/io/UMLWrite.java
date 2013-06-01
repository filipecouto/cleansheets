package csheets.io;

public class UMLWrite {
    /**
     * @startuml 
     * Interface->"CleanSheets":save()
     * CleanSheets->"CleanSheets":saveAs()
     * CleanSheets ->XMLCodec:write(Workbook, FileOutputStream)
     * @enduml
     **/
}

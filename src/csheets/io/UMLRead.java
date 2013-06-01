package csheets.io;

public class UMLRead {
    /**
     * @startuml 
     * Interface->"CleanSheets":load()
     * CleanSheets->XMLValidator:validate(FileInputStream)
     * CleanSheets ->XMLCodec:read(FileInputStream)
     * XMLCodec-->CleanSheets : Workbook
     * @enduml
     **/
}

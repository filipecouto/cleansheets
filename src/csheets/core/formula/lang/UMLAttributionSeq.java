package csheets.core.formula.lang;

/**
 *
 * @author Filipe Silva
 */
public class UMLAttributionSeq {
    /**
    *@startuml
    *actor Actor
    *Actor -> Interface : Inserts Formula
    *Interface -> Attribution : applyTo(Expression, Expression)
    *Attribution -> CellImpl : setContent(String)
    *CellImpl -> CellImpl : storeContent(String)
    *CellImpl -> FormulaCompiler : compile(Cell, String)
    *CellImpl -> CellImpl : updateDependencies()
    *@enduml
    **/
}

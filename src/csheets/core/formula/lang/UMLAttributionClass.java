package csheets.core.formula.lang;

/**
 *
 * @author Filipe Silva
 */
public class UMLAttributionClass {
    /**
    *@startuml
    *class Attribution
    *class ExcelExpressionNewCompiler
    *class FormulaNewCompiler
    *class FormulaLexer
    *class FormulaParser
    *class FormulaParserTokenTypes
    *ExcelExpressionNewCompiler -- FormulaLexer
    *ExcelExpressionNewCompiler -- FormulaParser
    *ExcelExpressionNewCompiler -- FormulaParserTokenTypes
    *FormulaLexer -- FormulaNewCompiler
    *FormulaParser -- FormulaNewCompiler
    *FormulaParserTokenTypes -- FormulaNewCompiler
    *FormulaParserTokenTypes <|-- FormulaLexer
    *FormulaParserTokenTypes <|-- FormulaParser
    *@enduml
    **/
}

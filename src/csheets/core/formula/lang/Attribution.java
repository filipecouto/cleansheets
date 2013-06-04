package csheets.core.formula.lang;

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
 *
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

import csheets.core.Cell;
import csheets.core.IllegalValueTypeException;
import csheets.core.Value;
import csheets.core.formula.BinaryOperator;
import csheets.core.formula.Expression;
import csheets.core.formula.compiler.FormulaCompilationException;

/**
 * This BinaryOperator attributes a value to a specific cell.
 * 
 * Syntax:
 * <p>
 * <i>CELL := EXPRESSION</i>
 * </p>
 * 
 * @author Filipe Silva & Gil Castro (gil_1110484)
 */
public class Attribution implements BinaryOperator {
	private static final long serialVersionUID = -6209651835251405013L;

	/**
	 * Creates a new attribution.
	 */
	public Attribution() {
	}

	public Value applyTo(Expression leftOperand, Expression rightOperand)
			throws IllegalValueTypeException {
		CellReference cellRef = (CellReference) leftOperand;
		Cell newCell = cellRef.getCell();
		try {
			newCell.setContent(rightOperand.evaluate().toString());
		} catch (FormulaCompilationException e) {
			System.out.println(e);
		}
		return new Value(rightOperand.evaluate().toDouble());
	}

	public String getIdentifier() {
		return ":=";
	}

	public Value.Type getOperandValueType() {
		return Value.Type.UNDEFINED;
	}

	public String toString() {
		return getIdentifier();
	}
}
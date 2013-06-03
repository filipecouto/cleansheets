package csheets.core.formula.lang;

import csheets.core.Cell;
import csheets.core.IllegalValueTypeException;
import csheets.core.Value;
import csheets.core.formula.BinaryOperator;
import csheets.core.formula.Expression;
import csheets.core.formula.compiler.FormulaCompilationException;

/**
 * An attribution.
 * @author Filipe Silva
 */
public class Attribution implements BinaryOperator {

	/**
	 * Creates a new attribution.
	 */
	public Attribution() {}

	public Value applyTo(Expression leftOperand, Expression rightOperand) throws IllegalValueTypeException {
		CellReference cellRef = (CellReference)leftOperand;
                Cell newCell = cellRef.getCell();
                try{
                    newCell.setContent(rightOperand.evaluate().toString());
                } catch(FormulaCompilationException e){
                    System.out.println(e);
                }
            return new Value(rightOperand.evaluate().toDouble());
	}

	public String getIdentifier() {
		return ":=";
	}

	public Value.Type getOperandValueType() {
		return Value.Type.NUMERIC;
	}

	public String toString() {
		return getIdentifier();
	}
}
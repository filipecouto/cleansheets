package csheets.core.formula.lang;

import java.util.ArrayList;

import csheets.core.IllegalValueTypeException;
import csheets.core.Value;
import csheets.core.formula.BinaryOperation;
import csheets.core.formula.Expression;
import csheets.core.formula.util.ExpressionVisitor;

/**
 * This class, implementing Expression, is a collection of Expressions and can
 * be used to execute many expressions in one.
 * 
 * @author Gil Castro (gil_1110484)
 */
public class ExpressionSet implements Expression {
	private static final long serialVersionUID = 6419944190293825722L;

	private ArrayList<Expression> expressions = new ArrayList<Expression>();

	/**
	 * Adds the expression to this collection in order to be used with evaluate()
	 * and accept()
	 * 
	 * @param expression
	 *           the Expression to add
	 */
	public void addExpression(Expression expression) {
		if (expression != null) {
			expressions.add(expression);
		}
	}

	@Override
	public Value evaluate() throws IllegalValueTypeException {
		Value value = null;
		for (Expression e : expressions) {
			value = e.evaluate();
		}
		return value;
	}

	@Override
	public Object accept(ExpressionVisitor visitor) {
		Object result = null;
		for (Expression e : expressions) {
			result = e.accept(visitor);
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Expression e : expressions) {
			builder.append(" *" + e.toString());
		}
		return builder.toString();
	}
}

package csheets.core.formula.lang;

/**
 * @startuml
 * 
 * participant Expression as exp
 * participant ExpressionSet
 * participant "expressions:Expression" as exps
 * 
 * exp -> ExpressionSet: evaluate()
 * activate ExpressionSet
 * loop for each Expression in expressions
 * 	ExpressionSet -> exps: evaluate()
 * 	activate exps
 * 	ExpressionSet <- exps: value
 * 	deactivate exps
 * end
 * exp <- ExpressionSet: value
 * deactivate ExpressionSet
 * 
 * @enduml
 */

import java.util.ArrayList;
import java.util.List;

import csheets.core.IllegalValueTypeException;
import csheets.core.Value;
import csheets.core.formula.Expression;
import csheets.core.formula.util.ExpressionVisitor;

/**
 * This class, implementing Expression, is a collection of Expressions and can
 * be used to execute many expressions in one.
 * 
 * Due to base class limitations, some long expressions may not fully update all
 * cells, editing other related cells may force the system to re-evaluate them.
 * 
 * @author Gil Castro (gil_1110484)
 */
public class ExpressionSet implements Expression {
	private static final long serialVersionUID = 6419944190293825722L;

	private List<Expression> expressions = new ArrayList<Expression>();

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

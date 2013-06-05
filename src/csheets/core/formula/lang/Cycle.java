package csheets.core.formula.lang;

import csheets.core.IllegalValueTypeException;
import csheets.core.Value;
import csheets.core.formula.Expression;
import csheets.core.formula.util.ExpressionVisitor;

/**
 * This Expression represents a cycle (initially made in order to support
 * <code>whyledo</code> keyword).
 * 
 * @author Gil Castro (gil_1110484)
 */
public class Cycle implements Expression {
	private static final long MAX_ITERATIONS = Integer.MAX_VALUE;

	private static final long serialVersionUID = -8962867441373667963L;

	private Expression stopCriteria;
	private Expression[] expressions;

	/**
	 * Constructor. Cycle will evaluate the given <code>expressions</code> while
	 * <code>stopCriteria</code> is <code>true</code>.
	 * 
	 * @param stopCriteria
	 *           an expression that should have boolean value which will
	 *           determine when to stop the cycle
	 * @param expressions
	 *           the expressions to evaluate while the cycle runs
	 */
	public Cycle(Expression stopCriteria, Expression... expressions) {
		this.stopCriteria = stopCriteria;
		this.expressions = expressions;
	}

	@Override
	public Value evaluate() throws IllegalValueTypeException {
		long iterations = 0;
		Value last = null;
		while (stopCriteria.evaluate().toBoolean()) {
			for (Expression e : expressions) {
				last = e.evaluate();
			}
			iterations++;
			if (iterations == MAX_ITERATIONS) {
				// maybe the user did something wrong, let's leave the cycle
				break;
			}
		}
		return last;
	}

	@Override
	public Object accept(ExpressionVisitor visitor) {
		stopCriteria.accept(visitor);
		Object accept = null;
		for (Expression e : expressions) {
			accept = e.accept(visitor);
		}
		return accept;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("while(" + stopCriteria + ") {");
		for (Expression e : expressions) {
			builder.append(" *" + e.toString());
		}
		builder.append('}');
		return builder.toString();
	}
}

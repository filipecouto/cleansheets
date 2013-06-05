package csheets.core.formula.lang;

import csheets.core.IllegalValueTypeException;
import csheets.core.Value;
import csheets.core.formula.Expression;
import csheets.core.formula.util.ExpressionVisitor;

public class Cycle implements Expression {
	private static final long serialVersionUID = -8962867441373667963L;

	Expression stopCriteria;
	Expression[] expressions;

	public Cycle(Expression stopCriteria, Expression... expressions) {
		this.stopCriteria = stopCriteria;
		this.expressions = expressions;
	}

	@Override
	public Value evaluate() throws IllegalValueTypeException {
		Value last = null;
		while (stopCriteria.evaluate().toBoolean()) {
			for (Expression e : expressions) {
				last = e.evaluate();
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

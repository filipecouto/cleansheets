package csheets.core.formula.lang;

/**
 * @startuml
 * 
 * participant Expression as exp
 * participant Loop
 * participant "stopCriteria:Expression" as stop
 * participant "expressions:Expression" as exps
 * 
 * exp -> Loop: evaluate()
 * activate Loop
 * loop while shouldContinue
 * 	Loop -> stop: evaluate.toBoolean()
 * 	activate stop
 * 	Loop <- stop: shouldContinue
 * 	deactivate stop
 * 	loop for each Expression in expressions
 * 		Loop -> exps: evaluate()
 * 		activate exps
 * 		Loop <- exps: last
 * 		deactivate exps
 * 	end
 * end
 * exp <- Loop: last
 * deactivate Loop
 * 
 * @enduml
 */

import csheets.core.IllegalValueTypeException;
import csheets.core.Value;
import csheets.core.formula.Expression;
import csheets.core.formula.util.ExpressionVisitor;

/**
 * This Expression represents a loop (initially made in order to support
 * <code>whyledo</code> keyword).
 * 
 * @author Gil Castro (gil_1110484)
 */
public class Loop implements Expression {
	private static final long MAX_ITERATIONS = Integer.MAX_VALUE/4;

	private static final long serialVersionUID = -8962867441373667963L;

	private Expression stopCriteria;
	private Expression[] expressions;

	/**
	 * Constructor. Loop will evaluate the given <code>expressions</code> while
	 * <code>stopCriteria</code> is <code>true</code>.
	 * 
	 * @param stopCriteria
	 *           an expression that should have boolean value which will
	 *           determine when to stop the loop
	 * @param expressions
	 *           the expressions to evaluate while the loop runs
	 */
	public Loop(Expression stopCriteria, Expression... expressions) {
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
				return null;
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

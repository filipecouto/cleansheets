package csheets.core.formula.compiler;

import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import antlr.ANTLRException;
import antlr.collections.AST;
import csheets.core.Cell;
import csheets.core.Value;
import csheets.core.formula.BinaryOperation;
import csheets.core.formula.BinaryOperator;
import csheets.core.formula.Expression;
import csheets.core.formula.Function;
import csheets.core.formula.FunctionCall;
import csheets.core.formula.Literal;
import csheets.core.formula.Reference;
import csheets.core.formula.UnaryOperation;
import csheets.core.formula.lang.CellReference;
import csheets.core.formula.lang.Loop;
import csheets.core.formula.lang.ExpressionSet;
import csheets.core.formula.lang.Language;
import csheets.core.formula.lang.RangeReference;
import csheets.core.formula.lang.ReferenceOperation;
import csheets.core.formula.lang.UnknownElementException;
import csheets.core.formula.newcompiler.FormulaLexer;
import csheets.core.formula.newcompiler.FormulaParser;
import csheets.core.formula.newcompiler.FormulaParserTokenTypes;

/**
 * A compiler that generates new formulas from strings based on Excel-style.
 * 
 * @author Filipe Silva & Gil Castro (gil_1110484)
 */
public class ExcelExpressionNewCompiler implements ExpressionCompiler {
	/* The character that signals that a cell's content is a formula ('#') */
	public static final char FORMULA_STARTER = '#';

	/**
	 * Creates the Excel expression compiler.
	 */
	public ExcelExpressionNewCompiler() {
	}

	public char getStarter() {
		return FORMULA_STARTER;
	}

	public Expression compile(Cell cell, String source)
			throws FormulaCompilationException {
		// Creates the lexer and parser
		FormulaParser parser = new FormulaParser(new FormulaLexer(
				new StringReader(source)));

		try {
			// Attempts to match an expression
			parser.content();
		} catch (ANTLRException e) {
			throw new FormulaCompilationException(e);
		}

		// Converts the expression and returns it
		return convert(cell, parser.getAST());
	}

	/**
	 * Converts the given ANTLR AST to an expression.
	 * 
	 * @param node
	 *           the abstract syntax tree node to convert
	 * @return the result of the conversion
	 */
	protected Expression convert(Cell cell, AST node)
			throws FormulaCompilationException {
		final int type = node.getType();
		/*
		 * System.out.println("Converting node '" + node.getText() + "' (" + type
		 * + ") of tree '" + node.toStringTree() + "' with " +
		 * node.getNumberOfChildren() + " children.");
		 */
		if (type == FormulaParserTokenTypes.WHILEDO || type == FormulaParserTokenTypes.DOWHILE) {
			AST exps = node.getFirstChild().getFirstChild();
			List<Expression> args = new ArrayList<Expression>();
			do {
				args.add(convert(cell, exps));
			} while((exps = exps.getNextSibling()) != null);
			Expression stopCriteria;
			boolean stop;
			if(type == FormulaParserTokenTypes.WHILEDO) {
			    stopCriteria = args.get(0);
			    stop = true;
			    args.remove(0);
			} else {
			    stopCriteria = args.get(args.size() - 1);
			    stop = false;
			    args.remove(args.size() - 1);
			}
			Expression[] argArray = args.toArray(new Expression[args.size()]);
			return new Loop(stopCriteria, stop, argArray);
		} else if (type == FormulaParserTokenTypes.LBRAC) {
			ExpressionSet exps = new ExpressionSet();
			AST exp = node.getFirstChild();
			do {
				exps.addExpression(convert(cell, exp));
			} while ((exp = exp.getNextSibling()) != null);
			return exps;
		} else {
			if (node.getNumberOfChildren() == 0) {
				try {
					switch (type) {
						case FormulaParserTokenTypes.NUMBER:
							return new Literal(Value.parseNumericValue(node.getText()));
						case FormulaParserTokenTypes.STRING:
							return new Literal(Value.parseValue(node.getText(),
									Value.Type.BOOLEAN, Value.Type.DATE));
						case FormulaParserTokenTypes.CELL_REF:
							return new CellReference(cell.getSpreadsheet(),
									node.getText());
						case FormulaParserTokenTypes.NAME:
							/*
							 * return cell.getSpreadsheet().getWorkbook().
							 * getRange(node.getText()) (Reference)
							 */
					}
				} catch (ParseException e) {
					throw new FormulaCompilationException(e);
				}
			}

			// Convert function call
			Function function = null;
			try {
				function = Language.getInstance().getFunction(node.getText());
			} catch (UnknownElementException e) {
			}

			if (function != null) {
				List<Expression> args = new ArrayList<Expression>();
				AST child = node.getFirstChild();
				if (child != null) {
					args.add(convert(cell, child));
					while ((child = child.getNextSibling()) != null)
						args.add(convert(cell, child));
				}
				Expression[] argArray = args.toArray(new Expression[args.size()]);
				return new FunctionCall(function, argArray);
			}

			if (node.getNumberOfChildren() == 1)
				// Convert unary operation
				return new UnaryOperation(Language.getInstance().getUnaryOperator(
						node.getText()), convert(cell, node.getFirstChild()));
			else if (node.getNumberOfChildren() == 2) {
				// Convert binary operation
				BinaryOperator operator = Language.getInstance().getBinaryOperator(
						node.getText());
				if (operator instanceof RangeReference)
					return new ReferenceOperation((Reference) convert(cell,
							node.getFirstChild()), (RangeReference) operator,
							(Reference) convert(cell, node.getFirstChild()
									.getNextSibling()));
				else
					return new BinaryOperation(convert(cell, node.getFirstChild()),
							operator, convert(cell, node.getFirstChild()
									.getNextSibling()));
			} else
				// Shouldn't happen
				throw new FormulaCompilationException();
		}
	}
}
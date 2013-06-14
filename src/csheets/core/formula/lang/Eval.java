package csheets.core.formula.lang;

import csheets.core.Address;
import csheets.core.Cell;
import csheets.core.IllegalValueTypeException;
import csheets.core.SpreadsheetImpl;
import csheets.core.Value;
import csheets.core.Workbook;
import csheets.core.formula.Expression;
import csheets.core.formula.Function;
import csheets.core.formula.FunctionParameter;
import csheets.core.formula.compiler.ExcelExpressionCompiler;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.core.formula.compiler.FormulaCompiler;
import csheets.ui.ctrl.UIController;

public class Eval implements Function {

    
    private static UIController uiController;
    
    public static final FunctionParameter[] parameters = new FunctionParameter[] {
	new FunctionParameter(Value.Type.TEXT, "Function", false,
		"A function")
    };
    
    @Override
    public String getIdentifier() {
	return "EVAL";
    }

    @Override
    public Value applyTo(Expression[] args) throws IllegalValueTypeException {
	Value value = args[0].evaluate();
	FormulaCompiler compiler = FormulaCompiler.getInstance();
	try {
	    return compiler.compile(uiController.getActiveCell(), value.toString()).evaluate();
	} catch (FormulaCompilationException e) {
	    e.printStackTrace();
	}
	return value;
    }

    @Override
    public FunctionParameter[] getParameters() {
	return parameters;
    }

    @Override
    public boolean isVarArg() {
	return false;
    }
    
    public static void setUIController(UIController controller) {
	uiController = controller;
    }
    
}

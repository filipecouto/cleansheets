package csheets.ui.ctrl;

import java.util.ArrayList;

import csheets.core.ActionStack;
import csheets.core.Workbook;
import csheets.io.mapping.MappedWorkbook;
/**
@startuml
abstract class ActionHistoryAction {
	#{static}ActionStack getUndoStack()
	#{static}ActionStack getRedoStack()
	#{static}boolean isChangingStacks()
	#void addState(state)
	#void undo(book)
	#void redo(book)
	-void moveState(book, from, to)
	#boolean hasActionsToUndo()
	#boolean hasActionsToRedo()
	#void start(book)
	#{static}void clearHistory()
	-{static}void notifyStacksChanged()
	#{abstract}void onHistoryChanged()
}
class ActionStack {
	+boolean isEmpty()
	+void push(change)
	+MappedWorkbook pop()
	+MappedWorkbook moveFrom(other)
	+void clear()
	+int getLimit()
	+void setLimit(int limit)
	+void removeLimit()
}
abstract class FocusOwnerAction
FocusOwnerAction		<|--		ActionHistoryAction
ActionStack				<--		ActionHistoryAction
ActionHistoryAction	<|--		UndoAction
ActionHistoryAction	<|--		RedoAction
@enduml
 */

/**
 * This abstract action can be used to manipulate editions in the workbook. This
 * class contains two stacks, one holds actions to undo, the other holds actions
 * to redo. <i>This class uses static members in order to manage the action
 * history. It could store the history on each Workbook but it wasn't
 * implemented in order to avoid modifying the core system too much.</i>
 * 
 * @author Gil Castro (gil_1110484)
 */
@SuppressWarnings("serial")
public abstract class ActionHistoryAction extends FocusOwnerAction {
	private static ActionStack UNDO_STACK;
	private static ActionStack REDO_STACK;
	private static MappedWorkbook CURRENT_STATE;

	private static boolean changing = false;

	private static ArrayList<ActionHistoryAction> instances = new ArrayList<ActionHistoryAction>();

	public ActionHistoryAction() {
		instances.add(this);
	}

	protected static final ActionStack getUndoStack() {
		if (UNDO_STACK == null) {
			UNDO_STACK = new ActionStack();
		}
		return UNDO_STACK;
	}

	protected static final ActionStack getRedoStack() {
		if (REDO_STACK == null) {
			REDO_STACK = new ActionStack();
		}
		return REDO_STACK;
	}

	/**
	 * Checks whether there is any change in progress at the moment in order to
	 * avoid possible infinite loops.
	 * 
	 * @return true if there is any loop risk, false otherwise
	 */
	protected static boolean isChangingStacks() {
		return changing;
	}

	/**
	 * Pushes the state of this workbook into the undo stacks
	 * 
	 * @param state
	 *           the new state to save on the top of the undo stack
	 */
	protected void addState(Workbook state) {
		if (isChangingStacks()) {
			return;
		}
		if (CURRENT_STATE != null) {
			getUndoStack().push(CURRENT_STATE);
		}
		CURRENT_STATE = new MappedWorkbook(state);
		getRedoStack().clear();
		notifyStacksChanged();
	}

	/**
	 * Loads the state at the top of the undo stack into the workbook
	 * 
	 * @param book
	 *           the Workbook to load the state into
	 */
	protected void undo(Workbook book) {
		moveState(book, getUndoStack(), getRedoStack());
	}

	/**
	 * Loads the state at the top of the redo stack into the workbook
	 * 
	 * @param book
	 *           the Workbook to load the state into
	 */
	protected void redo(Workbook book) {
		moveState(book, getRedoStack(), getUndoStack());
	}

	private void moveState(Workbook book, ActionStack from, ActionStack to) {
		changing = true;
		to.push(CURRENT_STATE);
		final MappedWorkbook state = from.pop();
		state.makeWorkbook(book);
		CURRENT_STATE = state;
		notifyStacksChanged();
		changing = false;
	}

	/**
	 * Tells whether there are any actions in the undo stack
	 * 
	 * @return true if there are any actions, false otherwise
	 */
	protected boolean hasActionsToUndo() {
		return !getUndoStack().isEmpty();
	}

	/**
	 * Tells whether there are any actions in the redo stack
	 * 
	 * @return true if there are any actions, false otherwise
	 */
	protected boolean hasActionsToRedo() {
		return !getRedoStack().isEmpty();
	}

	protected void start(Workbook book) {
		if (book != null) {
			getUndoStack().clear();
			addState(book);
		}
	}

	protected static void clearHistory() {
		getUndoStack().clear();
		getRedoStack().clear();
		notifyStacksChanged();
	}

	/**
	 * Tells all ActionHistoryAction instances that their stacks changed
	 */
	private static void notifyStacksChanged() {
		for (ActionHistoryAction action : instances) {
			action.onHistoryChanged();
		}
	}

	/**
	 * Called whenever the undo or redo stacks change
	 */
	protected abstract void onHistoryChanged();
}

package csheets.ui.ctrl;

import java.util.ArrayList;

import csheets.core.ActionStack;
import csheets.core.Workbook;
import csheets.io.mapping.MappedWorkbook;

/**
 * This abstract action can be used to manipulate editions in the workbook. This
 * class contains two stacks, one holds actions to undo, the other holds actions
 * to redo.
 * 
 * @author Gil Castro (gil_1110484)
 */
@SuppressWarnings("serial")
public abstract class ActionHistoryAction extends FocusOwnerAction {
	private static ActionStack UNDO_STACK;
	private static ActionStack REDO_STACK;

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
		getUndoStack().push(new MappedWorkbook(state));
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
		final MappedWorkbook state = to.moveFrom(from);
		state.makeWorkbook(book);
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

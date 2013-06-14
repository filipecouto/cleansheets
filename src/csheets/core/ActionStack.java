package csheets.core;

import java.util.Stack;

import csheets.io.mapping.MappedWorkbook;

/**
 * This class represents a stack of actions (actions are represented as
 * MappedWorkbook instances for now in order to avoid too many changes in
 * CleanSheets's core) that can be used to allow the user to undo/redo actions.
 * 
 * @author Gil Castro (gil_1110484)
 */
public class ActionStack {
	private Stack<MappedWorkbook> changes = new Stack<MappedWorkbook>();
	private int limit = 50;

	/**
	 * Tells whether this stack is empty
	 * 
	 * @return true if it is empty, false otherwise
	 */
	public boolean isEmpty() {
		return changes.isEmpty();
	}

	/**
	 * Adds a new item to this stack, caps the stack if it goes over the limit
	 * (if set)
	 * 
	 * @param change
	 */
	public void push(MappedWorkbook change) {
		changes.push(change);
		if (limit != -1 && changes.size() > limit) {
			changes.remove(0);
		}
	}

	/**
	 * Gets the last added item
	 * 
	 * @return the item at the top of the stack
	 */
	public MappedWorkbook pop() {
		return changes.pop();
	}

	/**
	 * Moves the item at the top of the stack <code>other</code> to the top of
	 * this one
	 * 
	 * @param other
	 *           the other stack to get the item from
	 * @return the item that was moved
	 */
	public MappedWorkbook moveFrom(ActionStack other) {
		final MappedWorkbook change = other.changes.pop();
		changes.push(change);
		return change;
	}

	/**
	 * Removes all items from the stack
	 */
	public void clear() {
		changes.clear();
	}

	/**
	 * Gets the limit of this stack, if its size goes over the limit, it will be
	 * capped
	 * 
	 * @return the current limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * Sets the limit of this stack, if its size goes over the limit, it will be
	 * capped
	 * 
	 * @param limit
	 *           the new limit
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * Removes the currently set limit, the stack may have an infinite size
	 */
	public void removeLimit() {
		this.limit = -1;
	}
}

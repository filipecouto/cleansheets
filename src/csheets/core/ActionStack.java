package csheets.core;

import java.util.Stack;

import csheets.io.mapping.MappedWorkbook;

public class ActionStack {
	private Stack<MappedWorkbook> changes = new Stack<MappedWorkbook>();

	public boolean isEmpty() {
		return changes.isEmpty();
	}

	public void push(MappedWorkbook change) {
		changes.push(change);
	}

	public MappedWorkbook pop() {
		return changes.pop();
	}

	public MappedWorkbook moveFrom(ActionStack other) {
		final MappedWorkbook change = other.changes.pop();
		changes.push(change);
		return change;
	}

	public void clear() {
		changes.clear();
	}
}

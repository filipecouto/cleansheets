/*
 * Copyright (c) 2005 Einar Pehrson <einar@pehrson.nu>.
 *
 * This file is part of
 * CleanSheets - a spreadsheet application for the Java platform.
 *
 * CleanSheets is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * CleanSheets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CleanSheets; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package csheets.ui.ctrl;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import csheets.CleanSheets;
import csheets.core.Address;
import csheets.core.Spreadsheet;
import csheets.core.Workbook;

/**
 * An redo operation.
 * 
 * @author Einar Pehrson
 */
@SuppressWarnings("serial")
public class RedoAction extends ActionHistoryAction {
	private UIController controller;

	/**
	 * Creates a new redo action.
	 */
	public RedoAction(UIController controller) {
		this.controller = controller;
		setEnabled(false);
	}

	protected String getName() {
		return "Redo";
	}

	protected void defineProperties() {
		putValue(MNEMONIC_KEY, KeyEvent.VK_R);
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		putValue(SMALL_ICON,
				new ImageIcon(CleanSheets.class.getResource("res/img/redo.gif")));
	}

	/**
	 * Inserts a column before the active cell in the focus owner table.
	 * 
	 * @param event
	 *           the event that was fired
	 */
	public void actionPerformed(ActionEvent event) {
		if (focusOwner == null)
			return;
		final Workbook activeBook = controller.getActiveWorkbook();
		final Spreadsheet activeSheet = controller.getActiveSpreadsheet();
		final Address address = controller.getActiveCell().getAddress();

		final int len = activeBook.getSpreadsheetCount();
		int selectedSheet = 0;
		for (int i = 0; i < len; i++) {
			if (activeBook.getSpreadsheet(i) == activeSheet) {
				selectedSheet = i;
				break;
			}
		}

		redo(controller.getActiveWorkbook());

		final Spreadsheet sheetToActivate = activeBook.getSpreadsheet(selectedSheet);
		controller.setActiveSpreadsheet(sheetToActivate);
		controller.setActiveCell(sheetToActivate.getCell(
				address));
	}

	@Override
	protected void onHistoryChanged() {
		setEnabled(hasActionsToRedo());
	}
}
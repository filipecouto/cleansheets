package csheets.ext.comments.ui;

/*
 * Copyright (c) 2013 Alexandre Braganca, Einar Pehrson
 *
 * This file is part of
 * CleanSheets Extension for Comments
 *
 * CleanSheets Extension for Assertions is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * CleanSheets Extension for Assertions is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CleanSheets Extension for Assertions; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import csheets.core.Cell;
import csheets.ext.assertion.AssertableCell;
import csheets.ext.assertion.AssertableCellListener;
import csheets.ext.assertion.Assertion;
import csheets.ext.assertion.AssertionExtension;
import csheets.ext.comments.CommentableCell;
import csheets.ext.comments.CommentableCellListener;
import csheets.ext.comments.CommentsExtension;
import csheets.ui.ctrl.FocusOwnerAction;
import csheets.ui.ctrl.SelectionEvent;
import csheets.ui.ctrl.SelectionListener;
import csheets.ui.ctrl.UIController;
import csheets.ui.sheet.SpreadsheetTable;

/**
 * A panel for adding or editing a comment for a cell
 * @author Alexandre Braganca
 * @author Einar Pehrson
 */
@SuppressWarnings("serial")
public class CommentPanel extends JPanel implements SelectionListener,
		CommentableCellListener {

	/** The assertion controller */
	private CommentController controller;

	/** The commentable cell currently being displayed in the panel */
	private CommentableCell cell;

	/** The text field in which the comment of the cell is displayed.*/
    private JTextArea commentField = new JTextArea();

	/**
	 * Creates a new comment panel.
	 * @param uiController the user interface controller
	 */
	public CommentPanel(UIController uiController) {
		// Configures panel
		super(new BorderLayout());
		setName(CommentsExtension.NAME);

		// Creates controller
		controller = new CommentController(uiController);
		uiController.addSelectionListener(this);

		// Creates comment components
		ApplyAction applyAction = new ApplyAction();
		
		commentField.setPreferredSize(new Dimension(120, 240));		// width, height
		commentField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));		// width, height
		commentField.addFocusListener(applyAction);
		commentField.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Lays out comment components
		JPanel commentPanel = new JPanel();
		commentPanel.setLayout(new BoxLayout(commentPanel, BoxLayout.PAGE_AXIS));
		commentPanel.setPreferredSize(new Dimension(130, 336));
		commentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));		// width, height
		commentPanel.add(commentField);

		// Adds borders
		TitledBorder border = BorderFactory.createTitledBorder("Comment");
		border.setTitleJustification(TitledBorder.CENTER);
		commentPanel.setBorder(border);

		// Adds panels
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(commentPanel, BorderLayout.NORTH);
		add(northPanel, BorderLayout.NORTH);
	}

	/**
	 * Updates the comments field
	 * @param event the selection event that was fired
	 */
	public void selectionChanged(SelectionEvent event) {
		Cell cell = event.getCell();
		if (cell != null) {
			CommentableCell activeCell
				= (CommentableCell)cell.getExtension(CommentsExtension.NAME);
			activeCell.addCommentableCellListener(this);
			commentChanged(activeCell);
		} else {
			commentField.setText("");
		}

		// Stops listening to previous active cell
		if (event.getPreviousCell() != null)
			((CommentableCell)event.getPreviousCell().getExtension(CommentsExtension.NAME))
				.removeCommentableCellListener(this);
	}

	/**
	 * Updates the comment field when the comments of the
	 * active cell is changed.
	 * @param cell the cell whose comments changed
	 */
	public void commentChanged(CommentableCell cell) {
		// Stores the cell for use when applying comments
		this.cell = cell;

		// Updates the text field and validates the comment, if any
		if (cell.hasComment()) {
			commentField.setText(cell.getUserComment());
		} else {
			commentField.setText("");
		}
	}
	
	protected class ApplyAction implements FocusListener {

		@Override
		public void focusGained(FocusEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub
			if (cell != null) {
				controller.setComment(cell, commentField.getText().trim());
			}
		}
	}
}

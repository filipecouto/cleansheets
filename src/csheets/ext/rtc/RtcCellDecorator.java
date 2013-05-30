package csheets.ext.rtc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import csheets.core.Cell;
import csheets.ui.ext.CellDecorator;

public class RtcCellDecorator extends CellDecorator {
    @Override
    public void decorate(JComponent component, Graphics g, Cell cell,
	    boolean selected, boolean hasFocus) {
	Graphics2D g2 = (Graphics2D) g;
	Color oldPaint = g2.getColor();
	Font oldFont = g2.getFont();

	g2.setColor(new Color(51, 181, 229));
	g2.drawRect(0, 0, component.getWidth() - 1, component.getHeight() - 1);

	g2.setColor(oldPaint);
	g2.setFont(oldFont);
    }
}
package csheets.ext.rtc.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import csheets.core.Cell;
import csheets.ext.rtc.RealTimeCollaboration;
import csheets.ui.ext.CellDecorator;

public class RtcCellDecorator extends CellDecorator {
    private RealTimeCollaboration extension;

    public RtcCellDecorator(RealTimeCollaboration extension) {
	this.extension = extension;
    }

    @Override
    public void decorate(JComponent component, Graphics g, Cell cell,
	    boolean selected, boolean hasFocus) {
	if (extension.isConnected() && !extension.isShared(cell.getAddress())) {
	    Graphics2D g2 = (Graphics2D) g;
	    Color oldPaint = g2.getColor();

	    g2.setColor(new Color(180, 180, 180, extension.isOwner() ? 40 : 80));
	    g2.fillRect(0, 0, component.getWidth() - 1,
		    component.getHeight() - 1);

	    g2.setColor(oldPaint);
	}
    }
}
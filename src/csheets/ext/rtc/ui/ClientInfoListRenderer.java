package csheets.ext.rtc.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

import csheets.ext.rtc.ClientInfo;

public class ClientInfoListRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 7269184556439312720L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
	    int index, boolean isSelected, boolean cellHasFocus) {
	ClientInfo info = (ClientInfo) value;
	setText(info.getName());
	final Color color = info.getColor();
	setIcon(new Icon() {
	    @Override
	    public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setColor(color);
		g2.fillRect(x + 4, y + 4, 16, 16);
	    }

	    @Override
	    public int getIconWidth() {
		return 24;
	    }

	    @Override
	    public int getIconHeight() {
		return 24;
	    }
	});
	return this;
    }
}
